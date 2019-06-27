async function futureTest1 () {
  let server = await {
    then: vertx
      .createHttpServer()
      .requestHandler(req => {
      })
      .listen(0)
  };

  should.assertEquals('io.vertx.core.http.impl.HttpServerImpl', server.getClass().getName());
}

futureTest1()
  .then(function (result) {
    test.complete();
  })
  .catch(function (err) {
    print(err)
  });
