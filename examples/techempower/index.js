/// <reference types="@vertx/core/runtime" />
// @ts-check

import { Router } from '@vertx/web';

import { PgClient, Tuple } from '@reactiverse/reactive-pg-client';
import { PgPoolOptions } from '@reactiverse/reactive-pg-client/options';

const util = require('./util');

const SERVER = 'vertx.js';

const app = Router.router(vertx);

let date = new Date().toString();

vertx.setPeriodic(1000, t => date = new Date().toString());

/*
 * This test exercises the framework fundamentals including keep-alive support, request routing, request header
 * parsing, object instantiation, JSON serialization, response header generation, and request count throughput.
 */
app.get("/json").handler(ctx => {
  ctx.response()
    .putHeader("Server", SERVER)
    .putHeader("Date", date)
    .putHeader("Content-Type", "application/json")
    .end(JSON.stringify({ message: 'Hello, World!' }));
});

const UPDATE_WORLD = "UPDATE world SET randomnumber=$1 WHERE id=$2";
const SELECT_WORLD = "SELECT id, randomnumber from WORLD where id=$1";
const SELECT_FORTUNE = "SELECT id, message from FORTUNE";

let client = PgClient.pool(
  vertx,
  new PgPoolOptions()
    .setUser('benchmarkdbuser')
    .setPassword('benchmarkdbpass')
    .setDatabase('hello_world'));

/*
 * This test exercises the framework's object-relational mapper (ORM), random number generator, database driver,
 * and database connection pool.
 */
app.get("/db").handler(ctx => {
  client.preparedQuery(SELECT_WORLD, Tuple.of(util.randomWorld()), res => {
    if (res.succeeded()) {
      let resultSet = res.result().iterator();

      if (!resultSet.hasNext()) {
        ctx.fail(404);
        return;
      }

      let row = resultSet.next();

      ctx.response()
        .putHeader("Server", SERVER)
        .putHeader("Date", date)
        .putHeader("Content-Type", "application/json")
        .end(JSON.stringify({ id: row.getInteger(0), randomNumber: row.getInteger(1) }));
    } else {
      ctx.fail(res.cause());
    }
  })
});

/*
 * This test is a variation of Test #2 and also uses the World table. Multiple rows are fetched to more dramatically
 * punish the database driver and connection pool. At the highest queries-per-request tested (20), this test
 * demonstrates all frameworks' convergence toward zero requests-per-second as database activity increases.
 */
// app.get("/queries").handler(pg.queriesHandler);

/*
 * This test exercises the ORM, database connectivity, dynamic-size collections, sorting, server-side templates,
 * XSS countermeasures, and character encoding.
 */
// app.get("/fortunes").handler(pg.fortunesHandler);

/*
 * This test is a variation of Test #3 that exercises the ORM's persistence of objects and the database driver's
 * performance at running UPDATE statements or similar. The spirit of this test is to exercise a variable number of
 * read-then-write style database operations.
 */
// app.route("/update").handler(pg.updateHandler);

/*
 * This test is an exercise of the request-routing fundamentals only, designed to demonstrate the capacity of
 * high-performance platforms in particular. Requests will be sent using HTTP pipelining. The response payload is
 * still small, meaning good performance is still necessary in order to saturate the gigabit Ethernet of the test
 * environment.
 */
app.get("/plaintext").handler(ctx => {
  ctx.response()
    .putHeader("Server", SERVER)
    .putHeader("Date", date)
    .putHeader("Content-Type", "text/plain")
    .end('Hello, World!');
});

vertx
  .createHttpServer()
  .requestHandler(req => app.accept(req))
  .listen(8080);

console.log('Server listening at: http://localhost:8080/');
