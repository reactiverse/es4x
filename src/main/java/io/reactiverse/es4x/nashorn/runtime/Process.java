package io.reactiverse.es4x.nashorn.runtime;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.JSObject;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public final class Process {

  private Process() {
    throw new RuntimeException("Should not be instantiated");
  }

  public static void install(Map<String, Object> bindings) {
    // get a reference to the global object
    final JSObject global = (JSObject) bindings.get("global");
    assert global != null;
    // get a reference to vertx instance
    final Vertx vertx = (Vertx) global.getMember("vertx");
    assert vertx != null;
    // get a reference to the "undefined type"
    final Object undefined = ((JSObject) global.eval("[undefined]")).getSlot(0);
    assert undefined != null;

    // this will be the "process" object
    final JSObject process = (JSObject) global.eval("new Object()");

    // map env vars
    process.setMember("env", java.lang.System.getenv());
    // map proc id
    String pid = ManagementFactory.getRuntimeMXBean().getName();
    process.setMember("pid", pid.substring(0, pid.indexOf('@')));

    process.setMember("exit", new AbstractJSObject() {
      @Override
      public boolean isFunction() {
        return true;
      }

      @Override
      public Object call(Object thiz, Object... args) {
        final int exitCode;

        if (args != null && args.length > 0) {
          Object retValue = args[0];
          if (retValue instanceof Number) {
            exitCode = ((Number) retValue).intValue();
          } else if (retValue instanceof String) {
            int parsed;
            try {
              parsed = Integer.parseInt((String) retValue);
            } catch (NumberFormatException e) {
              parsed = -1;
            }
            exitCode = parsed;
          } else {
            exitCode = -1;
          }
        } else {
          exitCode = 0;
        }

        vertx.close(res -> {
          if (res.failed()) {
            System.exit(-1);
          } else {
            System.exit(exitCode);
          }
        });
        return undefined;
      }
    });

    process.setMember("stdout", System.out);
    process.setMember("stderr", System.err);
    process.setMember("stdin", System.in);

    // will hold an atomic reference to the callback
    final AtomicReference<JSObject> onStopCB = new AtomicReference<>();

    process.setMember("onStop", new AbstractJSObject() {
      @Override
      public boolean isFunction() {
        return true;
      }

      @Override
      public Object call(Object thiz, Object... args) {
        final JSObject func;

        if (args != null && args.length > 0) {
          func = (JSObject) args[0];
          if (!func.isStrictFunction() && !func.isFunction()) {
            throw new RuntimeException("onStop callback is not a function.");
          }

          return onStopCB.getAndSet(func);
        }

        return undefined;
      }
    });

    process.setMember("stop", new AbstractJSObject() {
      @Override
      public boolean isFunction() {
        return true;
      }

      @Override
      @SuppressWarnings("unchecked")
      public Object call(Object thiz, Object... args) {
        final Future<Void> future;

        if (args != null && args.length > 0) {
          future = (Future<Void>) args[0];
        } else {
          future = null;
        }

        if (onStopCB.get() == null) {
          if (future != null) {
            future.complete();
          }
        } else {
          onStopCB.get().call(onStopCB, future);
        }

        return undefined;
      }
    });

    bindings.put("process", process);
  }
}
