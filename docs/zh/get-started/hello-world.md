# 你好，世界

在 `hello-es4x.js` 文件中您可以创建最简单的应用：

```js
vertx.createHttpServer()
  .requestHandler(req => {
    req.response()
      .end('Hello ES4X world!');
  })
  .listen(8080);
```

使用命令运行刚才创建的应用：

```bash
$ es4x hello-es4x.js
```

::: tip  
在UNIX系统中，脚本文件可以被作为可执行文件，使用shebang  `#!/usr/bin/env es4x` 可以使这些脚本自动执行  
::: 

新建终端窗口并输入：
```bash
$ curl localhost:9090
Hello ES4X world!
```

::: warning  
使用 `es4x` 命令可以直接运行除去 `vertx` 之外没有其他的依赖的小型脚本文件。对于更复杂的项目应该使用项目或包管理工具  
:::

## 创建一个新项目

ES4X 使用 `npm` 作为项目管理工具，使用以下命令创建一个新项目：
```bash
# 创建项目目录
mkdir myapp

# 进入项目目录
cd myapp

# 创建项目
es4x init
```

一个项目是一个 `package.json` 文件，文件中有一些预先配置好的属性：

```json{7-9,12}
{
  "version" : "1.0.0",
  "description" : "This is a ES4X empty project.",
  "name" : "myapp",
  "main" : "index.js",
  "scripts" : {
    "test" : "es4x-launcher test index.test.js",
    "postinstall" : "es4x install",
    "start" : "es4x-launcher"
  },
  "dependencies": {
    "@vertx/core": "latest"
  },
  "keywords" : [ ],
  "author" : "",
  "license" : "ISC"
}
```

`post-install` 命令会代理es4x来处理 `maven` maven依赖以及创建 `es4x-launcher` 脚本

::: tip  
`es4x-launcher` 脚本会确保应用使用es4x运行时运行以及测试而不是 `nodejs`  
:::

### create-vertx-app 命令

使用 `create-vertx-app` 命令，您可以迅速的创建一个基于TypeScript或JavaScript的ES4X应用。如果您偏向于使用GUI来创建应用，那么这个生成器可以被作为[PWA](https://vertx-starter.jetdrone.xyz/#npm)来使用

<asciinema :src="$withBase('/cast/es4x-ts.cast')" cols="80" rows="24" />

## 添加依赖

给项目添加依赖的方式和 `JavaScript` 开发者所使用的方式是一致的： 
```bash
# 添加其他的依赖...
yarn add -D @vertx/unit # 或者 npm install @vertx/unit --save-dev
yarn add @vertx/web # 或者 npm install @vertx/web --save-prod

# 将会开始下载Java的依赖
npm install
```

## 编写程序

当您的项目都创建好之后就可以开始编写您的程序了。正像之前说的那样，ES4X使用 `TypeScript` 的代码补全以及可选的类型检查等来向开发者提供更好的体验。

在所有的ES4X应用中，全局对象 `vertx` 是 *vert.x* 配置好的实例，它可以被用在应用中。

::: tip  
为了在 [Visual Studio Code](https://code.visualstudio.com/) 中使用代码补全，脚本文件第一行应该这么写：

```js
/// <reference types="@vertx/core/runtime" />
```
:::

Hello world 应用 `index.js` 文件应该是这样的：
```js{3}
/// <reference types="@vertx/core/runtime" />
// @ts-check
import { Router } from '@vertx/web';

const app = Router.router(vertx);

app.route('/').handler(ctx => {
  ctx.response()
    .end('Hello from Vert.x Web!');
});

vertx.createHttpServer()
  .requestHandler(app)
  .listen(8080);
  
console.log('Server listening at: http://localhost:8080/')
```

这个应用启动了一个服务器并监听8080端口等待链接，对于所有请求根URL(`/`)的都返回 "`Hello from Vert.x Web!`"，对于其他URL的请求都返回 **404 Not Found**。  
::: warning  
ES6的module语法可以在 `.js` 文件中使用。ES4X会把这些语法翻译成 `commonjs` `require()` 声明，但是 `exports` 将不会被翻译。这个特性只是为了辅助使用有自动import功能的IDE，比如 `Visual Studio Code`  
:::

## MJS 支持

ES4X也支持 `.mjs` 文件。在这个情况下module的处理方式不会使用 `commonjs` `require()` 而是使用 graaljs 的native module加载器。

使用graaljs的 `.mjs` 文件支持 `import` 和 `export` 以及可以作为符合ES6规范的一种设计。  

::: tip  
为了开启 `.mjs` 支持有两种方式：在 `JavaScript` 文件中使用 `.mjs` 扩展或者启动应用时添加标记 `-esm`  
:::  

::: warning
在同一个项目中不能混用 `commonjs` 和 `esm`，如果您不确定的话使用 `commonjs`  
:::
