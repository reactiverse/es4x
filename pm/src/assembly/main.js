#!/usr/bin/env node

const { existsSync } = require('fs');
const path = require('path');
const { spawn } = require('child_process');

let java = 'java';

if (process.env['JAVA_HOME']) {
  // Attempt to use JAVA_HOME
  let xjava = path.join(process.env['JAVA_HOME'], 'bin', 'java');
  if (existsSync(xjava)) {
    java = xjava;
  }
}

let argv = [
  '-cp',
  path.join(__dirname, 'es4x-pm-${project.version}.jar'),
  'io.reactiverse.es4x.cli.PM',
  'init'
  ];

const subProcess = spawn(java, argv.concat(process.argv.slice(2)), { cwd: process.cwd(), env: process.env, stdio: 'inherit' });

subProcess.on('error', (err) => {
  console.error(`es4x-pm ERROR: ${err}`);
  process.exit(1);
});

subProcess.on('error', (err) => {
  console.error(`es4x-pm ERROR: ${err}`);
  process.exit(1);
});

subProcess.on('close', process.exit);
