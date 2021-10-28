async function futureTest2 () {
  let server = await {
    then: vertx
      .createHttpServer()
      .requestHandler(req => {
      })
      .listen(-1)
  };
  should.assertEquals('io.vertx.core.http.impl.HttpServerImpl', server.getClass().getName());
  return server;
}

futureTest2()
  .then(function (result) {
    // 4.2. behavior
    if (result.actualPort() <= 0) {
      should.fail('Expected to fail with negative port');
    } else {
      test.complete();
    }
  })
  .catch(function (err) {
    // 4.1. behavior
    test.complete();
  });
