# htpasswd Auth Provider implementation

\<p\> We provide an implementation of `AuthProvider` which uses the
Apache htpasswd file format to perform authentication. The provider will
not watch for updates to the file after loading. If you need dynamic
user management it would be more convenient to use dynamic providers
such as jdbc or mongo providers. \<p\> To use this project, add the
following dependency to the *dependencies* section of your build
descriptor: \<p\> \* Maven (in your `pom.xml`): \<p\>

``` xml
<dependency>
<groupId>io.vertx</groupId>
<artifactId>vertx-auth-htpasswd</artifactId>
<version>${maven.version}</version>
</dependency>
```

\<p\> \* Gradle (in your `build.gradle` file): \<p\>

``` groovy
compile 'io.vertx:vertx-auth-htpasswd:${maven.version}'
```

\<p\> To create an instance you first need an htpasswd file. This file
is created using the apache htpasswd tool. \<p\> Once you’ve got one of
these you can create a `HtpasswdAuth` instance as follows: \<p\>

``` js
import { HtpasswdAuth } from "@vertx/auth-htpasswd"
let authProvider = HtpasswdAuth.create(vertx, new HtpasswdAuthOptions());
```

\<p\> Once you’ve got your instance you can authenticate with it just
like any `AuthProvider`. \<p\> The out of the box config assumes the
usage of the file htpasswd in the root of the project. \<p\> == Provider
internal behavior \<p\> The provider will load the specified htpasswd
file at start time and will not watch for modifications. If you require
dynamic reloads, you will need to restart the provider. \<p\> The
implementation does not have any other state than the htpasswd file
itself. \<p\> == Authentication \<p\> When authenticating using this
implementation, it assumes that the username and password are parsed as
a JSON object which we refer from now on as authentication info: \<p\>

``` js
let authInfo = {
  "username" : "someUser",
  "password" : "somePassword"
};

authProvider.authenticate(authInfo, (res, res_err) => {
  if (res.succeeded()) {
    let user = res.result();
  } else {
    // Failed!
  }
});
```

\<p\> == Autorization \<p\> Apache htpasswd file is a pure
authentication mechanism and not authorization. This means that
permission checks will always be `false`.
