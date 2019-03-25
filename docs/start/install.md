Assuming you’ve already installed [Node.js](https://nodejs.org/) and ([Java](https://adoptopenjdk.net/) or
[GraalVM](http://www.graalvm.org/) \[Preferred\]), install the project management utilities development tool:

```bash
npm install -g es4x-pm
# If you prefer not using NPM you can download the
# package and add the bin directory to the PATH
ES4X_VERSION=0.7.3 \
  curl -sL https://github.com/reactiverse/es4x/releases/download/${ES4X_VERSION}/es4x-pm-${ES4X_VERSION}-bin.tar.gz \
  | tar zx --strip-components=1 -C /usr/local
```

You should now have a `es4x` command available in your path, you can test it by running:

```bash
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

Create a directory to hold your application, and make that your working directory.

```bash
mkdir myapp
cd myapp
```

Use the `npm init` command to create a `package.json` file for your application. For more information on how
`package.json` works, see [Specifics of npm’s package.json handling](https://docs.npmjs.com/files/package.json).

```bash
npm init
```

This command prompts you for a number of things, such as the name and version of your application. For now, you can
simply hit RETURN to accept the defaults for most of them, with the following exception:

```
entry point: (index.js)
```

Enter `app.js`, or whatever you want the name of the main file to be. If you want it to be `index.js`, hit RETURN to
accept the suggested default file name.

Update the `package.json` to accomodate the ES4X scripts in the `myapp` directory. For example:

```bash
es4x init
```

Now install `@vertx/core` and optionally `@vertx/web` and `@vertx/unit` dependencies by adding them to the
`package.json` or running the command:

```bash
npm install @vertx/unit --save-dev
npm install @vertx/core --save-prod
npm install @vertx/web --save-prod

npm install
```

As this moment there should be a minimal `package.json`.
