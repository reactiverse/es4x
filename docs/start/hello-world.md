```js
/// <reference types="@vertx/core/runtime" />
// @ts-check
import { Router } from '@vertx/web';

const app = Router.router(vertx);

app.route('/').handler(function (ctx) {
  ctx.response().end('Hello from Vert.x Web!');
});

vertx.createHttpServer()
  .requestHandler(app)
  .listen(8080);
```

This app starts a server and listens on port 8080 for connections. The app responds with “`Hello from Vert.x Web!`” for
requests to the root URL (`/`) or route. For every other path, it will respond with a **404 Not Found**.

## Running locally

First create a directory named `myapp`, change to it and run `npm init`. Then install `es4x` as per the
[installation guide](./install.md).

In the `myapp` directory, create a file named `app.js` and copy in the code from the example above.

Run the app with the following command:

```bash
npm start
```

Then, load `http://localhost:8080/` in a browser to see the output.
