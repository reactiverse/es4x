# GraalVM Support

ES4X has **experimental** GraalVM support. The same code will run either on Nashorn (JS Engine in JDK>=8) or GraalJS
(if run on GraalVM).

There are benefits on using GraalJS namely the updated language support >=ES6 and support out of the box for generators,
promises, etc....

The *downsides* are that the engine is not in 1 to 1 parity of features to the old Nashorn, for example, Nashorn allows
the usage of property names to refer to getters and setters, while Graal is strict. For example:

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

When using this Java Object from Nashorn one could get and set the name as:

```js
var hello = new Hello();
// get the name
var name = hello.name;
// set the name
hello.name = 'Paulo';
```

This is not valid in Graal and should be:

```js
var hello = new Hello();
// get the name
var name = hello.getName();
// set the name
hello.setName('Paulo');
```

While Nashorn would not complain when using threads or being executed from different threads, GraalJS will not allow
this, so you should ensure that you're always executing in the right context. If there is a need to work with multiple
threads, then consider looking at the [Worker API](./WORKER).

## Native Images

Currently you cannot generate native images with ES4X, the limitation is because the static analysis of the AOT compiler
will not take in consideration the java code invoked from the script (so classes won't be available), plus the fact that
the compiler has no support for jvm interop at runtime.

## REPL / Shell

Every ES4X project can run within a very simple REPL. This shell will take scripts **line** by **line** and evaluate the
result, so there is currently no support for multiline scripts.

To start a REPL you can execute:

```sh
java -cp your_fatjar.jar io.reactiverse.es4x.GraalShell
```

If you need to pass arguments to your REPL, say for example, that you would like to have `cluster` support, then use the
`kebab` notation in the arguments as:

```sh
java -cp your_fatjar.jar io.reactiverse.es4x.GraalShell -clustered
```

If the last argument is not in the `kebab` format it is assumed that is the startup script and will be executed on
start:

```sh
java -cp your_fatjar.jar io.reactiverse.es4x.GraalShell -clustered ./index.js
```
