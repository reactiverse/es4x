# ES4X Cli

This is a helper dev package to work with Eclipse Vert.x JS/TS projects.

Included scripts:

* init
* postinstall
* launcher
* shell

## Init

Init the ES4X scripts section in the current `package.json` usually used with `npx`:

```bash
npm es4x-cli init
```

## Postinstall

Postinstall will bootstrap a `pom.xml` file from the `package.json`. The maven
pom file can be customized by supplying a handlebars template named
`.pom.xml`.

By default both `groupId` and `artifactId` will map to the package json
`name` property, the version will map to the `version` property and the
main verticle will map to the `main` property.

All entries in `files` will be added to the final `fatJar` with the caveat
that directories **must** be sufixed with `/` so maven can understand
how to handle it.

In order to use this script in your project you should have *init'ed* your
`package.json`.

You can add normal dependencies as normal too.

## Launcher

This script will delegate to vert.x launcher, it will invoke the command
you want to run and pass all arguments to it.

There are a couple of helper.

* `-d` will start the JVM in debug mode listening on port 9797
* `-t` will use the `test` classpath instead of the `runtime` classpath

When no arguments are passed the main verticle is derived from the
`package.json` `main` property. When the test classpath is enabled then
this value is suffixed by `.test.js`.


An example running tests would be after adding `vertx-unit` to the
`devDependencies`:

```json
{
  "scripts": {
    "test": "es4x launcher test -v"
  },
  
  "devDependencies": {
    "es4x-cli": "*",
    "@vertx/unit": "3.5.3"
  },
  
  "dependencies": {
    "@vertx/core": "3.5.3"
  }
}
```

## Shell

You can run your code from a shell by executing the command:

```sh
es4x shell
```

or if you package your application into a java `jar` file:

```bash
java -jar your-jar.jar io.reactiverse.es4x.Shell
```
