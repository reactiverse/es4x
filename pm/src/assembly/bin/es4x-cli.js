#!/usr/bin/env node

const { existsSync } = require('fs');
const path = require('path');
const { spawnSync } = require('child_process');

let java = 'java';

// Attempt to use GRAALVM_HOME
if (existsSync(`${process.env['GRAALVM_HOME']}/bin/java`)) {
  java = `${process.env['GRAALVM_HOME']}/bin/java`;
} else {
  // Attempt to use JAVA_HOME
  if (existsSync(`${process.env['JAVA_HOME']}/bin/java`)) {
    java = `${process.env['JAVA_HOME']}/bin/java`;
  }
}

spawnSync(java, ['-jar', path.join(__dirname, 'es4x-bin.jar')].concat(process.argv.slice(2)), { cwd: process.cwd(), stdio: 'inherit' });
