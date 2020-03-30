const Buffer = Java.type('io.vertx.core.buffer.Buffer');

vertx.fileSystem()
  .writeFileBlocking("target/test.txt", Buffer.buffer("Hello world!"));
