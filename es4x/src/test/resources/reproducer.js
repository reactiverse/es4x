const Buffer = Java.type('io.vertx.core.buffer.Buffer');

vertx.fileSystem()
  .writeFileBlocking("test.txt", Buffer.buffer("Hello world!"));
