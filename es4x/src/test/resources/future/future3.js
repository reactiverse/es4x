async function futureTest3 () {
  return await vertx
      .createHttpServer()
      .requestHandler(req => {})
      .listen(12345);
}

futureTest3()
  .then(function (result) {
    should.assertNotNull(result);
    should.assertEquals('io.vertx.core.http.impl.HttpServerImpl', result.getClass().getName());
    should.assertEquals(12345, result.actualPort());
    test.complete();
  })
  .catch(function (err) {
    should.fail('Expected to create a HTTP Server on port 12345');
  });
