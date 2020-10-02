---
home: true
heroImage: /hero.png
actionText: Get Started →
actionLink: /get-started/
footer: MIT Licensed | Copyright © 2018-present Paulo Lopes
---

<div class="features">
  <div class="feature">
    <h2>La simplicité d'abord</h2>
    <p>Une configuration minimale avec une structure de projet centrée sur npm vous aide à vous concentrer sur votre code.</p>
  </div>
  <div class="feature">
    <h2>Vert.x-Powered</h2>
    <p>Profitez de l'évolutivité et des performances de <a href="https://vertx.io">Vert.x</a>, utilisez les composants Reactive Vert.x en JavaScript, et développz en JavaScript ou <a href="https://www.typescriptlang.org/">TypeScript</a>.</p>
  </div>
  <div class="feature">
    <h2>Performant</h2>
    <p>ES4X fonctionne au-dessus de GraalVM offrant une <a href="https://www.techempower.com/benchmarks/#section=data-r18&hw=ph&test=db&l=zik0sf-f">grande performance pour les applications JavaScript</a>à par ou <a href="https://www.techempower.com/benchmarks/#section=data-r18&hw=ph&test=db">meilleure que Java</a>.</p>
  </div>
</div>

### Simple comme bonjour

``` bash
# (1) créer le projet
npm init @es4x project

# (2) installer les dépendances
npm install # OR yarn

# (3) lancer le programme
npm start # OR yarn start
```

::: tip NOTE DE COMPATIBILITE  
ES4X nécessite [GraalVM] (https://www.graalvm.org) ou Java >= 8. Si vous n'êtes pas sûr de la version installée sur votre
envisagez d'utiliser [jabba] (https://github.com/shyiko/jabba).
:::
