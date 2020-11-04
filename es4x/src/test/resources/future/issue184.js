const Promise = Java.type('io.vertx.core.Promise');
const CompositeFuture = Java.type('io.vertx.core.CompositeFuture');

const fs = vertx.fileSystem();

let p0 = Promise.promise();
let p1 = Promise.promise();

fs.readFile("pom.xml", p0);
fs.readFile("src/main/java/io/reactiverse/es4x/Runtime.java", p1);

let f0 = p0.future();
let f1 = p1.future();

CompositeFuture.all(f0, f1)
  .onComplete(res => {
    if (res.succeeded()) {
      test.complete();
    } else {
      should.fail(res.cause());
    }
  });
