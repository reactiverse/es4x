async function futureTest1 () {
  let server = await vertx
      .createHttpServer()
      .requestHandler(req => {
      })
      .listen(0);

  console.log('Server Ready!');
}

futureTest1()
  .then(function (result) {
    test.complete();
  })
  .catch(function (err) {
    should.fail(err)
  });
