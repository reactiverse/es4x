# Shell

Zdarzają się sytuacje gdzie posiadanie REPLa lub shella może być przydatne. ES4X zapewnia taką funkcjonalność poprzez
wywołanie komendy:

```bash
$ npm run "js:>"

js:>
```

I teraz możesz korzystać z REPL. Na przykład:

```
js:> require('./index.js');
Server listening at: http://localhost:8080/

js:>
```

Shell może być również uruchamiany bez `npm` korzystając z wykonywalnego jara `es4x-launcher`.

```bash
java -jar es4x-launcher.jar run "js:>"
```

Uruchomiona powłoka będzie dostępna by wykonać kod ze wszystkimi komponentami dostępnymi w classpath.

::: warning
REPL nie będzie w stanie używać referencji do modułów `MJS` jako że te są wyliczane w trakcie inicjalizacji.
:::
