#!/usr/bin/env node
'use strict';

const fs = require('fs');
const path = require('path');
const spawn = require('child_process').spawn;
const program = require('commander');
const c = require('ansi-colors');
const Mustache = require('mustache');
const mkdirp = require('mkdirp');

const version = require('../package.json').version;
const dir = process.cwd();
const isWindows = /^win/.test(process.platform);

// quickly abort if there's no package.json
if (!fs.existsSync(path.resolve(dir, 'package.json'))) {
  console.error(c.red.bold('Cannot find `package.json` in current directory!'));
  process.exit(1);
}

const npm = require(path.resolve(dir, 'package.json'));

/**
 * Executes an external command.
 *
 * @param {String} command command to execute
 * @param {String[]} args arguments to the command
 * @param {Object} env environment variables
 * @param {Object} options verbose logging (log command stdout)
 * @param {Function?} callback callback at command termination
 */
function exec(command, args, env, options, callback) {
  const proc = spawn(command, args, {env: env});
  if (args && args.length > 0) {
    const lastArg = args[args.length - 1];
    console.log('Running: ' + c.bold(command) + ' ... ' + c.bold(lastArg));
  } else {
    console.log('Running: ' + c.bold(command));
  }

  proc.stdout.on('data', function (data) {
    if (options.verbose) {
      process.stdout.write(data);
    }
  });

  proc.stderr.on('data', function (data) {
    process.stderr.write(data);
  });

  proc.on('close', function (code) {
    if (code) {
      console.error(c.yellow.bold('Error: ' + command + " exit code " + code + '.' + (options.verbose ? '' : ' Re-run with verbose enabled for more details.')));
      if (options.stopOnError) {
        process.exit(code);
      }
    }
    callback && callback(code);
  });

  proc.on('error', function (err) {
    if (err) {
      console.error(c.red.bold(err));
      if (options.stopOnError) {
        process.exit(err);
      }
    }
    callback && callback(err);
  });
}

function installMavenWrapper() {
  mkdirp.sync(path.resolve(dir, '.mvn/wrapper'));
  fs.writeFileSync(path.resolve(dir, '.mvn/wrapper/maven-wrapper.jar'), fs.readFileSync(__dirname + '/../.mvn/wrapper/maven-wrapper.jar'));
  fs.writeFileSync(path.resolve(dir, '.mvn/wrapper/maven-wrapper.properties'), fs.readFileSync(__dirname + '/../.mvn/wrapper/maven-wrapper.properties'));
  fs.writeFileSync(path.resolve(dir, '.mvn/wrapper/MavenWrapperDownloader.java'), fs.readFileSync(__dirname + '/../.mvn/wrapper/MavenWrapperDownloader.java'));
  fs.writeFileSync(path.resolve(dir, 'mvnw.cmd'), fs.readFileSync(__dirname + '/../mvnw.cmd'));
  fs.writeFileSync(path.resolve(dir, 'mvnw'), fs.readFileSync(__dirname + '/../mvnw'));
  try {
    fs.chmodSync(path.resolve(dir, 'mvnw'), '0755');
  } catch (e) {
    // it's ok to fail if not windows i guess...
    if (!isWindows) {
      throw e;
    }
  }
}

/**
 * Helper to select local maven wrapper or system maven
 *
 * @returns {string} the maven command
 */
function getMaven() {
  let mvn = isWindows ? 'mvnw.cmd' : './mvnw';
  // check for wrapper
  if (!fs.existsSync(path.resolve(dir, mvn))) {
    try {
      installMavenWrapper();
    } catch (e) {
      console.log(c.yellow.bold('Failed to install maven wrapper, falling back to the system default!'));
      // don't care fallback to system wide maven
      return 'mvn';
    }
  }

  return path.resolve(dir, mvn);
}

function generateClassPath(callback) {
  const readClassPath = function () {
    let classPath = fs.readFileSync(path.resolve(dir, 'target/classpath.txt')).toString('UTF-8');
    // trim by the first line
    let idx = classPath.indexOf('\n');
    if (idx !== -1) {
      classPath = classPath.substring(0, idx);
    }
    // we need to attach the classes directory just in case
    classPath += (isWindows ? ';' : ':') + path.resolve(dir, 'target/classes');

    callback(classPath);
  };

  if (!fs.existsSync(path.resolve(dir, 'target/classpath.txt'))) {
    let params = [
      '-f', path.resolve(dir, 'pom.xml'),
      'generate-sources'
    ];

    return exec(getMaven(), params, process.env, { verbose: false, stopOnError: true }, readClassPath);
  }

  readClassPath();
}

program
  .version(version)
  .description('Utility scripts to work with Eclipse Vert.x projects');

program
  .command('postinstall')
  .option('-v, --verbose', 'Verbose logging')
  .description('Generate a pom.xml from the current package.json')
  .action(function (options) {
    // if there is a local template, prefer it over ours...
    let source = __dirname + '/../.pom.xml';

    if (fs.existsSync(path.resolve(dir, '.pom.xml'))) {
      source = path.resolve(dir, '.pom.xml');
    }

    const template = fs.readFileSync(source).toString('UTF-8');

    const dependencies = {};
    const files = npm.files || [];

    // we must include 2 files by default: `package.json` and npm.main
    if (files.indexOf('package.json') === -1) {
      files.push('package.json');
    }
    if (npm.main) {
      if (files.indexOf(npm.main) === -1) {
        files.push(npm.main);
      }
    }

    const find = function (npm, dev) {
      // locate dependencies
      const jsonDependencies = npm[dev ? 'devDependencies' : 'dependencies'] || {};
      for (let dependency in jsonDependencies) {
        if (jsonDependencies.hasOwnProperty(dependency)) {
          // skip if we already visited this dependency
          if (!dependencies[dependency]) {
            const d = path.resolve(dir, 'node_modules/' + dependency);
            if (fs.existsSync(d)) {
              if (!dev) {
                if (files.indexOf('node_modules/' + dependency + '/') === -1) {
                  files.push('node_modules/' + dependency + '/');
                }
              }

              const f = path.resolve(path.resolve(dir, 'node_modules/' + dependency), 'package.json');
              if (fs.existsSync(f)) {
                const json = require(f);
                if (json.maven) {
                  // add this dependency
                  dependencies[dependency] = {
                    groupId: json.maven.groupId,
                    artifactId: json.maven.artifactId,
                    version: json.maven.version,
                    scope: dev ? 'test' : json.maven.scope,
                    classifier: json.maven.classifier
                  };
                  // recurse...
                  find(json, false);
                }
              }
            }
          }
        }
      }
    };

    const toMavenDep = function (npm, key) {
      const jsonDependencies = npm[key] || {};
      // locate dependencies
      for (let dependency in jsonDependencies) {
        if (jsonDependencies.hasOwnProperty(dependency)) {
          const ga = dependency.split(':');
          const vsc = npm[key][dependency].split(':');
          // add this dependency
          dependencies[dependency] = {
            groupId: ga[0],
            artifactId: ga[1],
            version: vsc[0],
            scope: vsc[1],
            classifier: vsc[2]
          };
        }
      }
    };

    // extension to the default package.json to describe pure maven dependencies
    toMavenDep(npm, 'mvnDependencies');
    // standard dependencies
    find(npm, false);
    find(npm, true);

    // mark all files as dirs
    files.forEach(function (el, i) {
      // if marked as dir, adapt to ant pattern
      if (el.charAt(el.length - 1) === '/') {
        files[i] = el + '**.*';
      }
      // remove the "./" if present (maven does not like it)
      if (el.startsWith('./')) {
        files[i] = el.substring(2);
      }
    });

    const data = {
      groupId: npm.groupId || npm.name,
      artifactId: npm.artifactId || npm.name,
      version: npm.version,

      name: npm.name,
      description: npm.description,
      main: npm.main,

      files: files,
      dependencies: Object.values(dependencies),
      packageJson: npm
    };

    try {
      fs.writeFileSync(path.resolve(dir, 'pom.xml'), Mustache.render(template, data));
      // init the maven bits
      exec(getMaven(), [], process.env, {stopOnError: true, verbose: options.verbose});
    } catch (e) {
      console.error(c.red.bold(e));
      process.exit(1);
    }
  });

program
  .command('launcher <cmd> [args...]')
  .description('Runs vertx launcher command (e.g.: run, bare, test, ...)')
  .option('-d, --debug [jdwp]', 'Enable debug mode (default: transport=dt_socket,server=y,suspend=n,address=9229)')
  .option('-i, --inspect [port]', 'Enable chrome devtools debug mode (default: 9229)')
  .option('-s, --suspend', 'While debug/inspect, suspend at start')
  .option('-w, --watch [watch]', 'Watches for modifications on the given expression')
  .option('-v, --verbose', 'Verbose logging')
  .action(function (cmd, args, options) {
    // if it doesn't exist stop
    if (!fs.existsSync(path.resolve(dir, 'pom.xml'))) {
      console.error(c.red.bold('No \'pom.xml\' found, please init it first.'));
      process.exit(1);
    }

    const test = ('test' === cmd);

    if (!args || args.length === 0) {
      // main verticle name is derived from main
      if (!npm.main) {
        console.error(c.red.bold('No \'main\' or \'verticle\' was defined!'));
        process.exit(1);
      }

      if (test) {
        if (npm.main.endsWith('.js')) {
          args = [npm.main.substr(0, npm.main.length - 3) + '.test.js'];
        } else {
          args = [npm.main + '.test.js'];
        }
      } else {
        args = [npm.main];
      }
    }

    // if the file 'target/classpath.txt' doesn't exist then we
    // need to run maven as a prepare step
    generateClassPath(function (classPath) {
      let params = [];

      if (options.debug) {
        if (options.debug === true) {
          console.log(c.yellow.bold('Debug socket listening at port: 9229'));
          if (options.suspend) {
            params.push('-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=9229');
          } else {
            params.push('-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=9229');
          }
        } else {
          console.log(c.yellow.bold('Debug at: ' + options.debug));
          params.push('-agentlib:jdwp=' + options.debug);
        }
      }

      if (options.inspect) {
        if (options.inspect === true) {
          console.log(c.yellow.bold('Chrome devtools listening at port: 9229'));
          params.push('-Dpolyglot.inspect.Suspend=' + !!options.suspend);
          params.push('-Dpolyglot.inspect=9229');
        } else {
          console.log(c.yellow.bold('Chrome devtools listenting at: ' + options.inspect));
          params.push('-Dpolyglot.inspect.Suspend=' + !!options.suspend);
          params.push('-Dpolyglot.inspect=' + options.inspect);
        }
      }

      params.push('-cp', classPath);

      if (options.debug || options.inspect) {
        // in debug delay thread checks
        params.push('-Dvertx.options.blockedThreadCheckInterval=100000')
      }

      params.push('io.vertx.core.Launcher');
      params.push(cmd);

      if (options.watch) {
        params.push('--redeploy=' + options.watch);
        params.push('--on-redeploy=' + getMaven() + ' compile');
        params.push('--launcher-class=io.vertx.core.Launcher');
      }

      params = params.concat(args);

      // run the command
      exec('java', params, process.env, {verbose: true});
    });
  });

program
  .command('shell [args...]')
  .description('Starts a REPL with the current project in the classpath')
  .action(function (args) {

    generateClassPath(function (classPath) {
      let params = [
        '-cp',
        classPath
      ];

      params.push('io.reactiverse.es4x.Shell');

      if (args && Array.isArray(args) && args.length > 0) {
        params = params.concat(args);
      }

      // Releasing stdin
      process.stdin.setRawMode(false);

      spawn('java', params, {stdio: [0, 1, 2]})
        .on("exit", function (code) {
          // Don't forget to switch pseudo terminal on again
          process.stdin.setRawMode(true);
          process.exit(code);
        });
    });
  });

program
  .command('init')
  .description('Installs utility scripts into the current package.json')
  .action(function (options) {

    for (var k in npm.scripts || {}) {
      if (npm.scripts.hasOwnProperty(k)) {
        if (('' + npm.scripts[k]).indexOf('es4x') !== -1) {
          // there is already a reference to es4x, nothing else to do!
          return;
        }
      }
    }

    if (!npm.devDependencies) {
      npm.devDependencies = {};
    }

    npm.devDependencies['es4x-cli'] = version;

    if (!npm.scripts) {
      npm.scripts = {};
    }

    npm.scripts.postinstall = 'es4x postinstall';
    npm.scripts.start = 'es4x launcher run';
    npm.scripts.test = 'es4x launcher test';
    npm.scripts.shell = 'es4x shell';

    try {
      fs.writeFileSync(path.resolve(dir, 'package.json'), JSON.stringify(npm, null, 2));
    } catch (e) {
      console.error(c.red.bold(e));
      process.exit(1);
    }
  });

program
  .command('*')
  .description('Prints this help')
  .action(function(env) {
    program.help();
  });

program.parse(process.argv);

if (program.args.length === 0) {
  program.help();
}
