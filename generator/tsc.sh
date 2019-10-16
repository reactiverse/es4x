#!/usr/bin/env bash
set -e

REGISTRY="https://registry.npmjs.org"

if [ "$1" = "local" ]; then
  REGISTRY="http://localhost:4873"
fi

# install all dependencies
mvn -fae -Pio.vertx,io.reactiverse -Dnpm-registry="$REGISTRY" exec:exec@npm-install
# run tsc
tsc &> tsc.log
