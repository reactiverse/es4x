# 运行

ES4X 应用会在安装`npm`阶段创建`es4x-launcher`。 如果`es4x-launcher`还不存在,
执行命令:

```bash
yarn # OR npm install
```

::: tip

`es4x-launcher`的位置在`node_modules/.bin/es4x-lancher.*`。

:::

安装好后, 启动应用非常简单 ::

```bash
yarn start # OR npm start
```

这行命令将会在JVM上运行你的应用, 来替换默认的`npm`操作。使用样例的*Hello World*
项目的话, 可以看到像这样的输出:

```bash
Server listening at: http://localhost:8080/
Succeeded in deploying verticle
```

此时你可以打开浏览器或者使用http客户端, 来和你的应用交互啦:


```bash
> curl localhost:8080

Hello from Vert.x Web!
```

## 没有npm/yarn时怎样运行

当发布应用到生产环境时, 通常不会将包管理器和应用绑定在一起。这种情况下不应该使用`npm`/`yarn`运行应用。而是像这样:

```bash
./node_modules/.bin/es4x-launcher
```

::: tip

自定义应用的启动项也是可以的, 查看帮助:

```bash
./node_modules/.bin/es4x-launcher -help
```
:::

## 配置verticles的数量

扩容/缩容`verticles`的数量(某些情况下可以提升性能), 可以这么做:

```bash
# number of verticles to use:
N=2 \
  ./node_modules/.bin/es4x-launcher -instances $N
```

::: tip

通常增加`verticles`的数量为cpu核数的两倍, 将会收获最优的性能。

:::

## 集群

和配置`verticles`的数量一样, ES4X 的集群配置也很简单:


```bash
./node_modules/.bin/es4x-launcher -cluster
```

要了解关于集群的更多内容, 请参考vert.x的官方文档。
