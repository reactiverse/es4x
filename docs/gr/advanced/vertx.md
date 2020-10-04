# Eclipse Vert.x

Όπως θα έπρεπε να είναι σαφές σε αυτό το σημείο, το Vert.x είναι το IO και το προεπιλεγμένο μοντέλο προγραμματισμού που χρησιμοποιείται από το ES4X. 56/5000
Υπάρχουν ωστόσο μερικές ωραίες βελτιώσεις στα πρότυπα [Vert.x APIs](https://vertx.io).

## Δημιουργημένα API

Όλα τα API που δημοσιεύτηκαν στο `npm` κάτω από τους χώρους ονομάτων `@vertx` και `@reactiverse` έχουν δημιουργηθεί ap;o κώδικa. Δημιουργία κώδικα είναι ένας βοηθός που επιτρέπει τη χρήση αυτών των API από χρήστες `JavaScript` σε μορφή που είναι οικεία χωρίς να διακυβεύεται η
απόδοση της εφαρμογής.

Η αλληλεπίδραση με το JVM συμβαίνει πάνω από το αντικείμενο `Java`. Το πιο σημαντικό κομμάτι είναι να χρησιμοποιήστε μια class JVM για JS:

```js
// Εισαγάγετε την class java.lang.Math για να μπορείτε να χρησιμοποιήσετε
// ως τύπος JS
const Math = Java.type('javalang.Math');
```

Τώρα μπορεί κανείς να το κάνει αυτό για όλα τα API, αλλά υπάρχουν αρκετοί περιορισμοί που προσπαθεί να αντιμετωπίσει το ES4X:

* **Επιρρεπής σε λάθη** - Κάποιος πρέπει να γνωρίζει τα ακριβή API και τους τύπους Java για να τα χρησιμοποιήσει από το JavaScript.
* **Δεν υπάρχει τρόπος καθορισμού εξαρτήσεων** - Εάν πρέπει να χρησιμοποιήσετε API από διαφορετικές ενότητες, η εισαγωγή class ανά class δεν μπορεί να καθορίσει τις εξαρτήσεις μεταξύ τους.
* **Χωρίς υποστήριξη IDE** - Ο προγραμματιστής θα πρέπει να γνωρίζει το API πριν το χρησιμοποιήσει γιατί το IDE δεν θα βοηθήσει.

Ο δημιουργός ES4X το επιλύει δημιουργώντας μια ενότητα `npm` για κάθε ενότητα `vertx` και ορισμούς τύπων για κάθε τάξη.

Κάθε ενότητα θα έχει τα ακόλουθα αρχεία:

* `package.json` - Ορίζει τις εξαρτήσεις μεταξύ των ενοτήτων
* `index.js` - commonjs API interfaces
* `index.mjs` - ESM API interfaces
* `index.d.ts` - Πλήρης ορισμοί τύπων για τα API interfaces
* `enum.js` - commonjs API enumerations
* `enum.mjs` - ESM API enumerations
* `enum.d.ts` - Πλήρης ορισμοί τύπων για το API enumerations
* `options.js` - commonjs API αντικείμενα δεδομένων.
* `options.mjs` - ESM API αντικείμενα δεδομένων.
* `options.d.ts` - Πλήρης ορισμοί τύπων για τα API αντικείμενα δεδομένων.

Όλα τα αρχεία `index` θα απλοποιήσουν την εισαγωγή JVM classes αντικαθιστώντας, για παράδειγμα:

```js
// χωρίς ES4X
const Router = Java.type('io.vertx.ext.web.Router');
// me
import { Router } from '@vertx/web';
```
Αυτή η μικρή αλλαγή θα κάνει τους IDE να βοηθήσουν στην ανάπτυξη και στους διαχειριστές πακέτων να κατεβάσουν εξαρτήσεις ανάλογα με τις ανάγκες τους.
Τέλος, όλα τα αρχεία `.d.ts` θα υποδείξουν το IDE σχετικά με τους τύπους και θα υποστηρίξουν την ολοκλήρωση κώδικα.


## Promise/Future

Vert.x έχει 2 τύπους:

* `io.vertx.core.Future`
* `io.vertx.core.Promise`

Παραδόξως, ένα Vert.x `Promise` δεν είναι το ίδιο με ένα JavaScript `Future`. Ένα Vert.x `Promise` είναι η εγγράψιμη πλευρά του
Vert.x `Future`. Σε όρους JavaScript:

* Vert.x `Future` === JavaScript `Σαν υπόσχεση (Thenable)`
* Vert.x `Promise` === JavaScript `Λειτουργία Executor`

## async/await

`async/await` υποστηρίζεται χωρίς καμία ανάγκη για compilation step από το `GraalVM`. ES4X προσθέτει ένα επιπλέον χαρακτηριστικό στον Vert.x
`Future` τύπο. API που επιστρέφουν Vert.x `Future` μπορούν να χρησιμοποιηθούν ως `Thenable`, αυτό σημαίνει ότι:

```js
// χρησιμοποιώντας το Java API
vertx.createHttpServer()
  .listen(0)
  .onSuccess(server => {
    console.log('Server ready!')
  })
  .onFailure(err => {
    console.log('Server startup failed!')
  });
```

Μπορεί να χρησιμοποιηθεί ως `Thenable`:

```js
try {
  let server = await vertx
    .createHttpServer()
    .listen(0);

  console.log('Server Ready!');
} catch (err) {
  console.log('Server startup failed!')
}
```

:::υπόδειξη
`async/await` λειτουργεί ακόμη και με loops, γεγονός που καθιστά την εργασία με ασύγχρονο κώδικα αρκετά εύκολη, ακόμη και ανάμειξη κώδικα JS και Java.
:::

## Μετατροπές Τύπων

Vert.x κωδικοποιείται σε `Java`, ωστόσο σε `JavaScript` δεν χρειάζεται να ανησυχούμε για τους τύπους όσο στην `Java`. Το ES4X
εκτελεί ορισμένες αυτόματες μετατροπές εκτός πλαισίου:

| Java | TypeScript |
| :--- | ---------: |
| void | void |
| boolean | boolean |
| byte | number |
| short | number |
| int | number |
| long | number |
| float | number |
| double | number |
| char | string |
| boolean[] | boolean[] |
| byte[] | number[] |
| short[] | number[] |
| int[] | number[] |
| long[] | number[] |
| float[] | number[] |
| double[] | number[] |
| char[] | string[] |
| java.lang.Void | void |
| java.lang.Object | any |
| java.lang.Boolean | boolean |
| java.lang.Double | number |
| java.lang.Float | number |
| java.lang.Integer | number |
| java.lang.Long | number |
| java.lang.Short | number |
| java.lang.Char | string |
| java.lang.String | string |
| java.lang.CharSequence | string |
| java.lang.Boolean[] | boolean[] |
| java.lang.Double[] | number[] |
| java.lang.Float[] | number[] |
| java.lang.Integer[] | number[] |
| java.lang.Long[] | number[] |
| java.lang.Short[] | number[] |
| java.lang.Char[] | string[] |
| java.lang.String[] | string[] |
| java.lang.CharSequence[] | string[] |
| java.lang.Object[] | any[] |
| java.lang.Iterable | any[] |
| java.util.function.BiConsumer | &lt;T extends any, U extends any&gt;(arg0: T, arg1: U) =&gt; void |
| java.util.function.BiFunction | &lt;T extends any, U extends any, R extends any&gt;(arg0: T, arg1: U) =&gt; R |
| java.util.function.BinaryOperator | &lt;T extends any&gt;(arg0: T, arg1: T) =&gt; T |
| java.util.function.BiPredicate | &lt;T extends any, U extends any&gt;(arg0: T, arg1: U) =&gt; boolean |
| java.util.function.Consumer | &lt;T extends any&gt;(arg0: T) =&gt; void |
| java.util.function.Function | &lt;T extends any, R extends any&gt;(arg0: T) =&gt; R |
| java.util.function.Predicate | &lt;T extends any&gt;(arg0: T) =&gt; boolean |
| java.util.function.Supplier | &lt;T extends any&gt;() =&gt; T |
| java.util.function.UnaryOperator | &lt;T extends any&gt;(arg0: T) =&gt; T |
| java.time.Instant | Date |
| java.time.LocalDate | Date |
| java.time.LocalDateTime | Date |
| java.time.ZonedDateTime | Date |
| java.lang.Iterable&lt;T&gt; | &lt;T&gt;[] |
| java.util.Collection&lt;T&gt; | &lt;T&gt;[] |
| java.util.List&lt;T&gt; | &lt;T&gt;[] |
| java.util.Map&lt;K, V&gt; | { [key: &lt;K&gt;]: &lt;V&gt; } |
