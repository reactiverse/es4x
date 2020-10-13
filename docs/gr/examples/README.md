# Παραδείγματα

## Hello World

Όπως κάθε άλλη βιβλιοθήκη, θα ξεκινήσουμε με ένα παράδειγμα hello world. Το πρώτο βήμα είναι να δημιουργήσετε ένα πρότζεκτ:

<<< @/docs/examples/hello-world/package.json

### Εγκαταστήστε τις απαιτούμενες εξαρτήσεις

```bash
$ npm i

> hello-es4x@1.0.0 postinstall .../hello-world
> es4x install

npm notice created a lockfile as package-lock.json. You should commit this file.
added 2 packages from 1 contributor and audited 2 packages in 6.704s
found 0 vulnerabilities
```

::: warning
Εάν δείτε το μήνυμα `Installing GraalJS...`, αυτό σημαίνει ότι το σύστημά σας δεν έχει εγκατάσταση GraalVM `java`.
Αυτό είναι απολύτως εντάξει, καθώς λαμβάνονται επιπλέον πακέτα για να εξασφαλιστεί η καλύτερη απόδοση.
:::

::: danger
Εάν δείτε το μήνυμα `Current JDK only supports GraalJS in Interpreted mode!`, σημαίνει οτι η έκδοση της `java` είναι είτε μικρότερη από 11 ή `OpenJ9`.
:::

### Γράψτε τον κωδικό

Τώρα που το πρότζεκτ είναι έτοιμο για χρήση, μπορούμε να γράψουμε τον κωδικό:

<<< @/docs/examples/hello-world/index.js

### εκτελέστε το

```bash
$ npm start

> hello-es4x@1.0.0 start .../hello-world
> es4x

Hello ES4X
Succeeded in deploying verticle
```

::: danger
Εάν δείτε το μήνυμα `Current JDK only supports GraalJS in Interpreted mode!`, σημαίνει οτι η έκδοση της `java` είναι είτε μικρότερη από 11 ή `OpenJ9`.
:::

## Εφαρμογή ιστού

Σε αυτό το παράδειγμα θα δημιουργήσουμε μια απλή διαδικτυακή εφαρμογή:

<<< @/docs/examples/web-application/package.json

### Εγκαταστήστε τις απαιτούμενες εξαρτήσεις

```bash
$ npm i

> hello-es4x@1.0.0 postinstall .../hello-world
> es4x install

npm notice created a lockfile as package-lock.json. You should commit this file.
added 2 packages from 1 contributor and audited 2 packages in 6.704s
found 0 vulnerabilities
```

::: warning
Εάν δείτε το μήνυμα `Installing GraalJS...`, αυτό σημαίνει ότι το σύστημά σας δεν έχει εγκατάσταση GraalVM `java`.
Αυτό είναι απολύτως εντάξει, καθώς λαμβάνονται επιπλέον πακέτα για να εξασφαλιστεί η καλύτερη απόδοση.
:::

::: danger
Εάν δείτε το μήνυμα `Current JDK only supports GraalJS in Interpreted mode!`, σημαίνει οτι η έκδοση της `java` είναι είτε μικρότερη από 11 ή `OpenJ9`.
:::

### Γράψτε τον κωδικό

Τώρα που το έργο είναι έτοιμο για χρήση, μπορούμε να γράψουμε τον κωδικό:

<<< @/docs/examples/web-application/index.js

## Πρόσβαση σε Postgres

Σε αυτό το παράδειγμα θα δημιουργήσουμε μια απλή εφαρμογή Postgres:

<<< @/docs/examples/postgresql/package.json

### Εγκαταστήστε τις απαιτούμενες εξαρτήσεις

```bash
$ npm i

> hello-es4x@1.0.0 postinstall .../hello-world
> es4x install

npm notice created a lockfile as package-lock.json. You should commit this file.
added 2 packages from 1 contributor and audited 2 packages in 6.704s
found 0 vulnerabilities
```

::: warning
Εάν δείτε το μήνυμα `Installing GraalJS...`, αυτό σημαίνει ότι το σύστημά σας δεν έχει εγκατάσταση GraalVM `java`.
Αυτό είναι απολύτως εντάξει, καθώς λαμβάνονται επιπλέον πακέτα για να εξασφαλιστεί η καλύτερη απόδοση.
:::

::: danger
Εάν δείτε το μήνυμα `Current JDK only supports GraalJS in Interpreted mode!`, σημαίνει οτι η έκδοση της `java` είναι είτε μικρότερη από 11 ή `OpenJ9`.
:::

### Γράψτε τον κωδικό

Τώρα που το έργο είναι έτοιμο για χρήση, μπορούμε να γράψουμε τον κωδικό:

<<< @/docs/examples/postgresql/index.js

## More examples?

Εάν θέλετε να δείτε περισσότερα παραδείγματα, απλώς μεταβείτε στο [vertx-examples](https://github.com/vert-x3/vertx-examples). Eπαρόλο που τα παραδείγματα είναι γραμμένα σε Java, ακολουθώντας τον  οδηγό [advanced](../advanced), θα δείτε πώς η χρήση των Java API μπορεί να είναι ασήμαντη.
