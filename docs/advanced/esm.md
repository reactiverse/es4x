# EcmaScript Modules

EcmaScript modules are the **official** module format for the JavaScript language. `ESM` are supported by ES4X by using
one of the two options:

* The initial script has extension `.mjs`
* The initial script is prefixed with: `mjs:`

## Initial Script

For the naked eye, the initial script is not that different from the commonjs script, for example `index.mjs`:

```js
import { Router } from '@vertx/web';
import { someRoute } from './routes';

const app = Router.router(vertx);

app.route('/').handler(someRoute);

vertx.createHttpServer()
  .requestHandler(app)
  .listen(8080);
```

In this case the `someRoute` is imported from the `routes.mjs` file:

```js
export function someRoute(ctx) {
  ctx.response()
    .end('Hello from ES4X!');
}
```

### Compatibility

For compatibility reasons, you may have noticed that the `import` statement in the initial script, does not include an
extension:

```js{2}
import { Router } from '@vertx/web';
import { someRoute } from './routes';

// ...
```

This is a small divergence from the official spec where ES4X loader will lookup modules as follows:

1. Look up the exact file name: `./routes`
2. Look up with `.mjs` suffix: `./routes.mjs`
2. Look up with `.js` suffix: `./routes.js`

::: warning
When working with `ESM` the `require()` is not available!
:::
