#!/usr/bin/env node

const {existsSync} = require('fs');
const path = require('path');
const {spawn, spawnSync} = require('child_process');

let java = 'java';
let pm = 'es4x-pm-${project.version}.jar';

if (process.env['JAVA_HOME']) {
  // Attempt to use JAVA_HOME
  let xjava = path.join(process.env['JAVA_HOME'], 'bin', 'java');
  if (existsSync(xjava)) {
    java = xjava;
  }
}

let prefix;
let binDir;

if (existsSync(path.join(process.cwd(), 'package.json'))) {
  prefix = "node_modules";
  binDir = path.join(prefix, ".bin");
} else {
  prefix = "";
  binDir = "bin";
}

if (!existsSync(path.join(process.cwd(), binDir, 'es4x-launcher.jar'))) {
  // classpath is incomplete we need to run the PM package
  let statusCode =
    spawnSync(
      java,
      ['-Dsilent-install', '-jar', `${path.join(__dirname, '..', pm)}`].concat(process.argv.slice(2)),
      {cwd: process.cwd(), env: process.env, stdio: 'inherit'}).status;

  if (statusCode !== 0) {
    process.exit(statusCode);
  }
}

let argv = [
  '-XX:+IgnoreUnrecognizedVMOptions'
];

let jvmci = path.join(prefix, '.jvmci');
if (existsSync(path.join(process.cwd(), jvmci))) {
  argv.push(`--module-path=${jvmci}`);
  argv.push('-XX:+UnlockExperimentalVMOptions');
  argv.push('-XX:+EnableJVMCI');
  argv.push(`--upgrade-module-path=${path.join(jvmci, 'compiler.jar')}`);
}

// If exists security.policy
if (existsSync(path.join(process.cwd(), 'security.policy'))) {
  argv.push('-Djava.security.manager');
  argv.push('-Djava.security.policy=security.policy');
}

// If exists logging.properties
if (existsSync(path.join(process.cwd(), 'logging.properties'))) {
  argv.push('-Djava.util.logging.config.file=logging.properties');
}

if (!process.stdout.isTTY) {
  argv.push('-Dbare=true');
}

argv.push('-cp');

let launcher = path.join(binDir, 'es4x-launcher.jar');
if (existsSync(path.join(process.cwd(), launcher))) {
  argv.push(`${launcher}${path.delimiter}${path.join(__dirname, '..', pm)}`);
  argv.push('io.reactiverse.es4x.ES4X');

  const subProcess = spawn(java, argv.concat(process.argv.slice(2)), {
    cwd: process.cwd(),
    env: process.env,
    stdio: 'inherit'
  });

  subProcess.on('error', (err) => {
    console.error(`es4x ERROR: ${err}`);
    process.exit(3);
  });

  subProcess.on('close', process.exit);
} else {
  console.error(`Missing ${launcher}`);
  process.exit(3);
}
