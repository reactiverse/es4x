# Отладка

## Инструмент исследования Chrome

Работая с [GraalVM](https://graalvm.org) или JDK с graalvm (JVMCI), запустите ваше приложение следующим образом:

```sh
npm start -- -Dinspect
```

Это запустит агента отладки для инструмента исследования Chrome на порту 9229, который может быть использован для сессии
удаленной отладки из вашего браузера.

```
Chrome devtools listening at port: 9229
Running: java ...
Debugger listening on port 9229.
To start debugging, open the following URL in Chrome:
    chrome-devtools://devtools/bundled/js_app.html?ws=127.0.0.1:9229/436e852b-329b5c44c3e
Server listening at: http://localhost:8080/
```

![chrome-inspector](./res/debug.png)

Вы сможете ставить точки останова, вести процесс отладки и т.д.

## Отладка из VSCode

Использование инструментов разработки Chrome не является обязательным. Вы также можете вести отладку с помощью
[Visual Studio Code](https://code.visualstudio.com). Создайте конфигурацию для запуска:


```
es4x vscode
```

Вы получите файл `launcher.json`, похожий на следующий:

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

Используйте его в своем инструменте отладки.

![vscode-chrome-inspector](./res/vscode-debug.png)

Если вы отправите сообщение `Server started on port 8000`, оно будет перехвачено Visual Studio, и в браузере будет
открыто окно.
