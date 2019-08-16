#!/usr/bin/env node

const { existsSync } = require('fs');
const path = require('path');
const { spawnSync } = require('child_process');

const VERSION='%%VERSION%%';

let java = 'java';

if (process.env['GRAALVM_HOME']) {
// Attempt to use GRAALVM_HOME
  let xjava = path.join(process.env['GRAALVM_HOME'], 'bin', 'java');
  if (existsSync(xjava)) {
    java = xjava;
  } else {
    if (process.env['JAVA_HOME']) {
      // Attempt to use JAVA_HOME
      let xjava = path.join(process.env['JAVA_HOME'], 'bin', 'java');
      if (existsSync(xjava)) {
        java = xjava;
      }
    }
  }
}

let arguments = [
  '-XX:+IgnoreUnrecognizedVMOptions'
];

// Use JVMCI if installed
let jvmci = path.join('node_modules', '.jvmci');

if (existsSync(path.join(process.cwd(), jvmci))) {
  arguments.push(`--module-path=${jvmci}`);
  arguments.push('-XX:+UnlockExperimentalVMOptions');
  arguments.push('-XX:+EnableJVMCI');
  arguments.push(`--upgrade-module-path=${path.join(jvmci, 'compiler.jar')}`);
}

arguments.push('-cp');

// If exists node_modules/.bin/es4x-launcher.jar
// use it's class path (else rely on default runtime)
let launcher = path.join('node_modules', '.bin', 'es4x-launcher.jar');
let pm = `es4x-pm-${VERSION}.jar`;
if (existsSync(path.join(process.cwd(), launcher))) {
  arguments.push(`${launcher}${path.delimiter}${path.join(__dirname, '..', pm)}`);
} else {
  arguments.push(`${path.join(__dirname, '..', 'runtime', '*')}${path.delimiter}${path.join(__dirname, '..', pm)}`);
}

arguments.push('io.reactiverse.es4x.ES4X');

spawnSync(java, arguments.concat(process.argv.slice(2)), { cwd: process.cwd(), stdio: 'inherit' });
