#!/usr/bin/env bash
set -e
# build
if [ "$1" = "publish-local" ]; then
  REGISTRY="http://localhost:4873"
  mvn -fae -Pio.vertx,io.reactiverse -Dnpm-registry="$REGISTRY" clean generate-sources install exec:exec@npm-publish
elif [ "$1" = "publish" ]; then
  REGISTRY="https://registry.npmjs.org"

  echo "login as vertx"
  npm adduser --registry "$REGISTRY"
  mvn -fae -Pio.vertx -Dnpm-registry="$REGISTRY" exec:exec@npm-publish

  echo "login as reactiverse"
  npm adduser --registry "$REGISTRY"
  mvn -fae -Pio.reactiverse -Dnpm-registry="$REGISTRY" exec:exec@npm-publish
else
  mvn -fae -Pio.vertx,io.reactiverse clean generate-sources install exec:exec@typedoc
fi
