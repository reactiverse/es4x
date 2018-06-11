#!/usr/bin/env node
'use strict';

const fs = require('fs');
const path = require('path');
const spawn = require('child_process').spawn;
const program = require('commander');
const chalk = require('chalk');
const handlebars = require('handlebars');

const version = require('../package.json').version;
const dir = process.cwd();
const npm = require(dir + '/package.json');

/**
 * Executes an external command.
 *
 * @param command command to execute
 * @param args arguments to the command
 * @param env environment variables
 * @param verbose verbose logging (log command stdout)
 * @param callback callback at command termination
 */
function exec(command, args, env, verbose, callback) {

  const proc = spawn(command, args, {env: env});
  if (args && args.length > 0) {
    const lastArg = args[args.length - 1];
    console.log('Running: ' + chalk.bold(command) + ' ... ' + chalk.bold(lastArg));
  } else {
    console.log('Running: ' + chalk.bold(command));
  }

  proc.stdout.on('data', function (data) {
    if (verbose) {
      process.stdout.write(data);
    }
  });

  proc.stderr.on('data', function (data) {
    process.stderr.write(data);
  });

  proc.on('close', function (code) {
    if (code) {
      console.error(chalk.yellow.bold('Error: ' + command + " exit code " + code + '.' + (verbose ? '' : ' Re-run with verbose enabled for more details.')));
    }
    callback && callback(code);
  });

  proc.on('error', function (err) {
    if (err) {
      console.error(chalk.red.bold(err));
    }
    callback && callback(err);
  });
}

/**
 * Helper to select local maven wrapper or system maven
 *
 * @param dir current working directory
 * @returns {string} the maven command
 */
function getMaven(dir) {
  let mvn = 'mvn';
  let isWin = /^win/.test(process.platform);

  // check for wrapper
  if (isWin) {
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

program
  .version(version)
  .description('Utility scripts to work with Eclipse Vert.x projects');

program
  .command('init')
  .description('Updates the type definitions from the JVM world')
  .action(function () {
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
      for (let dependency in (npm[dev ? 'devDependencies' : 'dependencies'] || {})) {
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
    };

    const toMavenDep = function (npm, key) {
      // locate dependencies
      for (let dependency in (npm[key] || {})) {
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
      dependencies: dependencies,
      packageJson: npm
    };

    try {
      fs.writeFileSync(path.resolve(dir, 'pom.xml'), template(data));
    } catch (e) {
      console.error(chalk.red.bold(e));
      process.exit(1);
    }
  });

program
  .command('launcher <cmd> [args...]')
  .description('Runs vertx launcher command (e.g.: run, bare, test, ...)')
  .option('-c, --clean', 'Perform a clean before running the task')
  .option('-d , --debug [jdwp]', 'Enable debug mode (default: transport=dt_socket,server=y,suspend=n,address=9797)')
  .option('-v, --verbose', 'Verbose logging')
  .action(function (cmd, args, options) {
    // if it doesn't exist stop
    if (!fs.existsSync(path.resolve(dir, 'pom.xml'))) {
      console.error(chalk.red.bold('No \'pom.xml\' found, please init it first.'));
      process.exit(1);
    }

    var test = ('test' === cmd);

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

    const env = Object.create(process.env);

    if (options.debug) {
      if (options.debug === true) {
        console.log(chalk.yellow.bold('Debug socket listening at port: 9797'));
        env['MAVEN_OPTS'] = '-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=9797';
      } else {
        env['MAVEN_OPTS'] = '-agentlib:jdwp=' + options.debug;
      }
    }

    const params = [
      '-f', path.resolve(dir, 'pom.xml')
    ];

    if (options.clean) {
      params.push('clean', 'compile');
    }

    params.push(
      'exec:java',
      '-Dexec.classpathScope=' + (test ? 'test' : 'runtime'),
      '-Dexec.mainClass=io.vertx.core.Launcher',
      '-Dexec.args=' + cmd + ' ' + args.join(' ')
    );

    exec(getMaven(dir), params, env, options.verbose);
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
      getMaven(dir),
      [
        '-f', path.resolve(dir, 'pom.xml'),
        'clean',
        'package'
      ],
      Object.create(process.env),
      options.verbose);
  });

program
  .command('repl')
  .description('Starts a REPL with the current project in the classpath')
  .action(function () {
    var shell;

    // attempt to get graal from the env
    if (!shell && process.env['GRAAL_HOME']) {
      shell = path.resolve(process.env['GRAAL_HOME'], 'bin/node');
      if (!fs.existsSync(shell)) {
        shell = null;
      }
    }
    // attempt to get graal from the env (perhaps it's on the JAVA_HOME ?)
    if (!shell && process.env['JAVA_HOME']) {
      shell = path.resolve(process.env['JAVA_HOME'], 'bin/node');
      if (!fs.existsSync(shell)) {
        shell = null;
      }
    }

    // Releasing stdin
    process.stdin.setRawMode(false);

    // fallback to jjs
    if (!shell) {
      // give some instructions...
      console.log('please load vertx into the shell: ' + chalk.yellow.bold('load(\'classpath:@vertx/core/runtime\');'));

      const jjs = spawn(
        'jjs',
        [
          '-cp', path.resolve(dir, 'target/' + npm.name + '-' + npm.version + '-fat.jar'),
          '--language=es6'
        ],
        {stdio: [0, 1, 2]});

      jjs.on("exit", function (code) {
        // Don't forget to switch pseudo terminal on again
        process.stdin.setRawMode(true);
        process.exit(code);
      });
    } else {
      // give some instructions...
      console.log('please load vertx into the shell: ' + chalk.yellow.bold('require(\'@vertx/core/runtime\');'));

      const node = spawn(
        shell,
        [
          '--polyglot',
          '--jvm.classpath=' + path.resolve(dir, 'target/' + npm.name + '-' + npm.version + '-fat.jar')
        ],
        {stdio: [0, 1, 2]});

      node.on("exit", function (code) {
        // Don't forget to switch pseudo terminal on again
        process.stdin.setRawMode(true);
        process.exit(code);
      });
    }
  });

program.parse(process.argv);

if (program.args.length === 0) {
  program.help();
}
