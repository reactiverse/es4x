# 安装

假设你已经安装了 [Node.js](https://nodejs.org/) 和 ([Java](https://adoptopenjdk.net/) 或[GraalVM](http://www.graalvm.org/))，安装工程管理开发工具。

## 使用 NPM

```bash
yarn global add es4x-pm # 或 npm install -g es4x-pm
```

::: tip
使用`npm`安装工程是我们比较推崇的方式，因为可以轻松的升级依赖而且在不同的**操作系统**上可移植性较好。 
:::

## 操作系统打包

当在有包数量限制的持续集成环境中工作时，可以解压预先打包好的tar/zip文件来安装包管理器（pm）。

```bash
ES4X='0.8.2' \
  curl -sL \
  https://github.com/reactiverse/es4x/releases/download/$ES4X/es4x-pm-$ES4X-bin.tar.gz \
  | tar zx --strip-components=1 -C /usr/local
```

对于Windows操作系统可以用`zip`文件来取而代之用以做同样的事情。


## 验证安装

此时在你的path里面应该存在一个`es4x`命令，你可以按如下指示进行测试：

```
$ es4x --help

Usage: java -jar /usr/local/bin/es4x-bin.jar [COMMAND] [OPTIONS]
            [arg...]

Commands:
    bare         Creates a bare instance of vert.x.
    dockerfile   Creates a generic Dockerfile for building and deploying the
                 current project.
    init         Initializes the 'package.json' to work with ES4X.
    install      Installs required jars from maven to 'node_modules'.
    list         List vert.x applications
    run          Runs a JS script called <main-verticle> in its own instance of
                 vert.x.
    start        Start a vert.x application in background
    stop         Stop a vert.x application
    version      Displays the version.

Run 'java -jar /usr/local/bin/es4x-bin.jar COMMAND --help' for
more information on a command.
```

::: warning
为了达到最佳的实验效果和性能表现，请安装[GraalVM](https://www.graalvm.org)。在使用标准JDK时，小于Java11的版本将在`解释`模式（`Interpreted` mode）下运行，这种模式不利于性能提升也不适合用于生产环境。
:::