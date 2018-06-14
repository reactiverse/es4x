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
  var fs = global.vertx.fileSystem();
  var System = Java.type('java.lang.System');
  var URI = Java.type('java.net.URI');
  var ESModuleAdapter = Java.type('io.reactiverse.es4x.ESModuleAdapter');

  function getParent(uri) {
    if (!(uri instanceof URI)) {
      try {
        uri = new URI(uri);
      } catch (e) {
        // there is no parent
        return null;
      }
    }
    var path = uri.getPath();
    var last = path.lastIndexOf('/');
    if (path.length > last) {
      return uri.getScheme() + ':' + path.substring(0, last);
    }
  }

  function exists(uri) {
    switch (uri.getScheme()) {
      case 'jar':
        return fs.existsBlocking(uri.getPath().substr(1));
      case 'file':
        return fs.existsBlocking(uri.getPath());
      default:
        return false;
    }
  }

  function isFile(uri) {
    switch (uri.getScheme()) {
      case 'jar':
        return fs.propsBlocking(uri.getPath().substr(1)).isRegularFile();
      case 'file':
        return fs.propsBlocking(uri.getPath()).isRegularFile();
      default:
        return false;
    }
  }

  function readFile(uri) {
    try {
      switch (uri.getScheme()) {
        case 'jar':
          return ESModuleAdapter.adapt(fs.readFileBlocking(uri.getPath().substr(1)).toString());
        case 'file':
          return ESModuleAdapter.adapt(fs.readFileBlocking(uri.getPath()).toString());
      }
    } catch (e) {
      throw new ModuleError('Cannot read file [' + uri + ']: ', 'IO_ERROR', e);
    }
    // if we fall through it's an error as we can't handle this scheme
    throw new ModuleError('Cannot handle scheme [' + uri.getScheme() + ']: ', 'IO_ERROR');
  }

  function Module(id, parent) {
    this.id = id;
    this.parent = parent;
    this.children = [];
    this.filename = id;
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

  Module._load = function _load(uri, parent, main) {
    var module = new Module(uri.toString(), parent);
    var body = readFile(uri);
    var dir = getParent(uri);

    var sourceURL = '<unknown>';

    switch (uri.getScheme()) {
      case 'jar':
        sourceURL = uri.getPath().substr(1);
        break;
      case 'file':
        sourceURL = uri.getPath();
        break;
    }

    var func = new Function('exports', 'module', 'require', '__filename', '__dirname', body + '\n//# sourceURL=' + sourceURL);
    func.apply(module, [module.exports, module, module.require, module.filename, dir]);

    module.loaded = true;
    module.main = main;
    return module.exports;
  };

  Module.runMain = function runMain(main) {
    var uri = Require.resolve(main);
    Module._load(uri, undefined, true);
  };

  function Require(id, parent) {
    var uri = Require.resolve(id, parent);

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
    var roots = findRoots(parent);
    var start = id.substring(0, 2);
    for (var i = 0; i < roots.length; ++i) {
      var root = roots[i];

      var result;
      // node_modules do not start with a prefix
      if (start !== './' && start !== '..') {
        result = resolveAsNodeModule(id, root);
      } else {
        result =
          resolveAsFile(id, root, '.js') ||
          resolveAsFile(id, root, '.json') ||
          resolveAsDirectory(id, root);
      }

      if (result) {
        return result;
      }
    }
    return false;
  };

  Require.NODE_PATH = undefined;

  function findRoots(parent) {
    if (!parent || !parent.id) {
      return Require.paths();
    }

    return [findRoot(parent)].concat(Require.paths());
  }

  function parsePaths(paths) {
    if (!paths) {
      return [];
    }
    if (paths === '') {
      return [];
    }
    var osName = System.getProperty('os.name').toLowerCase();
    var separator;

    if (osName.indexOf('win') >= 0) {
      separator = ';';
      // transform \ into /
      paths = paths.replaceAll('\\', '/');
    } else {
      separator = ':';
    }

    return paths.split(separator);
  }

  Require.paths = function () {
    var r = [
      // classpath resources
      'jar://',
      // current working dir
      'file://' + parsePaths(System.getProperty("user.dir"))[0],
      // user node modules cache
      'file://' + parsePaths(System.getProperty('user.home'))[0] + '/.node_modules',
      // user node libraries cache
      'file://' + parsePaths(System.getProperty('user.home'))[0] + '/.node_libraries'
    ];

    if (Require.NODE_PATH) {
      r = r.concat('file://' + parsePaths(Require.NODE_PATH));
    } else {
      var NODE_PATH = System.getenv('NODE_PATH');
      if (NODE_PATH) {
        r = r.concat('file://' + parsePaths(NODE_PATH));
      }
    }

    return r;
  };

  function findRoot(parent) {
    var pathParts = parent.id.split(/[\/|\\,]+/g);
    pathParts.pop();
    return pathParts.join('/');
  }

  Require.cache = {};
  Require.extensions = {};
  global.require = Require;

  function loadJSON(uri) {
    var json = JSON.parse(readFile(uri));
    Require.cache[uri] = json;
    return json;
  }

  function resolveAsNodeModule(id, root) {
    var base = root ? [root, 'node_modules'].join('/') : 'node_modules';
    return resolveAsFile(id, base) ||
      resolveAsDirectory(id, base) ||
      (root ? resolveAsNodeModule(id, getParent(root)) : false);
  }

  function resolveAsDirectory(id, root) {
    var base = root ? [root, id].join('/') : id;
    var uri = new URI([base, 'package.json'].join('/')).normalize();
    if (exists(uri)) {
      try {
        var body = readFile(uri);
        var package_ = JSON.parse(body);
        if (package_.main) {
          return (resolveAsFile(package_.main, base) ||
            resolveAsDirectory(package_.main, base));
        }
        // if no package.main exists, look for index.js
        return resolveAsFile('index.js', base);
      } catch (ex) {
        throw new ModuleError('Cannot load JSON file', 'PARSE_ERROR', ex);
      }
    }
    return resolveAsFile('index.js', base);
  }

  function resolveAsFile(id, root, ext) {
    var uri;
    if (id.length > 0 && id[0] === '/') {
      uri = new URI(normalizeName(id, ext)).normalize();
      if (!exists(uri)) {
        return resolveAsDirectory(id);
      }
    } else {
      uri = new URI(root ? [root, normalizeName(id, ext)].join('/') : normalizeName(id, ext)).normalize();
    }
    if (exists(uri) && isFile(uri)) {
      return uri;
    }
  }

  function normalizeName(fileName, ext) {
    var extension = ext || '.js';
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
})(this);
//# sourceURL=src/main/resources/io/reactiverse/es4x/jvm-npm.js
