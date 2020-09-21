async function futureTest2 () {
  let server = await {
    then: vertx
      .createHttpServer()
      .requestHandler(req => {
      })
      .listen(-1)
  };

  should.assertEquals('io.vertx.core.http.impl.HttpServerImpl', server.getClass().getName());
}

futureTest2()
  .then(function (result) {
    should.fail('Expected to fail with negative port');
  })
  .catch(function (err) {
    test.complete();
  });
