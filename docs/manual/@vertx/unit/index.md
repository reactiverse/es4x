Asynchronous polyglot unit testing.

# Introduction

Vertx Unit is designed for writing asynchronous unit tests with a
polyglot API and running these tests in the JVM. Vertx Unit Api borrows
from existing test frameworks like [JUnit](http://junit.org) or
[QUnit](http://qunitjs.com) and follows the Vert.x practices.

As a consequence Vertx Unit is the natural choice for testing Vert.x
applications.

To use vert.x unit, add the following dependency to the *dependencies*
section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-unit</artifactId>
 <version>${maven.version}</version>
 <scope>test</scope>
</dependency>
```

  - Gradle (in your `build.gradle` file):

<!-- end list -->

``` groovy
testCompile ${io.vertx}:${vertx-unit}:${maven.version}
```

Vert.x unit can be used in different ways and run anywhere your code
runs, it is just a matter of reporting the results the right way, this
example shows the bare minimum test suite:

``` js
import { TestSuite } from "@vertx/unit"
let suite = TestSuite.create("the_test_suite");
suite.test("my_test_case", (context) => {
  let s = "value";
  context.assertEquals("value", s);
});
suite.run();
```

The `run` method will execute the suite and go through all the tests of
the suite. The suite can fail or pass, this does not matter if the outer
world is not aware of the test result.

``` js
import { TestSuite } from "@vertx/unit"
let suite = TestSuite.create("the_test_suite");
suite.test("my_test_case", (context) => {
  let s = "value";
  context.assertEquals("value", s);
});
suite.run(new TestOptions()
  .setReporters([new ReportOptions()
    .setTo("console")]));
```

When executed, the test suite now reports to the console the steps of
the test suite:

    Begin test suite the_test_suite
    Begin test my_test
    Passed my_test
    End test suite the_test_suite , run: 1, Failures: 0, Errors: 0

The `reporters` option configures the reporters used by the suite runner
for reporting the execution of the tests, see the
[Reporting](#reporting) section for more info.

# Writing a test suite

A test suite is a named collection of test case, a test case is a
straight callback to execute. The suite can have lifecycle callbacks to
execute *before* and/or *after* the test cases or the test suite that
are used for initializing or disposing services used by the test suite.

``` js
import { TestSuite } from "@vertx/unit"
let suite = TestSuite.create("the_test_suite");
suite.test("my_test_case_1", (context) => {
  // Test 1
});
suite.test("my_test_case_2", (context) => {
  // Test 2
});
suite.test("my_test_case_3", (context) => {
  // Test 3
});
```

The API is fluent and therefore the test cases can be chained:

``` js
import { TestSuite } from "@vertx/unit"
let suite = TestSuite.create("the_test_suite");
suite.test("my_test_case_1", (context) => {
  // Test 1
}).test("my_test_case_2", (context) => {
  // Test 2
}).test("my_test_case_3", (context) => {
  // Test 3
});
```

The test cases declaration order is not guaranteed, so test cases should
not rely on the execution of another test case to run. Such practice is
considered as a bad one.

Vertx Unit provides *before* and *after* callbacks for doing global
setup or cleanup:

``` js
import { TestSuite } from "@vertx/unit"
let suite = TestSuite.create("the_test_suite");
suite.before((context) => {
  // Test suite setup
}).test("my_test_case_1", (context) => {
  // Test 1
}).test("my_test_case_2", (context) => {
  // Test 2
}).test("my_test_case_3", (context) => {
  // Test 3
}).after((context) => {
  // Test suite cleanup
});
```

The declaration order of the method does not matter, the example
declares the *before* callback before the test cases and *after*
callback after the test cases but it could be anywhere, as long as it is
done before running the test suite.

The *before* callback is executed before any tests, when it fails, the
test suite execution will stop and the failure is reported. The *after*
callback is the last callback executed by the testsuite, unless the
*before* callback reporter a failure.

Likewise, Vertx Unit provides the *beforeEach* and *afterEach* callback
that do the same but are executed for each test case:

``` js
import { TestSuite } from "@vertx/unit"
let suite = TestSuite.create("the_test_suite");
suite.beforeEach((context) => {
  // Test case setup
}).test("my_test_case_1", (context) => {
  // Test 1
}).test("my_test_case_2", (context) => {
  // Test 2
}).test("my_test_case_3", (context) => {
  // Test 3
}).afterEach((context) => {
  // Test case cleanup
});
```

The *beforeEach* callback is executed before each test case, when it
fails, the test case is not executed and the failure is reported. The
*afterEach* callback is the executed just after the test case callback,
unless the *beforeEach* callback reported a failure.

# Asserting

Vertx Unit provides the `TestContext` object for doing assertions in
test cases. The *context* object provides the usual methods when dealing
with assertions.

## assertEquals

Assert two objects are equals, works for *basic* types or *json* types.

``` js
suite.test("my_test_case", (context) => {
  context.assertEquals(10, callbackCount);
});
```

There is also an overloaded version for providing a message:

``` js
suite.test("my_test_case", (context) => {
  context.assertEquals(10, callbackCount, "Should have been 10 instead of " + callbackCount);
});
```

Usually each assertion provides an overloaded version.

## assertNotEquals

The counter part of *assertEquals*.

``` js
suite.test("my_test_case", (context) => {
  context.assertNotEquals(10, callbackCount);
});
```

## assertNull

Assert an object is null, works for *basic* types or *json* types.

``` js
suite.test("my_test_case", (context) => {
  context.assertNull(null);
});
```

## assertNotNull

The counter part of *assertNull*.

``` js
suite.test("my_test_case", (context) => {
  context.assertNotNull("not null!");
});
```

## assertInRange

The `assertInRange` targets real numbers.

    suite.test("my_test_case", (context) => {
    
      // Assert that 0.1 is equals to 0.2 +/- 0.5
    
      context.assertInRange(0.1, 0.2, 0.5);
    });

## assertTrue and assertFalse

Asserts the value of a boolean expression.

``` js
suite.test("my_test_case", (context) => {
  context.assertTrue(var);
  context.assertFalse(value > 10);
});
```

## Failing

Last but not least, *test* provides a *fail* method that will throw an
assertion error:

``` js
suite.test("my_test_case", (context) => {
  context.fail("That should never happen");
  // Following statements won't be executed
});
```

The failure can either be a *string* as seen previously or an *error*.
The *error* object depends on the target language, for Java or Groovy it
can be any class extending *Throwable- , for JavaScript it is an
\_error*, for Ruby it is an *Exception*.

## Using third-party assertion framework

It is also possible to use any other assertion framework, like the
popular *hamcrest* and *assertj*. The recommended way to go is to use
`verify` and perform the assertions within the supplied *Handler*. This
way, asynchronous testing termination will be correctly handled.

``` js
suite.test("my_test_case", (context) => {
  context.verify((v) => {
    // Using here Assert from junit, could be assertj, hamcrest or any other
    // Even manually throwing an AssertionError.
    Java.type("org.junit.Assert").assertNotNull("not null!");
    Java.type("org.junit.Assert").assertEquals(10, callbackCount);
  });
});
```

# Asynchronous testing

The previous examples supposed that test cases were terminated after
their respective callbacks, this is the default behavior of a test case
callback. Often it is desirable to terminate the test after the test
case callback, for instance:

**The Async object asynchronously completes the test case.**

``` js
suite.test("my_test_case", (context) => {
  let async = context.async();
  eventBus.consumer("the-address", (msg) => {
    
    async.complete();
  });
  
});
```

  - The callback exits but the test case is not terminated

  - The event callback from the bus terminates the test

Creating an `Async` object with the `async` method marks the executed
test case as non terminated. The test case terminates when the
`complete` method is invoked.

> **Note**
> 
> When the `complete` callback is not invoked, the test case fails after
> a certain timeout.

Several `Async` objects can be created during the same test case, all of
them must be *completed* to terminate the test.

**Several Async objects provide coordination.**

``` js
suite.test("my_test_case", (context) => {

  let async1 = context.async();
  let client = vertx.createHttpClient();
  let req = client.get(8080, "localhost", "/");
  req.exceptionHandler((err) => {
    context.fail(err.getMessage());
  });
  req.handler((resp) => {
    context.assertEquals(200, resp.statusCode());
    async1.complete();
  });
  req.end();

  let async2 = context.async();
  vertx.eventBus().consumer("the-address", (msg) => {
    async2.complete();
  });
});
```

Async objects can also be used in *before* or *after* callbacks, it can
be very convenient in a *before* callback to implement a setup that
depends on one or several asynchronous results:

**Async starts an http server before test cases.**

``` js
suite.before((context) => {
  let async = context.async();
  let server = vertx.createHttpServer();
  server.requestHandler(requestHandler);
  server.listen(8080, (ar, ar_err) => {
    context.assertTrue(ar.succeeded());
    async.complete();
  });
});
```

It is possible to wait until the completion of a specific `Async`,
similar to Java’s count-down latch:

**Wait for completion.**

``` js
let async = context.async();
let server = vertx.createHttpServer();
server.requestHandler(requestHandler);
server.listen(8080, (ar, ar_err) => {
  context.assertTrue(ar.succeeded());
  async.complete();
});

// Wait until completion
async.awaitSuccess();

// Do something else
```

> **Warning**
> 
> this should not be executed from the event loop\!

Async can also be created with an initial count value, it completes when
the count-down reaches zero using `countDown`:

**Wait until the complete count-down reaches zero.**

``` js
let async = context.async(2);
let server = vertx.createHttpServer();
server.requestHandler(requestHandler);
server.listen(8080, (ar, ar_err) => {
  context.assertTrue(ar.succeeded());
  async.countDown();
});

vertx.setTimer(1000, (id) => {
  async.complete();
});

// Wait until completion of the timer and the http request
async.awaitSuccess();

// Do something else
```

Calling `complete()` on an async completes the async as usual, it
actually sets the value to `0`.

# Asynchronous assertions

`TestContext` provides useful methods that provides powerful constructs
for async testing:

The `asyncAssertSuccess` method returns an {@literal
Handler\<AsyncResult\<T\>\>} instance that acts like `Async`, resolving
the `Async` on success and failing the test on failure with the failure
cause.

``` java
Async async = context.async();
vertx.deployVerticle("my.verticle", ar -> {
  if (ar.succeeded()) {
    async.complete();
  } else {
    context.fail(ar.cause());
  }
});

// Can be replaced by

vertx.deployVerticle("my.verticle", context.asyncAssertSuccess());
```

The `asyncAssertSuccess` method returns an {@literal
Handler\<AsyncResult\<T\>\>} instance that acts like `Async`, invoking
the delegating {@literal Handler\<T\>} on success and failing the test
on failure with the failure cause.

``` java
AtomicBoolean started = new AtomicBoolean();
Async async = context.async();
vertx.deployVerticle(new AbstractVerticle() {
  public void start() throws Exception {
    started.set(true);
  }
}, ar -> {
  if (ar.succeeded()) {
    context.assertTrue(started.get());
    async.complete();
  } else {
    context.fail(ar.cause());
  }
});

// Can be replaced by

vertx.deployVerticle("my.verticle", context.asyncAssertSuccess(id -> {
  context.assertTrue(started.get());
}));
```

The async is completed when the `Handler` exits, unless new asyncs were
created during the invocation, which can be handy to *chain*
asynchronous behaviors:

``` java
Async async = context.async();
vertx.deployVerticle("my.verticle", ar1 -> {
  if (ar1.succeeded()) {
    vertx.deployVerticle("my.otherverticle", ar2 -> {
      if (ar2.succeeded()) {
        async.complete();
      } else {
        context.fail(ar2.cause());
      }
    });
  } else {
    context.fail(ar1.cause());
  }
});

// Can be replaced by

vertx.deployVerticle("my.verticle", context.asyncAssertSuccess(id ->
        vertx.deployVerticle("my_otherverticle", context.asyncAssertSuccess())
));
```

The `asyncAssertFailure` method returns an {@literal
Handler\<AsyncResult\<T\>\>} instance that acts like `Async`, resolving
the `Async` on failure and failing the test on success.

``` java
Async async = context.async();
vertx.deployVerticle("my.verticle", ar -> {
  if (ar.succeeded()) {
    context.fail();
  } else {
    async.complete();
  }
});

// Can be replaced by

vertx.deployVerticle("my.verticle", context.asyncAssertFailure());
```

The `asyncAssertFailure` method returns an {@literal
Handler\<AsyncResult\<T\>\>} instance that acts like `Async`, invoking
the delegating {@literal Handler\<Throwable\>} on failure and failing
the test on success.

``` java
Async async = context.async();
vertx.deployVerticle("my.verticle", ar -> {
  if (ar.succeeded()) {
    context.fail();
  } else {
    context.assertTrue(ar.cause() instanceof IllegalArgumentException);
    async.complete();
  }
});

// Can be replaced by

vertx.deployVerticle("my.verticle", context.asyncAssertFailure(cause -> {
  context.assertTrue(cause instanceof IllegalArgumentException);
}));
```

The async is completed when the `Handler` exits, unless new asyncs were
created during the invocation.

# Repeating test

When a test fails randomly or not often, for instance a race condition,
it is convenient to run the same test multiple times to increase the
failure likelihood of the test.

**Repeating a test.**

``` js
import { TestSuite } from "@vertx/unit"
TestSuite.create("my_suite").test("my_test", 1000, (context) => {
  // This will be executed 1000 times
});
```

When declared, *beforeEach* and *afterEach* callbacks will be executed
as many times as the test is executed.

> **Note**
> 
> test repetition are executed sequentially

# Sharing objects

The `TestContext` has `get`/`put`/`remove` operations for sharing state
between callbacks.

Any object added during the *before* callback is available in any other
callbacks. Each test case will operate on a copy of the shared state, so
updates will only be visible for a test case.

**Sharing state between callbacks.**

``` js
import { TestSuite } from "@vertx/unit"
TestSuite.create("my_suite").before((context) => {

  // host is available for all test cases
  context.put("host", "localhost");

}).beforeEach((context) => {

  // Generate a random port for each test
  let port = helper.randomPort();

  // Get host
  let host = context.get("host");

  // Setup server
  let async = context.async();
  let server = vertx.createHttpServer();
  server.requestHandler((req) => {
    req.response().setStatusCode(200).end();
  });
  server.listen(port, host, (ar, ar_err) => {
    context.assertTrue(ar.succeeded());
    context.put("port", port);
    async.complete();
  });

}).test("my_test", (context) => {

  // Get the shared state
  let port = context.get("port");
  let host = context.get("host");

  // Do request
  let client = vertx.createHttpClient();
  let req = client.get(port, host, "/resource");
  let async = context.async();
  req.handler((resp) => {
    context.assertEquals(200, resp.statusCode());
    async.complete();
  });
  req.end();
});
```

> **Warning**
> 
> sharing any object is only supported in Java, other languages can
> share only basic or json types. Other objects should be shared using
> the features of that language.

# Running

When a test suite is created, it won’t be executed until the `run`
method is called.

**Running a test suite.**

``` js
suite.run();
```

The test suite can also be run with a specified `Vertx` instance:

**Provides a Vertx instance to run the test suite.**

``` js
suite.run(vertx);
```

When running with a `Vertx` instance, the test suite is executed using
the Vertx event loop, see the [Event loop](#event_loop) section for more
details.

A test suite can be run with the Vert.x Command Line Interface with the
`vertx test` command:

**Running a test suite with the Vert.x CLI.**

    > vertx test the_test_suite.js
    Begin test suite the_test_suite
    Succeeded in deploying verticle
    Begin test my_test_case
    Passed my_test_case
    End test suite my_suite , run: 1, Failures: 0, Errors: 0

Such test suite just need to be executed via the `run` command, the
`vertx test` command takes care of configuring reporting, timeout,
etc…​, pretty much like in this example:

``` js
import { TestSuite } from "@vertx/unit"
let suite = TestSuite.create("the_test_suite");
suite.test("my_test_case", (context) => {
  let s = "value";
  context.assertEquals("value", s);
});
suite.run();
```

The `vertx test` command extends the `vertx run` command. The exit
behavior of the JVM is changed the JVM exits when the test suite is
executed and a return value is provided indicating the tests success (0)
or failure (1).

> **Note**
> 
> several test suites can executed in the same verticle, Vert.x Unit
> waits until completion of all suite executed.

## Test suite completion

No assumptions can be made about when the test suite will be completed,
and if some code needs to be executed after the test suite, it should
either be in the test suite *after* callback or as callback of the
`Completion`:

**Test suite execution callback.**

``` js
let completion = suite.run(vertx);

// Simple completion callback
completion.handler((ar, ar_err) => {
  if (ar.succeeded()) {
    console.log("Test suite passed!");
  } else {
    console.log("Test suite failed:");
    ar.cause().printStackTrace();
  }
});
```

The `Completion` object provides also a `resolve` method that takes a
`Promise` object, this `Promise` will be notified of the test suite
execution:

**Resolving the start Promise with the test suite.**

``` js
let completion = suite.run();

// When the suite completes, the promise is resolved
completion.resolve(startPromise);
```

This allow to easily create a *test* verticle whose deployment is the
test suite execution, allowing the code that deploys it to be easily
aware of the success or failure.

The completion object can also be used like a latch to block until the
test suite completes. This should be used when the thread running the
test suite is not the same than the current thread:

**Blocking until the test suite completes.**

``` js
let completion = suite.run();

// Wait until the test suite completes
completion.await();
```

The `await` throws an exception when the thread is interrupted or a
timeout is fired.

The `awaitSuccess` is a variation that throws an exception when the test
suite fails.

**Blocking until the test suite succeeds.**

``` js
let completion = suite.run();

// Wait until the test suite succeeds otherwise throw an exception
completion.awaitSuccess();
```

## Time out

Each test case of a test suite must execute before a certain timeout is
reached. The default timeout is of *2 minutes*, it can be changed using
*test options*:

**Setting the test suite timeout.**

``` js
let options = new TestOptions()
  .setTimeout(10000);

// Run with a 10 seconds time out
suite.run(options);
```

## Event loop

Vertx Unit execution is a list of tasks to execute, the execution of
each task is driven by the completion of the previous task. These tasks
should leverage Vert.x event loop when possible but that depends on the
current execution context (i.e the test suite is executed in a `main` or
embedded in a `Verticle`) and wether or not a `Vertx` instance is
configured.

The `setUseEventLoop` configures the usage of the event loop:

|                  | useEventLoop:null      | useEventLoop:true      | useEventLoop:false  |
| ---------------- | ---------------------- | ---------------------- | ------------------- |
| `Vertx` instance | use vertx event loop   | use vertx event loop   | force no event loop |
| in a `Verticle`  | use current event loop | use current event loop | force no event loop |
| in a *main*      | use no event loop      | raise an error         | use no event loop   |

Event loop usage

The default `useEventLoop` value is `null`, that means that it will uses
an event loop when possible and fallback to no event loop when no one is
available.

# Reporting

Reporting is an important piece of a test suite, Vertx Unit can be
configured to run with different kind of reporters.

By default no reporter is configured, when running a test suite, *test
options* can be provided to configure one or several:

**Using the console reporter and as a junit xml file.**

``` js
// Report to console
let consoleReport = new ReportOptions()
  .setTo("console");

// Report junit files to the current directory
let junitReport = new ReportOptions()
  .setTo("file:.")
  .setFormat("junit");

suite.run(new TestOptions()
  .setReporters([consoleReport, junitReport]));
```

## Console reporting

Reports to the JVM `System.out` and `System.err`:

  - to  
    *console*

  - format  
    *simple* or *junit*

## File reporting

Reports to a file, a `Vertx` instance must be provided:

  - to  
    *file* `:` *dir name*

  - format  
    *simple* or *junit*

  - example  
    `file:.`

The file reporter will create files in the configured directory, the
files will be named after the test suite name executed and the format
(i.e *simple* creates *txt* files and *junit* creates *xml* files).

## Log reporting

Reports to a logger, a `Vertx` instance must be provided:

  - to  
    *log* `:` *logger name*

  - example  
    `log:mylogger`

## Event bus reporting

Reports events to the event bus, a `Vertx` instance must be provided:

  - to  
    *bus* `:` *event bus address*

  - example  
    `bus:the-address`

It allow to decouple the execution of the test suite from the reporting.

The messages sent over the event bus can be collected by the
`EventBusCollector` and achieve custom reporting:

``` js
import { EventBusCollector } from "@vertx/unit"
let collector = EventBusCollector.create(vertx, new ReportingOptions()
  .setReporters([new ReportOptions()
    .setTo("file:report.xml")
    .setFormat("junit")]));

collector.register("the-address");
```

# Vertx integration

By default, assertions and failures must be done on the `TestContext`
and throwing an assertion error works only when called by Vert.x Unit:

``` js
suite.test("my_test_case", (ctx) => {

  // The failure will be reported by Vert.x Unit
  throw "it failed!";
});
```

In a regular Vert.x callback, the failure will be ignored:

``` js
suite.test("test-server", (testContext) => {
  let server = vertx.createHttpServer().requestHandler((req) => {
    if (req.path() == "/somepath") {
      throw "Wrong path!";
    }
    req.response().end();
  });
});
```

Since Vert.x 3.3, a global exception handler can be set to report the
event loop uncaught exceptions:

``` js
suite.before((testContext) => {

  // Report uncaught exceptions as Vert.x Unit failures
  vertx.exceptionHandler(testContext.exceptionHandler());
});

suite.test("test-server", (testContext) => {
  let server = vertx.createHttpServer().requestHandler((req) => {
    if (req.path() == "/somepath") {
      throw "Wrong path!";
    }
    req.response().end();
  });
});
```

The exception handler is set during the *before* phase, the
`TestContext` is shared between each *before*, *test* and *after* phase.
So the exception handler obtained during the *before* phase is correct.

# Junit integration

Although Vertx Unit is polyglot and not based on JUnit, it is possible
to run a Vertx Unit test suite or a test case from JUnit, allowing you
to integrate your tests with JUnit and your build system or IDE.

**Run a Java class as a JUnit test suite.**

``` java
@RunWith(VertxUnitRunner.class)
public class JUnitTestSuite {
  @Test
  public void testSomething(TestContext context) {
    context.assertFalse(false);
  }
}
```

The `VertxUnitRunner` uses the junit annotations for introspecting the
class and create a test suite after the class. The methods should
declare a `TestContext` argument, if they don’t it is fine too. However
the `TestContext` is the only way to retrieve the associated Vertx
instance of perform asynchronous tests.

The JUnit integration is also available for the Groovy language with the
`io.vertx.groovy.ext.unit.junit.VertxUnitRunner` runner.

## Running a test on a Vert.x context

By default the thread invoking the test methods is the JUnit thread. The
`RunTestOnContext` JUnit rule can be used to alter this behavior for
running these test methods with a Vert.x event loop thread.

Thus there must be some care when state is shared between test methods
and Vert.x handlers as they won’t be on the same thread, e.g
incrementing a counter in a Vert.x handler and asserting the counter in
the test method. One way to solve this is to use proper synchronization,
another is to execute test methods on a Vert.x context that will be
propagated to the created handlers.

For this purpose the `RunTestOnContext` rule needs a `Vertx` instance.
Such instance can be provided, otherwise the rule will manage an
instance under the hood. Such instance can be retrieved when the test is
running, making this rule a way to manage a `Vertx` instance as well.

**Run a Java class as a JUnit test suite.**

``` java
@RunWith(VertxUnitRunner.class)
public class RunOnContextJUnitTestSuite {

  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Test
  public void testSomething(TestContext context) {
    // Use the underlying vertx instance
    Vertx vertx = rule.vertx();
  }
}
```

The rule can be annotated by {@literal @Rule} or {@literal @ClassRule},
the former manages a Vert.x instance per test, the later a single Vert.x
for the test methods of the class.

> **Warning**
> 
> keep in mind that you cannot block the event loop when using this
> rule. Usage of classes like `CountDownLatch` or similar classes must
> be done with care.

## Timeout

The Vert.x Unit 2 minutes timeout can be overriden with the `timeout`
member of the `@Test` annotation:

**Configure the timeout at the test level.**

``` java
public class JunitTestWithTimeout {

  @Test(timeout = 1000l)
  public void testSomething(TestContext context) {
    //...
  }

}
```

For a more global configuration, the `Timeout` rule can be used:

**Configure the timeout at the class level.**

``` java
@RunWith(VertxUnitRunner.class)
public class TimeoutTestSuite {

  @Rule
  public Timeout rule = Timeout.seconds(1);

  @Test
  public void testSomething(TestContext context) {
    //...
  }
}
```

> **Note**
> 
> the `@Test` timeout overrides the the `Timeout` rule.

## Parameterized tests

JUnit provides useful `Parameterized` tests, Vert.x Unit tests can be
ran with this particular runner thanks to the
`VertxUnitRunnerWithParametersFactory`:

**Running a Vert.x Unit parameterized test.**

``` java
@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(VertxUnitRunnerWithParametersFactory.class)
public class SimpleParameterizedTest {

  @Parameterized.Parameters
  public static Iterable<Integer> data() {
    return Arrays.asList(0, 1, 2);
  }

  public SimpleParameterizedTest(int value) {
    //...
  }

  @Test
  public void testSomething(TestContext context) {
    // Execute test with the current value
  }
}
```

Parameterized tests can also be done in Groovy with the
`io.vertx.groovy.ext.unit.junit.VertxUnitRunnerWithParametersFactory`.

## Repeating a test

When a test fails randomly or not often, for instance a race condition,
it is convenient to run the same test multiple times to increase the
likelihood failure of the test.

With JUnit a test has to be annotated with `@Repeat` to be repeated. The
test must also define the `RepeatRule` among its rules.

**Repeating a test with JUnit.**

``` js
@RunWith(VertxUnitRunner.class)
public class RepeatingTest {

  @Rule
  public RepeatRule rule = new RepeatRule();

  @Repeat(1000)
  @Test
  public void testSomething(TestContext context) {
    // This will be executed 1000 times
  }
}
```

When declared, *before* and *after* life cycle will be executed as many
times as the test is executed.

> **Note**
> 
> test repetition are executed sequentially

## Using with other assertion libraries

Vert.x Unit usability has been greatly improved in Vert.x 3.3. You can
now write tests using [Hamcrest](http://hamcrest.org/),
[AssertJ](http://joel-costigliola.github.io/assertj/), [Rest
Assured](https://github.com/rest-assured/rest-assured/), or any
assertion library you want. This is made possible by the global
exception handler described in [Vertx integration](#vertx_integration).

You can find Java examples of using Vert.x Unit with Hamcrest and
AssertJ in the
[vertx-examples](https://github.com/vert-x3/vertx-examples/tree/master/unit-examples)
project.

# Java language integration

## Test suite integration

The Java language provides classes and it is possible to create test
suites directly from Java classes with the following mapping rules:

The `testSuiteObject` argument methods are inspected and the public, non
static methods with `TestContext` parameter are retained and mapped to a
Vertx Unit test suite via the method name:

  - `before` : before callback

  - `after` : after callback

  - `beforeEach` : beforeEach callback

  - `afterEach` : afterEach callback

  - when the name starts with *test* : test case callback named after
    the method name

**Test suite written using a Java class.**

``` java
public class MyTestSuite {

  public void testSomething(TestContext context) {
    context.assertFalse(false);
  }
}
```

This class can be turned into a Vertx test suite easily:

**Create a test suite from a Java object.**

``` java
TestSuite suite = TestSuite.create(new MyTestSuite());
```
