#!/usr/bin/env node
'use strict';

const fs = require('fs');
const path = require('path');
const spawn = require('child_process').spawn;
const program = require('commander');
const c = require('ansi-colors');
const Mustache = require('mustache');

const version = require('../package.json').version;
const dir = process.cwd();

// quickly abort if there's no package.json
if (!fs.existsSync(path.resolve(dir, 'package.json'))) {
  console.error(c.red.bold('Cannot find `package.json` in current directory!'));
  process.exit(1);
}

const npm = require(path.resolve(dir, 'package.json'));
const isWindows = /^win/.test(process.platform);
const graalVersion = '1.0.0-rc9';
const mvn = __dirname + '/../' + (isWindows ? 'mvnw.cmd' : 'mvnw');

/**
 * Executes an external command.
 *
 * @param {String} command command to execute
 * @param {String[]} args arguments to the command
 * @param {String} cwd CWD
 * @param {Object} env environment variables
 * @param {Object} options verbose logging (log command stdout)
 * @param {Function?} callback callback at command termination
 */
function exec(command, args, cwd, env, options, callback) {
  const proc = spawn(command, args, {env: env, cwd: cwd});
  let out = '';

  if (!options.silent) {
    const idx = command.lastIndexOf('/');
    console.log('Running: ' + c.bold(idx === -1 ? command : (command.substring(idx + 1))) + ' ... ');
  }

  proc.stdout.on('data', function (data) {
    if (options.collect) {
      out += data;
    } else {
      if (options.verbose) {
        process.stdout.write(data);
      }
    }
  });

  proc.stderr.on('data', function (data) {
    if (!options.silent) {
      process.stderr.write(data);
    }
  });

  proc.on('close', function (code) {
    if (code) {
      console.error(c.yellow.bold('Error: ' + command + " exit code " + code + '.' + (options.verbose ? '' : ' Re-run with verbose enabled for more details.')));
      if (options.stopOnError) {
        process.exit(code);
      }
    }
    callback && callback(code, out);
  });

  proc.on('error', function (err) {
    if (err) {
      console.error(c.red.bold(err));
      if (options.stopOnError) {
        process.exit(err);
      }
    }
  });
}

/**
 * Helper to select what from JAVA_HOME/bin
 *
 * @returns {string} the what command
 */
function jdk(what) {
  let bin = what + (isWindows ? '.exe' : '');
  let javaHome = process.env['JAVA_HOME'];

  if (javaHome) {
    let resolved = path.resolve(javaHome, 'bin/' + bin);
    if (fs.existsSync(resolved)) {
      return resolved;
    }
  }

  return bin;
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
      '-DincludeScope=test',
      '-Dmdep.outputFile=target/classpath.txt',
      'dependency:build-classpath'
    ];

    return exec(mvn, params, __dirname + '/..', process.env, {verbose: false, stopOnError: true}, readClassPath);
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
    const template = fs.readFileSync(__dirname + '/../pom.xml').toString('UTF-8');

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
      packageJson: npm,
      graalVersion: npm.graal || graalVersion
    };

    try {
      fs.writeFileSync(path.resolve(dir, 'pom.xml'), Mustache.render(template, data));
      // init the maven bits
      exec(mvn, ['-f', path.resolve(dir, 'pom.xml'), 'clean'], __dirname + '/..', process.env, {
        stopOnError: true,
        verbose: options.verbose
      });
    } catch (e) {
      console.error(c.red.bold(e));
      process.exit(1);
    }
  });

program
  .command('exec <cmd> [args...]')
  .description('Runs vertx launcher command (e.g.: run, bare, test, ...)')
  .option('-d, --debug [jdwp]', 'Enable debug mode (default: transport=dt_socket,server=y,suspend=n,address=9229)')
  .option('-i, --inspect [port]', 'Enable chrome devtools debug mode (default: 9229)')
  .option('-s, --suspend', 'While debug/inspect, suspend at start')
  .option('-w, --watch <watch>', 'Watches for modifications on the given expression')
  .option('-r, --redeploy <redeploy>', 'When watching will run the redeploy action before re-start')
  .option('-v, --verbose', 'Verbose logging')
  .action(function (cmd, args, options) {
    // debug validation and they overlap
    if (options.debug && options.inspect) {
      console.error(c.red.bold('--debug and --inspect options are exclusive (choose one)'));
      process.exit(1);
    }

    switch (cmd) {
      case 'run':
        if (args.length === 0) {
          // main verticle name is derived from main
          if (!npm.main) {
            console.error(c.red.bold('No \'main\' or \'verticle\' was defined!'));
            process.exit(1);
          }
          args = [npm.main];
        }
        break;
      case 'test':
        // main verticle name is derived from main
        if (args.length === 0) {
          if (!npm.main) {
            console.error(c.red.bold('No \'main\' or \'verticle\' was defined!'));
            process.exit(1);
          }
          if (npm.main.endsWith('.js')) {
            args = [npm.main.substr(0, npm.main.length - 3) + '.test.js'];
          } else {
            args = [npm.main + '.test.js'];
          }
        }
        break;
      case 'shell':
        // shell is a virtual command
        cmd = 'run';
        // add to the head
        args.unshift('js:>');
        break;
    }

    // if the file 'target/classpath.txt' doesn't exist then we
    // need to run maven as a prepare step
    generateClassPath(function (classPath) {
      // will install JVMCI compiler if needed
      let jvmci = fs.existsSync(path.resolve(dir, 'target/dist/compiler'));
      let params = [];

      if (jvmci) {
        // enable modules
        params.push('--module-path=target/dist/compiler');
        // enable JVMCI
        params.push('-XX:+UnlockExperimentalVMOptions');
        params.push('-XX:+EnableJVMCI');
        // upgrade graal compiler
        params.push('--upgrade-module-path=target/dist/compiler/compiler.jar');
      }

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
        params.push('--redeploy=' + (options.watch === true ? path.resolve(dir, args[0]) : options.watch));
        if (options.redeploy) {
          params.push('--on-redeploy=' + options.redeploy);
        }
        params.push('--launcher-class=io.vertx.core.Launcher');
      }

      params = params.concat(args);

      // Releasing stdin
      process.stdin.setRawMode(false);

      spawn(jdk('java'), params, {stdio: [0, 1, 2]})
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

    if (!npm.devDependencies) {
      npm.devDependencies = {};
    }

    npm.devDependencies['es4x-cli'] = "^" + version;

    if (!npm.scripts) {
      npm.scripts = {};
    }

    npm.scripts.postinstall = 'es4x postinstall';
    npm.scripts.start = 'es4x exec run';
    npm.scripts.test = 'es4x exec test';
    npm.scripts.package = 'es4x package';

    try {
      fs.writeFileSync(path.resolve(dir, 'package.json'), JSON.stringify(npm, null, 2));
    } catch (e) {
      console.error(c.red.bold(e));
      process.exit(1);
    }
  });

program
  .command('package')
  .description('Packages the application as a runnable jar to "target/dist"')
  .option('-d, --docker [image]', 'Build a Docker image')
  .option('-v, --verbose', 'Verbose logging')
  .action(function (options) {

    // init the maven bits
    let params = [];

    params.push('-f', path.resolve(dir, 'pom.xml'), 'package');

    exec(mvn, params, __dirname + '/..', process.env, {stopOnError: true, verbose: options.verbose}, function (code) {
      if (code !== 0) {
        console.error(c.red.bold('Maven exited with code: ' + code));
        process.exit(1);
      }

      let jvmci = fs.existsSync(path.resolve(dir, 'target/dist/compiler'));

      if (options.docker) {
        // collect the variables
        let params = [
          'build',
          '-f', path.resolve(__dirname, '../Dockerfile' + (jvmci ? '.jvmci' : '')),
          '-t', npm.name + ':' + npm.version,
          '--build-arg', 'JAR=' + (npm.artifactId || npm.name) + '-' + npm.version + '.jar'
        ];

        if (options.docker !== true) {
          params.push('--build-arg', 'BASEIMAGE=' + options.docker);
        }

        // context location
        params.push('.');

        exec('docker', params, dir, process.env, {
          stopOnError: true,
          verbose: options.verbose
        }, function (code) {
          if (code !== 0) {
            console.error(c.red.bold('docker exited with code: ' + code));
            process.exit(1);
          }

          console.log(c.green.bold('Run your application with:'));
          console.log();

          console.log(c.bold('  docker run --rm -it --net=host ' + npm.name + ':' + npm.version));
          console.log();
        });
      } else {
        console.log(c.green.bold('Run your application with:'));
        console.log();

        console.log(c.bold('  ' + jdk('java') + ' \\'));
        if (jvmci) {
          console.log(c.bold('    --module-path=target/dist/compiler \\'));
          console.log(c.bold('    -XX:+UnlockExperimentalVMOptions \\'));
          console.log(c.bold('    -XX:+EnableJVMCI \\'));
          console.log(c.bold('    --upgrade-module-path=target/dist/compiler/compiler.jar \\'));
        }
        console.log(c.bold('    -jar target/dist/' + (npm.artifactId || npm.name) + '-' + npm.version + '.jar'));
        console.log();
      }
    });
  });

program
  .command('*')
  .description('Prints this help')
  .action(function (env) {
    program.help();
  });

program.parse(process.argv);

if (program.args.length === 0) {
  program.help();
}
