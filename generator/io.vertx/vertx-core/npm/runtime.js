(function (self) {
  if (typeof vertx === 'undefined') {
    var clustered = false;
    var i;
    // graalvm shell
    if (typeof load === 'undefined' && typeof require !== 'undefined') {
      // do we want clustering support?
      if (process && process.argv) {
        for (i = 0; i < process.argv.length; i++) {
          if (process.argv[i] === '--cluster') {
            clustered = true;
            break;
          }
        }
      }
      // will setup vertx + default codec
      global['vertx'] = Java.type('io.reactiverse.es4x.impl.graal.GraalJSRuntime').install({}, JSON, clustered);
    }
  }
})(this);
