# CommonJS Ενώτητες

Το [CommonJS](http://www.commonjs.org/) είναι φορτωτής ενοτήτων, η `require()` μέθοδος, είναι επίσης διαθέσιμη στο ES4X. Είναι σημαντικό
να σημειώσουμε οτι αυτός ο φορτωτής δεν είναι όπως ο `nodejs` φορτωτής. Είναι fork του [npm-jvm](https://github.com/nodyn/jvm-npm)
το οποίο είναι ειδικό για το ES4X.

## Συγκεκριμένες Διαφορές

Ενότητες μπορούν να φορτωθούν από το σύστημα αρχείων ή από αρχεία `jar`. Η διαδικασία φόρτωσης πάντα χρησιμοποιεί το
[Vert.x FileSystem](https://vertx.io/docs/vertx-core/java/#_using_the_file_system_with_vert_x).

## Συντακτικό εισαγωγής ESM

Eπεξεργαστές κειμένου όπως το [Visual Studio Code](https://code.visualstudio.com/) προτιμούν να ολοκληρώνουν αυτόματα την εισαγωγή χρησιμοποιώντας συντακτικό ESM. Φυσικά, αυτό το συντακτικό δεν είναι συμβατό με το `commonjs`, ωστόσο, ο φορτωτής θα προσπαθήσει να  προσαρμώσει τις εισαγωγές στο `commonjs` αν είναι δυνατόν.

Λάβετε υπ' όψιν το επόμενο παράδειγμα:

```js{1}
import { TestSuite } from '@vertx/unit';

const suite = TestSuite.create("the_test_suite");
// ...
suite.run();
```

Αυτός ο κώδικας δεν είναι συμβατός με το `commonjs`, ωστόσο η `require()` μέθοδος θα αλλάξει τον κώδικα σε:

```js{1}
const TestSuite = require('@vertx/unit').TestSuite;

const suite = TestSuite.create("the_test_suite");
// ...
suite.run();
```

::: warning
Αν και ή εισαγωγή θα προσαρμωστεί, οι εξαγωγές δεν θα αλλάξουν. Όλες οι εξαγωγές πρέπει να είναι σε commonjs συντακτικό:
```js
module.exports = { /* ... */ }
```
:::
