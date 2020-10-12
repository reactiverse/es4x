# Workers

[MDN](https://developer.mozilla.org/en-US/docs/Web/API/Web_Workers_API/Using_web_workers) define "Web Workers" como:

> Web Workers is a simple means for web content to run scripts in background threads.
> *"Web Workers" es una manera simple para ejectuar hilos subyacentes en contenido web*
> The worker thread can perform tasks without interfering with the user interface.
> *El hilo "worker" puede realizar tareas sin interferir con la interfaz de usuario*

ES4X no es un explorador web y no se preocupa de la interfaz de usuario, sin embargo puedes ejecutar trabajos largos
en el lado del servidor. En Vert.x todo es antibloqueo asi que incluso creer "workers" deberia seguir la misma semantica.
Por esta razon no podemos seguir la interfaz del "worker" de manera estricta y reemplazamos el constructor con una funcion factoria.

Imagina que tu necesitar ejecutar en tu codigo una tarea intensa para la CPU, no deberias bloquar eventos de bucle, asi que
el paso logico es utilizar "workers" Vert.x. El API del "worker" puede conectarse con el API de Vert.x con unos pocos detalles.

## Ejemplo de Worker

Imagina el siguiente codigo "worker":

```js
// Consigue una referencia a la clase Thread para provocar un bloqueo
const Thread = Java.type('java.lang.Thread');

// El contexto "worker" es referido con la variable `self` como en los documentos MDN
self.onmessage = function(e) {
  console.log('Message received from main script, will sleep 5 seconds...');
  // Causa un poco de locura en el bucle de eventos
  Thread.sleep(5 * 1000);
  var workerResult = 'Result: ' + (e.data[0] * e.data[1]);
  console.log('Posting message back to main script');
  // Devuelve los datos al vehiculo principal
  postMessage(workerResult);
};
```

## Lo que necesitas saber

Los "worker" se cargan en un contexto separado asi que no puedes compartir funciones desde el verticle principal y
un "worker", y toda la comunicacion funciona con un mensaje pasado (eventbus) utilizando:

* `postMessage()` envia un mensaje al otro lado
* `onmessage` recibe un mensaje desde el otro lado

### Lado Verticle

El lado verticle en el API te permite que recibas errors y "workers" `terminate()`, mientras que los "workers" ellos mismos no pueden.

## Ejemplo Verticle

```js
Worker.create('workers/worker.js', function (create) {
  if (create.succeeded()) {
    var worker = create.result();

    worker.onmessage = function (msg) {
      console.log('onmessage: ' + msg);
    };

    worker.onerror = function (err) {
      console.err(err);
      // Termina el "worker"
      worker.terminate();
    };

    console.log('posting...');
    worker.postMessage({data: [2, 3]});
  }
});
```

Asi que el codigo que no permitiria ejecutar en el bucle de eventos `Thread.sleep(5000)` se ejecuta en un hilo "worker",
dejando el hilo del bucle de eventos libre para todas las otras tareas IO.

## "Workers" poliglotas

Todavia es posible escribir "workers" que no son "workers" de JavaScript. Los "workers" deben seguir una pequeña lista de reglas:

* Los "workers" deben registrar la direccion: `{deploymentId}.out` para recibir mensajes del script principal.
* Los "workers" deberia enviar mensajes a: `{deploymentId}.in` para enviar mensajes al script principal.
* Las cargas de los mensajes se espera que sean `JSON.stringify(message)` para evitar problemas entre lenguajes.
* Se espera que los "workers" sean locales, si quieres conectar con un "worker" en cualquier parte del cluster, necesitas utilizar
el constructor con el argumento extra `true`, e.g.: `new Worker('deploymentId', true)`.
