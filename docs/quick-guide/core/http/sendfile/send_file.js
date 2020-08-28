/// <reference types="es4x" />
// @ts-check

// In reality it's highly recommend you use Vert.x-Web for applications like this.

vertx.createHttpServer().requestHandler(req => {
  let filename;

  if (req.path() == "/") {
    filename = "sendfile/index.html";
  } else if (req.path() == "/page1.html") {
    filename = "sendfile/page1.html";
  } else if (req.path() == "/page2.html") {
    filename = "sendfile/page2.html";
  } else {
    req.response().setStatusCode(404).end();
  }

  if (filename) {
    req.response().sendFile(filename);
  }
}).listen(8080);
