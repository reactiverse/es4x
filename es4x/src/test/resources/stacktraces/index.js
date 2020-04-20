print(Error.captureStackTrace)

var asyncError = require('async-error');
var fs = vertx.fileSystem();

function one() {
  two(function (err) {
    should.assertNotNull(err);
    if (err) {
      // the trace should contain 3 frames from JS code that were
      // stitched to the original exception
      var trace = err.getStackTrace();
      should.assertTrue(trace.length > 3);
      should.assertNotEquals(-1, trace[0].getFileName().indexOf('stacktraces/index.js'));
      should.assertNotEquals(-1, trace[1].getFileName().indexOf('stacktraces/index.js'));
      should.assertNotEquals(-1, trace[2].getFileName().indexOf('stacktraces/index.js'));
      console.trace(err);
      test.complete();
      return;
    }

    console.log("two finished");
    should.fail("Should not reach here");
  });
}

function two(callback) {
  setTimeout(function () {
    three(function (err) {
      if (err) {
        setTimeout(function () {
          callback(asyncError(err));
        }, 0);
        return;
      }

      console.log("three finished");
      callback();
    });
  }, 0);
}

function three(callback) {
  setTimeout(function () {
    four(function (err) {
      if (err) {
        setTimeout(function () {
          callback(asyncError(err));
        }, 0);
        return;
      }

      console.log("four finished");
      callback();
    });
  }, 0);
}

function four(callback) {
  setTimeout(function () {
    fs.readFile("durpa/durp.txt", function (ar) {
      if (ar.failed()) {
        callback(asyncError(ar));
      }
    });
  }, 0);
}

one();
