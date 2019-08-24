# 打包

打包整个应用应该遵循`NPM`风格：

```sh
npm pack
```

[npm pack](https://docs.npmjs.com/cli/pack) 会在应用当中生成一个`TGZ`文件，你可以把这个应用移动到其他的目录下。然而应用也可以发布到NPM注册中心。

请注意(这很重要)：为了能与`published/packed`正常的运行，我们需要在目标环境拥有打包[es4x-pm](https://www.npmjs.com/package/es4x-pm)的权限，因为我们有必要安装`java`的部分。


## Docker

你也可以为自己创建Docker镜像：

```bash
es4x dockerfile
```

这样可以生成一个简化的`dockerfile`，你可以根据自己的需要自定义这个`dockerfile`,默认状况下这个文件分3步构建：

1. 第一步，`node`用来安装所有的`NPM`依赖
2. 第二步，`java`用来安装`Maven`依赖
3. 最后一步，GraalVM镜像用来启动应用

默认情况下，我们使用[oracle/graalvm-ce](https://hub.docker.com/r/oracle/graalvm-ce)docker镜像，但是你也可以用其他含有openJdk并支持JVMCI的镜像(请使用java11及以上的版本)。

```bash
docker build . -arg BASEIMAGE=openjdk:11
```

## JLink

Java 11 支持 [jlink](https://docs.oracle.com/en/java/javase/11/tools/jlink.html)。你可以使用jlink工具来把所有模块及其依赖整理优化到自定义的运行镜像。

```bash
es4x jlink
```

这会生成一个最优的运行环境，这意味着可以不用依赖于完整的JDK环境。

做一个对比，一个hello world应用在运行时占用**80Mb**的空间，但是完整的JDK环境却要求**200Mb**。

这个特性可以和`Dockerfile`配合使用。用`OpenJDK`为环境的镜像以取代graal为环境的镜像。然后 第二步，运行jlink：

```dockerfile
# 第二步（构建JVM相关代码）
FROM openjdk:11 AS JVM
ARG ES4X_VERSION=${project.version}
# force es4x maven resolution only to consider production dependencies
# 强制es4x的maven解析仅仅关心生产环境的依赖
ENV ES4X_ENV=production
# 复制上一步的构建步骤
COPY --from=NPM /usr/src/app /usr/src/app
# 指定工作空间
WORKDIR /usr/src/app
# 下载ES4X运行环境工具
RUN curl -sL https://github.com/reactiverse/es4x/releases/download/${ES4X_VERSION}/es4x-pm-${ES4X_VERSION}-bin.tar.gz | \
    tar zx --strip-components=1 -C /usr/local
# 安装java依赖
RUN es4x install -f
# 创建最简化的运行环境
RUN es4x jlink -t /usr/local
```

这会在jre中生成一个最简的运行环境，这个运行环境在最后一步当中当作最简的基础镜像来使用。

```dockerfile
FROM debian:slim
# 整理前一步骤生成的jar包
COPY --from=JVM /usr/local /usr/local
COPY --from=JVM /usr/src/app /usr/src/app
# 指定工作空间
WORKDIR /usr/src/app
# 打包应用源
COPY . .
# 为docker容器自定义java options参数
ENV JAVA_OPTS="-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:+UseContainerSupport"
# 定义入口文件
ENTRYPOINT [ "./node_modules/.bin/es4x-launcher" ]
```

这一步会生成一个小的最终镜像(比上一步生成的镜像稍大)，因为您也同时将最简运行环境打包进入了最终的docker镜像。

