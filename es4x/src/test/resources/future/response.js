// web 4.0.0 introduces a <T> respond(Function<RoutingContext, Future<T>> fn)
// as Future is not a polyglot type anymore we need to ensure that this works with Futures and PromiseLike
const Future = Java.type('io.vertx.core.Future');
should.assertNotNull(mock);

mock.respond((ctx) => Promise.resolve(ctx));
mock.respond((ctx) => Future.succeededFuture(ctx));

mock.fail((ctx) => Future.failedFuture('Ooops!'));
mock.fail((ctx) => Promise.reject('Ooops!'));
mock.fail((ctx) => Promise.reject(new Error('Ooops!')));
