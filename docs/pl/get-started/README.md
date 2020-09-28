# Wprowadzenie

ES4X jest małym skryptem dla aplikacji EcmaScript >= 5, które działają przy pomocy [graaljs]
(https://github.com/graalvm/graaljs)
oraz [vert.x](https://vertx.io).
JavaScript jest językiem skryptowym, ale **nie** używa on `nodejs`.

## Jak to działa

Tworzenie aplikacji ES4x niczym nie rózni się od tworzenia aplikacji przy pomocy `JavaScript`.
Plik `package.json` definiuje projekt.
Ten zaś będzie używał i pobierał dependencje z dwóch różnych źródeł:

* [npm](https://www.npmjs.com/)
* **oraz** [maven central](https://search.maven.org/)

ES4X używa [GraalVM](https://www.graalvm.org), który jest wielojęzykowym runtimem JVM.
To znaczy, że można go użyć z każdym językiem JVM tak samo jak w aplikacjach opartych o `JavaScript`.

Vert.x z kolei jest używany przez ES4X, aby zapewnić zoptymalizowaną pętlę wydarzeń i dużą wydajnosć biblioteki IO.
Używając `Javy` zamiast `JavaScript` może bywać uciążliwe ze względu na brak IDE, które dawałoby wskazówki oraz gotowego
i przyjaznego API.
Z tego powodu ES4X ma kilka paczek opublikowanych na `npm`, co czyni development prostszym dzięki łątwiejszemu mapowaniu
`Java` API do `JavaScript` oraz pełnemu API w postaci plików `TypeScript` o rozszerzeniu `.d.ts`.


## Wydajność

ES4X był **najszybszym** `JavaScriptem` według TechEmpower Frameworks Benchmark
[Round #18](https://www.techempower.com/benchmarks/#section=data-r18). ES4X był najszybszy w porónaniu do innych
frameworków `JavaScriptowych` we wszystkich przeprowadzonych testach.

![round-18-js](./res/round-18-js.png)

Dodatkowo ES4X był w top #10 w porównaniu do wszystkich innych frameworków, wykazując się lepszą wydajnością niż
najpopularniejsze frameworki JVM.

![round-18-js](./res/round-18.png)
