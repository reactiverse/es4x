Vert.x Web GraphQL extends Vert.x Web with the
[GraphQL-Java](https://www.graphql-java.com) library so that you can
build a GraphQL server.

> **Tip**
>
> This is the reference documentation for Vert.x Web GraphQL. It is
> highly recommended to get familiar with the GraphQL-Java API first.
> You may start by reading the [GraphQL-Java
> documentation](https://www.graphql-java.com/documentation/${graphql.java.doc.version}/).

> **Warning**
>
> This module has *Tech Preview* status, this means the API can change
> between versions.

# Getting started

To use this module, add the following to the *dependencies* section of
your Maven POM file:

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-web-graphql</artifactId>
 <version>${maven.version}</version>
</dependency>
```

Or, if you use Gradle:

``` groovy
compile 'io.vertx:vertx-web-graphql:${maven.version}'
```

# Handler setup

Create a Vert.x Web `Route` and a `GraphQLHandler` for it:

``` js
import { GraphQLHandler } from "@vertx/web-graphql"
let graphQL = setupGraphQLJava();

router.route("/graphql").handler(GraphQLHandler.create(graphQL));
```

The handler serves both `GET` and `POST` requests. However you can
restrict the service to one type of HTTP method:

``` js
import { GraphQLHandler } from "@vertx/web-graphql"
let graphQL = setupGraphQLJava();

router.post("/graphql").handler(GraphQLHandler.create(graphQL));
```

> **Tip**
>
> The `GraphQLHandler` does not require a `BodyHandler` to read `POST`
> requests content.

## GraphiQL client

As you are building your application, testing your GraphQL queries in
[GraphiQL](https://github.com/graphql/graphiql) can be handy.

To do so, create a route for GraphiQL resources and a `GraphiQLHandler`
for them:

``` js
import { GraphiQLHandler } from "@vertx/web-graphql"
let options = new GraphiQLHandlerOptions()
  .setEnabled(true);

router.route("/graphiql/*").handler(GraphiQLHandler.create(options));
```

Then browse to <http://localhost:8080/graphiql/>.

> **Note**
>
> The GraphiQL user interface is disabled by default for security
> reasons. This is why you must configure the `GraphiQLHandlerOptions`
> to enable it.

> **Tip**
>
> GraphiQL is enabled automatically when Vert.x Web runs in development
> mode. To switch the development mode on, use the
> `VERTXWEB_ENVIRONMENT` environment variable or `vertxweb.environment`
> system property and set it to `dev`. In this case, create the
> `GraphiQLHandler` without changing the `enabled` property.

If your application is protected by authentication, you can customize
the headers sent by GraphiQL dynamically:

``` js
import { MultiMap } from "@vertx/core"
graphiQLHandler.graphiQLRequestHeaders((rc) => {
  let token = rc.get("token");
  return MultiMap.caseInsensitiveMultiMap().add(Java.type("io.vertx.core.http.HttpHeaders").AUTHORIZATION, "Bearer " + token)
});

router.route("/graphiql/*").handler(graphiQLHandler);
```

Please refer to the `GraphiQLHandlerOptions` documentation for further
details.

## Enable query batching

Query batching consists in posting an array instead of a single object
to the GraphQL endpoint.

Vert.x Web GraphQL can handle such requests but by default the feature
is disabled. To enable it, create the `GraphQLHandler` with options:

``` js
import { GraphQLHandler } from "@vertx/web-graphql"
let options = new GraphQLHandlerOptions()
  .setRequestBatchingEnabled(true);

let handler = GraphQLHandler.create(graphQL, options);
```

# Building a GraphQL server

The GraphQL-Java API is very well suited for the asynchronous world: the
asynchronous execution strategy is the default for queries (serial
asynchronous for mutations).

To [avoid blocking the event
loop](https://vertx.io/docs/vertx-core/java/#golden_rule), all you have
to do is implement [data
fetchers](https://www.graphql-java.com/documentation/${graphql.java.doc.version}/data-fetching/)
that return a `CompletionStage` instead of the result directly.

``` js
let dataFetcher = (environment) => {

  let completableFuture = new (Java.type("java.util.concurrent.CompletableFuture"))();

  retrieveLinksFromBackend(environment, (ar) => {
    if (ar.succeeded()) {
      completableFuture.complete(ar.result());
    } else {
      completableFuture.completeExceptionally(ar.cause());
    }
  });

  return completableFuture
};

let runtimeWiring = Java.type("graphql.schema.idl.RuntimeWiring").newRuntimeWiring().type("Query", (builder) => {
  builder.dataFetcher("allLinks", dataFetcher);
}).build();
```

## Fetching data with callback-based APIs

Implementing a data fetcher that returns a `CompletionStage` is not a
complex task. But when you work with Vert.x callback-based APIs, it
requires a bit of boilerplate.

This is where the `VertxDataFetcher` can help:

``` js
let dataFetcher = new (Java.type("io.vertx.ext.web.handler.graphql.VertxDataFetcher"))((environment, future) => {

  retrieveLinksFromBackend(environment, future);

});

let runtimeWiring = Java.type("graphql.schema.idl.RuntimeWiring").newRuntimeWiring().type("Query", (builder) => {
  builder.dataFetcher("allLinks", dataFetcher);
}).build();
```

## Providing data fetchers with some context

Very often, the `GraphQLHandler` will be declared after other route
handlers. For example, you could protect your application with
authentication.

In this case, it is likely that your data fetchers will need to know
which user is logged-in to narrow down the results. Let’s say your
authentication layer stores a `User` object in the `RoutingContext`.

You may retrieve this object by inspecting the
`DataFetchingEnvironment`:

``` js
let dataFetcher = new (Java.type("io.vertx.ext.web.handler.graphql.VertxDataFetcher"))((environment, future) => {

  let routingContext = environment.getContext();

  let user = routingContext.get("user");

  retrieveLinksPostedBy(user, future);

});
```

> **Note**
>
> The routing context is available with any kind of data fetchers, not
> just `VertxDataFetcher`.

If you prefer not to expose the routing context to your data fetchers,
configure the GraphQL handler to customize the context object:

``` js
import { GraphQLHandler } from "@vertx/web-graphql"
let dataFetcher = new (Java.type("io.vertx.ext.web.handler.graphql.VertxDataFetcher"))((environment, future) => {

  // User as custom context object
  let user = environment.getContext();

  retrieveLinksPostedBy(user, future);

});

let graphQL = setupGraphQLJava(dataFetcher);

// Customize the query context object when setting up the handler
let handler = GraphQLHandler.create(graphQL).queryContext((routingContext) => {

  return routingContext.get("user")

});

router.route("/graphql").handler(handler);
```

## JSON data results

The default GraphQL data fetcher is `PropertyDataFetcher`. As a
consequence, it will be able to read the fields of your domain objects
without further configuration.

Nevertheless, some Vert.x data clients return `JsonArray` and
`JsonObject` results.

If you don’t need (or don’t wish to) use a domain object layer, you can
configure GraphQL-Java to use `VertxPropertyDataFetcher` instead:

``` js
let builder = Java.type("graphql.schema.idl.RuntimeWiring").newRuntimeWiring();

builder.wiringFactory(new (Java.type("graphql.schema.idl.WiringFactory"))());
```

> **Tip**
>
> `VertxPropertyDataFetcher` wraps a `PropertyDataFetcher` so you can
> still use it with domain objects.

## Using dataloaders

Dataloaders help you to load data efficiently by batching fetch requests
and caching results.

First, create a batch loader:

``` js
let linksBatchLoader = (keys, environment) => {

  return retrieveLinksFromBackend(keys, environment)

};
```

> **Tip**
>
> If you work with Vert.x callback-based APIs, you may use a
> `VertxBatchLoader` or a `VertxMappedBatchLoader` to simplify your
> code.

Then, configure the `GraphQLHandler` to create a `DataLoaderRegistry`
for each request:

``` js
Code not translatable
```

## Apollo WebSocketLink

You can use an [Apollo
WebSocketLink](https://www.apollographql.com/docs/link/links/ws/) which
connects over a websocket. This is specially useful if you want to add
subscriptions to your GraphQL schema, but you can also use the websocket
for queries and mutations.

``` js
import { ApolloWSHandler } from "@vertx/web-graphql"
let graphQL = setupGraphQLJava();

router.route("/graphql").handler(ApolloWSHandler.create(graphQL));
```

> **Important**
>
> To support the `graphql-ws` websocket subprotocol, it has to be added
> to the server configuration:

``` js
let httpServerOptions = new HttpServerOptions()
  .setWebSocketSubProtocols(["graphql-ws"]);
vertx.createHttpServer(httpServerOptions).requestHandler(router).listen(8080);
```

> **Note**
>
> If you want to support a WebSocketLink and a HttpLink in the same
> path, you can add the ApolloWSHandler in first place and then the
> GraphQLHandler.

``` js
import { ApolloWSHandler } from "@vertx/web-graphql"
import { GraphQLHandler } from "@vertx/web-graphql"
let graphQL = setupGraphQLJava();

router.route("/graphql").handler(ApolloWSHandler.create(graphQL));
router.route("/graphql").handler(GraphQLHandler.create(graphQL));
```

Here you can find how to configure the Apollo SubscriptionClient:
<https://github.com/apollographql/subscriptions-transport-ws>

> **Important**
>
> A subscription `DataFetcher` has to return a
> `org.reactivestreams.Publisher` instance.
