#!/usr/bin/env bash
set -e
# build
if [ "$1" = "publish-local" ]; then
  REGISTRY="http://localhost:4873"
  TAG=${2:-release}
  npm adduser --registry "$REGISTRY"
  mvn -fae -Pio.vertx,io.reactiverse -Dnpm-registry="$REGISTRY" -Dnpm-tag=${TAG} clean generate-sources install exec:exec@npm-publish | tee build-$1.log
elif [ "$1" = "publish" ]; then
  REGISTRY="https://registry.npmjs.org"
  TAG=${2:-release}

  echo "login as vertx"
  npm adduser --registry "$REGISTRY"
  mvn -fae -Pio.vertx -Dnpm-registry="$REGISTRY" -Dnpm-tag=${TAG} exec:exec@npm-publish | tee build-$1.log

  echo "login as reactiverse"
  npm adduser --registry "$REGISTRY"
  mvn -fae -Pio.reactiverse -Dnpm-registry="$REGISTRY" -Dnpm-tag=${TAG} exec:exec@npm-publish | tee build-$1-reactiverse.log
elif [ "$1" = "tsdocs" ]; then
  mvn -fae -Pio.vertx,io.reactiverse clean generate-sources install exec:exec@typedoc | tee build-$1.log
else
  mvn -fae -Pio.vertx,io.reactiverse clean generate-sources install | tee build.log
fi
