#!/usr/bin/env bash
set -e
# add the adoc2md script to the path
export PATH=$PATH:$(pwd)/../scripts

# build
if [ "$1" = "publish-local" ]; then
  REGISTRY="http://localhost:4873"
  mvn -fae -Pio.vertx,io.reactiverse exec:exec@npm-publish
elif [ "$1" = "publish" ]; then
  REGISTRY="https://registry.npmjs.org"

  echo "login as vertx"
  npm adduser --registry "$REGISTRY"
  mvn -fae -Pio.vertx -Dnpm-registry="$REGISTRY" exec:exec@npm-publish

  echo "login as reactiverse"
  npm adduser --registry "$REGISTRY"
  mvn -fae -Pio.reactiverse -Dnpm-registry="$REGISTRY" exec:exec@npm-publish
else
  mvn -fae -Pio.vertx,io.reactiverse clean generate-sources exec:exec@typedoc exec:exec@adoc2md
fi
