In order to test code a test framework should be added to the project, since vert.x provides
[vert.x unit](https://github.com/vert-x3/vertx-unit), we can add it easily with:

```bash
npm install @vertx/unit --save-dev
npm install
```

## Writing test

Writing tests should follow the same rules as any other JavaScript code, a common convention is to use the suffix
`.test.js` to test the code from the base script.

When working with `vert.x unit`, tests should be organized in `suites` and a main suite should be used to start the
testing process. For example:

```js
import { TestSuite } from '@vertx/unit';

const suite = TestSuite.create("the_test_suite");

suite.test("my_test_case", function (context) {
  var s = "value";
  context.assertEquals("value", s);
});

suite.run();
```


## Running

```bash
> npm test
```

This command replaces the default `npm` operation by running the application on the JVM runtime.

```bash
Running: java ... 
Begin test suite the_test_suite
Begin test my_test_case
Passed my_test_case
End test suite the_test_suite , run: 1, Failures: 0, Errors: 0
```
