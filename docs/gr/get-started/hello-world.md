# Hello World

Η απλούστερη εφαρμογή που μπορούμε να δημιουργήσουμε είναι ένα ``Hello World`` σε ένα αρχείο ``hello-es4x.js``:

```js
vertx.createHttpServer()
  .requestHandler(req => {
    req.response()
      .end('Hello ES4X world!');
  })
  .listen(8080);
```

Τώρα μπορείτε να εκτελέσετε αυτήν την εφαρμογή χρησιμοποιώντας:

```bash
$ es4x hello-es4x.js
```

::: tip
Σε συστήματα UNIX, τα σενάρια μπορούν να γίνουν εκτελέσιμα και το shebang `#!/usr/bin/env es4x` χρησιμοποιείται που θα τους κάνει αυτόματη εκτέλεση. Σημειώστε ωστόσο ότι οι εξαρτήσεις πρέπει να υπάρχουν ήδη στο τρέχων directort.
:::

Και σε ένα δεύτερο τερματικό:

```bash
$ curl localhost:8080
Hello ES4X world!
```

::: προειδοποίηση
Η εκτέλεση σεναρίων χρησιμοποιώντας την εντολή ``es4x`` μπορεί να είναι χρήσιμη για μικρά προγράμματα που δεν έχουν άλλες εξαρτήσεις από το ``vertx``. Για πιο περίπλοκη εφαρμογή θα χρησιμοποιηθεί ένα package manager και project .
:::

## Δημιουργήστε ένα νέο project

Το ES4X χρησιμοποιεί το ``npm`` ως εργαλείο διαχείρισης project, για να δημιουργήσει ένα νέο project παρέχεται μια εντολή χρησιμότητας:

```bash
# δημιουργία project directory
mkdir myapp

# κινηθείτε στο project directory
cd myapp

# δημιουργήστε το project
es4x project
```

Ένα project είναι ένα αρχείο `package.json` με μερικά στοιχεία που έχουν ήδη διαμορφωθεί:

```json{7-9,11-17}
{
  "version" : "1.0.0",
  "description" : "This is a ES4X empty project.",
  "name" : "myapp",
  "main" : "index.js",
  "scripts" : {
    "test" : "es4x test index.test.js",
    "postinstall" : "es4x install",
    "start" : "es4x"
  },
  "dependencies": {
    "@es4x/create": "latest",
    "@vertx/unit": "latest"
  },
  "dependencies": {
    "@vertx/core": "latest"
  },
  "keywords" : [ ],
  "author" : "",
  "license" : "ISC"
}
```

::: υπόδειξη
Για έργα ``TypeScript``, εκτελέστε το εργαλείο δημιουργίας έργου με: ``es4x project --ts``
:::

Το `post-install` hook θα αναθέσει στο es4x για να επιλύσει όλες τις εξαρτήσεις  ``maven`` και να δημιουργήσει το script ``es4x-launcher``.

::: υπόδειξη
Το script ``es4x-launcher`` θα διασφαλίσει ότι η εφαρμογή εκτελείται χρησιμοποιώντας es4x και όχι ``nodejs``. Αυτό το script μπορεί να χρησιμοποιηθεί στην παραγωγή, όπου μπορείτε να αποφύγετε το πακέτο ``@es4x/create``.
:::

### create-vertx-app

Με το ``create-vertx-app`` μπορείτε να κάνετε γρήγορη εκκίνηση της εφαρμογής ES4X TypeScript ή JavaScript πολύ εύκολα. Εάν το GUI είναι ο προτιμώμενος τρόπος δημιουργίας εφαρμογών, τότε ο ίδιος generator μπορεί να χρησιμοποιηθεί ως
 [PWA](https://vertx-starter.jetdrone.xyz/#npm).

<asciinema :src="$withBase('/cast/es4x-ts.cast')" cols="80" rows="24" />

## Προσθήκη εξαρτήσεων

Η προσθήκη εξαρτήσεων δεν διαφέρει από αυτό που χρησιμοποιούν οι προγραμματιστές ``JavaScript``:

```bash
# προσθήκη άλλων εξαρτήσεων ...
npm install @vertx/unit --save-dev # Ή yarn add -D @vertx/unit
npm install @vertx/web --save-prod # Ή yarn add @vertx/web

# θα ενεργοποιήσει τη λήψη των εξαρτήσεων npm + java
npm install
```

## Γράφοντας Κώδικα

Με την ολοκλήρωση της εγκατάστασης του project, είναι καιρός να γράψετε κάποιο κωδικό. Όπως αναφέρθηκε προηγουμένως, το ES4X χρησιμοποιεί τους ορισμούς ``TypeScript`` για να προσφέρει μια καλύτερη εμπειρία προγραμματιστή με ολοκλήρωση κώδικα και προαιρετικούς ελέγχους τύπου.

Με όλες τις εφαρμογές ES4X υπάρχει ένα παγκόσμιο αντικείμενο ``vertx`` που είναι η διαμορφωμένη παρουσία του *vert.x* που μπορεί να χρησιμοποιηθεί στην εφαρμογή.

::: υπόδειξη
Για να ολοκληρώσετε τον κώδικα στο [Visual Studio Code](https://code.visualstudio.com/) η πρώτη γραμμή του κύριου σεναρίου σας πρέπει να είναι:

```js
/// <reference types="es4x" />
```
:::

Η hello εφαρμογή ``index.js`` θα πρέπει να είναι:

```js{1-2}
/// <reference types="es4x" />
// @ts-check
import { Router } from '@vertx/web';

const app = Router.router(vertx);

app.route('/').handler(ctx => {
  ctx.response()
    .end('Hello from Vert.x Web!');
});

vertx.createHttpServer()
  .requestHandler(app)
  .listen(8080);

console.log('Server listening at: http://localhost:8080/')
```

Αυτή η εφαρμογή ξεκινά έναν διακομιστή και ακούει στη θύρα 8080 για συνδέσεις. Η εφαρμογή ανταποκρίνεται με το "``Hello from Vert.x Web!``" Για αιτήματα προς το root URL (`/`). Για κάθε άλλη διαδρομή, θα ανταποκριθεί με **404 Not Found**.

::: προειδοποίηση
Η σύνταξη μονάδας ES6 μπορεί να χρησιμοποιηθεί σε αρχεία `.js`. Το ES4X θα μεταφράσει αυτά σε δηλώσεις ``commonjs require()`` δηλώσεις ωστόσο οι ``εξαγωγές`` δεν θα μεταφραστούν. Αυτή η λειτουργία είναι μόνο για να βοηθήσει στην εργασία με IDE που μπορούν να πραγματοποιήσουν αυτόματη εισαγωγή, όπως ``Visual Studio Code``.
:::

## Υποστήριξη MJS

Το ES4X υποστηρίζει επίσης αρχεία ``.mjs``. Σε αυτήν την περίπτωση, η ανάλυση της λειτουργικής μονάδας δεν θα χρησιμοποιεί το ``commonjs``  ``require()`` αλλά θα κάνει χρήση του graaljs native module loader.

Με την υποστήριξη graaljs ``.mjs`` τόσο η ``εισαγωγή`` όσο και η ``εξαγωγή`` θα λειτουργούν σύμφωνα με το σχεδιασμό των προδιαγραφών ES6.

::: υπόδειξη
Για να ενεργοποιήσετε την υποστήριξη ``.mjs`` χρησιμοποιήστε την επέκταση ``.mjs`` στα  ``JavaScript`` αρχεία σας ή ξεκινήστε την εφαρμογή σας με τη σημαία ``-Desm``.
:::

::: προειδοποίηση
Δεν είναι δυνατή η ανάμειξη ``commonjs`` και ``esm`` στο ίδιο έργο. Εάν δεν είστε σίγουροι, χρησιμοποιήστε το ``commonjs``.
:::
