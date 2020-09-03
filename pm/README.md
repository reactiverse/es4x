# ES4X Project Utilities

Small set of utilities to get started with [es4x](https://reactiverse.io/es4x).

## Bootstrap a project

```bash
npm init @es4x project
```

This will create a basic set of files:

* `package.json`
* `index.js`
* `index.test.js`

## Other commands

This project is more than just a *project template generator*. You can also perform
more taks, just consult the tool help:

```bash
es4x --help

Usage: es4x [COMMAND] [OPTIONS] [arg...]

Commands:
project         Initializes the 'package.json' to work with ES4X.
install         Installs required jars from maven to 'node_modules'.
security-policy Initializes a secure by default VM 'security.policy' to work with ES4X.
versions        Displays the versions.

Current VM:
Name:   Java HotSpot(TM) 64-Bit Server VM - 11.0.7
Vendor: GraalVM CE 20.1.0

Run 'es4x COMMAND --help' for more information on a command.
```

## Sub commands

### Project

Same as running `npm init @es4x project`

```bash
es4x project
```

### Install

Install `jvm` dependencies extracted from the npm dependencies. This command will process the
`package.json` and download the required `jvm` artifacts from Maven Central.

```bash
es4x install
```

### Security Policy

Installs a security policy for the project. This can be used to constraint the runtime to be
sandboxed in all the possibilities the `jvm` allows.

```bash
es4x security-policy
```

### Versions

Prints all the versions (runtime + dependencies).

```bash
es4x versions
```
