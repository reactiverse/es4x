# Install

Assuming you’ve already installed [Node.js](https://nodejs.org/), you will need a working JVM. The requirements are
either ([Java](https://adoptium.net/) or [GraalVM](http://www.graalvm.org/)).

```bash
$ java -version
openjdk version "11.0.15" 2022-04-19
OpenJDK Runtime Environment (build 11.0.15+10-Ubuntu-0ubuntu0.22.04.1)
OpenJDK 64-Bit Server VM (build 11.0.15+10-Ubuntu-0ubuntu0.22.04.1, mixed mode)
```

If you see a similar output, this means your system currently has `java` **8**, which is not the best option as it will
not benefit from the high performance engine `es4x` uses.

## GraalVM/OpenJDK

In order to have a compatible runtime it is recommended to install a higher runtime (for example using
[jabba](https://github.com/shyiko/jabba)). For instructions on installing `jabba`, please refer to the official
[manual](https://github.com/shyiko/jabba#installation).

::: tip
Using `jabba` you can install `openjdk 11` and/or `graalvm` (once) as:

```bash
jabba install openjdk@1.11.0
jabba install graalvm@21.3.0
```

And later switch to the desired runtime by executing:

```bash
jabba use openjdk@1.11 # OR jabba use graalvm@21.2
```
:::

Once a valid JVM is installed you can optionally install the project management utilities development tool.

## Project Tools

```bash
npm install -g @es4x/create # OR yarn global add @es4x/create
```

The package will install a `es4x` command globally that can be used to create projects and perform other tasks. To know
more about the tool:

```bash
es4x --help
```

### Using NPX

The same package can be used as a one-shot operation with `npx`. In this case refer to it as:

```bash
npx @es4x/create --help
```

## OS package

When working on CI environments where the amount of packages is limited, the package manager can be installed by
unzipping the prepackaged tar/zip file.

```bash
ES4X='0.16.0' \
  curl -sL \
  https://github.com/reactiverse/es4x/releases/download/$ES4X/es4x-pm-$ES4X-bin.tar.gz \
  | tar zx --strip-components=1 -C /usr/local
```

For Windows Operating Systems the same can be done using a `zip` file instead.

::: tip
Using `npm` should be the preferred way to install as it allows easy upgrades and should be portable across different
*Operating Systems*.
:::


## Verify

You should now have a `es4x` command available in your path, you can test it by running:

```
$ es4x --help

Usage: java -jar /usr/local/bin/es4x-bin.jar [COMMAND] [OPTIONS]
            [arg...]

Commands:
    bare         Creates a bare instance of vert.x.
    dockerfile   Creates a generic Dockerfile for building and deploying the
                 current project.
    project      Initializes the 'package.json' to work with ES4X.
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
For best experience and performance please install [GraalVM](https://www.graalvm.org). When working on standard JDK,
using Java < 11 will run on `Interpreted` mode which is not performance or recommended for production.
:::
