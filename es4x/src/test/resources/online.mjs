import { Router } from 'https://unpkg.io/@vertx/web@4.1.0/mod.js'

Router.router(vertx);

import { a } from './mjs/moduleA.mjs'

a();

print('OK!');
