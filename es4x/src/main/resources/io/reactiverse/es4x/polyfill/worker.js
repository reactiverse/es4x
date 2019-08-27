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
  const Future = Java.type('io.vertx.core.Future');

  const eventBus = vertx.eventBus();

  /**
   * A Worker is a Worker verticle that is deployed and connected to the eventbus.
   *
   * @param {String} deploymentId the verticle deploymentId
   * @param {Boolean?} remote is this deploymentId local
   * @constructor
   */
  function Worker(deploymentId, remote) {
    const self = this;

    // keep a reference to the context
    Object.defineProperty(this, 'context', {
      value: Vertx.currentContext(),
      writable: false
    });

    // keep a reference to the context
    Object.defineProperty(this, 'id', {
      value: deploymentId,
      writable: false
    });

    // in order to interact with the worker we need to have a message producer
    Object.defineProperty(this, 'producer', {
      value: eventBus.sender(deploymentId + '.out'),
      writable: false
    });

    this.producer.exceptionHandler(function (error) {
      if (self.onerror) {
        if (self.context) {
          self.context.runOnContext(function () {
            self.onerror(error);
          });
        } else {
          self.onerror(error);
        }
      }
    });

    // the interface contract defines 2 callbacks, "onmessage" to receive message, "onerror" for error handling.

    // keep a reference to the context
    Object.defineProperty(this, 'consumer', {
      value: undefined,
      writable: true
    });

    Object.defineProperty(this, 'onmessage', {
      enumerable: true,
      set: function (value) {
        // small clean up
        if (self.consumer) {
          self.consumer.unregister();
        }
        // create a new consumer
        self.consumer = eventBus[remote ? 'consumer' : 'localConsumer'](deploymentId + '.in', function (aMessage) {
          if (self.context) {
            self.context.runOnContext(function () {
              value(JSON.parse(aMessage.body()));
            });
          } else {
            value(JSON.parse(aMessage.body()));
          }
        });
        // attach any errors to the error handler
        self.consumer.exceptionHandler(function (error) {
          if (self.onerror) {
            if (self.context) {
              self.context.runOnContext(function () {
                self.onerror(error);
              });
            } else {
              self.onerror(error);
            }
          }
        });
      },
      get: function () {
        return self.consumer;
      }
    });
  }

  /**
   * The postMessage() method of the Worker interface sends a message to the worker's inner scope.
   * This accepts a single parameter, which is the data to send to the worker. The data may be any
   * value or JavaScript object handled by the JSON stringify algorithm.
   *
   * @param {Object} aMessage - The object to deliver to the worker.
   * @return {void} void
   */
  Worker.prototype.postMessage = function (aMessage) {
    this.producer.write(JSON.stringify(aMessage));
  };

  /**
   * The terminate() method of the Worker interface immediately terminates the Worker. This does not
   * offer the worker an opportunity to finish its operations; it is simply stopped at once.
   */
  Worker.prototype.terminate = function () {
    // close the producer
    this.producer.close();
    // undeploy the worker
    vertx.undeployVerticle(this.id);
    // unregister the consumer
    this.consumer.unregister();
  };

  /**
   * Worker factory.
   *
   * This factory will create workers following the Vert.x semantics, not the Browser semantics. Once the worker is
   * created it will be delivered over the handler instead of being a common constructor.
   *
   * Vert.x Workers are plain Worker Verticles that bind to the eventbus using their own deployment id as base address.
   *
   * A Worker is a Worker verticle that will be deployed and will connect to the eventbus.
   * @param {String} workerScript - following commonjs guidelines **BUT** resolution starts form
   *                                the start up path. The path is not relative to the current module.
   * @param {Function} handler - the handler will contain a asynchronous result with the worker instance.
   */
  Worker.create = function (workerScript, handler) {
    vertx.deployVerticle(
      // the script is prefixed to ensure we get the right loader
      'js:' + workerScript,
      // workers **must** be deployed as worker
      new DeploymentOptions().setWorker(true),
      // handler
      function (deployVerticle) {
        if (deployVerticle.failed()) {
          // with JS we don't need to match types, so no need to re wrap the failure
          return handler(deployVerticle);
        }
        // return the worker as an asynchronous result
        handler(Future.succeededFuture(new Worker(deployVerticle.result())));
      });
  };

  // Install (or replace) the Worker implementation
  global.Worker = Worker;

})(global || this);
