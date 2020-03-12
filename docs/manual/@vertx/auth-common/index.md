This Vert.x component provides interfaces for authentication and
authorisation that can be used from your Vert.x applications and can be
backed by different providers.

Vert.x auth is also used by vertx-web to handle its authentication and
authorisation.

To use this project, add the following dependency to the *dependencies*
section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-auth-common</artifactId>
 <version>${maven.version}</version>
</dependency>
```

  - Gradle (in your `build.gradle` file):

<!-- end list -->

``` groovy
compile 'io.vertx:vertx-auth-common:${maven.version}'
```

# Basic concepts

*Authentication* means verifying the identity of a user.

*Authorisation* means verifying a user has an authority.

What the authority means is determined by the particular implementation
and we don’t mandate any particular model, e.g. a permissions/roles
model, to keep things very flexible.

For some implementations an authority might represent a permission, for
example the authority to access all printers, or a specific printer.
Other implementations must support roles too, and will often represent
this by prefixing the authority with something like `role:`, e.g.
`role:admin`. Another implementation might have a completely different
model of representing authorities.

To find out what a particular auth provider expects, consult the
documentation for that auth provider..

# Authentication

To authenticate a user you use `authenticate`.

The first argument is a JSON object which contains authentication
information. What this actually contains depends on the specific
implementation; for a simple username/password based authentication it
might contain something like:

    {
     "username": "tim"
     "password": "mypassword"
    }

For an implementation based on JWT token or OAuth bearer tokens it might
contain the token information.

Authentication occurs asynchronously and the result is passed to the
user on the result handler that was provided in the call. The async
result contains an instance of `User` which represents the authenticated
user and contains operations which allow the user to be authorised.

Here’s an example of authenticating a user using a simple
username/password implementation:

``` js
let authInfo = {
  "username" : "tim",
  "password" : "mypassword"
};

authProvider.authenticate(authInfo, (res) => {
  if (res.succeeded()) {

    let user = res.result();

    console.log("User " + user.principal() + " is now authenticated");

  } else {
    res.cause().printStackTrace();
  }
});
```

# Authorisation

Once you have an `User` instance you can call methods on it to authorise
it.

to check if a user has a specific authority you use `isAuthorised`.

The results of all the above are provided asynchronously in the handler.

Here’s an example of authorising a user:

``` js
user.isAuthorized("printers:printer1234", (res) => {
  if (res.succeeded()) {

    let hasAuthority = res.result();

    if (hasAuthority) {
      console.log("User has the authority");
    } else {
      console.log("User does not have the authority");
    }

  } else {
    res.cause().printStackTrace();
  }
});
```

And another example of authorising in a roles based model which uses
`role:` as a prefix.

Please note, as discussed above how the authority string is interpreted
is completely determined by the underlying implementation and Vert.x
makes no assumptions here.

## Caching authorities

The user object will cache any authorities so subsequently calls to
check if it has the same authorities will result in the underlying
provider being called.

In order to clear the internal cache you can use `clearCache`.

## The User Principal

You can get the Principal corresponding to the authenticated user with
`principal`.

What this returns depends on the underlying implementation.

# Creating your own auth implementation

If you wish to create your own auth provider you should implement the
`AuthProvider` interface.

We provide an abstract implementation of user called `AbstractUser`
which you can subclass to make your user implementation. This contains
the caching logic so you don’t have to implement that yourself.

If you wish your user objects to be clusterable you should make sure
they implement `ClusterSerializable`.

# Pseudo Random Number Generator

Since Secure Random from java can block during the acquisition of
entropy from the system, we provide a simple wrapper around it that can
be used without the danger of blocking the event loop.

By default this PRNG uses a mixed mode, blocking for seeding, non
blocking for generating. The PRNG will also reseed every 5 minutes with
64bits of new entropy. However this can all be configured using the
system properties:

  - io.vertx.ext.auth.prng.algorithm e.g.: SHA1PRNG

  - io.vertx.ext.auth.prng.seed.interval e.g.: 1000 (every second)

  - io.vertx.ext.auth.prng.seed.bits e.g.: 128

Most users should not need to configure these values unless if you
notice that the performance of your application is being affected by the
PRNG algorithm.

## Sharing Pseudo Random Number Generator

Since the Pseudo Random Number Generator objects are expensive in
resources, they consume system entropy which is a scarce resource it can
be wise to share the PRNG’s across all your handlers. In order to do
this and to make this available to all languages supported by Vert.x you
should look into the `VertxContextPRNG`.

This interface relaxes the lifecycle management of PRNG’s for the end
user and ensures it can be reused across all your application, for
example:

``` js
import { VertxContextPRNG } from "@vertx/auth-common"
// Generate a secure token of 32 bytes as a base64 string
let token = VertxContextPRNG.current(vertx).nextString(32);
// Generate a secure random integer
let randomInt = VertxContextPRNG.current(vertx).nextInt();
```

@author \<a href="mailto:julien@julienviet.com"\>Julien Viet\</a\>
@author \<a href="http://tfox.org"\>Tim Fox\</a\>
