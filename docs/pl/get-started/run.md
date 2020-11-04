# Uruchomienie

Aplikacja ES4x powinna stworzyć `es4x-launcher` podczas instalacji `npm`. Jeśli launcher nie jest obecny uruchom:

```bash
npm install # LUB yarn
```

::: tip
Launcher powinien się pojawić w folderze `node_modules/.bin/es4x-lancher.*`.
:::

Od tego momentu uruchomienie aplikacji wykonywać się bedzie po wywołaniu:

```bash
npm start # LUB yarn start
```

Ta komenda zamienia domyślną operację `npm` poprzez uruchomienie aplikacji na JVM runtime. Przy użyciu komendy z
 projektem *Hello World* output powinien wyglądać następująco:

```bash
Server listening at: http://localhost:8080/
Succeeded in deploying verticle
```

Teraz możesz wchodzić w interakcje z aplikacją przez przeglądarkę lub z pomocą klienta http:

```bash
> curl localhost:8080

Hello from Vert.x Web!
```

## Uruchamianie bez npm/yarn

Podczas deployowania aplikacji na produkcję może być naturalnym nie bundlowanie menadżera pakietów razem z aplikacją. W
takim przypadku podczas uruchamiania aplikacji nie używa się `npm`/`yarn` lecz:

```bash
./node_modules/.bin/es4x-launcher
```

::: tip
Możliwa jest zmiana sposobu startowania aplikacji, po wiecej sięgnij do:

```bash
./node_modules/.bin/es4x-launcher -help
```
:::

## Skalowanie liczby verticle

Skalowanie liczby verticle (które w niektórych przypadkach zwiększają wydajność) może zostać wykonane za pomocą:

```bash
# number of verticles to use:
N=2 \
  ./node_modules/.bin/es4x-launcher -instances $N
```

::: tip
Zazwyczaj zwiększenie liczby verticle do podwojonej liczby core dalej najlepszą wydajność.
:::

## Grupowanie

Tak samo jak liczba verticle, aplikacja ES4X może zostać zgrupowana za pomocą:

```bash
./node_modules/.bin/es4x-launcher -cluster
```

Aby dowiedzieć się więcej o grupowaniu przeczytaj oficjalną dokumentację [vert.x](https://www.vertx.io).
