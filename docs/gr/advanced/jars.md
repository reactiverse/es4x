# JARs

Το ES4Xτρέχει σε ένα JVM, και αρα προσθέτοντας ή χρησιμοποιώντας `jar`s από το Maven Central είναι απλό. Αυτή η δυνατότητα είναι χρήσιμη, για παράδειγμα
όταν πρέπει να προσθέσουμε βιβλιοθήκες χρόνου εκτέλεσης που δεν έχουν `npm` αντίστοιχο, ή είναι απλώς βιβλιοθήκες υποστήριξης. Για παράδειγμα,
μπορούμε σε ορισμένες περιπτώσεις να βελτιώσουμε την απόδοση IO του `vert.x` προσθέτοντας
[native-transports](https://netty.io/wiki/native-transports.html) στον χρόνος εκτέλεσης.

```json
{
  "name": "benchmark",
  "version": "0.12.0",
  "private": true,
  "main": "index.js",
  "dependencies": {
    "@vertx/core": "3.9.2"
  },
  "mvnDependencies": [
    "io.netty:netty-transport-native-epoll:jar:linux-x86_64:4.1.49.Final"
  ]
}
```

Προσθέτοντας `mvnDependencies` arrayστο package json, αυτές οι εξαρτήσεις προστίθενται στον χρόνο εκτέλεσης της εφαρμογής. Η μορφή είναι η συνήθης για το maven:

```
group:artifact[:type][:classifier]:version
```

* **group** τον οργανισμό που διαθέτει την ενότητα
* **artifact** η ενότητα αυτή καθεαυτή
* **type** προαιρετικός τύπος αρχείου
* **classifier** προαιρετικός ταξινομητής, αυτό επιτρέπει την ύπαρξη ξεχωριστών ενοτήτων για συγκεκριμένους ρόλους
* **version** η έκδοση της ενότητας




