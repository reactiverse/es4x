#!/usr/bin/env bash
set -e
#mvn clean package
cd target
mkdir package
VERSION=$(cat classes/META-INF/es4x-commands/VERSIONS.properties | grep es4x | cut -c6-)
tar -zxvf es4x-pm-${VERSION}-bin.tar.gz --strip-components=1 -C package

sed -i "s/%%VERSION%%/${VERSION}/g" package/bin/es4x-cli.js

echo "
{
  \"name\": \"es4x-pm\",
  \"version\": \"${VERSION}\",
  \"description\": \"ES4X Project Manager Utilities\",
  \"scripts\": {
    \"test\": \"echo \\\"Error: no test specified\\\" && exit 1\"
  },
  \"author\": \"Paulo Lopes <pmlopes@gmail.com>\",
  \"license\": \"MIT\",
  \"bin\": {
    \"es4x\": \"./bin/es4x-cli.js\"
  }
}
" > package/package.json

cd package

REGISTRY="https://registry.npmjs.org"

if [ "$1" = "local" ]; then
  REGISTRY="http://localhost:4873"
fi

# publish
#npm publish --registry $REGISTRY
