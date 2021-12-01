package io.reactiverse.es4x;

import io.reactiverse.es4x.sourcemap.SourceMap;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class MappingTest {

  @Test
  public void testMapping() throws IOException {
    /*
    Failed in deploying verticle caused by ReferenceError: URL is not defined
      at <js> :=>(dist/bundle.js:44:1695-1697)
      at <js> :=>(dist/bundle.js:43-60:1647-2222)
      at <js> :module(dist/bundle.js:1-80:9-2934)
      at org.graalvm.sdk/org.graalvm.polyglot.Context.eval(Context.java:347)
      at io.reactiverse.es4x.Runtime.eval(Runtime.java:148)
      at io.reactiverse.es4x.impl.MJSVerticleFactory$1.start(MJSVerticleFactory.java:76)
      at io.vertx.core.impl.DeploymentManager.lambda$doDeploy$5(DeploymentManager.java:196)
      at io.vertx.core.impl.AbstractContext.dispatch(AbstractContext.java:96)
      at io.vertx.core.impl.AbstractContext.dispatch(AbstractContext.java:59)
      at io.vertx.core.impl.EventLoopContext.lambda$runOnContext$0(EventLoopContext.java:37)
      at io.netty.util.concurrent.AbstractEventExecutor.safeExecute(AbstractEventExecutor.java:164)
      at io.netty.util.concurrent.SingleThreadEventExecutor.runAllTasks(SingleThreadEventExecutor.java:472)
      at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:500)
      at io.netty.util.concurrent.SingleThreadEventExecutor$4.run(SingleThreadEventExecutor.java:989)
      at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74)
      at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
      at java.base/java.lang.Thread.run(Thread.java:829)
     */



    SourceMap source = new SourceMap(
      Files.readAllBytes(new File("src/test/resources/bundle.js.map").toPath()));

    source.eachMapping(System.out::println);

    System.out.println(
      source.getMapping(44, 0));

    System.out.println(
      source.getMapping(60, 0));

    System.out.println(
      source.getMapping(80, 0));
  }
}
