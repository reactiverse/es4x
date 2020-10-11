# Debug

## Inspector de Chrome

Cuandro trabajes con [GraalVM](https://graalvm.org) o un JDK con los bits graalvm (JVMCI), comienza tu aplicacion asi:

```sh
npm start -- -Dinspect
```

Esto comenzara el agente debugger del inspector de Chrome en el puerto 9229 que puedes conectar a una sesion remota
de debug desde tu navegador.

```
Chrome devtools listening at port: 9229
Running: java ... 
Debugger listening on port 9229.
To start debugging, open the following URL in Chrome:
    chrome-devtools://devtools/bundled/js_app.html?ws=127.0.0.1:9229/436e852b-329b5c44c3e
Server listening at: http://localhost:8080/
```

![chrome-inspector](./res/debug.png)

Podras elegir breakpoints, debug, etc...

## Debug desde VSCode

El uso de devtools en Chrome no es obligatorio. Tambien puedes hacer un debug de tu aplicacion utilizando
[Visual Studio Code](https://code.visualstudio.com). Crea una configuracion runner asi:


```
es4x vscode
```

Esto creara un `launcher.json` similar a esto:

```json
{
  "version" : "0.2.0",
  "configurations" : [ {
    "name" : "Launch empty-project",
    "type" : "node",
    "request" : "launch",
    "cwd" : "${workspaceFolder}",
    "runtimeExecutable" : "${workspaceFolder}/node_modules/.bin/es4x-launcher",
    "runtimeArgs" : [ "-Dinspect=5858" ],
    "port" : 5858,
    "outputCapture" : "std",
    "serverReadyAction" : {
      "pattern" : "started on port ([0-9]+)",
      "uriFormat" : "http://localhost:%s",
      "action" : "openExternally"
    }
  } ]
}
```

Y conecta tu debugger.

![vscode-chrome-inspector](./res/vscode-debug.png)

Si imprimes el mensaje `Server started on port 8000` sera capturado en visual studio y una ventana del navegador 
abrira la URL adecuada.
