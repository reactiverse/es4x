#!/usr/bin/env bash
set -e

#mvn clean package
cd target
rm -Rf package || true
mkdir package
VERSION=$(cat classes/META-INF/es4x-commands/VERSIONS.properties | grep es4x | cut -c6-)
tar -zxvf es4x-pm-${VERSION}-bin.tar.gz --strip-components=1 -C package
# exclude the native bin scripts
rm package/bin/es4x || true
rm package/bin/es4x.cmd || true

cd package

REGISTRY="https://registry.npmjs.org"
if [ "$1" = "local" ]; then
  REGISTRY="http://localhost:4873"
else
  echo "login as es4x"
  npm adduser --registry "$REGISTRY"
fi
TAG=${2:-release}

# publish
npm publish --registry $REGISTRY --tag=${TAG}
