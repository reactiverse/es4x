# GraalVM

ES4X uses GraalVM, however the same code will run either in **interpreted** mode on Java 8, 9, 10 and OpenJ9 or
**compiled** mode on JDK >= 11 (with JVMCI support) or GraalVM.

There are benefits on using GraalJS namely the updated language support >=ES6 and support out of the box for generators,
promises, etc....

Java interop follows the **exact** class/method name from Java. For example, the usage of property names to refer to
getters and setters, must use the *getter* and *setter*. For example:

```java
class Hello {
  private String name;
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
}
```

When using this Java Object from Graal, this will not work:

```js
var hello = new Hello();
// get the name
var name = hello.name; // FAIL
// set the name
hello.name = 'Paulo';  // FAIL
```

This is not valid in Graal and should be:

```js
var hello = new Hello();
// get the name
var name = hello.getName();
// set the name
hello.setName('Paulo');
```

GraalJS will not allow multi thread access to the same script context. If there is a need to work with multiple
threads, then consider looking at the [Worker API](./worker).

## Native Images

Currently you cannot generate native images with ES4X, the limitation is because the static analysis of the AOT compiler
will not take in consideration the java code invoked from the script (so classes won't be available), plus the fact that
the compiler has no support for jvm interop at runtime.

There is work in progress in this are so it might be possible in the future.
