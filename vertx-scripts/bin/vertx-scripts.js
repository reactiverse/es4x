#!/usr/bin/env node
'use strict';

const fs = require('fs');
const path = require('path');
const spawn = require('child_process').spawn;
const program = require('commander');
const chalk = require('chalk');
const handlebars = require('handlebars');
const mkdirp = require('mkdirp');

const version = require('../package.json').version;
const dir = process.cwd();
const isWindows = /^win/.test(process.platform);

// there are 2 options, either a local package.json exists
// and we're working in npm mode, no package.json then we're
// in the global install utility mode
let npm;

if (fs.existsSync(path.resolve(dir, 'package.json'))) {
  npm = require(path.resolve(dir, 'package.json'));
} else {
  npm = {
    name: path.basename(dir),
    version: "0.0.1-SNAPSHOT",
    devDependencies: {
      "vertx-scripts": version
    },
    mvnDependencies: {
      "io.vertx:vertx-core": "[3.5.2,)"
    }
  };
}

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
    console.log('Running: ' + chalk.bold(command) + ' ... ' + chalk.bold(lastArg));
  } else {
    console.log('Running: ' + chalk.bold(command));
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
      console.error(chalk.yellow.bold('Error: ' + command + " exit code " + code + '.' + (options.verbose ? '' : ' Re-run with verbose enabled for more details.')));
      if (options.stopOnError) {
        process.exit(code);
      }
    }
    callback && callback(code);
  });

  proc.on('error', function (err) {
    if (err) {
      console.error(chalk.red.bold(err));
      if (options.stopOnError) {
        process.exit(err);
      }
    }
    callback && callback(err);
  });
}

/**
 * Helper to select local maven wrapper or system maven
 *
 * @returns {string} the maven command
 */
function getMaven() {
  let mvn = 'mvn';

  // check for wrapper
  if (isWindows) {
    if (fs.existsSync(path.resolve(dir, 'mvnw.bat'))) {
      mvn = path.resolve(dir, 'mvnw.bat');
    }
  } else {
    if (fs.existsSync(path.resolve(dir, 'mvnw'))) {
      mvn = path.resolve(dir, 'mvnw');
    }
  }

  return mvn;
}

function generateClassPath(force, callback) {
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

  if (force || !fs.existsSync(path.resolve(dir, 'target/classpath.txt'))) {
    let params = [
      '-f', path.resolve(dir, 'pom.xml')
    ];

    if (options.clean) {
      params.push('clean');
    }

    params.push('compile');

    return exec(getMaven(), params, process.env, { verbose: false, stopOnError: true }, readClassPath);
  }

  readClassPath();
}

program
  .version(version)
  .description('Utility scripts to work with Eclipse Vert.x projects');

program
  .command('init')
  .option('-b, --bare [main]', 'bare pom.xml without ES4X and using the given main verticle')
  .option('-v, --verbose', 'Verbose logging')
  .description('Generate a pom.xml')
  .action(function (options) {
    // if there is a local template, prefer it over ours...
    let source = __dirname + '/../.pom.xml';

    if (fs.existsSync(path.resolve(dir, '.pom.xml'))) {
      source = path.resolve(dir, '.pom.xml');
    }

    const template = handlebars.compile(fs.readFileSync(source).toString('UTF-8'));

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

    let mainClass = 'io.vertx.core.Launcher';

    if (options.bare && options.bare !== true) {
      mainClass = options.bare;
    }

    const data = {
      bare: options.bare,
      mainClass: mainClass,
      groupId: npm.groupId || npm.name,
      artifactId: npm.artifactId || npm.name,
      version: npm.version,

      name: npm.name,
      description: npm.description,
      main: npm.main,

      files: files,
      dependencies: dependencies,
      packageJson: npm
    };

    try {
      fs.writeFileSync(path.resolve(dir, 'pom.xml'), template(data));
      // init the maven bits
      exec(getMaven(), [], process.env, {stopOnError: true, verbose: options.verbose});
    } catch (e) {
      console.error(chalk.red.bold(e));
      process.exit(1);
    }
  });

program
  .command('launcher <cmd> [args...]')
  .description('Runs vertx launcher command (e.g.: run, bare, test, ...)')
  .option('-c, --clean', 'Perform a clean before running the task')
  .option('-d , --debug [jdwp]', 'Enable debug mode (default: transport=dt_socket,server=y,suspend=n,address=9229)')
  .option('-i , --inspect [port]', 'Enable chrome devtools debug mode (default: 9229)')
  .option('-w , --watch [watch]', 'Watches for modifications on the given expression')
  .option('-v, --verbose', 'Verbose logging')
  .action(function (cmd, args, options) {
    // if it doesn't exist stop
    if (!fs.existsSync(path.resolve(dir, 'pom.xml'))) {
      console.error(chalk.red.bold('No \'pom.xml\' found, please init it first.'));
      process.exit(1);
    }

    const test = ('test' === cmd);

    if (!args || args.length === 0) {
      // main verticle name is derived from main
      if (!npm.main) {
        console.error(chalk.red.bold('No \'main\' or \'verticle\' was defined!'));
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

    // if the clean is active or the file 'target/classpath.txt' doesn't exist then we
    // need to run maven as a prepare step
    generateClassPath(options.clean, function (classPath) {
      let params = [
        '-cp',
        classPath
      ];

      if (options.debug) {
        if (options.debug === true) {
          console.log(chalk.yellow.bold('Debug socket listening at port: 9229'));
          params.push('-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=9229');
        } else {
          console.log(chalk.yellow.bold('Debug at: ' + options.debug));
          params.push('-agentlib:jdwp=' + options.debug);
        }
      }

      if (options.inspect) {
        if (options.inspect === true) {
          console.log(chalk.yellow.bold('Chrome devtools listening at port: 9229'));
          params.push('-Dpolyglot.inspect=9292');
          params.push('-Dpolyglot.inspect.Suspend=false');
        } else {
          console.log(chalk.yellow.bold('Chrome devtools listenting at: ' + options.inspect));
          params.push('-Dpolyglot.inspect=' + options.inspect);
          params.push('-Dpolyglot.inspect.Suspend=false');
        }
      }

      params.push('io.vertx.core.Launcher');
      params.push(cmd);

      if (options.watch) {
        params.push('--redeploy=' + options.watch);
        params.push('--on-redeploy=' + getMaven() + ' package');
        params.push('--launcher-class=io.vertx.core.Launcher');
      }

      params = params.concat(args);

      // run the command
      exec('java', params, process.env, {verbose: true});
    });
  });

program
  .command('package')
  .description('Packages the application')
  .option('-v, --verbose', 'Verbose logging')
  .action(function (options) {
    // if it doesn't exist stop
    if (!fs.existsSync(path.resolve(dir, 'pom.xml'))) {
      console.error(chalk.red.bold('No \'pom.xml\' found, please init it first.'));
      process.exit(1);
    }

    exec(
      getMaven(),
      [
        '-f', path.resolve(dir, 'pom.xml'),
        'clean',
        'package'
      ],
      process.env,
      { verbose: options.verbose});
  });

program
  .command('repl')
  .option('-c, --clean', 'Perform a clean before running the task')
  .option('-g, --graal', 'Run the builtin Graal Shell')
  .description('Starts a REPL with the current project in the classpath')
  .action(function (options) {

    let shell = options.graal ? 'java' : 'jjs';

    generateClassPath(options.clean, function (classPath) {
      let params = [
        '-cp',
        classPath
      ];

      if (shell === 'jjs') {
        params.push('--language=es6');
        // give some instructions...
        console.log('please load vertx into the shell: ' + chalk.yellow.bold('load(\'classpath:vertx.js\');'));
      } else {
        params.push('io.reactiverse.es4x.GraalShell');
      }

      // Releasing stdin
      process.stdin.setRawMode(false);

      spawn(shell, params, {stdio: [0, 1, 2]})
        .on("exit", function (code) {
          // Don't forget to switch pseudo terminal on again
          process.stdin.setRawMode(true);
          process.exit(code);
        });
    });
  });

program
  .command('native-image')
  .option('-c, --clean', 'Perform a clean before running the task')
  .option('-r, --resources [resources]', 'RexEx specifying the resources to be included [default: (static|webroot|template)/.*]')
  .option('-v, --verbose', 'Verbose logging')
  .description('Build a native image for the current pom.xml project')
  .action(function (options) {

    // if it doesn't exist stop
    if (!fs.existsSync(path.resolve(dir, 'pom.xml'))) {
      console.error(chalk.red.bold('No \'pom.xml\' found, please init it first.'));
      process.exit(1);
    }

    // if there is a local template, prefer it over ours...
    let source = __dirname + '/../.substitutions.java';

    if (fs.existsSync(path.resolve(dir, '.substitutions.java'))) {
      source = path.resolve(dir, '.substitutions.java');
    }

    mkdirp(path.resolve(dir, 'src/main/java'), function (err) {
      if (err) {
        console.error(chalk.red.bold('Could not create \'src/main/java\'.'));
        process.exit(1);
      }

      try {
        fs.writeFileSync(path.resolve(dir, 'src/main/java/substitutions.java'), fs.readFileSync(source));
        // package the maven bits
        let args = [
          '-Pnative-image',
          '-f',
          path.resolve(dir, 'pom.xml')
        ];

        if (options.clean) {
          args.push('clean');
        }

        args.push('package');

        // first step is to build the fat jar
        exec(getMaven(), args, process.env, {stopOnError: true, verbose: options.verbose}, function () {

          let resources = '(static|webroot|template)/.*';

          if (options.resources) {
            if (options.resources !== true) {
              resources = options.resources;
            }
          }

          let args = [
            '--no-server',
            '-H:IncludeResources=' + resources,
            '-H:+ReportUnsupportedElementsAtRuntime',
            '-jar',
            'target/' + npm.name + '-' + npm.version + '-fat.jar'
          ];

          // second step run native image command
          exec('native-image', args, process.env, {stopOnError: true, verbose: options.verbose});
        });
      } catch (e) {
        console.error(chalk.red.bold(e));
        process.exit(1);
      }
    });
  });

program.parse(process.argv);

if (program.args.length === 0) {
  program.help();
}
