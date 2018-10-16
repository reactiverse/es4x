## Modules

ES4X supports `commonjs` modules, which means that the format used by `nodejs` will work
as is. There are some important notes. As the current state there is no support for
node native modules so as a user, this could limit the amount of modules you can use
from NPM. However there is a way to allow per project substitutions of modules.

#### Alias

Imagine the following scenario. Your application requires a module that in turn requires `uuid`.

In this scenario, the module will fail to load as `uuid` makes use of `node` specific modules
such as `crypto`. In order to overcome this limitation a developer should define a set of
substitutions, or alias in the `package.json`.

So going back to the example, if the application makes use of `uuid v4` then the crypto
module is missing, in order to fix this we define an alias to the module `uuid/lib/rng.js` pointing
at `./alias/rng.js` like this:

```json
{
  "name": "alias",
  "private": true,
  "version": "0.0.1",
  "main": "index.js",

  "dependencies": {
    "uuid": "3.3.2"
  },

  "es4xAlias": {
    "uuid/lib/rng.js": "./alias/rng.js"
  }
}
```

This shows that when a module will require uuid/lib/rng.js and this always expects to be modules (i.e.: a file in node_modules/uuid/lib/rng.js) then it will not load it and in place load the file ./alias/rng.js relative to the location of the package.json.

So if we provide the file `alias/rng.js`:

```js
const SecureRandom = Java.type('java.security.SecureRandom');

const rnd = new SecureRandom();

module.exports = function jvmRNG() {
  let bytes = new Array(16);

  for (let i = 0; i< 4; i++) {
    let r = rnd.nextInt();
    let idx = i * 4;
    bytes[idx] = r & 0xff;
    bytes[idx + 1] = (r >> 8) & 0xff;
    bytes[idx + 2] = (r >> 16) & 0xff;
    bytes[idx + 3] = (r >> 24) & 0xff;
  }

  return bytes;
};
```

Which now uses the JVM specific crypto bits, the code above will load properly and print:

```
70e8df84-073b-4723-a3b2-9602ea899734
```
