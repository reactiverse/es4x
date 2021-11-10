if (global === null || global === undefined) {
  throw new Error('"global" is missing!');
}

if (process === null || process === undefined) {
  throw new Error('"process" is missing!');
}

if (vertx === null || vertx === undefined) {
  throw new Error('"vertx" is missing!');
}
