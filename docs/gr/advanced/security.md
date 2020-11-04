# Πολιτική ασφαλείας

Το ES4X σας επιτρέπει να εκτελείτε τις εφαρμογές σας σε ένα απόλυτο ασφαλές περιβάλλον δοκιμών. Οπως ακριβώς και το [deno](https://deno.land/), το ES4X μπορεί να απομονώσει την εφαρμογή. Η τεχνολογία πίσω από αυτό είναι το JVM
[security manager](https://docs.oracle.com/javase/tutorial/essential/environment/security.html). Τα security managers ήταν αυτό που απομόνωσε το παλιό `Applet` από την πρόσβαση στον κεντρικό υπολογιστή. Είναι πιο συντονισμένοι από αυτό που παίρνετε σήμερα με το `deno`.

## Δημιουργήστε μια πολιτική ασφαλείας

Για να δημιουργήσετε μια πολιτική ασφαλείας, εκτελέστε το `es4x` εργαλείο:

```bash
$ es4x security-policy

Creating a new 'security.policy' with full network access and
read-only IO access to the working directory.
```

Το δημιουργημένο αρχείο είναι ένα απλό πρότυπο, εάν το ανοίξετε μπορείτε να διαβάσετε:

```text
// Grant the following permissions to code that shall be executed from
// the node_modules/.lib/* directory
grant codeBase "file:\${user.dir}\${/}node_modules\${/}.lib\${/}*" {
  // vert.x will need full access to the temp dir.
  permission java.io.FilePermission "\${java.io.tmpdir}\${/}-", "read,write,delete";

  // the code should be able to read the JVM/GraalVM runtime libs
  permission java.io.FilePermission "\${java.home}", "read";
  permission java.io.FilePermission "\${java.home}\${/}..\${/}release", "read";
  permission java.io.FilePermission "\${java.home}\${/}-", "read";

  // applications are allowed to read all files from the CWD
  permission java.io.FilePermission "\${user.dir}\${/}-", "read";
  // uncomment the following to allow full read access
  //permission java.io.FilePermission "<<ALL FILES>>", "read";

  // Netty performs some reflection we need to allow it
  permission java.lang.reflect.ReflectPermission "suppressAccessChecks";

  // By default we allow all runtime permissions
  // users may want to restrict this further say for example to
  // deny access to environment variables, etc...
  permission java.lang.RuntimePermission "*";

  // ES4X setup a nice looking logger
  permission java.util.logging.LoggingPermission "control";

  // Allow full access to JVM system properties
  permission java.util.PropertyPermission "*", "read,write";

  // currently we allow all access to the network
  permission java.net.SocketPermission "*", "accept,connect,listen,resolve";
};
```

Έτσι, αυτό το πρότυπο παρέχει πλήρη πρόσβαση στο δίκτυο και πρόσβαση μόνο για ανάγνωση σε όλα τα αρχεία από το `CWD` όπου η εφαρμογή
ξεκίνησε.

::: tip
Μόλις μάθετε όλα τα απαιτούμενα δικαιώματα για την εφαρμογή σας, μπορείτε να αρχίσετε να περιορίζετε ακόμη περισσότερο!
:::

::: warning
Παρόλο που αυτό το πρότυπο μοιάζει με καλή αρχή, θυμηθείτε ότι το `CWD` είναι read only, για παράδειγμα, εάν εκτελείτε έναν διακομιστή http που κάνει μεταφορτώσεις αρχείων, εκτός αν οι μεταφορτώσεις προσγειώνονται στον `$TEMP` κατάλογο, θα αποτύχουν καθώς δεν υπάρχει τρόπος εγγραφής από την εφαρμογή.
:::
