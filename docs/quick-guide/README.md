# Quick Guides

In this area you will find some _byte_ size examples that can help you get quickly started with **ES4X**.

## Core

### Vert.x Core

The Vert.x core APIs contain the backbone for writing Vert.x applications and low-level support for HTTP, TCP, UDP, file system, asynchronous streams and many other building blocks. It is also used by many other components of Vert.x.

[Core Examples](./core)

## Web

### Vert.x Web

A tool-kit for writing sophisticated modern web applications and HTTP microservices.

### Web Client

An easy-to-use client for HTTP and HTTP/2 with many advanced features.

### Web Validation

A library to declaratively parse and validate incoming Vert.x Web HTTP requests.

### Web OpenAPI

Extends Vert.x Web to support OpenAPI 3, bringing a simple interface for building web routers that conform to OpenAPI contracts.

### Web API Service (Technical Preview)

Directly connect your OpenAPI 3 web routers to event-bus services.

### GraphQL (Technical Preview)

Implement GraphQL servers with Vert.x web.

## Clustering

### Hazelcast Clustering

Cluster manager implementation that uses Hazelcast.

### Infinispan Clustering

Cluster manager implementation that uses Infinispan.

### Apache Ignite Clustering

Cluster manager implementation that uses Apache Ignite.

## Testing
### Vert.x JUnit 5

Asynchronous testing with Vert.x and JUnit 5.

### Vert.x Unit

A unit testing tool-kit designed to work with asynchronous code. Includes JUnit 4 support.

## Standards
### JSON Schema

An extensible implementation of the Json Schema specification to validate every JSON data structure, asynchronously.

## Authentication and authorization
### Auth common

Common APIs for authentication and authorization for your Vert.x applications, backed by several providers.

### Oauth2 Auth

OAuth2 (and to some extent OpenID Connect) implementation.

### JWT Auth

JSON web tokens (JWT) implementation.

### Webauthn Auth (Technical Preview)

FIDO2 WebAuthn (password-less) implementation.

### SQL Client Auth

Authentication and authorization support based on the Vert.x SQL client and a relational database.

### MongoDB Auth

Authentication and authorization support based on MongoDB.

### Properties Auth

Authentication and authorization support based on Java properties files.

### LDAP Auth

Implementation using JDK built-in LDAP capabilities.

### .htpasswd Auth

Authentication and authorization support based on .htpasswd files.

### .htdigest Auth

Authentication and authorization support based on .htdigest files.

## Databases
### PostgreSQL Client (Technical Preview)

A PostgreSQL client focusing on scalability and low overhead.

### MySQL Client (Technical Preview)

A lightweight, event-driven client for MySQL.

### SQL Client Templates

A small library designed to facilitate the execution and data manipulation of SQL queries.

### MongoDB Client

MongoDB client.

### Redis Client

Redis client.

### Cassandra Client

Apache Cassandra client.

### JDBC client

JDBC support for Vert.x.

## Messaging
### Kafka client

A client for Apache Kafka.

### AMQP Client (Technical Preview)

A client for AMQP 1.0 brokers and routers.

### RabbitMQ Client

A client for RabbitMQ brokers.

### MQTT

A client and server for MQTT, compliant with MQTT 3.1.1.

## Integration
### Mail Client

A SMTP client to send emails from your applications.

### STOMP

A client and server implementation of the STOMP protocol.

### Consul client

A client for Consul.

## Event bus bridges
### TCP Eventbus Bridge

An event-bus bridge that lets you interact with Vert.x from any application over a simple TCP-based protocol.

### Camel Bridge

An event-bus bridge that lets you interact with Apache Camel endpoints and routes.

## Monitoring
### Zipkin

Distributed tracing with Zipkin.

### OpenTracing

Distributed tracing with OpenTracing.

### Metrics using Dropwizard

Captures metrics from Vert.x core components and exposes them using Dropwizard.

### Metrics using Micrometer

Captures metrics from Vert.x core components and exposes them using Micrometer.

### Health Check

A simple API to expose health checks over HTTP.

## Services
### gRPC

Implement gRPC clients and servers with Vert.x.

### Service Proxies

Proxies allow remote event bus services to be called as if they were local.

### SockJS Service Proxies

Allow event bus services to be called from JavaScript (web browser or Node.js).

## Microservices
### Service Discovery

Publish, lookup and bind any type of service.

### Config

An extensible way to configure Vert.x applications.

### Circuit Breaker

Implementation of the circuit-breaker pattern to mitigate failures.
