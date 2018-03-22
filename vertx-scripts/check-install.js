const fs = require('fs');
const path = require('path');
const chalk = require('chalk');

// depending on the NPM version the project dir is either
// available as a variable or must be computed from this file location
// assuming it is not installed globally
const projectDir = process.env.INIT_CWD || path.resolve("../../", __dirname);

if (!fs.existsSync(path.resolve(projectDir, 'package.json'))) {
  // cannot say anything as package.json was not found...
  return;
}

var npm = require(path.resolve(projectDir, 'package.json'));

for (var k in npm.scripts || {}) {
  if (npm.scripts.hasOwnProperty(k)) {
    if (('' + npm.scripts[k]).indexOf('vertx-scripts') !== -1) {
      // there is already a reference to vertx-scripts, nothing else to do!
      return;
    }
  }
}

console.log(chalk.yellow.bold('Please add the following scripts to your \'package.json\':'));
console.log("\"scripts\": " + JSON.stringify({
  "postinstall": "vertx-scripts init",
  "test": "vertx-scripts launcher test",
  "start": "vertx-scripts launcher run",
  "package": "vertx-scripts package"
}, null, 2));
