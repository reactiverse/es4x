# GraalVM

ES4X 使用的是Graalvm，但是同样的代码也可**运行**在 Java8，9，10 及 OpenJ9 之上 或

使用 JDK >=11 (有支持 JVMCI) 和 GraalVM 编译

建议还是使用GraalJS，因为可以支持 JS版本 >= ES6 以及开箱即用的generators，promises，etc……

与Java交互需要**严格**遵循Java中类/方法的名称。例如获取类的属性时，必须使用getter和setter。举个例子：

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

当下你在Graal中使用Java对象时，下面的代码将是无效的

```js
var hello = new Hello();
// get the name
var name = hello.name; // FAIL
// set the name
hello.name = 'Paulo';  // FAIL
```

在Graal中你应该这样：

```js
var hello = new Hello();
// get the name
var name = hello.getName();
// set the name
hello.setName('Paulo');
```
GraalJS 不允许多线程访问同样的脚本上下文。如果你需要多线程，请移步[Worker API](./WORKER).

## Native Images

现在还没办法使用ES4X生成 native images，因为AOT编译器的静态分析并不会考虑从脚本去调用Java代码(因此类是没办法用的)，

另外编译器也不支持在运行时与Jvm互操作

这方面的工作正在进行中，相信在不久的将来就会与我们见面

