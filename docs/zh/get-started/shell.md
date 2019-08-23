# Shell

有些场景下使用`REPL`或者`shell`更为方便实用。ES4X 里这项功能开箱即用,
只需要执行下边的命令:

```bash
$ npm run "js:>"

js:>
```

此时已经可以使用`REPL`啦。 就像这样:

```
js:> require('./index.js');
Server listening at: http://localhost:8080/

js:>
```

使用`es4x-launcher.jar`也可以在不需要`npm`的情况下运行shell。

```bash
java -jar es4x-launcher.jar run "js:>"
```

这样shell环境就建好了, 可以利用classpath下的所有可用组件来运行你的代码。
