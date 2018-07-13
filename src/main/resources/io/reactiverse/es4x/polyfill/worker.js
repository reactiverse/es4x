/**
 *  Copyright 2014-2018 Red Hat, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License")
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
(function (global) {

  const Vertx = Java.type('io.vertx.core.Vertx');
  const DeploymentOptions = Java.type('io.vertx.core.DeploymentOptions');
  const JsonObject = Java.type('io.vertx.core.json.JsonObject');
  const CountDownLatch = Java.type('java.util.concurrent.CountDownLatch');

  const eventBus = vertx.eventBus();

  /**
   * Worker constructor.
   *
   * A Worker is a Worker verticle that will be deployed and will connect to the eventbus.
   * @param {String} worker
   * @param {?String} address
   * @constructor
   */
  function Worker(worker, address) {
    // keep a self reference
    let self = this;
    // keep a reference to the context
    this._context = Vertx.currentContext();
    this._address = address || worker;

    vertx.deployVerticle(
      'worker-js:' + worker,
      new DeploymentOptions()
        .setWorker(true)
        .setConfig(new JsonObject().put('address', this._address)),
      function (deployVerticle) {
        if (deployVerticle.succeeded()) {
          self._deploymentId = deployVerticle.result();
          // TODO: push all queued messages to the verticle and disable queueing
        } else {
          self._failure = deployVerticle.cause();
          // TODO: save the throwable so when the onerror handle is set it will be pushed
        }
      });

    // // there are 2 modes to operate, either bind to an eventbus address
    // // or load a script and bind it to an eventbus address
    //
    // // 3rd option receive a function and do an execute blocking
    //
    // if (location.startsWith('eventbus:')) {
    //   this._publisher = eventBus.publisher(location.substr(9));
    //   // setup the handlers
    //   this._publisher.exceptionHandler(function (err) {
    //     if (self.onerror) {
    //       if (self._context) {
    //         self._context.runOnContext(function () {
    //           self.onerror(err);
    //         });
    //       } else {
    //         self.onerror(err);
    //       }
    //     }
    //   });
    //
    //   return;
    // }
    //
    // // the second mode is more tricky we need to load scripts
    // let script = fs.readFileBlocking(location).toString();
    //
    // let worker = loadWithNewGlobal({
    //   script: script,
    //   name: location
    // });
    //
    // print(worker);
    // print(worker.onmessage);
    //
    // // if (location.startsWith('consumer:')) {
    // //   this._consumer = eventBus.consumer(location.substr(9));
    // //   // setup the handlers
    // //   this._consumer.handler(function (msg) {
    // //     if (self.onmessage) {
    // //       if (self._context) {
    // //         self._context.runOnContext(function () {
    // //           self.onmessage(msg.body());
    // //         });
    // //       } else {
    // //         self.onmessage(msg.body());
    // //       }
    // //     }
    // //   });
    // //   this._consumer.exceptionHandler(function (err) {
    // //     if (self.onerror) {
    // //       if (self._context) {
    // //         self._context.runOnContext(function () {
    // //           self.onerror(err);
    // //         });
    // //       } else {
    // //         self.onerror(err);
    // //       }
    // //     }
    // //   });
    // // }
    // // if (location.startsWith('local-consumer:')) {
    // //   this._consumer = eventBus.localConsumer(location.substr(15));
    // //   // setup the handlers
    // //   this._consumer.handler(function (msg) {
    // //     if (self.onmessage) {
    // //       if (self._context) {
    // //         self._context.runOnContext(function () {
    // //           self.onmessage(msg.body());
    // //         });
    // //       } else {
    // //         self.onmessage(msg.body());
    // //       }
    // //     }
    // //   });
    // //   this._consumer.exceptionHandler(function (err) {
    // //     if (self.onerror) {
    // //       if (self._context) {
    // //         self._context.runOnContext(function () {
    // //           self.onerror(err);
    // //         });
    // //       } else {
    // //         self.onerror(err);
    // //       }
    // //     }
    // //   });
    // // }
  }

  /**
   * The postMessage() method of the Worker interface sends a message to the worker's inner scope.
   * This accepts a single parameter, which is the data to send to the worker. The data may be any
   * value or JavaScript object handled by the structured clone algorithm, which includes cyclical
   * references.
   *
   * The Worker can send back information to the thread that spawned it using the
   * DedicatedWorkerGlobalScope.postMessage method.
   *
   * @param {Object} aMessage - The object to deliver to the worker.
   * @return {void} void
   */
  Worker.prototype.postMessage = function (aMessage) {
    if (this._address) {
      eventBus.send('out.' + this._address, aMessage);
    }
  };

  /**
   * The terminate() method of the Worker interface immediately terminates the Worker. This does not
   * offer the worker an opportunity to finish its operations; it is simply stopped at once.
   */
  Worker.prototype.terminate = function () {
    if (this._consumer) {
      this._consumer.unregister();
    }
    if (this._publisher) {
      this._publisher.close();
    }
  };

  // Install (or replace) the Worker implementation
  global.Worker = Worker;

})(global || this);
