#!/usr/bin/env bash
mvn clean package
cd target
mkdir package
VERSION=$(cat classes/META-INF/es4x-commands/VERSIONS.properties | grep es4x | cut -c6-)
tar -zxvf es4x-pm-${VERSION}-bin.tar.gz --strip-components=1 -C package

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
    \"es4x\": \"./bin/es4x\"
  }
}
" > package/package.json

cd package
npm publish --registry http://localhost:4873
