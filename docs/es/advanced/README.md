# GraalVM

ES4X utiliza GraalVM, sin embargo el mismo codigo se ejecutara en mode **interpretado** (interpreted) en Java 8, 9, 10 y OpenJ9.

Para JDK >= 11 (con soporte JVMCI) o GraalVM ES4X se ejecuta en modo **compilado** (compiled).

::: Consejo
Es decir, por favor utiliza Java >= 11 o GraalVM.
:::

Hay beneficios en utilizar GraalJS, principalmente el soporte actualizado del lenguage >=ES6 y el suporte inicial para generadores,
promesas, etc...

## Diferencias con Nashorn

Diferente a `Nashorn`, `GraalJS` interoperatibilidad *java* sigue **exactamente** el nombre clase/metodo de Java. Por ejemplo, el uso de
propiedades nombradas para referirse a los getters y setters, deben usarse el *getter* y *setter*. Por ejemplo:

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

Cuando usas este objeto Java en Graal, esto no funcionara:

```js
var hello = new Hello();
// get the name
var name = hello.name; // FAIL
// set the name
hello.name = 'Paulo';  // FAIL
```

Esto no es valido en Graal y deberia ser:

```js
var hello = new Hello();
// get the name
var name = hello.getName();
// set the name
hello.setName('Paulo');
```

## Multihilo (threading)

GraalJS es muy estricto con un hilo unico en el contexto JS del momento. Cuando se trabaja con APIS de Vert.x asincronico 
esto no deberia suponer un problema. Sin embargo otras librerias pueden causar problemas. Para evitar esta limitacion, se
recomienda el uso del API `Worker` o de `EventBus`.

::: Advertencia
GraalJS no permitira el acceso multihilo en el contexto de un mismo script. Si es necesario trabajor con varios hilos,
considera mirar el [Worker API](./worker).
:::

## Imagines Nativas

Actualmente no puedes generar imagenes nativas en ES4X, esta limitacion es porque el analisis estatico del compilador AOT
no considerara el codigo Java invocado desde el script (las clases no estaran disponibles), ademas del hecho de que el 
compilador no dispone de interoperatibilidad JVM durante la ejecucion.

Hay trabajo en progreso en esta area asi que podria ser disponible en el futuro.
