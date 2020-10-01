# API Παραπομπή (Reference)

All generated APIs relate to the core vert.x APIs, so for better understanding one should read the vert.x
[manuals](https://vertx.io/docs) and adapt the manual imports/examples as explained in
[vert.x interop](../advanced/vertx#generated-apis).

Όλα τα παραγόμενα APIs σχετίζονται με τα vert.x APIs, για καλύτερη κατανόηση διάβασε καλύτερα το vert.x [εγχειρίδιο](https://vertx.io/docs)
και προσάρμοσε τα παραδείγματα του όπως βλέπεις εδω [vert.x interop](../advanced/vertx#generated-apis)

## Vertx

The Vert.x core APIs contain the backbone for writing Vert.x applications and low-level support for HTTP, TCP, UDP,
file system, asynchronous streams and many other building blocks. It is also used by many other components of Vert.x.

Τα [βασικά API](/@vertx/core) του Vert.x περιέχουν τη ραχοκοκαλιά για τη σύνταξη εφαρμογών Vert.x και υποστήριξη χαμηλού επιπέδου για HTTP, TCP, UDP,
σύστημα αρχείων, ασύγχρονες ροές και πολλά άλλα δομικά στοιχεία. Χρησιμοποιείται επίσης από πολλά άλλα στοιχεία του Vert.x.

## Web

Η εργαλειοθήκη για σύγχρονη ανάπτυξη web εφαρμογών και HTTP microservices:

* [web](/es4x/@vertx/web)
* [web-api-contract](/es4x/@vertx/web-api-contract)
* [web-client](/es4x/@vertx/web-client)
* [web-common](/es4x/@vertx/web-common)

## Αυθεντικότητα / Εξουσιοδότηση

Βιβλιοθήκες (modules) για Αυθεντικότητα και/ή Εξουσιοδότηση.

* [auth-common](/es4x/@vertx/auth-common)
* [auth-digest](/es4x/@vertx/auth-digest)
* [auth-jdbc](/es4x/@vertx/auth-jdbc)
* [auth-jwt](/es4x/@vertx/auth-jwt)
* [auth-mongo](/es4x/@vertx/auth-mongo)
* [auth-oauth2](/es4x/@vertx/auth-oauth2)
* [auth-shiro](/es4x/@vertx/auth-shiro)

## Πρόσβαση στα δεδομένα

Βιβλιοθήκες (modules) για πρόσβαση σε δεδομένα

* [sql-common](/es4x/@vertx/sql-common)
* [jdbc-client](/es4x/@vertx/jdbc-client)
* [kafka-client](/es4x/@vertx/kafka-client)
* [mongo-client](/es4x/@vertx/mongo-client)
* [redis-client](/es4x/@vertx/redis-client)

## Επίβλεψη (Monitoring)

Βιβλιοθήκες (modules) που συνεργάζονται με κάποια εργαλεία επίβλεψης

* [dropwizard-metrics](/es4x/@vertx/dropwizard-metrics)
* [hawkular-metrics](/es4x/@vertx/hawkular-metrics)

## Microservices

Microservice σχετικές βιβλιοθήκες (modules).

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

## EventBus Bridges

EventBus bridge βιβλιοθήκες.

* [amqp-bridge](/es4x/@vertx/amqp-bridge)
* [bridge-common](/es4x/@vertx/bridge-common)
* [camel-bridge](/es4x/@vertx/camel-bridge)
* [tcp-eventbus-bridge](/es4x/@vertx/tcp-eventbus-bridge)

## Άλλες βιβλιοθήκες

Άλλες βιβλιοθήκες

* [mail-client](/es4x/@vertx/mail-client)
* [shell](/es4x/@vertx/shell)
* [stomp](/es4x/@vertx/stomp)
* [unit](/es4x/@vertx/unit)
* [vertx-mqtt](/es4x/@vertx/vertx-mqtt)
* [rabbitmq-client](/es4x/@vertx/rabbitmq-client)

## Reactiverse

* [reactive-pg-client](/es4x/@reactiverse/reactive-pg-client)
* [elasticsearch-client](/es4x/@reactiverse/elasticsearch-client)
