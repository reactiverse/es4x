/**
 *  Copyright 2014-2018 Red Hat, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License")
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
// Since we intend to use the Function constructor.
(function (global) {
  const System = Java.type('java.lang.System');
  const URI = Java.type('java.net.URI');
  const ESModuleIO = Java.type('io.reactiverse.es4x.impl.ESModuleIO');
  const io = new ESModuleIO(global.vertx);

  function Module(id, parent) {
    this.id = id;
    this.parent = parent;
    this.children = [];
    this.filename = id.toString();
    this.loaded = false;

    Object.defineProperty(this, 'exports', {
      get: function () {
        return this._exports;
      }.bind(this),
      set: function (val) {
        Require.cache[this.filename] = val;
        this._exports = val;
      }.bind(this)
    });
    this.exports = {};

    if (this.parent && this.parent.children) {
      this.parent.children.push(this);
    }

    this.require = function (id) {
      return Require(id, this);
    }.bind(this);
  }

  Module._load = function _load(uri, parent, main, workerAddress) {
    const module = new Module(uri, parent);
    const body = io.readFile(uri, !!main);
    const dir = io.getParent(uri);

    let sourceURL = '<unknown>';

    switch (uri.getScheme()) {
      case 'jar':
        sourceURL = uri.getPath().substr(1);
        break;
      case 'file':
        sourceURL = uri.getPath();
        break;
    }

    if (workerAddress) {

      let workerContext = {
        postMessage: function (msg) {
          // this implementation is not totally correct as it should be
          // a shallow copy not a full encode/decode of JSON payload, however
          // this works better in vert.x as we can be interacting with any
          // polyglot language or across the cluster
          vertx.eventBus().send(workerAddress + '.in', JSON.stringify(msg));
        }
      };

      // wrap the module with a eval statement instead of Function object so we
      // can preserve the correct line numbering during exceptions
      const func = load({
        script: '(function (self, require, postMessage, __filename, __dirname) { ' + body + '\n});',
        name: sourceURL
      });
      func.apply(module, [workerContext, module.require, workerContext.postMessage, module.filename, dir]);
      module.loaded = true;
      module.main = main;
      // we don't return the exported, but the worker context
      return workerContext;
    } else {
      // wrap the module with a eval statement instead of Function object so we
      // can preserve the correct line numbering during exceptions
      const func = load({
        script: '(function (exports, require, module, __filename, __dirname) { ' + body + '\n});',
        name: sourceURL
      });

      func.apply(module, [module.exports, module.require, module, module.filename, dir]);
      module.loaded = true;
      module.main = main;
      return module.exports;
    }
  };

  Module.runMain = function runMain(main) {
    const uri = Require.resolve(main);

    if (!uri) {
      throw new ModuleError('Module "' + main + '" was not found', 'MODULE_NOT_FOUND');
    }
    return Module._load(uri, undefined, true, undefined);
  };

  Module.runWorker = function runWorker(main, address) {
    if (!address) {
      throw new ModuleError('Worker address must be supplied!', 'ADDRESS_NOT_FOUND');
    }
    // workers should supply a uri, so we only look in 2 roots
    const uri =
      // in the jar
      resolveAsFile(main, 'jar://', '.js') ||
      // from the current working dir
      resolveAsFile(main, parsePaths('file://', System.getProperty("user.dir")), '.js');

    if (!uri) {
      throw new ModuleError('Module "' + main + '" was not found', 'MODULE_NOT_FOUND');
    }

    return Module._load(uri, undefined, true, address);
  };

  function Require(id, parent) {
    const uri = Require.resolve(id, parent);

    if (!uri) {
      throw new ModuleError('Module "' + id + '" was not found', 'MODULE_NOT_FOUND');
    }

    if (Require.cache[uri]) {
      return Require.cache[uri];
    } else if (uri.getPath().endsWith('.js')) {
      return Module._load(uri, parent);
    } else if (uri.getPath().endsWith('.json')) {
      return loadJSON(uri);
    }
  }

  Require.resolve = function (id, parent) {
    const roots = findRoots(parent);
    let start = id.substring(0, 2);
    for (let i = 0; i < roots.length; ++i) {
      let root = roots[i];

      let result;
      try {
        // node_modules do not start with a prefix
        if (start !== './' && start !== '..') {
          result = resolveAsNodeModule(id, root);
        } else {
          result =
            resolveAsFile(id, root, '.js') ||
            resolveAsFile(id, root, '.json') ||
            resolveAsDirectory(id, root);
        }
      } catch (ex) {
        throw new ModuleError('Module "' + id + '" was not found', 'MODULE_NOT_FOUND');
      }

      if (result) {
        return result;
      }
    }
    return false;
  };

  function findRoots(parent) {
    if (!parent || !parent.id) {
      return Require.paths();
    }
    // always prepend the current parent dir
    return [parent.id.resolve('.')].concat(Require.paths());
  }

  function parsePaths(prefix, paths, suffix) {
    const out = [];

    if (!paths) {
      return out;
    }

    const osName = System.getProperty('os.name').toLowerCase();
    let separator;

    if (osName.indexOf('win') >= 0) {
      separator = ';';
      // transform \ into /
      paths = paths.replace(/\\/g, '/');
    } else {
      separator = ':';
    }

    // append the desired prefix, suffix
    paths.split(separator).forEach(function (p) {
      if (p) {
        // all paths need to be absolute
        if (p.indexOf('./') === 0) {
          let cwd = System.getProperty("user.dir");
          if (cwd.length > 0) {
            if (cwd[cwd.length - 1] !== '/') {
              cwd += '/';
            }
          }
          p = cwd + p.substr(2);
        }
        out.push((prefix || '') + p + (suffix || ''));
      }
    });

    return out;
  }

  Require.paths = function () {
    let r = [
      // classpath resources
      'jar://'
    ]
    // current working dir
    .concat(parsePaths('file://', System.getProperty("user.dir")))
    // user node modules cache
    .concat(parsePaths('file://', System.getProperty('user.home'), '/.node_modules'))
    // user node libraries cache
    .concat(parsePaths('file://', System.getProperty('user.home'), '/.node_libraries'));

    let NODE_PATH = process.env['NODE_PATH'];
    if (NODE_PATH) {
      // NODE_PATH takes precedence
      r = parsePaths('file://', NODE_PATH).concat(r);
    }

    return r;
  };

  Require.cache = {};
  Require.extensions = {};
  // extension (non standard)
  Require.alias = {};

  global.require = Require;

  function loadJSON(uri) {
    const json = JSON.parse(io.readFile(uri));
    Require.cache[uri] = json;
    return json;
  }

  function resolveAsNodeModule(id, root) {
    let base = root ? [root, 'node_modules'].join('/') : 'node_modules';
    return resolveAsFile(id, base) ||
      resolveAsDirectory(id, base) ||
      (root ? resolveAsNodeModule(id, io.getParent(root)) : false);
  }

  function resolveAsDirectory(id, root) {
    let base = root ? [root, id].join('/') : id;
    const uri = new URI([base, 'package.json'].join('/')).normalize();
    if (io.exists(uri)) {
      const body = io.readFile(uri);
      const package_ = JSON.parse(body);
      // add alias to alias cache
      if (package_.es4xAlias) {
        for (let k in package_.es4xAlias) {
          if (package_.es4xAlias.hasOwnProperty(k)) {
            let key = new URI([base, 'node_modules', k].join('/')).normalize();
            if (Require.alias[key]) {
              console.warn('Replacing alias [' + key + ']');
            }
            Require.alias[key] = new URI([base, package_.es4xAlias[k]].join('/')).normalize();
          }
        }
      }
      // resolve main if present
      if (package_.main) {
        return (resolveAsFile(package_.main, base) ||
          resolveAsDirectory(package_.main, base));
      }
      // if no package.main exists, look for index.js
      return resolveAsFile('index.js', base);
    }
    return resolveAsFile('index.js', base);
  }

  function resolveAsFile(id, root, ext) {
    let uri;
    if (id.length > 0 && id[0] === '/') {
      uri = new URI('file://' + normalizeName(id, ext)).normalize();
      if (!io.exists(uri)) {
        return resolveAsDirectory('file://' + id);
      }
    } else {
      uri = new URI(root ? [root, normalizeName(id, ext)].join('/') : normalizeName(id, ext)).normalize();
    }
    // perform alias
    uri = Require.alias[uri] || uri;

    if (io.exists(uri) && io.isFile(uri)) {
      return uri;
    }
  }

  function normalizeName(fileName, ext) {
    let extension = ext || '.js';
    if (fileName.endsWith(extension)) {
      return fileName;
    }
    return fileName + extension;
  }

  function ModuleError(message, code, cause) {
    this.code = code || 'UNDEFINED';
    this.message = message || 'Error loading module';
    this.cause = cause;
  }

  // Helper function until ECMAScript 6 is complete
  if (typeof String.prototype.endsWith !== 'function') {
    String.prototype.endsWith = function (suffix) {
      if (!suffix) return false;
      return this.indexOf(suffix, this.length - suffix.length) !== -1;
    };
  }

  ModuleError.prototype = new Error();
  ModuleError.prototype.constructor = ModuleError;

  return Module;
})(global || this);
