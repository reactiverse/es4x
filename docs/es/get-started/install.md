# Instalacion

Asumiendo que ya has instalado [Node.js](https://nodejs.org/), necesitaras una JVM funcionando. Los requisitos son
([Java](https://adoptopenjdk.net/) o [GraalVM](http://www.graalvm.org/)).

```bash
$ java -version
openjdk version "1.8.0_265"
OpenJDK Runtime Environment (build 1.8.0_265-8u265-b01-0ubuntu2~20.04-b01)
OpenJDK 64-Bit Server VM (build 25.265-b01, mixed mode)
```

Se ver
Si ves una salida similar, esto significa que tu sistema tiene actualmente `java` **8**, lo que no es la mejor opcion
ya que no se beneficia del motor de alto rendimiento que `es4x` utiliza.

## GraalVM/OpenJDK

Para poder tener un runtime compatible se recomienda instalar un runtime superior (por ejemplo utilizando
[jabba](https://github.com/shyiko/jabba)). Para instrucciones instalando `jabba`, por favor sigue el manual oficial
[manual](https://github.com/shyiko/jabba#installation).

::: tip
Utilizando `jabba` puedes instalar `openjdk 11` y/o `graalvm` (una vez) como:

```bash
jabba install openjdk@1.11.0
jabba install graalvm@20.2.0
```

Y despues cambiar al runtime preferido ejecutando:

```bash
jabba use openjdk@1.11 # O jabba use graalvm@20.2
```
:::

Una vez que una JVM valida es instalada puedes opcionalmente instalar la herramienta de desarrollo para gestionar proyectos.

## Herramientas Para Proyectos

```bash
npm install -g @es4x/create # O yarn global add @es4x/create
```

El paquete instalara el comando `es4x` globalmente que puede ser usado para crear proyectos y hacer otras tareas. Para
saber mas de esta herramienta:

```bash
es4x --help
```

### Usando NPX

El mismo paquete puede ser utilizado como una unica operacion con `npx`. En este caso consultalo asi:

```bash
npx @es4x/create --help
```

## Paquete OS

Cuando trabajas con ambientes CI con un numero limitado de paquetes, el gestor de paquetes puede ser instalado
descomprimiendo el archivo tar / zip empaquetado previamente.

```bash
ES4X='0.9.0' \
  curl -sL \
  https://github.com/reactiverse/es4x/releases/download/$ES4X/es4x-pm-$ES4X-bin.tar.gz \
  | tar zx --strip-components=1 -C /usr/local
```

Para sistemas operativos Windows lo mismo se puede realizar usando el archivo `zip` en su lugar.

::: tip
Utilizar `npm` deberia ser la manera preferida de instalar porque permite actualizaciones faciles y es portable
a diferentes *Sistemas Operativos*
:::


## Verifica

Ahora deberias tener el comando `es4x` disponible en tu path, lo puedes comprobar ejecutando:

```
$ es4x --help

Usage: java -jar /usr/local/bin/es4x-bin.jar [COMMAND] [OPTIONS]
            [arg...]

Commands:
    bare         Creates a bare instance of vert.x.
    dockerfile   Creates a generic Dockerfile for building and deploying the
                 current project.
    project      Initializes the 'package.json' to work with ES4X.
    install      Installs required jars from maven to 'node_modules'.
    list         List vert.x applications
    run          Runs a JS script called <main-verticle> in its own instance of
                 vert.x.
    start        Start a vert.x application in background
    stop         Stop a vert.x application
    version      Displays the version.

Run 'java -jar /usr/local/bin/es4x-bin.jar COMMAND --help' for
more information on a command.
```

::: warning
Para la mejor experiencia y rendimiento por favor instala [GraalVM](https://www.graalvm.org). Mientras trabajes con JDK
standard, utilizar Java < 11 lo ejecutara en modo `interpretado` lo que no es bueno en rendimiento o recomendado en
produccion.
:::
