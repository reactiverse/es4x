---
home: true
heroImage: /hero.png
actionText: 由此开始 →
actionLink: /get-started/
footer: MIT Licensed | Copyright © 2018-present Paulo Lopes
---

<div class="features">
  <div class="feature">
    <h2>精简优先</h2>
    <p>最小化配置基于npm的项目结构，帮助您专注于您的代码。</p>
  </div>
  <div class="feature">
    <h2>Vert.x助力</h2>
    <p>您可享受由<a href="https://vertx.io">Vert.x</a>提供的高性能及可扩展性，在JavaScript中使用Vert.x响应式编程，并使用JavaScript或<a href="https://www.typescriptlang.org/">TypeScript</a>。</p>
  </div>
  <div class="feature">
    <h2>性能表现</h2>
    <p>ES4X基于GraalVM提供的<a href="https://www.techempower.com/benchmarks/#section=data-r18&hw=ph&test=db&l=zik0sf-f">高性能JavaScript</a>应用可匹敌甚至超越<a href="https://www.techempower.com/benchmarks/#section=data-r18&hw=ph&test=db">Java</a>应用。</p>
  </div>
</div>

### 就像1，2，3一样简单

``` bash
# 安装
yarn global add es4x-pm # 或 npm install -g es4x-pm

# 建立项目文件
es4x init

# 创建hello world代码
cat << EOF
vertx.createHttpServer()
  .requestHandler(req => req.response().end('Hello ES4X world!'))
  .listen(8080);
EOF > index.js

# 安装依赖 （npm 及 maven）
yarn # 或 npm install

# 跑起来  
yarn start # 或 npm start
```

::: 警告 兼容性 请注意 ES4X 需要 [GraalVM](https://www.graalvm.org) 或者 Java >= 8.:::
