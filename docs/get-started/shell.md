# Shell

There are situations where having a REPL or shell can be useful. ES4X provides such a feature out of the box by running
the command:

```bash
$ npm run "js:>"

js:>
```

And you are now able to REPL. For example:

```
js:> require('./index.js');
Server listening at: http://localhost:8080/

js:>
```

The shell can also be run without the need of `npm` using the `es4x-launcher` runnable jar.

```bash
java -jar es4x-launcher.jar run "js:>"
```

A bootstrapped shell will be available to run your code with all the components available in your classpath.

::: warning
The REPL will not be able to use `MJS` modules references as those are computed at initialization time.
:::
