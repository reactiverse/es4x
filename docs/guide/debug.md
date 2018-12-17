## Chrome Inspector

When working on [GraalVM](https://graalvm.org) or a JDK with the graalvm (JVMCI) bits, start your application as:

```sh
npm start -- -i
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

![chrome-inspector](res/debug.png)

You will be able to set breakpoints, debug etc...

## Debug from VSCode

The usage of Chrome devtools is not a hard requirement. You can also debug the application using
[Visual Studio Code](https://code.visualstudio.com). Create a runner configuration as:


```json
{
  "type": "node",
  "request": "attach",
  "name": "es4x app",
  "port": "9229",
  "localRoot": "/",
  "remoteRoot": "/"
}
```

And attach your debugger.

![vscode-chrome-inspector](res/vscode-debug.png)

## Debug Nashorn code

Nashorn is now a deprecated technology and a fallback when there's no `graaljs` available. It is still possible to debug
in this scenario, but it will require the usage of java tools. Currently this has only been tested on `IntelliJ IDEA`.

To debug your application start it as:

```sh
npm start -- -d
```

And use the standard java debugger to attach and debug the code.
