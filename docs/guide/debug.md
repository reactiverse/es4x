When working on the standard JDK you can start your application as:

```sh
npm start -- -d
```

This will start a JVM debugger agent on port 9229 that you can attach for a remote
debug session from your IDE (currently only tested with IntelliJ IDEA).

The experimental GraalVM support allows debugging over the JVM agent and also supoprt
the Chrome devtools protocol to debug the scripts. In order to activate this mode:

```sh
npm start -- -i
```

And follow the instructions.
