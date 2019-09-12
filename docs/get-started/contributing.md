---
title: Contributing
lang: 'en'
---

Interested in contributing to the ES4X? Want to report a bug? Before
you do, please read the following guidelines.

## Submission context

### Got a question or problem?

For quick questions there's no need to open an issue as you can reach us on
[gitter.im][1].

  [1]: https://gitter.im/es4x/Lobby

### Found a bug?

If you found a bug in the source code, you can help us by submitting an issue
to the [issue tracker][2] in our GitHub repository. Even better, you can submit
a Pull Request with a fix. However, before doing so, please read the
[submission guidelines][3].

  [2]: https://github.com/reactiverse/es4x/issues
  [3]: #submission-guidelines

### Missing a feature?

You can request a new feature by submitting an issue to our GitHub Repository.
If you would like to implement a new feature, please submit an issue with a
proposal for your work first, to be sure that it is of use for everyone, as
the Material theme is highly opinionated. Please consider what kind of change
it is:

* For a **major feature**, first open an issue and outline your proposal so
  that it can be discussed. This will also allow us to better coordinate our
  efforts, prevent duplication of work, and help you to craft the change so
  that it is successfully accepted into the project.

* **Small features and bugs** can be crafted and directly submitted as a Pull
  Request. However, there is no guarantee that your feature will make it into
  the master, as it's always a matter of opinion whether if benefits the
  overall functionality of the theme.

## Submission guidelines

### Submitting an issue

Before you submit an issue, please search the issue tracker, maybe an issue for
your problem already exists and the discussion might inform you of workarounds
readily available.

We want to fix all the issues as soon as possible, but before fixing a bug we
need to reproduce and confirm it. In order to reproduce bugs we will
systematically ask you to provide a minimal reproduction scenario using the
custom issue template. Please stick to the issue template.

Unfortunately we are not able to investigate / fix bugs without a minimal
reproduction scenario, so if we don't hear back from you we may close the issue.

### Submitting a Pull Request (PR)

Search GitHub for an open or closed PR that relates to your submission. You
don't want to duplicate effort. If you do not find a related issue or PR,
go ahead.

1. **Development**: Fork the project, make your changes in a separate git branch
  and add descriptive messages to your commits.

2. **Build**: Before submitting a pull requests, **build** the project. This is
  a mandatory requirement for your PR to get accepted, as the theme should at
  all times be installable through GitHub.

3. **Pull Request**: After building the theme, commit the compiled output, push
  your branch to GitHub and send a PR to `es4x:develop`. If we
  suggest changes, make the required updates, rebase your branch and push the
  changes to your GitHub repository, which will automatically update your PR.

After your PR is merged, you can safely delete your branch and pull the changes
from the main (upstream) repository.

## Building the world

In order to build the `world` you will need several tools installed in your host
environment:

* [GraalVM](https://www.graalvm.org/downloads/)
* [Apache Maven](https://maven.apache.org/)
* [Node.js](https://nodejs.org/en/download/)
* [NPM](https://www.npmjs.com/)

If you have `GraalVM` and `Maven` installed you might skip the installation of `Node.js`
and `NPM` although the `node` binary included with `GraalVM` is known to have some
performance issues with some of `npm` packages such as `TypeScript Compiler`.
 
### Modules

This projects is composed of several main modules/components:

1. [es4x](https://github.com/reactiverse/es4x/tree/develop/es4x) the main java code that
   bootstraps the GraalJS and Vert.x
2. [pm](https://github.com/reactiverse/es4x/tree/develop/pm) the package manager utility
3. [codegen](https://github.com/reactiverse/es4x/tree/develop/codegen) the codegen library
   that will generate the `npm` package counterparts for `vert.x` modules
4. [generator](https://github.com/reactiverse/es4x/tree/develop/generator) maven script that
   generates the `npm` full package for a given `vert.x` module
5. [docs](https://github.com/reactiverse/es4x/tree/develop/docs) the directory you're seeing
   right now.

### Build the Java part

Building the java part is as simple as:

```bash
mvn -Pcodegen install
```

Use the profile `codegen` if you want to generate the npm modules too. Otherwise only:

* es4x
* pm
* codegen

Are built.

### Deploying the NPM modules

During development you might want to deploy to a local NPM registry, one of these registries
you can use is [verdaccio](https://verdaccio.org/).

```bash
npm install -g verdaccio
```

Once you have it installed follow the instructions to login:

```bash
npm adduser --registry "http://localhost:4873"
```

::: warning package upload limits

Currently the `pm` package is quite large and will not be handled by default by `verdaccio` in order to get the upload to work you will need to update the default config and restart.

:::

Edit the file `~/.config/verdaccio/config.yaml` and add:

```yaml
# max package size
max_body_size: 100mb
```

Once you have a local registry configured you can deploy the `npm` packages locally:

```bash
cd generator
mvn -Dnpm-registry="http://localhost:4873" \
    clean \
    generate-sources \
    exec:exec@npm-publish
```

::: warning API docs

If you would like to have API docs for the generated packages then you will need a few extra
tools and an extra maven.

:::

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

### Deploy PM to npm

For convenience, the `pm` project can also be deployed to the NPM registry, in order to achieve this:

```bash
cd pm
mvn package
./publish.sh local
```

This will generate the maven fat jar and the final script will convert it to a npm package and deploy to
your local verdaccio installation.
