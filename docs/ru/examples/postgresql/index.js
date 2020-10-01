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

// Настройки пула
let poolOptions = new PoolOptions()
  .setMaxSize(1);

// Создаем клиентский пул
let client = PgPool.pool(vertx, connectOptions, poolOptions);

// Получаем случайную строку из таблицы
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
