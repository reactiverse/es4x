(window.webpackJsonp=window.webpackJsonp||[]).push([[66],{435:function(t,a,e){"use strict";e.r(a);var n=e(42),s=Object(n.a)({},(function(){var t=this,a=t.$createElement,e=t._self._c||a;return e("ContentSlotsDistributor",{attrs:{"slot-key":t.$parent.slotKey}},[e("h1",{attrs:{id:"eclipse-vert-x"}},[e("a",{staticClass:"header-anchor",attrs:{href:"#eclipse-vert-x"}},[t._v("#")]),t._v(" Eclipse Vert.x")]),t._v(" "),e("p",[t._v("Vert.x jest domyślnym modelem programowania używanym przez ES4X. Jednak w tym wydaniu jest tu kilka usprawnień, w\nporównaniu do standardowego "),e("a",{attrs:{href:"https://vertx.io",target:"_blank",rel:"noopener noreferrer"}},[t._v("Vert.x APIs"),e("OutboundLink")],1),t._v(".")]),t._v(" "),e("h2",{attrs:{id:"wygenerowane-api"}},[e("a",{staticClass:"header-anchor",attrs:{href:"#wygenerowane-api"}},[t._v("#")]),t._v(" Wygenerowane API")]),t._v(" "),e("p",[t._v("Wszystkie API publikowane w "),e("code",[t._v("npm")]),t._v(" pod nazwami "),e("code",[t._v("@vertx")]),t._v(" i "),e("code",[t._v("@reactiverse")]),t._v(" są kodem wygenerowanym. Generacja kodu jest\npozwala na to, żeby wszystkie takie API mogły być używane przez użytkowników "),e("code",[t._v("JavaScript")]),t._v(" w takim formacie, jaki jest\ndla nich wygodny bez wpływu na wydajność aplikacji.")]),t._v(" "),e("p",[t._v("Interakcja z JVM dzieje się w obiektach "),e("code",[t._v("Javy")]),t._v(". Najważniejszą częścią jest wyciągnięcie klasy JVM do JS:")]),t._v(" "),e("div",{staticClass:"language-js extra-class"},[e("pre",{pre:!0,attrs:{class:"language-js"}},[e("code",[e("span",{pre:!0,attrs:{class:"token comment"}},[t._v("// Import the java.lang.Math class to be usable")]),t._v("\n"),e("span",{pre:!0,attrs:{class:"token comment"}},[t._v("// as a JS type in the script")]),t._v("\n"),e("span",{pre:!0,attrs:{class:"token keyword"}},[t._v("const")]),t._v(" Math "),e("span",{pre:!0,attrs:{class:"token operator"}},[t._v("=")]),t._v(" Java"),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(".")]),e("span",{pre:!0,attrs:{class:"token function"}},[t._v("type")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("(")]),e("span",{pre:!0,attrs:{class:"token string"}},[t._v("'javalang.Math'")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(")")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(";")]),t._v("\n")])])]),e("p",[t._v("Teraz można tak zrobić dla wszystkich API, jakie chcemy, jednak istnieje kilka ograniczeń, które ES4X próbuje rozwiązać:")]),t._v(" "),e("ul",[e("li",[e("strong",[t._v("Podatność na usterki")]),t._v(" - Korzystający musi dokładnie znać Java API i Typ, aby poprawnie użyć ich w JavaScript.")]),t._v(" "),e("li",[e("strong",[t._v("Brak możliwości zdefiniowania zależności")]),t._v(" - Jeśli potrzebujesz użyć API z innych modułów, importowanie klasy za\nklasą nie może definiować zależności między nimi.")]),t._v(" "),e("li",[e("strong",[t._v("Brak wsparcia IDE")]),t._v(" - Developer musi znać API zanim zacznie z niego korzystać, ponieważ IDE mu w tym nie pomoże.")])]),t._v(" "),e("p",[t._v("Generator ES4X rozwiązuje problemy poprzez stworzenie modułu "),e("code",[t._v("npm")]),t._v(" dla każdego modułu "),e("code",[t._v("vertx")]),t._v(" i wypisuje definicje dla\nkażdej klasy.")]),t._v(" "),e("p",[t._v("Każdy moduł ma następujące pliki:")]),t._v(" "),e("ul",[e("li",[e("code",[t._v("package.json")]),t._v(" - Definiuje zależności między modułami")]),t._v(" "),e("li",[e("code",[t._v("index.js")]),t._v(" - interfejsy commonjs API")]),t._v(" "),e("li",[e("code",[t._v("index.mjs")]),t._v(" - interfejsy ESM API")]),t._v(" "),e("li",[e("code",[t._v("index.d.ts")]),t._v(" - Pełne definicje typów dla interfejsów API")]),t._v(" "),e("li",[e("code",[t._v("enum.js")]),t._v(" - wyliczenia commonjs API")]),t._v(" "),e("li",[e("code",[t._v("enum.mjs")]),t._v(" - wyliczenia ESM API")]),t._v(" "),e("li",[e("code",[t._v("enum.d.ts")]),t._v(" - Pełne definicje typów dla wyliczeń API")]),t._v(" "),e("li",[e("code",[t._v("options.js")]),t._v(" - obiekty danych commonjs API.")]),t._v(" "),e("li",[e("code",[t._v("options.mjs")]),t._v(" - obiekty danych ESM API.")]),t._v(" "),e("li",[e("code",[t._v("options.d.ts")]),t._v(" - Pełne definicje typów dla obiektów danych API")])]),t._v(" "),e("p",[t._v("Wszystkie pliki "),e("code",[t._v("index")]),t._v(" będą uproszczały import klas JVM poprzez zamianę, np:")]),t._v(" "),e("div",{staticClass:"language-js extra-class"},[e("pre",{pre:!0,attrs:{class:"language-js"}},[e("code",[e("span",{pre:!0,attrs:{class:"token comment"}},[t._v("// without ES4X")]),t._v("\n"),e("span",{pre:!0,attrs:{class:"token keyword"}},[t._v("const")]),t._v(" Router "),e("span",{pre:!0,attrs:{class:"token operator"}},[t._v("=")]),t._v(" Java"),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(".")]),e("span",{pre:!0,attrs:{class:"token function"}},[t._v("type")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("(")]),e("span",{pre:!0,attrs:{class:"token string"}},[t._v("'io.vertx.ext.web.Router'")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(")")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(";")]),t._v("\n"),e("span",{pre:!0,attrs:{class:"token comment"}},[t._v("// with")]),t._v("\n"),e("span",{pre:!0,attrs:{class:"token keyword"}},[t._v("import")]),t._v(" "),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("{")]),t._v(" Router "),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("}")]),t._v(" "),e("span",{pre:!0,attrs:{class:"token keyword"}},[t._v("from")]),t._v(" "),e("span",{pre:!0,attrs:{class:"token string"}},[t._v("'@vertx/web'")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(";")]),t._v("\n")])])]),e("p",[t._v("Ta mała zmiana spowoduje, że IDE będą asystować w developmencie, jak również menadżerom pakietów w pobieraniu\npotrzebnych dependencji. Wszystkie pliki "),e("code",[t._v(".d.ts")]),t._v(" będą podpowiadać IDE w kwestii typów oraz będą wspierały proces\nuzupełniania kodu.")]),t._v(" "),e("h2",{attrs:{id:"promise-future"}},[e("a",{staticClass:"header-anchor",attrs:{href:"#promise-future"}},[t._v("#")]),t._v(" Promise/Future")]),t._v(" "),e("p",[t._v("Vert.x ma 2 typy:")]),t._v(" "),e("ul",[e("li",[e("code",[t._v("io.vertx.core.Future")])]),t._v(" "),e("li",[e("code",[t._v("io.vertx.core.Promise")])])]),t._v(" "),e("p",[t._v("Co dziwne, "),e("code",[t._v("Promise")]),t._v(" z Vert.x nie jest tym samym co "),e("code",[t._v("Future")]),t._v(" z JavaScript. W języku JavaScript:")]),t._v(" "),e("ul",[e("li",[t._v("Vert.x "),e("code",[t._v("Future")]),t._v(" === JavaScript "),e("code",[t._v("Promise Like (Thenable)")])]),t._v(" "),e("li",[t._v("Vert.x "),e("code",[t._v("Promise")]),t._v(" === JavaScript "),e("code",[t._v("Executor Function")])])]),t._v(" "),e("h2",{attrs:{id:"async-await"}},[e("a",{staticClass:"header-anchor",attrs:{href:"#async-await"}},[t._v("#")]),t._v(" async/await")]),t._v(" "),e("p",[e("code",[t._v("async/await")]),t._v(" jest wspierany bez żadnej konieczności kompilacji ze strony "),e("code",[t._v("GraalVM")]),t._v(". ES4X dodaje extra funkcjonalność do\ntypu "),e("code",[t._v("Future")]),t._v(" Vert.x. API, które zwracają Vert.x "),e("code",[t._v("Future")]),t._v(" mogą być użyte jako "),e("code",[t._v("Thenable")]),t._v(", to znaczy że:")]),t._v(" "),e("div",{staticClass:"language-js extra-class"},[e("pre",{pre:!0,attrs:{class:"language-js"}},[e("code",[e("span",{pre:!0,attrs:{class:"token comment"}},[t._v("// using the Java API")]),t._v("\nvertx"),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(".")]),e("span",{pre:!0,attrs:{class:"token function"}},[t._v("createHttpServer")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("(")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(")")]),t._v("\n  "),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(".")]),e("span",{pre:!0,attrs:{class:"token function"}},[t._v("listen")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("(")]),e("span",{pre:!0,attrs:{class:"token number"}},[t._v("0")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(")")]),t._v("\n  "),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(".")]),e("span",{pre:!0,attrs:{class:"token function"}},[t._v("onSuccess")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("(")]),e("span",{pre:!0,attrs:{class:"token parameter"}},[t._v("server")]),t._v(" "),e("span",{pre:!0,attrs:{class:"token operator"}},[t._v("=>")]),t._v(" "),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("{")]),t._v("\n    console"),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(".")]),e("span",{pre:!0,attrs:{class:"token function"}},[t._v("log")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("(")]),e("span",{pre:!0,attrs:{class:"token string"}},[t._v("'Server ready!'")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(")")]),t._v("\n  "),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("}")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(")")]),t._v("\n  "),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(".")]),e("span",{pre:!0,attrs:{class:"token function"}},[t._v("onFailure")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("(")]),e("span",{pre:!0,attrs:{class:"token parameter"}},[t._v("err")]),t._v(" "),e("span",{pre:!0,attrs:{class:"token operator"}},[t._v("=>")]),t._v(" "),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("{")]),t._v("\n    console"),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(".")]),e("span",{pre:!0,attrs:{class:"token function"}},[t._v("log")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("(")]),e("span",{pre:!0,attrs:{class:"token string"}},[t._v("'Server startup failed!'")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(")")]),t._v("\n  "),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("}")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(")")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(";")]),t._v("\n")])])]),e("p",[t._v("Może zostać użyte jako "),e("code",[t._v("Thenable")]),t._v(":")]),t._v(" "),e("div",{staticClass:"language-js extra-class"},[e("pre",{pre:!0,attrs:{class:"language-js"}},[e("code",[e("span",{pre:!0,attrs:{class:"token keyword"}},[t._v("try")]),t._v(" "),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("{")]),t._v("\n  "),e("span",{pre:!0,attrs:{class:"token keyword"}},[t._v("let")]),t._v(" server "),e("span",{pre:!0,attrs:{class:"token operator"}},[t._v("=")]),t._v(" "),e("span",{pre:!0,attrs:{class:"token keyword"}},[t._v("await")]),t._v(" vertx\n    "),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(".")]),e("span",{pre:!0,attrs:{class:"token function"}},[t._v("createHttpServer")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("(")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(")")]),t._v("\n    "),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(".")]),e("span",{pre:!0,attrs:{class:"token function"}},[t._v("listen")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("(")]),e("span",{pre:!0,attrs:{class:"token number"}},[t._v("0")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(")")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(";")]),t._v("\n\n  console"),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(".")]),e("span",{pre:!0,attrs:{class:"token function"}},[t._v("log")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("(")]),e("span",{pre:!0,attrs:{class:"token string"}},[t._v("'Server Ready!'")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(")")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(";")]),t._v("\n"),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("}")]),t._v(" "),e("span",{pre:!0,attrs:{class:"token keyword"}},[t._v("catch")]),t._v(" "),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("(")]),t._v("err"),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(")")]),t._v(" "),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("{")]),t._v("\n  console"),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(".")]),e("span",{pre:!0,attrs:{class:"token function"}},[t._v("log")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("(")]),e("span",{pre:!0,attrs:{class:"token string"}},[t._v("'Server startup failed!'")]),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(")")]),t._v("\n"),e("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("}")]),t._v("\n")])])]),e("div",{staticClass:"custom-block tip"},[e("p",{staticClass:"custom-block-title"},[t._v("TIP")]),t._v(" "),e("p",[e("code",[t._v("async/await")]),t._v(" działa nawet z pętlami, co powoduje, że praca z asynchronicznym kodem staje się prostsza, nawet podczas\nmieszania kodu Javy i JavaScript.")])]),t._v(" "),e("h2",{attrs:{id:"konwersja-typow"}},[e("a",{staticClass:"header-anchor",attrs:{href:"#konwersja-typow"}},[t._v("#")]),t._v(" Konwersja typów")]),t._v(" "),e("p",[t._v("Vert.x jest stworzony w "),e("code",[t._v("Javie")]),t._v(", jednak w "),e("code",[t._v("JavaScript")]),t._v(" nie musimy się martwić w takim stopniu jak tam. ES4X wykonuje\nkilka automatycznych konwersji:")]),t._v(" "),e("table",[e("thead",[e("tr",[e("th",{staticStyle:{"text-align":"left"}},[t._v("Java")]),t._v(" "),e("th",{staticStyle:{"text-align":"right"}},[t._v("TypeScript")])])]),t._v(" "),e("tbody",[e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("void")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("void")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("boolean")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("boolean")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("byte")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("number")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("short")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("number")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("int")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("number")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("long")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("number")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("float")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("number")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("double")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("number")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("char")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("string")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("boolean[]")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("boolean[]")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("byte[]")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("number[]")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("short[]")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("number[]")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("int[]")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("number[]")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("long[]")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("number[]")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("float[]")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("number[]")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("double[]")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("number[]")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("char[]")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("string[]")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.lang.Void")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("void")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.lang.Object")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("any")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.lang.Boolean")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("boolean")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.lang.Double")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("number")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.lang.Float")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("number")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.lang.Integer")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("number")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.lang.Long")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("number")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.lang.Short")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("number")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.lang.Char")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("string")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.lang.String")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("string")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.lang.CharSequence")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("string")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.lang.Boolean[]")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("boolean[]")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.lang.Double[]")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("number[]")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.lang.Float[]")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("number[]")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.lang.Integer[]")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("number[]")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.lang.Long[]")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("number[]")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.lang.Short[]")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("number[]")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.lang.Char[]")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("string[]")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.lang.String[]")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("string[]")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.lang.CharSequence[]")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("string[]")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.lang.Object[]")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("any[]")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.lang.Iterable")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("any[]")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.util.function.BiConsumer")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("<T extends any, U extends any>(arg0: T, arg1: U) => void")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.util.function.BiFunction")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("<T extends any, U extends any, R extends any>(arg0: T, arg1: U) => R")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.util.function.BinaryOperator")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("<T extends any>(arg0: T, arg1: T) => T")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.util.function.BiPredicate")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("<T extends any, U extends any>(arg0: T, arg1: U) => boolean")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.util.function.Consumer")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("<T extends any>(arg0: T) => void")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.util.function.Function")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("<T extends any, R extends any>(arg0: T) => R")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.util.function.Predicate")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("<T extends any>(arg0: T) => boolean")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.util.function.Supplier")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("<T extends any>() => T")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.util.function.UnaryOperator")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("<T extends any>(arg0: T) => T")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.time.Instant")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("Date")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.time.LocalDate")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("Date")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.time.LocalDateTime")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("Date")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.time.ZonedDateTime")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("Date")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.lang.Iterable<T>")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("<T>[]")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.util.Collection<T>")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("<T>[]")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.util.List<T>")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("<T>[]")])]),t._v(" "),e("tr",[e("td",{staticStyle:{"text-align":"left"}},[t._v("java.util.Map<K, V>")]),t._v(" "),e("td",{staticStyle:{"text-align":"right"}},[t._v("{ [key: <K>]: <V> }")])])])])])}),[],!1,null,null,null);a.default=s.exports}}]);