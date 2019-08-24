# Worker API

[MDN](https://developer.mozilla.org/en-US/docs/Web/API/Web_Workers_API/Using_web_workers) Web Workers 的定义为：

> Web Workers 是将任务分配给后台运行的一种简单的办法。
> 工作线程可以在不干扰用户界面的情况下执行任务。

ES4X不是浏览器，并不需要关注用户界面，但是你也可以在服务端长时间的运行任务。在Vert.x中 一切都是非阻塞的，所以创建工作线程也应该是一样的。

因此我们不能完整的遵循工作线程的接口定义，但是我们可以使用方法去替换构造函数。

想象一下在你的代码中你需要运行CPU密集型任务，你不能阻塞 event loop，所以我们应该使用vert.x的 worker verticles。
Worker API 可以映射到Vert.x API。不过有一些细微的差别


## 工作线程实例

想象一下下面的代码

```js
// Get a reference to the Thread class to cause some blocking...
const Thread = Java.type('java.lang.Thread');

// The worker context is referenced by the variable `self` like on the MDN docs
self.onmessage = function(e) {
  console.log('Message received from main script, will sleep 5 seconds...');
  // Cause some havok in the event loop
  Thread.sleep(5 * 1000);
  var workerResult = 'Result: ' + (e.data[0] * e.data[1]);
  console.log('Posting message back to main script');
  // return data back to the main verticle
  postMessage(workerResult);
};
```

## 注意事项

工作线程是在单独的上下文中,所以你无法的verticle 和worker中共享方法，
所有的通信将用消息传递(eventbus) 使用方法：

* `postMessage()` 发送消息到另一端
* `onmessage` 从另一端接收消息

### Verticle 方面

verticle这边提供接收错误和 `terminate()` 方法以终止worker，但是worker本身并不会主动调用该方法

## Verticle 示例

```js
Worker.create('workers/worker.js', function (create) {
  if (create.succeeded()) {
    var worker = create.result();
    
    worker.onmessage = function (msg) {
      console.log('onmessage: ' + msg);
    };
    
    worker.onerror = function (err) {
      console.err(err);
      // terminate the worker
      worker.terminate();
    };

    console.log('posting...');
    worker.postMessage({data: [2, 3]});
  }
});
```
所以不能在event loop中运行的代码 `Thread.sleep(5000)` 应当使其运行在worker 线程中，为其他的IO任务保留event loop的线程

## 多语言的 工作线程

你仍然可以使用非JavaScript的工作线程，但是必须遵守下面的规则：

* 工作线程必须注册地址: `{deploymentId}.out` 从main script接收消息。
* 工作线程应该发送消息: `{deploymentId}.in` 发送消息到 main script。
* 发送消息应该使用`JSON.stringify(message)` 以避免不同语言间的问题
* 工作线程应该是本地的 如果你想要连接到集群中其他的worker，那么你需要使用带参数的构造方法 `true`，例如：`new Worker('deploymentId', true)`。
