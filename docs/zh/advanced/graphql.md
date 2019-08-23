# 其他社区项目

## GraphQL

* [vertx-graphql](https://vertx-graphql.github.io/) 用ES4X创建一个GraphQL HTTP服务器,
ES4X是Vert.x的一个快速,无条件,简约的 JavaScript运行时

```js
/// <reference types="@vertx/core/runtime" />
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
    if (listen.failed()) {
      console.log(`🚀 GraphQL ready at http://${host}:${port}${server.graphqlPath}`)
    } else {
      console.log('Failed to bind!');
    }
  });
```
