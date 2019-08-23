# 测试

编写测试用例时, 我们可以使用vert.x提供的测试框架
[vert.x unit](https://github.com/vert-x3/vertx-unit)。安装非常简单:

```bash
yarn add -D @vertx/unit # OR npm install @vertx/unit --save-dev

# ensure es4x fetches the non npm dependencies
yarn # OR npm install
```

## 编写测试用例

测试用例和其它任何`JavaScript`代码没有什么区别。 通常约定使用`.test.js`为后缀的文件来存放测试使用的脚本代码。

`vert.x unit`框架使用`suites`来组织你的测试脚本, 同时需要使用一个`main
suite`作为启动测试的入口。就像这样:

```js
import { TestSuite } from '@vertx/unit';

const suite = TestSuite.create("the_test_suite");

suite.test("my_test_case", function (context) {
  var s = "value";
  context.assertEquals("value", s);
});

suite.run();
```


## 运行

```bash
> npm test
```

这行命令将会在JVM上运行你的应用, 来替换默认的`npm`操作

```bash
Running: java ... 
Begin test suite the_test_suite
Begin test my_test_case
Passed my_test_case
End test suite the_test_suite , run: 1, Failures: 0, Errors: 0
```

::: 注意 使用`npm`/`yarn`运行脚本前,
需要显示地在`package.json`中引入`test`脚本文件. 就像这样:

```json{4}
{
   ...
  "scripts" : {
    "test" : "es4x-launcher test index.test.js",
    ...
}
```
:::
