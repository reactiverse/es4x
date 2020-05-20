#!/usr/bin/env node

const { existsSync } = require('fs');
const path = require('path');
const { spawn } = require('child_process');

/**
 * Parse a String value to a argv format
 * @param {String} value
 * @param {String[]} argv
 */
function parseArgsToArgv(value, argv) {
  // ([^\s'"]([^\s'"]*(['"])([^\3]*?)\3)+[^\s'"]*) Matches nested quotes until the first space outside of quotes
  // [^\s'"]+ or Match if not a space ' or "
  // (['"])([^\5]*?)\5 or Match "quoted text" without quotes
  // `\3` and `\5` are a backreference to the quote style (' or ") captured
  const myRegexp = /([^\s'"]([^\s'"]*(['"])([^\3]*?)\3)+[^\s'"]*)|[^\s'"]+|(['"])([^\5]*?)\5/gi;

  let match = null;

  do {
    // Each call to exec returns the next regex match as an array
    match = myRegexp.exec(value);
    if (match !== null) {
      // Index 1 in the array is the captured group if it exists
      // Index 0 is the matched text, which we use if no captured group exists
      let str = firstString(match[1], match[6], match[0]);
      if (str) {
        argv.push(str);
      }
    }
  } while (match !== null);
}

// Accepts any number of arguments, and returns the first one that is a string
// (even an empty string)
function firstString(...args) {
  for (let i = 0; i < args.length; i++) {
    const arg = args[i];
    if (typeof arg === "string") {
      return arg;
    }
  }
}

let java = 'java';
let skipJvmci = false;

if (process.env['JAVA_HOME']) {
  // Attempt to use JAVA_HOME
  let xjava = path.join(process.env['JAVA_HOME'], 'bin', 'java');
  if (existsSync(xjava)) {
    java = xjava;
    skipJvmci = existsSync(path.join(process.env['JAVA_HOME'], 'bin', 'gu'))
  }
}

let argv = [
  '-XX:+IgnoreUnrecognizedVMOptions'
];

// Use JVMCI if installed and not skipping
if (!skipJvmci) {
  let jvmci = path.join('node_modules', '.jvmci');

  if (existsSync(path.join(process.cwd(), jvmci))) {
    argv.push(`--module-path=${jvmci}`);
    argv.push('-XX:+UnlockExperimentalVMOptions');
    argv.push('-XX:+EnableJVMCI');
    argv.push(`--upgrade-module-path=${path.join(jvmci, 'compiler.jar')}`);
  }
}

// If exists security.policy
// use it's class path (else rely on default runtime)
if (existsSync(path.join(process.cwd(), 'security.policy'))) {
  argv.push('-Djava.security.manager');
  argv.push('-Djava.security.policy=security.policy');
}

if (process.env['JAVA_OPTS']) {
  // Attempt to use JAVA_OPTS
  parseArgsToArgv(process.env['JAVA_OPTS'], argv);
}

argv.push('-cp');

// If exists node_modules/.bin/es4x-launcher.jar
// use it's class path (else rely on default runtime)
let launcher = path.join('node_modules', '.bin', 'es4x-launcher.jar');
let pm = 'es4x-pm-${project.version}.jar';
if (existsSync(path.join(process.cwd(), launcher))) {
  // in the case that there is a launcher we also require a .lib
  let lib = path.join('node_modules', '.lib');
  if (existsSync(path.join(process.cwd(), lib))) {
    argv.push(`${launcher}${path.delimiter}${path.join(__dirname, '..', pm)}`);
    argv.push('io.reactiverse.es4x.ES4X');
  } else {
    // there's no .lib fallback to PM
    argv.push(`${path.join(__dirname, '..', pm)}`);
    argv.push('io.reactiverse.es4x.cli.PM');
  }
} else {
  argv.push(`${path.join(__dirname, '..', pm)}`);
  argv.push('io.reactiverse.es4x.cli.PM');
}

const subProcess = spawn(java, argv.concat(process.argv.slice(2)), { cwd: process.cwd(), env: process.env, stdio: 'inherit' });

subProcess.on('error', (err) => {
  console.error(`es4x-pm ERROR: ${err}`);
  process.exit(1);
});

subProcess.on('close', (code) => {
  if (code !== 0) {
    console.debug(java);
    console.debug(argv.concat(process.argv.slice(2)));

    console.error(`java process exited with code ${code}`);
  }
});
