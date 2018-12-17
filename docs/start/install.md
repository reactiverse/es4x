Assuming you’ve already installed [Node.js](https://nodejs.org/) and ([Java](https://adoptopenjdk.net/) or
[GraalVM](http://www.graalvm.org/)), create a directory to hold your application, and make that your working directory.

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
npx es4x-cli init
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
