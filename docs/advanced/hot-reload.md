# Hot Reload

To speed the development cycles, `es4x` supports simple `hot-reload`. The functionality is very basic.
On a file change, the application will be stopped abruptly and restarted. This feature is not `es4x`
specific, in fact it just relies on `vertx` core functionality.

## Walk-through

Imagine the following application composed from 4 source `js` files:

1. index.js
2. m/a.js
3. m/b.js
4. m/c.js

The content of these files is as follows:

### index.js

```js
const a = require('./m/a');
a();
```

Call a function defined on a relative module `a`.

### m/a.js

```js
const b = require('./b');

function a() {
    print('Hello from A');
    b();
}

module.exports = a;
```

Prints a message and calls another module `b`.

### m/b.js

```js
const c = require('./c');

// changed B
function b() {
print('Hello from B');
c();
}

module.exports = b;
```

Prints a message and calls another module `c`.

### m/c.js

```js
function c() {
    print('Hello from C');
}

module.exports = c;
```

Prints the final message.

## Running

In order to run an application with `hot-reload` all you need is to use the `vert.x` **redeploy** command.

In this example one would execute:

```bash
es4x run --redeploy "m/*" index.js
```

It is important to know that the redeploy watch list works with files, so in order to watch a directory, one needs to
watch a **wildcard**.

```
$ ./node_modules/.bin/es4x --redeploy "m/*"
Watched paths: [/home/hello/./m]
Starting the vert.x application in redeploy mode
Starting vert.x application...
ec467de2-ca71-43c6-98d8-9da0cc0d24f8-redeploy
Hello from A
Hello from B
Hello from C
Succeeded in deploying verticle
```

When any of the files under `m` are touched you will see a similar message in the console:

```
Redeploying!
Stopping vert.x application 'ec467de2-ca71-43c6-98d8-9da0cc0d24f8-redeploy'
Application 'ec467de2-ca71-43c6-98d8-9da0cc0d24f8-redeploy' terminated with status 0
Starting vert.x application...
ec467de2-ca71-43c6-98d8-9da0cc0d24f8-redeploy
Redeployment done in 66 ms.
Hello from A
Hello from B
Hello from C
Succeeded in deploying verticle
```

This will happen for **each** file you touch.

## Running a task before redeploy

While re-deploy is already a time saver, there are usually steps required to be executed before the re-deploy happens.
For example a build step, like compile typescript to javascript, by running `tsc`. For this we can run the application
as:

```bash
es4x run --redeploy "m/*" --on-redeploy "tsc" index.js
```

For more information read the all the options on the `run` command:

```bash
es4x run --help
```
