# Workers

[MDN](https://developer.mozilla.org/en-US/docs/Web/API/Web_Workers_API/Using_web_workers) ορίζει τους Web Workers ως:

> Οι Web Workers είναι ένα απλό μέσο για το περιεχόμενο ιστού να εκτελεί σενάρια σε νήματα φόντου.
> Το worker thread μπορεί να εκτελεί εργασίες χωρίς να παρεμβαίνει στο περιβάλλον εργασίας χρήστη.

Το ES4X δεν είναι πρόγραμμα περιήγησης και δεν ενδιαφέρεται για τη διεπαφή χρήστη, ωστόσο μπορείτε επίσης να εκτελέσετε μακροχρόνιες εργασίες από την πλευρά του διακομιστή. Στο Vert.x όλα είναι non blocking, οπότε ακόμη και η δημιουργία workers πρέπει να ακολουθεί την ίδια σημασιολογία, για αυτόν τον λόγο δεν μπορούμε να ακολουθήσουμε πλήρως τη διεπαφή workers, αλλά αντικαθιστούμε τον κατασκευαστή με μια εργοστασιακή λειτουργία.

Φανταστείτε ότι στον κώδικά σας πρέπει να εκτελέσετε μια εντατική εργασία CPU, δεν πρέπει να αποκλείσετε τον event loop, επομένως το λογικό βήμα είναι να χρησιμοποιήσετε vert.x worker vertices. Το Worker API μπορεί να αντιστοιχιστεί στο Vert.x API με μερικές μικρή διαφορές.

## Worker Παράδειγμα

Φανταστείτε τον ακόλουθο κωδικό Worker:

```js
// Λάβετε μια αναφορά στην Thread class για να προκαλέσετε κάποιο μπλοκάρισμα ...
const Thread = Java.type('java.lang.Thread');

// Το περιβάλλον εργαζομένου αναφέρεται από τη μεταβλητή `self όπως στα έγγραφα MDN
self.onmessage = function(e) {
  console.log('Message received from main script, will sleep 5 seconds...');
  // Cause some havok in the event loop
  Thread.sleep(5 * 1000);
  var workerResult = 'Result: ' + (e.data[0] * e.data[1]);
  console.log('Posting message back to main script');
  // επιστρέψτε τα δεδομένα πίσω στο κύριο κατακόρυφο
  postMessage(workerResult);
};
```

## Τι πρέπει να είναι γνωστό

Οι Workers φορτώνονται σε ξεχωριστό περιβάλλον, ώστε να μην μπορείτε να κάνετε κοινή χρήση συναρτήσεων από την κύρια κορυφή και τον εργαζόμενο, όλες οι επικοινωνίες λειτουργούν με τη μετάδοση μηνυμάτων (eventbus) χρησιμοποιώντας:

* `postMessage()` στέλνει ένα μήνυμα στην άλλη πλευρά
* `onmessage` λαμβάνει ένα μήνυμα από την άλλη πλευρά

### Verticle πλευρά

Η πλευρά verticle του API σας επιτρέπει να λαμβάνετε σφάλματα και ναι κάνετε `terminate()` workers, ενώ ο worker από μόνος του δεν μπορεί.

## Verticle Παράδειγμα

```js
Worker.create('workers/worker.js', function (create) {
  if (create.succeeded()) {
    var worker = create.result();

    worker.onmessage = function (msg) {
      console.log('onmessage: ' + msg);
    };

    worker.onerror = function (err) {
      console.err(err);
      // τερματίστε τον worker
      worker.terminate();
    };

    console.log('posting...');
    worker.postMessage({data: [2, 3]});
  }
});
```

Έτσι, ο κώδικας που δεν θα επιτρέπεται να εκτελείται στο βρόχο συμβάντων `Thread.sleep(5000)` εκτελείται τώρα σε ένα νήμα εργαζομένου αφήνοντας το νήμα βρόχου συμβάντος δωρεάν για όλες τις άλλες εργασίες IO.

## Polyglot Workers

Είναι ακόμα δυνατό να γράψετε Workers που δεν είναι από JavaScript. Οι εργαζόμενοι πρέπει να ακολουθούν μια πολύ μικρή λίστα κανόνων:

* Οι Workers πρέπει να δηλώσουν τη διεύθυνση: `{deploymentId}.out` για να λαμβάνουν μηνύματα από το κύριο script.
* Οι Workers θα πρέπει να στείλουν μηνύματα στο: `{deploymentId}.in` να στείλουν μηνύματα στο κύριο script.
* Το φορτίο μηνυμάτων αναμένεται να είναι `JSON.stringify(message)` για να αποφύγετε τυχόν προβλήματα μεταξύ των γλωσσών
* Οι Workers αναμένεται να είναι τοπικοί, εάν θέλετε να συνδεθείτε με έναν worker οπουδήποτε στο σύμπλεγμα, τότε πρέπει να χρησιμοποιήσετε τον κατασκευαστή με μια επιπλέον παράμετρο `true`, e.g.: `new Worker('deploymentId', true)`.
