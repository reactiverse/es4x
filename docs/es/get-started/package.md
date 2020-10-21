# Package

Las aplicaciones de empaque deben seguir el estilo "NPM":

```sh
npm pack
```

[npm pack](https://docs.npmjs.com/cli/pack) producira un `TGZ` con tu aplicacion que puedes mover a otra localizacion.
Sin embargo, las aplicaciones tambien pueden ser [publicadas](https://docs.npmjs.com/cli/publish) a un registro NPM.

Es importante notar que para trabajar con `published/packed` el ambiente objetivo tiene que tener acceso a el paquete
[@es4x/create](https://www.npmjs.com/package/@es4x/create) ya que sera necesario para instalar las partes `java`.


## Docker

Imagenes Docker tambien pueden ser creadas para ti.

```bash
es4x dockerfile
```

Esto producira un sencillo `dockerfile` que puedes personalizar a tus necesidades, por defecto el archivo sera
construido en 3 fases.

1. En la primera fase `node` es utilizado para instalar todas las dependencias `NPM`
2. En la segunda fase `java` es utilizado para instalar las dependencias `Maven`
3. En la fase final la imagen GraalVM es utilizada para ejecutar la aplicacion

Por defecto la imagen docker [oracle/graalvm-ce](https://hub.docker.com/r/oracle/graalvm-ce) es usada, pero puede ser reemplazada
con cualquier imagen JDK (por favor prefiere versiones 11 o superiores) con soporte para JVMCI.

```bash
docker build . --build-arg BASEIMAGE=openjdk:11
```

## JLink

Java 11 soporta [jlink](https://docs.oracle.com/en/java/javase/11/tools/jlink.html). Puedes usar la herramienta jlink para
montar y optimizar un conjunto de modulos y sus dependencias en una imagen runtime personalizada.

```bash
es4x jlink
```

Esto producira un runtime **optimizado**, que significa que puede ser utilizado en lugar de depender de una instalacion
JDK completa. Como comparacion, una aplicacion "hello world" producira un runtime de unos **80Mb**, mientras que una
instalacion completa JDK necesita unos **200Mb**.

Esta caracteristica puede utilizarse en colaboracion con `Dockerfile`. En lugar de usar la imagen graal base, usa la imagen
`OpenJDK` base. Y en la segunda fase, ejecuta jlink:

```dockerfile
# Segunda fase (construye el cpdigo relacionado con JVM)
FROM openjdk:11 AS JVM
ARG ES4X_VERSION=${project.version}
# Copia el paso de construccion previo
COPY --from=NPM /usr/src/app /usr/src/app
# usa el espacio de trabajo (workspace) copiado
WORKDIR /usr/src/app
# Descarga la herramienta runtime ES4X
RUN curl -sL https://github.com/reactiverse/es4x/releases/download/${ES4X_VERSION}/es4x-pm-${ES4X_VERSION}-bin.tar.gz | \
    tar zx --strip-components=1 -C /usr/local
# Instala las dependencias Java
# fuerza a que la resolucion es4x maven solo considere dependencias de produccion
RUN es4x install --only=prod
# Crea un runtime optimizado
RUN es4x jlink -t /usr/local
```

Esto producira un runtime optimizado en jre, que puede ser usado con una pequeña imagen base en la fase final:

```dockerfile
FROM debian:slim
# Recoge los jars del paso previo
COPY --from=JVM /usr/local /usr/local
COPY --from=JVM /usr/src/app /usr/src/app
# usa el espacio de trabajo (workspace) copiado
WORKDIR /usr/src/app
# Empaqueta el codigo fuente
COPY . .
# Define opciones personalizadas java para los contenedores
ENV JAVA_OPTS="-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:+UseContainerSupport"
# define el punto de entrada (entrypoint)
ENTRYPOINT [ "./node_modules/.bin/es4x-launcher" ]
```

Esto producira una imagen final pequeña, pero una capa (layer) mas grande ya que estas empaquetando un runtime optimizado.
