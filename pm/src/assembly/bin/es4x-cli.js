#!/usr/bin/env node

const { existsSync } = require('fs');
const path = require('path');
const { spawnSync } = require('child_process');

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

let arguments = [
  '-XX:+IgnoreUnrecognizedVMOptions'
];

// Use JVMCI if installed and not skipping
if (!skipJvmci) {
  let jvmci = path.join('node_modules', '.jvmci');

  if (existsSync(path.join(process.cwd(), jvmci))) {
    arguments.push(`--module-path=${jvmci}`);
    arguments.push('-XX:+UnlockExperimentalVMOptions');
    arguments.push('-XX:+EnableJVMCI');
    arguments.push(`--upgrade-module-path=${path.join(jvmci, 'compiler.jar')}`);
  }
}

arguments.push('-cp');

// If exists node_modules/.bin/es4x-launcher.jar
// use it's class path (else rely on default runtime)
let launcher = path.join('node_modules', '.bin', 'es4x-launcher.jar');
let pm = 'es4x-pm-${project.version}.jar';
if (existsSync(path.join(process.cwd(), launcher))) {
  // in the case that there is a launcher we also require a .lib
  let lib = path.join('node_modules', '.lib');
  if (existsSync(path.join(process.cwd(), lib))) {
    arguments.push(`${launcher}${path.delimiter}${path.join(__dirname, '..', pm)}`);
  } else {
    arguments.push(`${launcher}${path.delimiter}${path.join(__dirname, '..', 'runtime', '*')}${path.delimiter}${path.join(__dirname, '..', pm)}`);
  }
} else {
  arguments.push(`${path.join(__dirname, '..', 'runtime', '*')}${path.delimiter}${path.join(__dirname, '..', pm)}`);
}

arguments.push('io.reactiverse.es4x.ES4X');

let result = spawnSync(java, arguments.concat(process.argv.slice(2)), { cwd: process.cwd(), stdio: 'inherit' });

// collect the error if any and log
if (result.error) {
  console.error(`es4x-pm ERROR: ${result.error.message}`);
  process.exit(1);
}
