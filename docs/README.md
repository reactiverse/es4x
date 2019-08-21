---
home: true
heroImage: /hero.png
actionText: Get Started →
actionLink: /get-started/
footer: MIT Licensed | Copyright © 2018-present Paulo Lopes
---

<div class="features">
  <div class="feature">
    <h2>Simplicity First</h2>
    <p>Minimal setup with npm-centered project structure helps you focus on your code.</p>
  </div>
  <div class="feature">
    <h2>Vert.x-Powered</h2>
    <p>Enjoy scalability and performance experience of <a href="https://vertx.io">Vert.x</a>, use Reactive Vert.x components in JavaScript, and develop with JavaScript or <a href="https://www.typescriptlang.org/">TypeScript</a>.</p>
  </div>
  <div class="feature">
    <h2>Performant</h2>
    <p>ES4X runs on top of GraalVM offering a <a href="https://www.techempower.com/benchmarks/#section=data-r18&hw=ph&test=db&l=zik0sf-f">great performance for JavaScript</a> applications on par or <a href="https://www.techempower.com/benchmarks/#section=data-r18&hw=ph&test=db">better than Java</a>.</p>
  </div>
</div>

### As Easy as 1, 2, 3

``` bash
# install
yarn global add es4x-pm # OR npm install -g es4x-pm

# create a project file
es4x init

# create a hello world
cat << EOF
vertx.createHttpServer()
  .requestHandler(req => req.response().end('Hello ES4X world!'))
  .listen(8080);
EOF > index.js

# install dependencies (npm and maven)
yarn # OR npm install

# run it
yarn start # OR npm start
```

::: warning COMPATIBILITY NOTE
ES4X requires [GraalVM](https://www.graalvm.org) or Java >= 8.
:::
