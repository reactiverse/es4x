# Debug

## Chrome Inspector

When working on [GraalVM](https://graalvm.org) or a JDK with the graalvm (JVMCI) bits, start your application as:

```sh
npm start -- -Dinspect
```

This will start a Chrome inspector debugger agent on port 9229 that you can attach for a remote
debug session from your Browser.

```
Chrome devtools listening at port: 9229
Running: java ... 
Debugger listening on port 9229.
To start debugging, open the following URL in Chrome:
    chrome-devtools://devtools/bundled/js_app.html?ws=127.0.0.1:9229/436e852b-329b5c44c3e
Server listening at: http://localhost:8080/
```

![chrome-inspector](./res/debug.png)

You will be able to set breakpoints, debug etc...

## Debug from VSCode

The usage of Chrome devtools is not a hard requirement. You can also debug the application using
[Visual Studio Code](https://code.visualstudio.com). Create a runner configuration as:


```
es4x vscode
```

This will create a `launcher.json` similar to this:

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

And attach your debugger.

![vscode-chrome-inspector](./res/vscode-debug.png)

If you print the message `Server started on port 8000` it will be captured by visual studio and a browser window will
open the url in question.
