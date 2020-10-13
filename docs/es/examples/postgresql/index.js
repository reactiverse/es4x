/// <reference types="es4x" />
// @ts-check

import { PgPool } from '@vertx/pg-client';
import { PoolOptions } from '@vertx/sql-client/options';
import { PgConnectOptions } from '@vertx/pg-client/options';
import { Tuple } from '@vertx/sql-client';

const SELECT_WORLD = "SELECT id, randomnumber from WORLD where id=$1";

let connectOptions = new PgConnectOptions()
  .setCachePreparedStatements(true)
  .setHost('database-server')
  .setUser('dbuser')
  .setPassword('dbpass')
  .setDatabase('hello_world');

// Opciones Pool
let poolOptions = new PoolOptions()
  .setMaxSize(1);

// Crea la Pool del cliente
let client = PgPool.pool(vertx, connectOptions, poolOptions);

// Elige una fila de la base de datos aleatoriamente
client.preparedQuery(SELECT_WORLD).execute(Tuple.of(Math.random()), res => {
  if (res.succeeded()) {
    let resultSet = res.result().iterator();

    if (!resultSet.hasNext()) {
      ctx.fail(404);
      return;
    }

    let row = resultSet.next();

    console.log({
      id: row.getInteger(0),
      randomNumber: row.getInteger(1)
    });
  } else {
    console.trace(res.cause());
  }
});
