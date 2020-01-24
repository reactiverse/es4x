"use strict";
/// <reference types="es4x" />
Object.defineProperty(exports, "__esModule", { value: true });
const web_1 = require("@vertx/web");
const routes_1 = require("./routes");
const app = web_1.Router.router(vertx);
console.log(typeof routes_1.home);
app.route('/').handler(routes_1.home);
vertx.createHttpServer()
    .requestHandler(app)
    .listen(8080);
//# sourceMappingURL=index.js.map
