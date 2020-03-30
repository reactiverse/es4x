# Vert.x Circuit Breaker

Vert.x Circuit Breaker is an implementation of the Circuit Breaker
*pattern* for Vert.x. It keeps track of the number of failures and
*opens the circuit* when a threshold is reached. Optionally, a fallback
is executed.

Supported failures are:

  - failures reported by your code in a `Future`

  - exception thrown by your code

  - uncompleted futures (timeout)

Operations guarded by a circuit breaker are intended to be non-blocking
and asynchronous in order to benefit from the Vert.x execution model.

# Using the vert.x circuit breaker

To use the Vert.x Circuit Breaker, add the following dependency to the
*dependencies* section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-circuit-breaker</artifactId>
 <version>${maven.version}</version>
</dependency>
```

  - Gradle (in your `build.gradle` file):

<!-- end list -->

``` groovy
compile 'io.vertx:vertx-circuit-breaker:${maven.version}'
```

# Using the circuit breaker

To use the circuit breaker you need to:

1.  Create a circuit breaker, with the configuration you want (timeout,
    number of failure before opening the circuit)

2.  Execute some code using the breaker

**Important**: Don’t recreate a circuit breaker on every call. A circuit
breaker is a staful entity. It is recommended to store the circuit
breaker instance in a field.

Here is an example:

``` js
import { CircuitBreaker } from "@vertx/circuit-breaker"
let breaker = CircuitBreaker.create("my-circuit-breaker", vertx, new CircuitBreakerOptions()
  .setMaxFailures(5)
  .setTimeout(2000)
  .setFallbackOnFailure(true)
  .setResetTimeout(10000));

// ---
// Store the circuit breaker in a field and access it as follows
// ---

breaker.execute((future) => {
  // some code executing with the breaker
  // the code reports failures or success on the given future.
  // if this future is marked as failed, the breaker increased the
  // number of failures
}).setHandler((ar) => {
  // Get the operation result.
});
```

The executed block receives a `Future` object as parameter, to denote
the success or failure of the operation as well as the result. For
example in the following example, the result is the output of a REST
endpoint invocation:

``` js
import { CircuitBreaker } from "@vertx/circuit-breaker"
let breaker = CircuitBreaker.create("my-circuit-breaker", vertx, new CircuitBreakerOptions()
  .setMaxFailures(5)
  .setTimeout(2000));

// ---
// Store the circuit breaker in a field and access it as follows
// ---

breaker.execute((future) => {
  vertx.createHttpClient().getNow(8080, "localhost", "/", (response) => {
    if (response.statusCode() !== 200) {
      future.fail("HTTP error");
    } else {
      response.exceptionHandler(future.fail).bodyHandler((buffer) => {
        future.complete(buffer.toString());
      });
    }
  });
}).setHandler((ar) => {
  // Do something with the result
});
```

The result of the operation is provided using the:

  - returned `Future` when calling `execute` methods

  - provided `Future` when calling the `executeAndReport` methods

Optionally, you can provide a fallback which is executed when the
circuit is open:

``` js
import { CircuitBreaker } from "@vertx/circuit-breaker"
let breaker = CircuitBreaker.create("my-circuit-breaker", vertx, new CircuitBreakerOptions()
  .setMaxFailures(5)
  .setTimeout(2000));

// ---
// Store the circuit breaker in a field and access it as follows
// ---

breaker.executeWithFallback((future) => {
  vertx.createHttpClient().getNow(8080, "localhost", "/", (response) => {
    if (response.statusCode() !== 200) {
      future.fail("HTTP error");
    } else {
      response.exceptionHandler(future.fail).bodyHandler((buffer) => {
        future.complete(buffer.toString());
      });
    }
  });
}, (v) => {
  // Executed when the circuit is opened
  return "Hello"
}).setHandler((ar) => {
  // Do something with the result
});
```

The fallback is called whenever the circuit is open, or if the
`isFallbackOnFailure` is enabled. When a fallback is set, the result is
using the output of the fallback function. The fallback function takes
as parameter a `Throwable` object and returns an object of the expected
type.

The fallback can also be set on the `CircuitBreaker` object directly:

``` js
import { CircuitBreaker } from "@vertx/circuit-breaker"
let breaker = CircuitBreaker.create("my-circuit-breaker", vertx, new CircuitBreakerOptions()
  .setMaxFailures(5)
  .setTimeout(2000)).fallback((v) => {
  // Executed when the circuit is opened.
  return "hello"
});

breaker.execute((future) => {
  vertx.createHttpClient().getNow(8080, "localhost", "/", (response) => {
    if (response.statusCode() !== 200) {
      future.fail("HTTP error");
    } else {
      response.exceptionHandler(future.fail).bodyHandler((buffer) => {
        future.complete(buffer.toString());
      });
    }
  });
});
```

# Retries

You can also specify how often the circuit breaker should try your code
before failing with `setMaxRetries`. If you set this to something higher
than 0 your code gets executed several times before finally failing in
the last execution. If the code succeeded in one of the retries your
handler gets notified and any retries left are skipped. Retries are only
supported when the circuit is closed.

Notice that if you set `maxRetries` to 2 for instance, your operation
may be called 3 times: the initial attempt and 2 retries.

By default the timeout between retries is set to 0 which means that
retries will be executed one after another without any delay. This,
however, will result in increased load on the called service and may
delay it’s recovery. In order to mitigate this problem it is recommended
to execute retries with delays. `retryPolicy` method can be used to
specify retry policy. Retry policy is a function which receives retry
count as single argument and returns timeout in milliseconds before
retry is executed and allows to implement a more complex policy, e.g.
exponential backoff with jitter. Below is an example of retry timeout
which grows linearly with each retry count:

``` js
import { CircuitBreaker } from "@vertx/circuit-breaker"
let breaker = CircuitBreaker.create("my-circuit-breaker", vertx, new CircuitBreakerOptions()
  .setMaxFailures(5)
  .setMaxRetries(5)
  .setTimeout(2000)).openHandler((v) => {
  console.log("Circuit opened");
}).closeHandler((v) => {
  console.log("Circuit closed");
}).retryPolicy((retryCount) => {
  retryCount * 100;
});

breaker.execute((future) => {
  vertx.createHttpClient().getNow(8080, "localhost", "/", (response) => {
    if (response.statusCode() !== 200) {
      future.fail("HTTP error");
    } else {
      // Do something with the response
      future.complete();
    }
  });
});
```

# Callbacks

You can also configures callbacks invoked when the circuit is opened or
closed:

``` js
import { CircuitBreaker } from "@vertx/circuit-breaker"
let breaker = CircuitBreaker.create("my-circuit-breaker", vertx, new CircuitBreakerOptions()
  .setMaxFailures(5)
  .setTimeout(2000)).openHandler((v) => {
  console.log("Circuit opened");
}).closeHandler((v) => {
  console.log("Circuit closed");
});

breaker.execute((future) => {
  vertx.createHttpClient().getNow(8080, "localhost", "/", (response) => {
    if (response.statusCode() !== 200) {
      future.fail("HTTP error");
    } else {
      // Do something with the response
      future.complete();
    }
  });
});
```

You can also be notified when the circuit breaker decides to attempt to
reset (half-open state). You can register such a callback with
`halfOpenHandler`.

# Event bus notification

Every time the circuit state changes, an event is published on the event
bus. The address on which the events are sent is configurable with
`setNotificationAddress`. If `null` is passed to this method, the
notifications are disabled. By default, the used address is
`vertx.circuit-breaker`.

Each event contains a Json Object with:

  - `state` : the new circuit breaker state (`OPEN`, `CLOSED`,
    `HALF_OPEN`)

  - `name` : the name of the circuit breaker

  - `failures` : the number of failures

  - `node` : the identifier of the node (`local` if Vert.x is not
    running in cluster mode)

# The half-open state

When the circuit is "open", calls to the circuit breaker fail
immediately, without any attempt to execute the real operation. After a
suitable amount of time (configured from `setResetTimeout`, the circuit
breaker decides that the operation has a chance of succeeding, so it
goes into the `half-open` state. In this state, the next call to the
circuit breaker is allowed to execute the dangerous operation. Should
the call succeed, the circuit breaker resets and returns to the `closed`
state, ready for more routine operation. If this trial call fails,
however, the circuit breaker returns to the `open` state until another
timeout elapses.

# Reported exceptions

The fallback receives a:

  - `OpenCircuitException` when the circuit breaker is opened

  - `TimeoutException` when the operation timed out

# Pushing circuit breaker metrics to the Hystrix Dashboard

Netflix Hystrix comes with a dashboard to present the current state of
the circuit breakers. The Vert.x circuit breakers can publish their
metrics in order to be consumed by this Hystrix Dashboard. The Hystrix
dashboard requires a SSE stream sending the metrics. This stream is
provided by the `HystrixMetricHandler` Vert.x Web Handler:

``` js
import { CircuitBreaker } from "@vertx/circuit-breaker"
import { Router } from "@vertx/web"
import { HystrixMetricHandler } from "@vertx/circuit-breaker"
// Create the circuit breaker as usual.
let breaker = CircuitBreaker.create("my-circuit-breaker", vertx);
let breaker2 = CircuitBreaker.create("my-second-circuit-breaker", vertx);

// Create a Vert.x Web router
let router = Router.router(vertx);
// Register the metric handler
router.get("/hystrix-metrics").handler(HystrixMetricHandler.create(vertx));

// Create the HTTP server using the router to dispatch the requests
vertx.createHttpServer().requestHandler(router).listen(8080);
```

In the Hystrix Dashboard, configure the stream url like:
`http://localhost:8080/metrics`. The dashboard now consumes the metrics
from the Vert.x circuit breakers.

Notice that the metrics are collected by the Vert.x Web handler using
the event bus notifications. If you don’t use the default notification
address, you need to pass it when creating the metrics handler.
