There are situations where having a REPL or shell can be useful. ES4X provides such a feature out of the box by running
the command:

```bash
> npm run shell

js:>
```

And you are now able to REPL. For example:

```js

js:> require('./index.js');
Server listening at: http://localhost:8080/

js:>
```

The shell can also be run without the need of `npm` using a [packaged](./package.md) runnable jar.

```bash
java -jar your-package.jar run "js:>"
```

A bootstrapped shell will be available to run your code with all the components available in your classpath.
