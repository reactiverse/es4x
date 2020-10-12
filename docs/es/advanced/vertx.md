# Eclipse Vert.x

Tal como deberia ya estar claro, Vert.x es el IO y el modelo por defecto de programacion usado en ES4X. Sin embargo hay
algunas buenas mejoras del standard [Vert.x APIs](https://vertx.io).

## APIs Generadas

Todas las APIs publicadas a `npm` con el namespace `@vertx` y `@reactiverse` son generadas en codigo. Generacion de codigo
es una ayuda que permite que estas APIs sean utilizadas por usuarios `JavaScript` en un formato que sea familiar sin
comprometer el rendimiento de la aplicacion.

Todas las interacciones con la JVM occuren sobre el objecto `Java`. La parte mas importance es mover una clase JVM a JS:

```js
// Importa la clase java.lang.Math para utilizarla
// como un tipo JS en el script
const Math = Java.type('javalang.Math');
```

Podriamos hacer esto con todos los APIs pero hay varios limites que ES4X intenta resolver:

* **Propenso a error** - Necesitas saber los APIs de Java y tipos exactos para utilizarlos en JavaScript.
* **Imposible definir dependencias** - Si necesitas utilizar APIs de otros modulos, importar de clase en clase no puede definir dependencia entre ellas.
* **Sin soporte IDE** - El desarrollador necesitara conocer el API antes de usarlo y el IDE no ofrecera asistencia.

El generador ES4X soluciona esto creando un modulo `npm` para cada modulo `vertx` y definicion de tipos en cada clase.

Cada modulo tedra los siguientes archivos:

* `package.json` - Define las dependencias entre modulos
* `index.js` - interfaz API commonjs
* `index.mjs` - Interfaz API ESM
* `index.d.ts` - Definicion completa de tipos para la interfaz API
* `enum.js` - Enumeraciones commonjs API
* `enum.mjs` - Enumeraciones ESM API
* `enum.d.ts` - Definicion completa de tipos para las enumeraciones API
* `options.js` - Objetos de datos commonjs API
* `options.mjs` - Objectos de datos ESM API
* `options.d.ts` - Definicion completa de tipos para los objectos de datos

Todos los archivos `index` simplificaran la importancion de clases a JVM con reemplazos, por ejemplo:

```js
// sin ES4X
const Router = Java.type('io.vertx.ext.web.Router');
// con ES4X
import { Router } from '@vertx/web';
```

Este pequeño cambio permite que los IDEs ayuden con el desarrollo y que los gestores de paquetes descarguen las dependencias necesarias.
Finalmente todos los archivos `.d.ts` avisaran a los IDEs sobre los tipos y datan ayuda de autocompletacion de codigo.


## promise/future

Vert.x tiene 2 tipos:

* `io.vertx.core.Future`
* `io.vertx.core.Promise`

Extrañamente, una `promesa` Vert.x no es lo mismo que un `futuro` en JavaScript. Una `promesa` Vert.x es la parte escribible de
un `futuro` Vert.x. En terminos de JavaScript:

* Vert.x `Future` === JavaScript `Promise Like (Thenable)`
* Vert.x `Promise` === JavaScript `Executor Function`

## async/await

`async/await` esta disponible sin necesidad de compilacion en `GraalVM`. ES4X añade una caracteristica mas al tipo
`Future` de Vert.x. Los APIs que devuelven un `Future` de Vert.x se pueden usar como `thenable`, esto significa que:

```js
// Usando Java API
vertx.createHttpServer()
  .listen(0)
  .onSuccess(server => {
    console.log('Servidor listo!')
  })
  .onFailure(err => {
    console.log('Fallo en el inicio del servidor!')
  });
```

Puede ser usado como `Thenable`:

```js
try {
  let server = await vertx
    .createHttpServer()
    .listen(0);

  console.log('Servidor listo!');
} catch (err) {
  console.log('Fallo en el inicio del servidor!')
}
```

:::Consejo
`async/await` funciona hasta con bucles, lo que hace trabajar con codigo asincronico muy facil, incluso mezclando codigo JS y Java.
:::

## Conversion de Tipos

Vert.x esta escrito en `Java`, sin embargo en `JavaScript` no tenemos que preocuparnos de los tipos tanto como en `Java`. ES4X
hace algunas conversiones de forma automatica por defecto:

| Java | TypeScript |
| :--- | ---------: |
| void | void |
| boolean | boolean |
| byte | number |
| short | number |
| int | number |
| long | number |
| float | number |
| double | number |
| char | string |
| boolean[] | boolean[] |
| byte[] | number[] |
| short[] | number[] |
| int[] | number[] |
| long[] | number[] |
| float[] | number[] |
| double[] | number[] |
| char[] | string[] |
| java.lang.Void | void |
| java.lang.Object | any |
| java.lang.Boolean | boolean |
| java.lang.Double | number |
| java.lang.Float | number |
| java.lang.Integer | number |
| java.lang.Long | number |
| java.lang.Short | number |
| java.lang.Char | string |
| java.lang.String | string |
| java.lang.CharSequence | string |
| java.lang.Boolean[] | boolean[] |
| java.lang.Double[] | number[] |
| java.lang.Float[] | number[] |
| java.lang.Integer[] | number[] |
| java.lang.Long[] | number[] |
| java.lang.Short[] | number[] |
| java.lang.Char[] | string[] |
| java.lang.String[] | string[] |
| java.lang.CharSequence[] | string[] |
| java.lang.Object[] | any[] |
| java.lang.Iterable | any[] |
| java.util.function.BiConsumer | &lt;T extends any, U extends any&gt;(arg0: T, arg1: U) =&gt; void |
| java.util.function.BiFunction | &lt;T extends any, U extends any, R extends any&gt;(arg0: T, arg1: U) =&gt; R |
| java.util.function.BinaryOperator | &lt;T extends any&gt;(arg0: T, arg1: T) =&gt; T |
| java.util.function.BiPredicate | &lt;T extends any, U extends any&gt;(arg0: T, arg1: U) =&gt; boolean |
| java.util.function.Consumer | &lt;T extends any&gt;(arg0: T) =&gt; void |
| java.util.function.Function | &lt;T extends any, R extends any&gt;(arg0: T) =&gt; R |
| java.util.function.Predicate | &lt;T extends any&gt;(arg0: T) =&gt; boolean |
| java.util.function.Supplier | &lt;T extends any&gt;() =&gt; T |
| java.util.function.UnaryOperator | &lt;T extends any&gt;(arg0: T) =&gt; T |
| java.time.Instant | Date |
| java.time.LocalDate | Date |
| java.time.LocalDateTime | Date |
| java.time.ZonedDateTime | Date |
| java.lang.Iterable&lt;T&gt; | &lt;T&gt;[] |
| java.util.Collection&lt;T&gt; | &lt;T&gt;[] |
| java.util.List&lt;T&gt; | &lt;T&gt;[] |
| java.util.Map&lt;K, V&gt; | { [key: &lt;K&gt;]: &lt;V&gt; } |
