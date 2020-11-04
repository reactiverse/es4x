# Referencia API

Todos los APIs generados estan relacionados con los APIs de vert.x principales, asi que para entenderlo mejor
deberias leer los [manuals](https://vertx.io/docs) de vert.x y adaptar el manual imports/examples como se explica en
[vert.x interop](../advanced/vertx#generated-apis).

## Vertx

Los APIs en el nucleo de Vert.x contienen la estructura para escribir aplicaciones Vert.x y suporte de bajo nivel para
HTTP, TCP, UDP, sistema de ficheros, flujos asincronicos y muchos otras piezas fundamentales. Tambies lo utilizan muchos
componentes de Vert.x

* [core](/es4x/@vertx/core)

## Web

Las herramientas para escribir sofisticadas aplicaciones web modernas y microservicios HTTP.

* [web](/es4x/@vertx/web)
* [web-api-contract](/es4x/@vertx/web-api-contract)
* [web-client](/es4x/@vertx/web-client)
* [web-common](/es4x/@vertx/web-common)

## Autenticacion / Autorizacion

Modulos para autenticar y/o autorizar.

* [auth-common](/es4x/@vertx/auth-common)
* [auth-digest](/es4x/@vertx/auth-digest)
* [auth-jdbc](/es4x/@vertx/auth-jdbc)
* [auth-jwt](/es4x/@vertx/auth-jwt)
* [auth-mongo](/es4x/@vertx/auth-mongo)
* [auth-oauth2](/es4x/@vertx/auth-oauth2)
* [auth-shiro](/es4x/@vertx/auth-shiro)

## Acceso de Datos

Modulos para acceder a datos.

* [sql-common](/es4x/@vertx/sql-common)
* [jdbc-client](/es4x/@vertx/jdbc-client)
* [kafka-client](/es4x/@vertx/kafka-client)
* [mongo-client](/es4x/@vertx/mongo-client)
* [redis-client](/es4x/@vertx/redis-client)

## Vigilancia

Modulos para trabajar con herramientas de vigilancia.

* [dropwizard-metrics](/es4x/@vertx/dropwizard-metrics)
* [hawkular-metrics](/es4x/@vertx/hawkular-metrics)

## Microservicios

Modulos relacionados con microservicios.

* [circuit-breaker](/es4x/@vertx/circuit-breaker)
* [cofig-redis](/es4x/@vertx/cofig-redis)
* [config](/es4x/@vertx/config)
* [config-consul](/es4x/@vertx/config-consul)
* [config-git](/es4x/@vertx/config-git)
* [config-hocon](/es4x/@vertx/config-hocon)
* [config-kubernetes-configmap](/es4x/@vertx/config-kubernetes-configmap)
* [config-spring-config-server](/es4x/@vertx/config-spring-config-server)
* [config-vault](/es4x/@vertx/config-vault)
* [config-yaml](/es4x/@vertx/config-yaml)
* [config-zookeeper](/es4x/@vertx/config-zookeeper)
* [consul-client](/es4x/@vertx/consul-client)
* [health-check](/es4x/@vertx/health-check)
* [mysql-postgresql-client](/es4x/@vertx/mysql-postgresql-client)
* [service-discovery](/es4x/@vertx/service-discovery)
* [service-discovery-backend-consul](/es4x/@vertx/service-discovery-backend-consul)
* [service-discovery-backend-redis](/es4x/@vertx/service-discovery-backend-redis)
* [service-discovery-backend-zookeeper](/es4x/@vertx/service-discovery-backend-zookeeper)
* [service-discovery-bridge-consul](/es4x/@vertx/service-discovery-bridge-consul)
* [service-discovery-bridge-docker](/es4x/@vertx/service-discovery-bridge-docker)
* [service-discovery-bridge-docker-links](/es4x/@vertx/service-discovery-bridge-docker-links)
* [service-discovery-bridge-kubernetes](/es4x/@vertx/service-discovery-bridge-kubernetes)
* [service-discovery-bridge-zookeeper](/es4x/@vertx/service-discovery-bridge-zookeeper)

## Puentes EventBus

Modulos puente para EventBus.

* [amqp-bridge](/es4x/@vertx/amqp-bridge)
* [bridge-common](/es4x/@vertx/bridge-common)
* [camel-bridge](/es4x/@vertx/camel-bridge)
* [tcp-eventbus-bridge](/es4x/@vertx/tcp-eventbus-bridge)

## Otros

Otros modulos sin categoria.

* [mail-client](/es4x/@vertx/mail-client)
* [shell](/es4x/@vertx/shell)
* [stomp](/es4x/@vertx/stomp)
* [unit](/es4x/@vertx/unit)
* [vertx-mqtt](/es4x/@vertx/vertx-mqtt)
* [rabbitmq-client](/es4x/@vertx/rabbitmq-client)

## Reactiverse

* [reactive-pg-client](/es4x/@reactiverse/reactive-pg-client)
* [elasticsearch-client](/es4x/@reactiverse/elasticsearch-client)
