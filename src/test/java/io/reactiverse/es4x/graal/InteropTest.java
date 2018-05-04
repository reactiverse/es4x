package io.reactiverse.es4x.graal;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.Test;

public class InteropTest {

  public static class CallMe {
    private final Value isObject;
    private final Value isArray;

    public CallMe(Value isObject, Value isArray) {
      this.isObject = isObject;
      this.isArray = isArray;
    }
    public void call(Object obj) {
      System.out.println(isObject.execute(obj).asBoolean());
      System.out.println(isArray.execute(obj).asBoolean());
      System.out.println(obj);
      System.out.println(obj.getClass());
    }
  }

  @Test
  public void testInterop() {
    Context ctx = Context.newBuilder("js").allowAllAccess(true).build();

    Value isObject = ctx.eval("js", "function (obj) { return typeof obj === 'object'; }");
    Value isArray = ctx.eval("js", "function () { return Array.isArray; }").execute();
    ctx.getBindings("js").putMember("cb", new CallMe(isObject, isArray));

    ctx.eval("js", "cb.call({'foo': 'bar'});");
    // prints:
    // {foo=bar}
    // class com.oracle.truffle.api.interop.java.TruffleMap

    // FIXME: This is the issue
    // I'd expect to see some type that implements java.util.List instead
    ctx.eval("js", "cb.call(['foo', 'bar']);");
    // prints:
    // {0=foo, 1=bar}
    // class com.oracle.truffle.api.interop.java.TruffleMap

  }
}
