# GraalVM

ES4X używa GraalVM, jednakże ten sam kod będzie mógł zostać uruchomiony w trybie **interpretowanym** w Javie 8, 9, 10 i
w OpenJ9.

ES4X uruchamia się w trybie **skompilowanym** dla JDK >= 11 (z JVMCI) lub GraalVM.

::: tip
W innych słowach lepiej jest używać Javy w wersji >= 11 lub GraalVM.
:::

Jest kilka benefitów z korzystania z GraalJS, chociażby zupdatowany support dla >= ES6, support generatorów, itp.

## Różnice z Nashornem

W przeciwieństwie do `Nashorn`, `GraalJS` *java* interop podąża za tą samą nazwą klasy/metody, co w Javie. Na przykład
jeśli chcemy odnieść się do pól będących getterem czy setterem w klasie Javy musimy używać nazw *getter* oraz *setter*.
Np.:

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

Używając obiektu Javy z Graala, to nie będzie działało w ten sposób:

```js
var hello = new Hello();
// get the name
var name = hello.name; // FAIL
// set the name
hello.name = 'Paulo';  // FAIL
```

Powyższy kod nie jest poprawny w Graal i powinien wyglądać:

```js
var hello = new Hello();
// get the name
var name = hello.getName();
// set the name
hello.setName('Paulo');
```

## Wątkowanie

GraalJS bardzo rygorystycznie podchodzi do posiadania jednego wątku w trakcie działania programu. Podczas pracy tylko z
asynchronicznym Vert.x API nie powinno być z tym problemu, jednak inne biblioteki mogą już sprawiać problem. Aby ominąć
te ograniczenia radzi się, aby używać `Worker` API lub `EventBusa`.

::: warning
GraalJS nie pozwala na wielowątkowy dostęp do tego samego kontekstu skryptowego. Jeśli wymagana jest praca na wielu
wątkach, rozważ użycie [Worker API](./worker).
:::

## Obrazy natywne

Obecnie nie możesz generować obrazów natywnych przy pomocy ES4X. Ograniczenie jest spowodowane tym, że statyczna analiza
kompilatora AOT nie weźmie pod uwagę kodu javowego wywołanego ze skryptu (więc klasy nie będą dostępne), dodatkowo
trzeba wziąć pod uwagę fakt, że kompilator nie wspiera jvm interop podczas runtime'u.

