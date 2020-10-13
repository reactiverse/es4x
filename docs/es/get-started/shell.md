# Shell

Hay situaciones en las que tener un REPL o una shell puede ser util. ES4X provee esta caracteristica ejecutando:

```bash
$ npm run "js:>"

js:>
```

Y ahora ya puedes REPL. Por ejemplo:

```
js:> require('./index.js');
Server listening at: http://localhost:8080/

js:>
```

La shell tambien puede ejecutarse sin `npm` usando el ejecutable jar `es4x-launcher`.

```bash
java -jar es4x-launcher.jar run "js:>"
```


Un shell de arranque estara disponible para ejecutar tu codigo con todos los componentes disponibles en su classpath.

::: Advertencia
El REPL no sera capaz de usar referencia de modulo `MJS` ya que se calculan durante la inicializacion
:::
