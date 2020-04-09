module.exports = {
  /**
   * Extension (not real part of the spec but nice to have for vert.x)
   * @param {object} obj native vert.x object
   * @return wrapper function that follows the promise flow.
   */
  promisify: function promisify(obj) {
    if (typeof obj === 'function') {
      return function () {
        const args = [obj].concat(Array.prototype.slice.call(arguments));
        return new Promise(function (resolve, reject) {
          args.push(function (err, res) {
            if (err) {
              reject(err);
            } else {
              resolve(res);
            }
          });
          Function.call.apply(obj, args);
        });
      }
    } else if (typeof obj === 'object') {
      return new Proxy(obj, {
        get: function (obj, prop) {
          if (typeof obj[prop] !== 'function') {
            return obj[prop];
          }

          return function () {
            let args = [obj].concat(Array.prototype.slice.call(arguments));
            return new Promise(function (resolve, reject) {
              args.push(function (res) {
                if (res.failed()) {
                  reject(res.cause());
                } else {
                  resolve(res.result());
                }
              });
              Function.call.apply(obj[prop], args);
            });
          };
        }
      });
    }
  }
};
