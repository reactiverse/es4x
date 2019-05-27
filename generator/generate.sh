#!/usr/bin/env bash
#set -e

REGISTRY="https://registry.npmjs.org"

if [ "$1" = "local" ]; then
  REGISTRY="http://localhost:4873"
fi

function vertx_io () {
  modules=(
    "io.vertx/vertx-core"
    "io.vertx/vertx-amqp-bridge"
    #"io.vertx/vertx-amqp-service"
    "io.vertx/vertx-auth-common"
    "io.vertx/vertx-auth-htdigest"
    "io.vertx/vertx-auth-htpasswd"
    "io.vertx/vertx-auth-jdbc"
    "io.vertx/vertx-auth-jwt"
    "io.vertx/vertx-auth-mongo"
    "io.vertx/vertx-auth-oauth2"
    "io.vertx/vertx-auth-shiro"
    "io.vertx/vertx-bridge-common"
    "io.vertx/vertx-cassandra-client"
#    "io.vertx/vertx-camel-bridge"
    "io.vertx/vertx-circuit-breaker"
    "io.vertx/vertx-config"
    "io.vertx/vertx-config-consul"
    "io.vertx/vertx-config-git"
    "io.vertx/vertx-config-hocon"
    "io.vertx/vertx-config-kubernetes-configmap"
    "io.vertx/vertx-config-redis"
    "io.vertx/vertx-config-spring-config-server"
    "io.vertx/vertx-config-vault"
    "io.vertx/vertx-config-yaml"
    "io.vertx/vertx-config-zookeeper"
    "io.vertx/vertx-consul-client"
    #"io.vertx/vertx-consul-service"
    "io.vertx/vertx-dropwizard-metrics"
    #"io.vertx/vertx-grpc"
#    "io.vertx/vertx-hawkular-metrics"
    #"io.vertx/vertx-hazelcast"
    "io.vertx/vertx-health-check"
    #"io.vertx/vertx-http-service-factory"
    #"io.vertx/vertx-maven-service-factory"
    #"io.vertx/vertx-ignite"
    #"io.vertx/vertx-infinispan"
    #"io.vertx/vertx-jca"
    "io.vertx/vertx-jdbc-client"
    #"io.vertx/vertx-jgroups"
    #"io.vertx/vertx-junit5"
    "io.vertx/vertx-jwt"
    "io.vertx/vertx-kafka-client"
    "io.vertx/vertx-mail-client"
    #"io.vertx/vertx-mail-service"
    "io.vertx/vertx-micrometer-metrics"
    "io.vertx/vertx-mongo-client"
    #"io.vertx/vertx-mongo-service"
    "io.vertx/vertx-mqtt"
    "io.vertx/vertx-mysql-postgresql-client"
    #"io.vertx/vertx-proton"
    "io.vertx/vertx-rabbitmq-client"
    #"io.vertx/vertx-reactive-streams"
    #"io.vertx/vertx-rx-js"
    "io.vertx/vertx-redis-client"
    "io.vertx/vertx-service-discovery"
    "io.vertx/vertx-service-discovery-backend-consul"
    "io.vertx/vertx-service-discovery-backend-redis"
    "io.vertx/vertx-service-discovery-backend-zookeeper"
    "io.vertx/vertx-service-discovery-bridge-consul"
    "io.vertx/vertx-service-discovery-bridge-docker"
    "io.vertx/vertx-service-discovery-bridge-docker-links"
    "io.vertx/vertx-service-discovery-bridge-kubernetes"
    "io.vertx/vertx-service-discovery-bridge-zookeeper"
    #"io.vertx/vertx-service-proxy"
    "io.vertx/vertx-shell"
    #"io.vertx/vertx-sockjs-service-proxy"
    "io.vertx/vertx-sql-common"
    "io.vertx/vertx-stomp"
    #"io.vertx/vertx-sync"
    "io.vertx/vertx-tcp-eventbus-bridge"
    "io.vertx/vertx-unit"
    "io.vertx/vertx-web"
    "io.vertx/vertx-web-api-contract"
    "io.vertx/vertx-web-client"
    "io.vertx/vertx-web-common"
    "io.vertx/vertx-web-graphql"
    "io.vertx/vertx-web-templ-freemarker"
    "io.vertx/vertx-web-templ-handlebars"
    "io.vertx/vertx-web-templ-jade"
    "io.vertx/vertx-web-templ-mvel"
    "io.vertx/vertx-web-templ-pebble"
    "io.vertx/vertx-web-templ-rocker"
    "io.vertx/vertx-web-templ-thymeleaf"
    #"io.vertx/vertx-zookeeper"
  )

  if [ ! "$1" = "local" ]; then
    # force login
    echo "<Login as vertx>"
    npm adduser --registry $REGISTRY
  fi

  # generate sources and publish
  for i in "${modules[@]}"
  do
    # touch the 3 required files
    mkdir -p ./$i/npm
    touch ./$i/npm/enums.d.ts
    touch ./$i/npm/options.d.ts
    touch ./$i/npm/index.d.ts
    # build
    if [ "$1" = "local" ]; then
      mvn -f ./$i/pom.xml -Dnpm-registry="$REGISTRY" clean generate-sources exec:exec@typedoc exec:exec@npm-publish
    else
      mvn -f ./$i/pom.xml -Dnpm-registry="$REGISTRY" exec:exec@npm-publish
    fi

  done
}

function reactiverse_io () {
  modules=(
    "io.reactiverse/reactive-pg-client"
    "io.reactiverse/elasticsearch-client"
  )

  if [ ! "$1" = "local" ]; then
    # force login
    echo "<Login as reactiverse>"
    npm adduser --registry $REGISTRY
  fi

  # generate sources and publish
  for i in "${modules[@]}"
  do
    # touch the 3 required files
    mkdir -p ./$i/npm
    touch ./$i/npm/enums.d.ts
    touch ./$i/npm/options.d.ts
    touch ./$i/npm/index.d.ts
    # build
    if [ "$1" = "local" ]; then
      mvn -f ./$i/pom.xml -Dnpm-registry="$REGISTRY" clean generate-sources exec:exec@typedoc exec:exec@npm-publish
    else
      mvn -f ./$i/pom.xml -Dnpm-registry="$REGISTRY" exec:exec@npm-publish
    fi
  done
}

vertx_io $1
reactiverse_io $1
