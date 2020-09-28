# Hello World

Najprostszą aplikacją jaką możemy stworzyć jest `Hello World`, który umieścimy w pliku `hello-es4x.js`:

```js
vertx.createHttpServer()
  .requestHandler(req => {
    req.response()
      .end('Hello ES4X world!');
  })
  .listen(8080);
```

Teraz możesz uruchomić aplikację wywołując:

```bash
$ es4x hello-es4x.js
```

::: tip
W systemach typu UNIX, skrypty mogą być wykonywalne i dzięki użyciu shebanga `#!/usr/bin/env es4x` mogą być uruchamiane
automatycznie. Jednak taka dependencja powinna być wówczas obecna w katalogu bieżącym.
:::

I w drugim terminalu wpisując:

```bash
$ curl localhost:8080
Hello ES4X world!
```

::: warning
Wywoływanie skryptów korzystających z komendy `es4x` może być przydatne w małych skryptach, nie posiadających innych
dependencji niż `vertx`. W przypadku bardziej złożonych aplikacji korzystaj z menadżera projektu lub z menadżera pakietów.
:::

## Tworzenie nowego projektu

ES4X używa `npm` jako menadżera pakietów. Aby stworzyć nowy projekt można użyć komendy przez niego zapewnionej:

```bash
# stworzenie folderu z projektem
mkdir myapp

# wejście do folderu z projektem
cd myapp

# stworzenie projektu
es4x project
```

Podstawowa konfiguracja projektu znajduje się w pliku `package.json`:

```json{7-9,11-17}
{
  "version" : "1.0.0",
  "description" : "This is a ES4X empty project.",
  "name" : "myapp",
  "main" : "index.js",
  "scripts" : {
    "test" : "es4x test index.test.js",
    "postinstall" : "es4x install",
    "start" : "es4x"
  },
  "dependencies": {
    "@es4x/create": "latest",
    "@vertx/unit": "latest"
  },
  "dependencies": {
    "@vertx/core": "latest"
  },
  "keywords" : [ ],
  "author" : "",
  "license" : "ISC"
}
```

::: tip
Dla projektów tworzonych za pomocą `TypeScript` uruchom narzędzie do tworzenia projektu wywołując: `es4x project --ts`
:::

`post-install` hook odwoła się do es4x, aby rozwiązać wszystkie `maven dependencies` i stworzyć skrypt `es4x-launcher`.

::: tip
Skrypt `es4x-launcher` zapewni, że aplikacja będzie korzystać z es4x a nie z `nodejs`. Skrypt może być użyty na
produkcji, gdzie pakiet `@es4x/create` może być pominięty.
:::

### create-vertx-app

Używając `create-vertx-app` możesz szybko uruchomić swoją aplikację ES4X TypeScript lub JavaScript używając zaledwie
kilku przycisków. Jeśli wolisz korzystać z GUI przy tworzeniu aplikacji możesz skorzystać z tego samego generatora, jak
przy [PWA](https://vertx-starter.jetdrone.xyz/#npm).

<asciinema :src="$withBase('/cast/es4x-ts.cast')" cols="80" rows="24" />

## Dodawanie dependencji

Dodawanie dependencji nie różni się od sposobu w jaki robią to deweloperzy `JavaScript`:

```bash
# dodanie innych dependencji...
npm install @vertx/unit --save-dev # OR yarn add -D @vertx/unit
npm install @vertx/web --save-prod # OR yarn add @vertx/web

# spowoduje pobranie dependencji npm + java
npm install
```

## Kodowanie

Po skończonym setupie projektu czas na napisanie kodu. Zgodnie z tym, co zostało powiedziane wcześniej, ES4X używa
definicji `TypeScripta`, aby zapewnić lepsze doświadczenia developerom za pomocą uzupełniania kodu i opcjonalnego
sprawdzania poprawności typów.

Dla wszystkich aplikacji ES4X istnieje globalny obiekt `vertx`, który jest skonfigurowaną instancją *vert.x* i którą
można użyć w aplikacji.

::: tip
Aby móc korzystać z uzupełniania kodu w [Visual Studio Code](https://code.visualstudio.com/) pierwsza linijka kodu w
głównym skrypcie powinna brzmieć:

```js
/// <reference types="es4x" />
```
:::

Aplikacja powitalna w pliku `index.js` powinna wyglądać następująco:

```js{1-2}
/// <reference types="es4x" />
// @ts-check
import { Router } from '@vertx/web';

const app = Router.router(vertx);

app.route('/').handler(ctx => {
  ctx.response()
    .end('Hello from Vert.x Web!');
});

vertx.createHttpServer()
  .requestHandler(app)
  .listen(8080);

console.log('Server listening at: http://localhost:8080/')
```

Powyższy program wystartuje serwer, który będzie nasłuchiwał połączeń na porcie 8080. Program będzie odpowiadał "`Hello
from Vert.x Web!`" na zapytania dla root URL (`/`) lub route. Dla każdej innej ścieżki będzie odpowiadał **404 Not
Found**.

::: warning
W plikach `.js` można korzystać ze składni ES6. ES4X przetłumaczy to na polecenia `require()` w `commonjs`. Polecenia
`exports` niestety nie zostaną przetłumaczone. To udogodnienie będzie działało wyłącznie w IDE mającym funkcję auto
importu takim jak `Visual Studio Code`.
:::

## Wsparcie MJS

ES4X wspiera też pliku o rozszerzeniu `.mjs`. W takim przypadku nie zostanie użyty `require()` z `commonjs`, lecz
graaljs native module loader.

Graaljs wspiera zarówno `import` jak i `export` w plikach o rozszerzeniu `.mjs`, powodując, że będą one działały jak w
specyfikacji ES6.

::: tip
Aby włączyć wsparcie dla `.mjs` skorzystaj z rozszerzenia `.mjs` w plikach `JavaScript` lub uruchom swoją aplikację z
flagą `-Desm`.
:::

::: warning
Niemożliwym jest mieszanie `commonjs` oraz `esm` w ramach jednego projektu. Jeśli nie jesteś pewny której specyfikacji
powinieneś użyć, korzystaj z 'commonjs'.
:::
