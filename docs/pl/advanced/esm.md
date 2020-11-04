# Moduły EcmaScript

Moduły EcmaScript są **oficjalnym** formatem modułów dla JavaScript. `ESM` są wspierane przez ES4X podczas używania
jednej z dwóch opcji:

* Początkowy skrypt ma rozszerzenie`.mjs`
* Początkowy skrypt jest poprzedzony prefixem: `mjs:`

## Początkowy Skrypt

Na pierwszy rzut oka widać, że początkowy skrypt nie różni się za bardzo od skryptu commonjs, np. od `index.mjs`:

```js
import { Router } from '@vertx/web';
import { someRoute } from './routes';

const app = Router.router(vertx);

app.route('/').handler(someRoute);

vertx.createHttpServer()
  .requestHandler(app)
  .listen(8080);
```

W tym przypadku `someRoute` jest importowany z pliku `routes.mjs`:

```js
export function someRoute(ctx) {
  ctx.response()
    .end('Hello from ES4X!');
}
```

## Zgodność

Możesz zauważyć, że polecenia `import` w podstawowym skrypcie nie zawierają wspomnianego wcześniej rozszerzenia.
Taki zapis wymagany jest aby zachować zgodność:

```js{2}
import { Router } from '@vertx/web';
import { someRoute } from './routes';

// ...
```

Ta mała rozbieżność z oficjalną specyfikacją powoduje, że ES4X loader będzie szukał modułów w następującej kolejności:

1. Szukanie pliku o nazwie: `./routes`
2. Szukanie pliku z sufixem `.mjs`: `./routes.mjs`
3. Szukanie pliku z sufixem `.js`: `./routes.js`

::: warning
Podczas pracy z `ESM` funkcja `require()` nie jest dostępna!
:::

## Pobieranie modułów

Możliwe jest również pobieranie modułów podczas działania programu. Ta funkcja nie jest określona w `ES4X`. Opiera się
ona na oficjalnym loaderze modułów z `GraalJS`. Importowanie takich modułów nie jest trudne:

```js
import { VertxOptions } from 'https://unpkg.io/@vertx/core@3.9.1/mod.mjs';
```

Aby wszystko działało poprawnie, należy przestrzegać kilku zasad:

1. Moduły **HTTP** nie będą pobierane jeśli nie będzie zapewnionego [menadżera bezpieczeństwa](./security).
2. Jeśli taki moduł ma swój odpowiednik jako moduł `maven`, to taki moduł **NIE** będzie pobierany.
3. Pobieranie wykonywalnego kodu podczas działania programu może być kwestią związaną z bezpieczeństwem.

Oczywiście, mogą się zdarzyć przypadki, gdzie takie rozwiązanie będzie przydatne, np. aby uniknąć dependencji `npm`,
kiedy kod nie jest publiczny.

::: warning
Pobrane moduły nie będą przetwarzać żadnych dependencji ani ich mavenowych odpowiedników.
:::
