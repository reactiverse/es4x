---
home: true
heroImage: /hero.png
actionText: Começar →
actionLink: /get-started/
footer: MIT Licensed | Copyright © 2018-presente Paulo Lopes
---

<div class="features">
  <div class="feature">
    <h2>Simplicidade Primeiro</h2>
    <p>Configuração mínima de projectos estruturados em npm ajudam a focar no seu código.</p>
  </div>
  <div class="feature">
    <h2>Vert.x-Powered</h2>
    <p>Disfruta de uma experiência de escalabilidate e performance em <a href="https://vertx.io">Vert.x</a>, usa components reactivos Vert.x em JavaScript e desenvolve com JavaScript ou <a href="https://www.typescriptlang.org/">TypeScript</a>.</p>
  </div>
  <div class="feature">
    <h2>Performance</h2>
    <p>ES4X corre em GraalVM oferecendo <a href="https://www.techempower.com/benchmarks/#section=data-r18&hw=ph&test=db&l=zik0sf-f">uma performance incrível</a>  para applicações JavaScript em par ou <a href="https://www.techempower.com/benchmarks/#section=data-r18&hw=ph&test=db">melhor que Java</a>.</p>
  </div>
</div>

### Simples como 1, 2, 3

``` bash
# instala
yarn global add es4x-pm # OU npm install -g es4x-pm

# cria um projecto
es4x init

# hello world
cat << EOF
vertx.createHttpServer()
  .requestHandler(req => req.response().end('Hello ES4X world!'))
  .listen(8080);
EOF > index.js

# instala dependencias (npm e maven)
yarn # OU npm install

# corre
yarn start # OU npm start
```

::: warning NOTA DE COMPATIBILIDADE
ES4X requer [GraalVM](https://www.graalvm.org) ou Java >= 8.
:::
