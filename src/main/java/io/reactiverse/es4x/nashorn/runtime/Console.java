/*
 * Copyright 2018 Red Hat, Inc.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */
package io.reactiverse.es4x.nashorn.runtime;

import io.vertx.core.Vertx;
import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.ScriptUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Console {

  private static final Pattern FORMAT = Pattern.compile("%[sdfj%]");

  private static final String RESET = "\u001B[0m";
  private static final String BOLD = "\u001B[1m";
  private static final String RED = "\u001B[31m";
  private static final String GREEN = "\u001B[32m";
  private static final String YELLOW = "\u001B[33m";
  private static final String BLUE = "\u001B[34m";

  private static String replace(String source, Pattern pattern, Function<String, String> fn) {
    final Matcher m = pattern.matcher(source);
    boolean result = m.find();
    if (result) {
      StringBuilder sb = new StringBuilder(source.length());
      int p = 0;
      do {
        sb.append(source, p, m.start());
        sb.append(fn.apply(m.group()));
        p = m.end();
      } while (m.find());
      sb.append(source, p, source.length());
      return sb.toString();
    }
    return source;
  }

  private static String format(JSObject stringify, Object... args) {
    if (!(args[0] instanceof String)) {
      List<String> objects = new ArrayList<>();
      for (Object arg : args) {
        objects.add((String) stringify.call(null, arg));
      }
      return String.join(" ", objects);
    }

    if (args.length == 1) {
      return (String) args[0];
    }

    final AtomicInteger i = new AtomicInteger(1);
    int len = args.length;
    String str = replace((String) args[0], FORMAT, x -> {
      if (x.equals("%%")) {
        return "%";
      }

      if (i.get() >= len) {
        return x;
      }

      Object v = args[i.getAndIncrement()];

      if (v == null) {
        return "null";
      }

      switch (x) {
        case "%s":
          return v.toString();
        case "%d":
          if (v instanceof Integer) {
            return Integer.toString((Integer) v);
          }
          if (v instanceof Long) {
            return Long.toString((Long) v);
          }
          break;
        case "%f":
          if (v instanceof Float) {
            return Float.toString((Float) v);
          }
          if (v instanceof Double) {
            return Double.toString((Double) v);
          }
          break;
        case "%j":
          try {
            return (String) stringify.call(null, v);
          } catch (Throwable e) {
            return "[Circular]";
          }
      }
      return x;
    });

    if (i.get() == len) {
      return str;
    }

    StringBuilder sb = new StringBuilder(str);

    do {
      Object x = args[i.get()];
      if (x == null) {
        sb.append(" null");
      } else {
        sb.append(" ");
        if (x instanceof JSObject) {
          sb.append(stringify.call(null, x));
        } else {
          sb.append(x.toString());
        }
      }
    } while (i.incrementAndGet() < len);

    return sb.toString();
  }


  private Console() {
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
    // we need a reference to the stringify function
    final JSObject stringify = (JSObject) ((JSObject) global.getMember("JSON")).getMember("stringify");
    assert stringify != null;

    final JSObject console = (JSObject) global.eval("new Object()");

    // state
    final Map<String, Integer> counters = new HashMap<>();
    final Map<String, Long> timers = new HashMap<>();

    console.setMember("log", new AbstractJSObject() {
      @Override
      public boolean isFunction() {
        return true;
      }

      @Override
      public Object call(Object thiz, Object... args) {
        System.out.println(format(stringify, args));
        return undefined;
      }
    });

    console.setMember("debug", new AbstractJSObject() {
      @Override
      public boolean isFunction() {
        return true;
      }

      @Override
      public Object call(Object thiz, Object... args) {
        System.out.println(GREEN + format(stringify, args) + RESET);
        return undefined;
      }
    });

    console.setMember("info", new AbstractJSObject() {
      @Override
      public boolean isFunction() {
        return true;
      }

      @Override
      public Object call(Object thiz, Object... args) {
        System.out.println(BLUE + format(stringify, args) + RESET);
        return undefined;
      }
    });

    console.setMember("warn", new AbstractJSObject() {
      @Override
      public boolean isFunction() {
        return true;
      }

      @Override
      public Object call(Object thiz, Object... args) {
        System.out.println(YELLOW + format(stringify, args) + RESET);
        return undefined;
      }
    });

    console.setMember("error", new AbstractJSObject() {
      @Override
      public boolean isFunction() {
        return true;
      }

      @Override
      public Object call(Object thiz, Object... args) {
        System.out.println(RED + format(stringify, args) + RESET);
        return undefined;
      }
    });

    console.setMember("assert", new AbstractJSObject() {
      @Override
      public boolean isFunction() {
        return true;
      }

      @Override
      public Object call(Object thiz, Object... args) {
        boolean test;

        try {
          test = (Boolean) ScriptUtils.convert(args[0], Boolean.class);
        } catch (RuntimeException e) {
          test = false;
        }

        if (test) {
          Object[] _args = new Object[args.length - 1];
          System.arraycopy(args, 1, _args, 0, _args.length);
          System.out.println(RED + format(stringify, _args) + RESET);
        }
        return undefined;
      }
    });

    console.setMember("count", new AbstractJSObject() {
      @Override
      public boolean isFunction() {
        return true;
      }

      @Override
      public Object call(Object thiz, Object... args) {
        if (args != null && args.length > 0 && args[0] != null) {
          String label = args[0].toString();

          int counter = 0;

          if (counters.containsKey(label)) {
            counter = counters.get(label);
          }

          // update
          counters.put(label, ++counter);
          System.out.println(GREEN + label + ":" + RESET + " " + counter);
        }
        return undefined;
      }
    });

    console.setMember("trace", new AbstractJSObject() {
      @Override
      public boolean isFunction() {
        return true;
      }

      @Override
      public Object call(Object thiz, Object... args) {
        if (args != null && args.length > 0 && args[0] != null) {
          if (args[0] instanceof Throwable) {
            final Throwable e = (Throwable) args[0];
            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            final String trace = sw.toString();
            if (trace != null) {
              int idx = trace.indexOf('\n');
              if (idx != -1) {
                System.out.println(BOLD + RED + trace.substring(0, idx) + RESET + trace.substring(idx));
                return undefined;
              }
            }
          }
          System.out.println(BOLD + RED + args[0] + RESET);
        }
        return undefined;
      }
    });


    console.setMember("time", new AbstractJSObject() {
      @Override
      public boolean isFunction() {
        return true;
      }

      @Override
      public Object call(Object thiz, Object... args) {
        if (args != null && args.length > 0 && args[0] != null) {
          timers.put(args[0].toString(), System.currentTimeMillis());
        }
        return undefined;
      }
    });

    console.setMember("timeEnd", new AbstractJSObject() {
      @Override
      public boolean isFunction() {
        return true;
      }

      @Override
      public Object call(Object thiz, Object... args) {
        if (args != null && args.length > 0 && args[0] != null) {
          final long now = System.currentTimeMillis();
          String label = args[0].toString();
          if (timers.containsKey(label)) {
            System.out.println(GREEN + label + ":" + RESET + " " + (now - timers.get(label)) + "ms");
            timers.remove(label);
          } else {
            System.out.println(RED + label + ":" + RESET + " <no timer>");
          }
        }
        return undefined;
      }
    });

    bindings.put("console", console);
  }
}
