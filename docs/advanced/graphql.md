# Community Projects

## GraphQL

* [vertx-graphql](https://vertx-graphql.github.io/) Create a GraphQL HTTP server with ES4X, a fast, unopinionated,
  minimalist JavaScript runtime for Vert.x. 

```js
/// <reference types="es4x" />
// @ts-check
import { Router } from '@vertx/web';
import { GraphQLServer } from 'vertx-graphql';

const typeDefs = `
  type Query {
    hello: String
  }
`;

const resolvers = {
  Query: {
    hello: () => 'Hello vertx-graphql!'
  }
};

const server = new GraphQLServer({
  typeDefs,
  resolvers,
  context: {}
});

const app = Router.router(vertx);

server.applyMiddleware({
  app,
  path: '/graphql'
});

const port = 9100;
const host = '0.0.0.0';

vertx
  .createHttpServer()
  .requestHandler(app)
  .listen(port, host, listen => {
    if (listen.succeeded()) {
      console.log(`🚀 GraphQL ready at http://${host}:${port}${server.graphqlPath}`)
    } else {
      console.log('Failed to bind!');
    }
  });
```
