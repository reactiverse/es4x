#!/usr/bin/env node
const path = require('path');
const fs = require('fs-extra');
const argv = require('minimist')(process.argv.slice(2));

async function init() {
  const targetDir = argv._[0] || '.';
  const cwd = process.cwd();
  const root = path.join(cwd, targetDir);
  const renameFiles = {
    _gitignore: '.gitignore'
  };

  console.log(`Scaffolding project in ${root}...`);

  await fs.ensureDir(root);
  const existing = await fs.readdir(root);
  if (existing.length) {
    console.error(`Error: target directory is not empty.`);
    process.exit(1);
  }

  const templateDir = path.join(
    __dirname,
    `template-${argv.t || argv.template || 'js'}`
  );

  const write = async (file, content) => {
    const targetPath = renameFiles[file]
      ? path.join(root, renameFiles[file])
      : path.join(root, file);
    if (content) {
      await fs.writeFile(targetPath, content);
    } else {
      await fs.copy(path.join(templateDir, file), targetPath);
    }
  }

  const files = await fs.readdir(templateDir);
  for (const file of files.filter(f => f !== 'package.json')) {
    await write(file);
  }

  const pkg = require(path.join(templateDir, `package.json`))
  pkg.name = path.basename(root);
  await write('package.json', JSON.stringify(pkg, null, 2));

  console.log(`\nDone. Now run:\n`);
  if (root !== cwd) {
    console.log(`  cd ${path.relative(cwd, root)}`);
  }
  console.log(`  npm install (or \`yarn\`)`);
  console.log(`  npm start (or \`yarn start\`)`);
  console.log();
}

init().catch((e) => {
  console.error(e);
})
