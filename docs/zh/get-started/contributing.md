---
title: 贡献
lang: 'zh'
---

有兴趣为ES4X项目贡献代码？想要反馈bug？在你行动之前请阅读如下说明。

## 提交内容

### 遇到问题或是遇到了困难？

对于能够快速解决的问题，您无需开启issue，您可以通过[gitter.im][1]联系我们

[1]: https://gitter.im/es4x/Lobby

### 发现了bug?

如果您在源代码中发现了bug，您可以在github仓库的[issue tracker][2]提交issue。如果您能提交Pull Request以修改其bug，那再好不过啦。但是在提交issue之前请阅读[提交指南][3]

[2]: https://github.com/reactiverse/es4x/issues
[3]: #提交指南

### 缺少特性?

您可以向Github仓库提交issue，以此方式提出新特性。如果您想实现一个新的特性，请首先提交issue以说明您的提议，目的在于确实此特性是否有益于所有用户，因为素材主题一般具有很大的主观性。请斟酌是什么种类的修改：

* 对于一个**重要特性**，首先开启一个issus，并概括你的提议以供讨论。这样也有利于我们之间更好的合作，从而避免重复的无用功，并且能够帮助你精心修改这些新特性 从而被成功的纳入工程。
* **小特性和bugs**可以用“提交pull request”的方式解决。然而不保证您的特性会被合并进入master分支，因为对于项目主题的所有功能是否有益，始终存在对该问题的看法和意见。

## 提交指南

### 提交一个issue

在您提交issue之前，请在issue追踪器当中检索一下，也许已经存在一个和您同样问题的issue，并且这个issue里可能已经指示了您问题的解决方案。

我们尽所能想要快速解决所有的issue，但是在修复一个bug之前，我们需要重现并确认。为了重现bug，我们会有条理地让您用自定义issue模板提供一个最简的重现方案。请坚持使用issue模版。

很遗憾的是 如果没有最简重现方案，那么我们无法调查和修改bug，所以如果我们没有收到您的进一步反馈，我们会关闭这个issue。

### 提交一个Pull Request (PR)

请在github上检索处于open/close状态的PR，以确定是否与您的提交相关。您不会愿意做重复的无用功。如果您没有发现到相关的PR，那么请提交PR。

1. **开发**: Fork 工程, 将您的更改提交到另一个独立的分支并添加必要的说明信息到提交注释。
2. **构建**:在提交PR之前，**构建**工程。这是为合并您的PR所作出的强制要求，因为我们的宗旨是要保证Github上的工程在任何时候都可以构建安装。
3. **Pull Request**:  在建立主题之后，提交编译输出，将您的分支push到github，然后向 `es4x:develop`提交Pull Request，如果我们建议修改，则作出必要的更新，rebase您的分支然后将这些更改push到GitHub仓库，这会自动更新您的PR。

在您的PR被合并之后，您可以安全的删除您的分支并从主仓库当中拉取这些修改。

## 创世

想要创造世界，您需要在本地主机环境安装几个工具：

* [GraalVM](https://www.graalvm.org/downloads/)
* [Apache Maven](https://maven.apache.org/)
* [Node.js](https://nodejs.org/en/download/)
* [NPM](https://www.npmjs.com/)

如果您已经安装了`GraalVM`和`Maven`，则可以不用安装`Node.js`和`NPM`(虽然在`GraalVM`上包含的`node`程序的某些`npm`包存在一些性能问题，例如`TypeScript Compiler`)。

### 模块

本项目是由一些主要的模块和组件所组成:

1. [es4x](https://github.com/reactiverse/es4x/tree/develop/es4x) :主要的java代码，用于启动GraalJS 和 Vert.x
2. [pm](https://github.com/reactiverse/es4x/tree/develop/pm) :包管理工具
3. [codegen](https://github.com/reactiverse/es4x/tree/develop/codegen) :codegen库用于生成和`vert.x`模块配对的`npm`包。
4. [generator](https://github.com/reactiverse/es4x/tree/develop/generator) : maven脚本，为`vert.x`模块生成`npm`完整包 。
5. [docs](https://github.com/reactiverse/es4x/tree/develop/docs) : 您正在看的目录

### 构建java部分

构建java部分很简单，具体如下：

```bash
mvn -Pcodegen install
```

如果你也想生成npm模块，则使用`codegen`，否则只有如下几个模块会被构建：

* es4x
* pm
* codegen

### 部署npm模块

在开发期间，您可能希望部署到本地NPM注册中心，你可以用 其中一个注册中心：
 [verdaccio](https://verdaccio.org/)。

```bash
npm install -g verdaccio
```

一旦安装成功 则根据如下指示进行登录:

```bash
npm adduser --registry "http://localhost:4873"
```

!!! 警告 "包上传限制"

> Currently the `pm` package is quite large and will not be handled by default by `verdaccio` in
  order to get the upload to work you will need to update the default config and restart.

编辑文件 `~/.config/verdaccio/config.yaml` 并添加如下代码：

```yaml
# max package size
max_body_size: 100mb
```

一旦你在本地配置了注册中心，你就可以在本地部署`npm`包：

```bash
cd generator
mvn -Dnpm-registry="http://localhost:4873" \
    clean \
    generate-sources \
    exec:exec@npm-publish
```

!!! 警告 "API 文档"

> If you would like to have API docs for the generated packages then you will need a few extra
  tools and an extra maven.

```bash
# install the API doc generator
npm install -g typedoc
# deploy to verdaccio and generate docs to the docs folder
cd generator
mvn -Dnpm-registry="http://localhost:4873" \
    clean \
    generate-sources \
    exec:exec@npm-publish \
    exec:exec@typedoc
```

### 将PM部署与npm

为了方便，`pm`工程也可以部署到NPM注册中心，按照如下指示即可达成：

```bash
cd pm
mvn package
./publish.sh local
```

这样会生成maven fat jar，并且最后的脚本会将它转换进npm包 而后部署到你的本地`verdaccio`
