# Workerzy

[MDN](https://developer.mozilla.org/en-US/docs/Web/API/Web_Workers_API/Using_web_workers) definiuje Web Workerów jako:

> Web Workerzy, najprościej rzecz ujmując, to część sieci web do uruchamiania skryptów w wątkach w tle. Wątek worker
>może wykonywać zadania bez ingerowania w interfejs użytkownika.

ES4X nie jest przeglądarką i nie zajmuje się interfejsem użytkownika, jednakże po stronie serwera również możesz
uruchamiać długotrwające zadania. W Vert.x wszystko, co nie blokuje działania, a więc nawet i tworzenie workerów powinno
korzystać z tej samej semantyki, dlatego nie możemy w pełni podążać za interfejsem workera. Zamieniliśmy więc jego
konstruktor na funkcję fabryki.

Wyobraź sobie, że w Twój kod musi uruchomić zadanie pochłaniające dużą ilość CPU. Nie powinieneś blokować pętli zdarzeń,
więc logicznym krokiem jest użycie w tym przypadku vert.x worker verticles. Worker API może zostać zmapowane do Vert.x
API z kilkoma małymi niuansami.

## Przykład Workera

Wyobraź sobie następujący kod workera:

```js
// Pobierz referencję do klasy Thread, żeby móc spowodować blokadę...
const Thread = Java.type('java.lang.Thread');

// Worker context jest referowany poprzez zmienną `self` tak jak w dokumentacji MDN
self.onmessage = function(e) {
  console.log('Message received from main script, will sleep 5 seconds...');
  // Spowoduj małe zaburzenie w pętli zdarzeń
  Thread.sleep(5 * 1000);
  var workerResult = 'Result: ' + (e.data[0] * e.data[1]);
  console.log('Posting message back to main script');
  // zwróć dane do głównego verticle
  postMessage(workerResult);
};
```

## Co musisz wiedzieć?

Workerzy są ładowani w osobnym kontekście, więc nie możesz dzielić funkcji z głównego verticle i workera, cała
komunikacja odbywa się poprzez przekazywanie wiadomości (eventbus) używając:

* `postMessage()` wysyła wiadomość na drugą stronę
* `onmessage` otrzymuje wiadomość z drugiej strony

### Verticle side

Vertical side API pozwala na otrzymywanie errorów i `terminate() - terminowanie` workerów, podczas gdy worker sam z
siebie nie może tego zrobić.

## Przykład Verticle

```js
Worker.create('workers/worker.js', function (create) {
  if (create.succeeded()) {
    var worker = create.result();

    worker.onmessage = function (msg) {
      console.log('onmessage: ' + msg);
    };

    worker.onerror = function (err) {
      console.err(err);
      // terminate the worker
      worker.terminate();
    };

    console.log('posting...');
    worker.postMessage({data: [2, 3]});
  }
});
```

Kod, który nie powininen zostać uruchomiony w pętli zdarzeń, to `Thread.sleep(5000)`. Działa on teraz jako wątek workera
pozostawiając pętlę zdarzeń wolną dla innych zadań IO.

## Wielojęzykowi Workerzy

Wciąż jest mozliwe napisanie workerów, którzy nie są workerami JavaScriptowymi. Workerzy muszą jednak spełniać bardzo
małą listę zasad:

* Workerzy muszą rejestrować adres: `{deploymentId}.out`, aby otrzymywać wiadomości z głównego skryptu.
* Workerzy powinni wysyłać wiadomości do: `{deploymentId}.in`, aby wysyłać wiadomości do głównego skryptu.
* Treść wiadomości powinna być w formacie `JSON.stringify(message)`, aby uniknąć konfliktów między językami.
* Spodziewa się, że workerzy będą działać lokalnie, jeśli chcesz połączyć workerów z jakiegokolwiek miejsca w klastrze,
musisz użyć konstruktora z dodatkowym argumentem `true`, np.: `new Worker('deploymentId', true)`.
