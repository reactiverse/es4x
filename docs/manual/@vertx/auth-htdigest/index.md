# .htdigest Auth Provider implementation

We provide an implementation of `AuthProvider` which uses the .digest
file format to perform authentication. The provider will not watch for
updates to the file after loading. If you need dynamic user management
it would be more convenient to use dynamic providers such as jdbc or
mongo providers.

To use this project, add the following dependency to the *dependencies*
section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-auth-htdigest</artifactId>
 <version>${maven.version}</version>
</dependency>
```

  - Gradle (in your `build.gradle` file):

<!-- end list -->

``` groovy
compile 'io.vertx:vertx-auth-htdigest:${maven.version}'
```

To create an instance you first need an .htdigest file. This file is
created using the apache htdigest tool.

Once you’ve got one of these you can create a `HtdigestAuth` instance as
follows:

``` js
import { HtdigestAuth } from "@vertx/auth-htdigest"
let authProvider = HtdigestAuth.create(vertx, ".htdigest");
```

Once you’ve got your instance you can authenticate with it just like any
`AuthProvider`.

The out of the box config assumes the usage of the file .htdigest in the
root of the project.

# Authentication

When authenticating using this implementation, it assumes that the
digest authorization header is parsed as a JSON object which we refer
from now on as authentication info:

``` js
let authInfo = {
  "username" : "Mufasa",
  "realm" : "testrealm@host.com",
  "nonce" : "dcd98b7102dd2f0e8b11d0f600bfb0c093",
  "method" : "GET",
  "uri" : "/dir/index.html",
  "response" : "6629fae49393a05397450978507c4ef1"
};

authProvider.authenticate(authInfo, (res, res_err) => {
  if (res.succeeded()) {
    let user = res.result();
  } else {
    // Failed!
  }
});
```

# Provider internal behavior

The provider will load the specified .htdigest file at start time and
will not watch for modifications. If you require dynamic reloads, you
will need to restart the provider.

The implementation does not have any other state than the digest file
itself, this means that validation and generation of `nonce` strings and
counters must be handled outside this provider.

Finally `auth-int` `qop` is not supported to avoid having to consume
potential large blobs of data in order to validate the hash of the full
request. This is usually also not present on modern web browsers.

If validating if a user has a particular permission it will always
return false since the htdigest file is a pure authentication mechanism
and not authorization.
