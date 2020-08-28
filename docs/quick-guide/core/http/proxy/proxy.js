/// <reference types="es4x" />
// @ts-check

const client = vertx.createHttpClient();

vertx
  .createHttpServer()
  .requestHandler(req => {
    console.log("Proxying request: " + req.uri());

    let c_req = client.request(req.method(), 8282, "localhost", req.uri(), c_res => {
      console.log("Proxying response: " + c_res.statusCode());
      req.response()
        .setChunked(true)
        .setStatusCode(c_res.statusCode())
        .headers()
        .setAll(c_res.headers());

      c_res.handler(data => {
        console.log("Proxying response body: " + data.toString("ISO-8859-1"));
        req.response().write(data);
      });
      c_res.endHandler(v => {
        req.response().end();
      });
    });

    c_req
      .setChunked(true)
      .headers()
      .setAll(req.headers());

    req.handler(data => {
      console.log("Proxying request body " + data.toString("ISO-8859-1"));
      c_req.write(data);
    });

    req.endHandler(v => {
      c_req.end();
    });
  }).listen(8080);
