# Справка по API

Все сгенерированные API связаны с основным vert.x API, потому для лучшего понимания вам следует ознакомиться с
[документацией](https://vertx.io/docs) vert.x и скорректировать данные для импорта/примеры, как описано в пункте
[Функциональная совместимость vert.x](../advanced/vertx#generated-apis).

## Vertx

Основное API Vert.x содержат основу для написания приложений на Vert.x и низкоуровневую поддержку HTTP, TCP, UDP,
файловой системы, асинхронной обработки потоков и многих других составных элементов, а также используется многими
другими компонентами Vert.x.

* [core](/@vertx/core)

## Web

Инструментарий для написания сложных современных web-приложений и HTTP-микросервисов.

* [web](/es4x/@vertx/web)
* [web-api-contract](/es4x/@vertx/web-api-contract)
* [web-client](/es4x/@vertx/web-client)
* [web-common](/es4x/@vertx/web-common)

## Авторизация/Аутентификация

Модули для выполнения аутентификации и/или авторизации.

* [auth-common](/es4x/@vertx/auth-common)
* [auth-digest](/es4x/@vertx/auth-digest)
* [auth-jdbc](/es4x/@vertx/auth-jdbc)
* [auth-jwt](/es4x/@vertx/auth-jwt)
* [auth-mongo](/es4x/@vertx/auth-mongo)
* [auth-oauth2](/es4x/@vertx/auth-oauth2)
* [auth-shiro](/es4x/@vertx/auth-shiro)

## Доступ к данным

Модули для доступа к данным.

* [sql-common](/es4x/@vertx/sql-common)
* [jdbc-client](/es4x/@vertx/jdbc-client)
* [kafka-client](/es4x/@vertx/kafka-client)
* [mongo-client](/es4x/@vertx/mongo-client)
* [redis-client](/es4x/@vertx/redis-client)

## Мониторинг

Модули для взаимодействия с инструментами мониторинга.

* [dropwizard-metrics](/es4x/@vertx/dropwizard-metrics)
* [hawkular-metrics](/es4x/@vertx/hawkular-metrics)

## Микросервисы

Модули, относящиеся к микросервисам.

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

## Мосты EventBus

Модули мостов EventBus.

* [amqp-bridge](/es4x/@vertx/amqp-bridge)
* [bridge-common](/es4x/@vertx/bridge-common)
* [camel-bridge](/es4x/@vertx/camel-bridge)
* [tcp-eventbus-bridge](/es4x/@vertx/tcp-eventbus-bridge)

## Другое

Остальные модули без какой-либо категории.

* [mail-client](/es4x/@vertx/mail-client)
* [shell](/es4x/@vertx/shell)
* [stomp](/es4x/@vertx/stomp)
* [unit](/es4x/@vertx/unit)
* [vertx-mqtt](/es4x/@vertx/vertx-mqtt)
* [rabbitmq-client](/es4x/@vertx/rabbitmq-client)

## Reactiverse

* [reactive-pg-client](/es4x/@reactiverse/reactive-pg-client)
* [elasticsearch-client](/es4x/@reactiverse/elasticsearch-client)
