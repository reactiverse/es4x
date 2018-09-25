(function (self) {

  function camelize(str) {
    return str.replace(/(?:^\w|[A-Z]|-\w|-+)/g, function(match, index) {
      if (!match) {
        return "";
      }
      let val = match.charAt(1);
      return index === 0 ? val.toLowerCase() : val.toUpperCase();
    });
  }

  if (typeof vertx === 'undefined') {
    let options = {};

    // remove the default quit functions
    delete self['exit'];
    delete self['quit'];

    // install the global object
    if (typeof global === 'undefined') {
      self['global'] = self;
    }

    // do we want clustering support?
    if (self['arguments']) {
      for (let i = 0; i < self['arguments'].length; i++) {
        let arg = self['arguments'][i];
        if (arg.length > 0 && arg.charAt(0) === '-') {
          if (arg === '-cluster') {
            // adapt argument to match vertx options
            arg = '-clustered';
          }
          let idx = arg.indexOf('=');
          if (idx !== -1) {
            // attempt to cast numeric values to number
            let value;
            try {
              value = parseInt(arg.substring(idx + 1), 10);
            } catch (e) {
              value = arg.substring(idx + 1);
            }

            options[camelize(arg.substring(0, idx))] = value;
          } else {
            options[camelize(arg.substring(0, idx))] = true;
          }
        }
      }
    }

    // will setup vertx + default codec
    if (typeof Graal !== 'undefined') {
      // Graal mode
      Java
        .type('java.lang.System')
        .setProperty('es4x.engine', 'GraalJS');
    } else {
      // Nashorn mode
      load("classpath:io/reactiverse/es4x/polyfill/object.js");
    }

    // install the vertx in the global scope
    global['vertx'] = Java
      .type('io.reactiverse.es4x.Runtime')
      .getCurrent()
      .vertx(options);

    // load polyfills
    load("classpath:io/reactiverse/es4x/polyfill/json.js");
    load("classpath:io/reactiverse/es4x/polyfill/global.js");
    load("classpath:io/reactiverse/es4x/polyfill/console.js");
    load("classpath:io/reactiverse/es4x/polyfill/promise.js");
    // install the commonjs loader
    load("classpath:io/reactiverse/es4x/jvm-npm.js");
  }
})(this);
