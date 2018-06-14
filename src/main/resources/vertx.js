(function (self) {
  if (typeof vertx === 'undefined') {
    var clustered = false;

    // remove the default quit functions
    delete self['exit'];
    delete self['quit'];

    // install the global object
    if (typeof global === 'undefined') {
      self['global'] = self;
    }

    // do we want clustering support?
    if (self['arguments']) {
      for (var i = 0; i < self['arguments'].length; i++) {
        if (self['arguments'][i] === '--cluster') {
          clustered = true;
          break;
        }
      }
    }

    // will setup vertx + default codec
    if (typeof Graal !== 'undefined') {
      // Graal mode
      global['vertx'] = Java.type('io.reactiverse.es4x.impl.graal.GraalJSRuntime').install({}, JSON, clustered);
    } else {
      // Nashorn mode
      self['vertx'] = Java.type('io.reactiverse.es4x.impl.nashorn.NashornJSRuntime').install(JSON, clustered);
    }

    // load polyfills
    load("classpath:io/reactiverse/es4x/polyfill/object.js");
    load("classpath:io/reactiverse/es4x/polyfill/json.js");
    load("classpath:io/reactiverse/es4x/polyfill/global.js");
    load("classpath:io/reactiverse/es4x/polyfill/console.js");
    load("classpath:io/reactiverse/es4x/polyfill/promise.js");
    // install the commonjs loader
    load("classpath:io/reactiverse/es4x/jvm-npm.js");
  }
})(this);
