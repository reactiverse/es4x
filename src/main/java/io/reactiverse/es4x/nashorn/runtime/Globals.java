package io.reactiverse.es4x.nashorn.runtime;

import io.vertx.core.Vertx;
import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.JSObject;

import java.util.Map;

public final class Globals {

  private Globals() {
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

    bindings.put("setTimeout", installSetTimeout(vertx, undefined));
    bindings.put("clearTimeout", installClearTimer(vertx, undefined));
    bindings.put("setInterval", installSetInterval(vertx, undefined));
    bindings.put("setImmediate", installSetImmediate(vertx, undefined));
  }

  private static JSObject installSetImmediate(final Vertx vertx, final Object undefined) {
    return new AbstractJSObject() {
      @Override
      public boolean isFunction() {
        return true;
      }

      @Override
      public Object call(Object thiz, Object... args) {
        final JSObject callback = (JSObject) args[0];
        final Object[] fnArgs = args.length > 1 ? new Object[args.length - 1] : null;

        if (args.length > 1) {
          System.arraycopy(args, 2, fnArgs, 0, args.length - 2);
        }

        vertx.runOnContext(v -> callback.call(null, fnArgs));
        return undefined;
      }
    };
  }

  private static JSObject installClearTimer(Vertx vertx, Object undefined) {
    return new AbstractJSObject() {
      @Override
      public boolean isFunction() {
        return true;
      }

      @Override
      public Object call(Object thiz, Object... args) {
        Number timeoutId = (Number) args[0];
        vertx.cancelTimer(timeoutId.longValue());
        return undefined;
      }
    };
  }

  private static JSObject installSetInterval(Vertx vertx, Object undefined) {
    return new AbstractJSObject() {
      @Override
      public boolean isFunction() {
        return true;
      }

      @Override
      public Object call(Object thiz, Object... args) {
        final JSObject callback = (JSObject) args[0];
        final Number ms = (Number) args[1];
        final Object[] fnArgs = args.length > 2 ? new Object[args.length - 2] : null;

        if (args.length > 2) {
          System.arraycopy(args, 2, fnArgs, 0, args.length - 2);
        }

        return vertx.setPeriodic(ms.longValue(), v -> callback.call(null, fnArgs));
      }
    };
  }

  private static JSObject installSetTimeout(Vertx vertx, Object undefined) {
    return new AbstractJSObject() {
      @Override
      public boolean isFunction() {
        return true;
      }

      @Override
      public Object call(Object thiz, Object... args) {
        final JSObject callback = (JSObject) args[0];
        final Number ms = (Number) args[1];
        final Object[] fnArgs = args.length > 2 ? new Object[args.length - 2] : null;

        if (args.length > 2) {
          System.arraycopy(args, 2, fnArgs, 0, args.length - 2);
        }

        if (ms.longValue() == 0) {
          // special case
          vertx.runOnContext(v -> callback.call(null, fnArgs));
          return undefined;
        } else {
          return vertx.setTimer(ms.longValue(), v -> callback.call(null, fnArgs));
        }
      }
    };
  }
}
