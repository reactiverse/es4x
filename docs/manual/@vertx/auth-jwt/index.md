# The JWT auth provider

This component contains an out of the box a JWT implementation. To use
this project, add the following dependency to the *dependencies* section
of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-auth-jwt</artifactId>
 <version>${maven.version}</version>
</dependency>
```

  - Gradle (in your `build.gradle` file):

<!-- end list -->

``` groovy
compile 'io.vertx:vertx-auth-jwt:${maven.version}'
```

JSON Web Token is a simple way to send information in the clear (usually
in a URL) whose contents can be verified to be trusted. JWT are well
suited for scenarios as:

  - In a Single Sign-On scenario where you want a separate
    authentication server that can then send user information in a
    trusted way.

  - Stateless API servers, very well suited for single page
    applications.

  - etc…​

Before deciding on using JWT, it’s important to note that JWT does not
encrypt the payload, it only signs it. You should not send any secret
information using JWT, rather you should send information that is not
secret but needs to be verified. For instance, sending a signed user id
to indicate the user that should be logged in would work great\! Sending
a user’s password would be very, very bad.

Its main advantages are:

  - It allows you to verify token authenticity.

  - It has a json body to contain any variable amount of data you want.

  - It’s completely stateless.

To create an instance of the provider you use `JWTAuth`. You specify the
configuration in a JSON object.

Here’s an example of creating a JWT auth provider:

``` java
import { JWTAuth } from "@vertx/auth-jwt"

let config = new JWTAuthOptions()
  .setKeyStore(new KeyStoreOptions()
    .setPath("keystore.jceks")
    .setPassword("secret"));

let provider = JWTAuth.create(vertx, config);
```

A typical flow of JWT usage is that in your application you have one end
point that issues tokens, this end point should be running in SSL mode,
there after you verify the request user, say by its username and
password you would do:

``` java
import { JWTAuth } from "@vertx/auth-jwt"

let config = new JWTAuthOptions()
  .setKeyStore(new KeyStoreOptions()
    .setPath("keystore.jceks")
    .setPassword("secret"));

let provider = JWTAuth.create(vertx, config);

// on the verify endpoint once you verify the identity of the user by its username/password
if ("paulo" == username && "super_secret" == password) {
  let token = provider.generateToken({
    "sub" : "paulo"
  }, new JWTOptions());
  // now for any request to protected resources you should pass this string in the HTTP header Authorization as:
  // Authorization: Bearer <token>
}
```

## Loading Keys

Loading keys can be performed in 3 different ways:

  - Using secrets (symmetric keys)

  - Using OpenSSL `pem` formatted files (pub/sec keys)

  - Using Java Keystore files (both symmetric and pub/sec keys)

It is recommended to avoid java keystores, as java keystores are complex
to generate and make many assumptions in order to choose the right key
for the authenticator.

### Using Symmetric Keys

The default signature method for JWT’s is known as `HS256`. `HS` stands
in this case for `HMAC Signature using SHA256`.

This is the simplest key to load. All you need is a secret that is
shared between you and the 3rd party, for example assume that the secret
is: `keyboard cat` then you can configure your Auth as:

``` java
import { JWTAuth } from "@vertx/auth-jwt"
let provider = JWTAuth.create(vertx, new JWTAuthOptions()
  .setPubSecKeys([new PubSecKeyOptions()
    .setAlgorithm("HS256")
    .setPublicKey("keyboard cat")
    .setSymmetric(true)]));

let token = provider.generateToken({
});
```

In this case the secret is configured as a public key, as it is a token
that is known to both parties and you configure your PubSec key as being
symmetric.

### Using RSA keys

This section is by no means a manual on OpenSSL and a read on OpenSSL
command line usage is advised. We will cover how to generate the most
common keys and how to use them with JWT auth.

Imagine that you would like to protect your application using the very
common `RS256` JWT algorithm. Contrary to some belief, 256 is not the
key length but the hashing algorithm signature length. Any RSA key can
be used with this JWT algorithm. Here is an information table:

| "alg" Param Value | Digital Signature Algorithm          |
| ----------------- | ------------------------------------ |
| *RS256*           | **RSASSA-PKCS1-v1\_5 using SHA-256** |
| *RS384*           | **RSASSA-PKCS1-v1\_5 using SHA-384** |
| *RS512*           | **RSASSA-PKCS1-v1\_5 using SHA-512** |

If you would like to generate a 2048bit RSA key pair, then you would do
(please remember **not** to add a passphrase otherwise you will not be
able to read the private key in the JWT auth):

    openssl genrsa -out private.pem 2048

You can observe that the key is correct as the file content is similar
to this:

    -----BEGIN RSA PRIVATE KEY-----
    MIIEowIBAAKCAQEAxPSbCQY5mBKFDIn1kggvWb4ChjrctqD4nFnJOJk4mpuZ/u3h
    ...
    e4k0yN3F1J1DVlqYWJxaIMzxavQsi9Hz4p2JgyaZMDGB6kGixkMo
    -----END RSA PRIVATE KEY-----

The standard JDK cannot read this file as is, so we **must** convert it
to PKCS8 format first:

    openssl pkcs8 -topk8 -inform PEM -in private.pem -out private_key.pem -nocrypt

Now the new file `private_key.pem` which resembles the original one
contains:

    -----BEGIN PRIVATE KEY-----
    MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDE9JsJBjmYEoUM
    ...
    0fPinYmDJpkwMYHqQaLGQyg=
    -----END PRIVATE KEY-----

If we are verifying tokens only (you will only need the private\_key.pem
file) however at some point you will need to issue tokens too, so you
will a public key. In this case you need to extract the public key from
the private key file:

    openssl rsa -in private.pem -outform PEM -pubout -out public.pem

And you should see that the content of the file is similar to this:

    -----BEGIN PUBLIC KEY-----
    MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxPSbCQY5mBKFDIn1kggv
    ...
    qwIDAQAB
    -----END PUBLIC KEY-----

Now you can use this to issue or validate tokens:

``` java
import { JWTAuth } from "@vertx/auth-jwt"
let provider = JWTAuth.create(vertx, new JWTAuthOptions()
  .setPubSecKeys([new PubSecKeyOptions()
    .setAlgorithm("RS256")
    .setPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxPSbCQY5mBKFDIn1kggv\nWb4ChjrctqD4nFnJOJk4mpuZ/u3h2ZgeKJJkJv8+5oFO6vsEwF7/TqKXp0XDp6IH\nbyaOSWdkl535rCYR5AxDSjwnuSXsSp54pvB+fEEFDPFF81GHixepIbqXCB+BnCTg\nN65BqwNn/1Vgqv6+H3nweNlbTv8e/scEgbg6ZYcsnBBB9kYLp69FSwNWpvPmd60e\n3DWyIo3WCUmKlQgjHL4PHLKYwwKgOHG/aNl4hN4/wqTixCAHe6KdLnehLn71x+Z0\nSyXbWooftefpJP1wMbwlCpH3ikBzVIfHKLWT9QIOVoRgchPU3WAsZv/ePgl5i8Co\nqwIDAQAB")
    .setSecretKey("MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDE9JsJBjmYEoUM\nifWSCC9ZvgKGOty2oPicWck4mTiam5n+7eHZmB4okmQm/z7mgU7q+wTAXv9Oopen\nRcOnogdvJo5JZ2SXnfmsJhHkDENKPCe5JexKnnim8H58QQUM8UXzUYeLF6khupcI\nH4GcJOA3rkGrA2f/VWCq/r4fefB42VtO/x7+xwSBuDplhyycEEH2Rgunr0VLA1am\n8+Z3rR7cNbIijdYJSYqVCCMcvg8cspjDAqA4cb9o2XiE3j/CpOLEIAd7op0ud6Eu\nfvXH5nRLJdtaih+15+kk/XAxvCUKkfeKQHNUh8cotZP1Ag5WhGByE9TdYCxm/94+\nCXmLwKirAgMBAAECggEAeQ+M+BgOcK35gAKQoklLqZLEhHNL1SnOhnQd3h84DrhU\nCMF5UEFTUEbjLqE3rYGP25mdiw0ZSuFf7B5SrAhJH4YIcZAO4a7ll23zE0SCW+/r\nzr9DpX4Q1TP/2yowC4uGHpBfixxpBmVljkWnai20cCU5Ef/O/cAh4hkhDcHrEKwb\nm9nymKQt06YnvpCMKoHDdqzfB3eByoAKuGxo/sbi5LDpWalCabcg7w+WKIEU1PHb\nQi+RiDf3TzbQ6TYhAEH2rKM9JHbp02TO/r3QOoqHMITW6FKYvfiVFN+voS5zzAO3\nc5X4I+ICNzm+mnt8wElV1B6nO2hFg2PE9uVnlgB2GQKBgQD8xkjNhERaT7f78gBl\nch15DRDH0m1rz84PKRznoPrSEY/HlWddlGkn0sTnbVYKXVTvNytKSmznRZ7fSTJB\n2IhQV7+I0jeb7pyLllF5PdSQqKTk6oCeL8h8eDPN7awZ731zff1AGgJ3DJXlRTh/\nO6zj9nI8llvGzP30274I2/+cdwKBgQDHd/twbiHZZTDexYewP0ufQDtZP1Nk54fj\nEpkEuoTdEPymRoq7xo+Lqj5ewhAtVKQuz6aH4BeEtSCHhxy8OFLDBdoGCEd/WBpD\nf+82sfmGk+FxLyYkLxHCxsZdOb93zkUXPCoCrvNRaUFO1qq5Dk8eftGCdC3iETHE\n6h5avxHGbQKBgQCLHQVMNhL4MQ9slU8qhZc627n0fxbBUuhw54uE3s+rdQbQLKVq\nlxcYV6MOStojciIgVRh6FmPBFEvPTxVdr7G1pdU/k5IPO07kc6H7O9AUnPvDEFwg\nsuN/vRelqbwhufAs85XBBY99vWtxdpsVSt5nx2YvegCgdIj/jUAU2B7hGQKBgEgV\nsCRdaJYr35FiSTsEZMvUZp5GKFka4xzIp8vxq/pIHUXp0FEz3MRYbdnIwBfhssPH\n/yKzdUxcOLlBtry+jgo0nyn26/+1Uyh5n3VgtBBSePJyW5JQAFcnhqBCMlOVk5pl\n/7igiQYux486PNBLv4QByK0gV0SPejDzeqzIyB+xAoGAe5if7DAAKhH0r2M8vTkm\nJvbCFjwuvhjuI+A8AuS8zw634BHne2a1Fkvc8c3d9VDbqsHCtv2tVkxkKXPjVvtB\nDtzuwUbp6ebF+jOfPK0LDuJoTdTdiNjIcXJ7iTTI3cXUnUNWWphYnFogzPFq9CyL\n0fPinYmDJpkwMYHqQaLGQyg=")]));

let token = provider.generateToken({
}, new JWTOptions()
  .setAlgorithm("RS256"));
```

Do note that all the lines `-----BEGIN …​` and `-----END…​` should be
stripped from the string to be passed to the configuration.

### Using EC keys

Elliptic Curse keys are also supported, however the default JDK has some
limitations on the features that can be used.

The usage is very similar to RSA, first you create a private key:

    openssl ecparam -name secp256r1 -genkey -out private.pem

So you will get something similar to this:

    -----BEGIN EC PARAMETERS-----
    BggqhkjOPQMBBw==
    -----END EC PARAMETERS-----
    -----BEGIN EC PRIVATE KEY-----
    MHcCAQEEIMZGaqZDTHL+IzFYEWLIYITXpGzOJuiQxR2VNGheq7ShoAoGCCqGSM49
    AwEHoUQDQgAEG1O9LCrP6hg3Y9q68+LF0q48UcOkwVKE1ax0b56wjVusf3qnuFO2
    /+XHKKhtzEavvFMeXRQ+ZVEqM0yGNb04qw==
    -----END EC PRIVATE KEY-----

However the JDK prefers PKCS8 format so we must convert:

    openssl pkcs8 -topk8 -nocrypt -in private.pem -out private_key.pem

Which will give you a key similar to this:

    -----BEGIN PRIVATE KEY-----
    MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgxkZqpkNMcv4jMVgR
    YshghNekbM4m6JDFHZU0aF6rtKGhRANCAAQbU70sKs/qGDdj2rrz4sXSrjxRw6TB
    UoTVrHRvnrCNW6x/eqe4U7b/5ccoqG3MRq+8Ux5dFD5lUSozTIY1vTir
    -----END PRIVATE KEY-----

Using the private key you can already generate tokens:

``` java
import { JWTAuth } from "@vertx/auth-jwt"
let provider = JWTAuth.create(vertx, new JWTAuthOptions()
  .setPubSecKeys([new PubSecKeyOptions()
    .setAlgorithm("ES256")
    .setSecretKey("MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgeRyEfU1NSHPTCuC9\nrwLZMukaWCH2Fk6q5w+XBYrKtLihRANCAAStpUnwKmSvBM9EI+W5QN3ALpvz6bh0\nSPCXyz5KfQZQuSj4f3l+xNERDUDaygIUdLjBXf/bc15ur2iZjcq4r0Mr")]));

let token = provider.generateToken({
}, new JWTOptions()
  .setAlgorithm("ES256"));
```

So in order to validate the tokens you will need a public key:

    openssl ec -in private.pem -pubout -out public.pem

So you can do all operations with it:

``` java
import { JWTAuth } from "@vertx/auth-jwt"
let provider = JWTAuth.create(vertx, new JWTAuthOptions()
  .setPubSecKeys([new PubSecKeyOptions()
    .setAlgorithm("ES256")
    .setPublicKey("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEraVJ8CpkrwTPRCPluUDdwC6b8+m4\ndEjwl8s+Sn0GULko+H95fsTREQ1A2soCFHS4wV3/23Nebq9omY3KuK9DKw==\n")
    .setSecretKey("MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgeRyEfU1NSHPTCuC9\nrwLZMukaWCH2Fk6q5w+XBYrKtLihRANCAAStpUnwKmSvBM9EI+W5QN3ALpvz6bh0\nSPCXyz5KfQZQuSj4f3l+xNERDUDaygIUdLjBXf/bc15ur2iZjcq4r0Mr")]));

let token = provider.generateToken({
}, new JWTOptions()
  .setAlgorithm("ES256"));
```

### The JWT keystore file

If you prefer to use Java Keystores, then you can do it either.

This auth provider requires a keystore in the classpath or in the
filesystem with either a `javax.crypto.Mac` or a
`java.security.Signature` in order to sign and verify the generated
tokens.

The implementation will, by default, look for the following aliases,
however not all are required to be present. As a good practice `HS256`
should be present:

    `HS256`:: HMAC using SHA-256 hash algorithm
    `HS384`:: HMAC using SHA-384 hash algorithm
    `HS512`:: HMAC using SHA-512 hash algorithm
    `RS256`:: RSASSA using SHA-256 hash algorithm
    `RS384`:: RSASSA using SHA-384 hash algorithm
    `RS512`:: RSASSA using SHA-512 hash algorithm
    `ES256`:: ECDSA using P-256 curve and SHA-256 hash algorithm
    `ES384`:: ECDSA using P-384 curve and SHA-384 hash algorithm
    `ES512`:: ECDSA using P-521 curve and SHA-512 hash algorithm

When no keystore is provided the implementation falls back in unsecure
mode and signatures will not be verified, this is useful for the cases
where the payload if signed and or encrypted by external means.

#### Generate a new Keystore file

The only required tool to generate a keystore file is `keytool`, you can
now specify which algorithms you need by running:

    keytool -genseckey -keystore keystore.jceks -storetype jceks -storepass secret -keyalg HMacSHA256 -keysize 2048 -alias HS256 -keypass secret
    keytool -genseckey -keystore keystore.jceks -storetype jceks -storepass secret -keyalg HMacSHA384 -keysize 2048 -alias HS384 -keypass secret
    keytool -genseckey -keystore keystore.jceks -storetype jceks -storepass secret -keyalg HMacSHA512 -keysize 2048 -alias HS512 -keypass secret
    keytool -genkey -keystore keystore.jceks -storetype jceks -storepass secret -keyalg RSA -keysize 2048 -alias RS256 -keypass secret -sigalg SHA256withRSA -dname "CN=,OU=,O=,L=,ST=,C=" -validity 360
    keytool -genkey -keystore keystore.jceks -storetype jceks -storepass secret -keyalg RSA -keysize 2048 -alias RS384 -keypass secret -sigalg SHA384withRSA -dname "CN=,OU=,O=,L=,ST=,C=" -validity 360
    keytool -genkey -keystore keystore.jceks -storetype jceks -storepass secret -keyalg RSA -keysize 2048 -alias RS512 -keypass secret -sigalg SHA512withRSA -dname "CN=,OU=,O=,L=,ST=,C=" -validity 360
    keytool -genkeypair -keystore keystore.jceks -storetype jceks -storepass secret -keyalg EC -keysize 256 -alias ES256 -keypass secret -sigalg SHA256withECDSA -dname "CN=,OU=,O=,L=,ST=,C=" -validity 360
    keytool -genkeypair -keystore keystore.jceks -storetype jceks -storepass secret -keyalg EC -keysize 384 -alias ES384 -keypass secret -sigalg SHA384withECDSA -dname "CN=,OU=,O=,L=,ST=,C=" -validity 360
    keytool -genkeypair -keystore keystore.jceks -storetype jceks -storepass secret -keyalg EC -keysize 521 -alias ES512 -keypass secret -sigalg SHA512withECDSA -dname "CN=,OU=,O=,L=,ST=,C=" -validity 360

## Read only tokens

If you need to consume JWT tokens issues by third parties you probably
won’t have the private key with you, in that case all you need to have
is a public key im PEM format.

``` js
import { JWTAuth } from "@vertx/auth-jwt"

let config = new JWTAuthOptions()
  .setPubSecKeys([new PubSecKeyOptions()
    .setAlgorithm("RS256")
    .setPublicKey("BASE64-ENCODED-PUBLIC_KEY")]);

let provider = JWTAuth.create(vertx, config);
```

# AuthN/AuthZ with JWT

A common scenario when developing for example micro services is that you
want you application to consume APIs. These api’s are not meant to be
consumed by humans so we should remove all the interactive part of
authenticating the consumer out of the picture.

In this scenario one can use HTTP as the protocol to consume this API
and the HTTP protocol already defines that there is a header
`Authorization` that should be used for passing authorization
information. In most cases you will see that tokens are sent as bearer
tokens, i.e.: `Authorization: Bearer some+base64+string`.

## Authenticating (AuthN)

For this provider a user is authenticated if the token passes the
signature checks and that the token is not expired. For this reason it
is imperative that private keys are kept private and not copy pasted
across project since it would be a security hole.

``` js
// This string is what you see after the string "Bearer" in the
// HTTP Authorization header
jwtAuth.authenticate({
  "jwt" : "BASE64-ENCODED-STRING"
}, (res, res_err) => {
  if (res.succeeded()) {
    let theUser = res.result();
  } else {
    // Failed!
  }
});
```

In a nutshell the provider is checking for several things:

  - token signature is valid against internal private key

  - fields: `exp`, `iat`, `nbf`, `audience`, `issuer` are valid
    according to the config

If all these are valid then the token is considered good and a user
object is returned.

While the fields `exp`, `iat` and `nbf` are simple timestamp checks only
`exp` can be configured to be ignored:

``` js
// This string is what you see after the string "Bearer" in the
// HTTP Authorization header

// In this case we are forcing the provider to ignore the `exp` field
jwtAuth.authenticate({
  "jwt" : "BASE64-ENCODED-STRING",
  "options" : {
    "ignoreExpiration" : true
  }
}, (res, res_err) => {
  if (res.succeeded()) {
    let theUser = res.result();
  } else {
    // Failed!
  }
});
```

In order to verify the `aud` field one needs to pass the options like
before:

``` js
// This string is what you see after the string "Bearer" in the
// HTTP Authorization header

// In this case we are forcing the provider to ignore the `exp` field
jwtAuth.authenticate({
  "jwt" : "BASE64-ENCODED-STRING",
  "options" : {
    "audience" : [
      "paulo@server.com"
    ]
  }
}, (res, res_err) => {
  if (res.succeeded()) {
    let theUser = res.result();
  } else {
    // Failed!
  }
});
```

And the same for the issuer:

``` js
// This string is what you see after the string "Bearer" in the
// HTTP Authorization header

// In this case we are forcing the provider to ignore the `exp` field
jwtAuth.authenticate({
  "jwt" : "BASE64-ENCODED-STRING",
  "options" : {
    "issuer" : "mycorp.com"
  }
}, (res, res_err) => {
  if (res.succeeded()) {
    let theUser = res.result();
  } else {
    // Failed!
  }
});
```

## Authorizing (AuthZ)

Once a token is parsed and is valid we can use it to perform
authorization tasks. The most simple is to verify if a user has a
specific authority. In this case one needs to to:

``` js
user.isAuthorized("create-report", (res, res_err) => {
  if (res.succeeded() && res.result()) {
    // Yes the user can create reports
  }
});
```

By default the provider will lookup under the key `permissions` but like
the other providers one can extend the concept to authorities to roles
by using the `:` as a splitter, so `role:authority` can be used to
lookup the token.

Since JWT are quite free form and there is no standard on where to
lookup for the claims the location can be configured to use something
else than `permissions`, for example one can even lookup under a path
like this:

``` js
import { JWTAuth } from "@vertx/auth-jwt"

let config = {
  "public-key" : "BASE64-ENCODED-PUBLIC_KEY",
  "permissionsClaimKey" : "realm_access/roles"
};

let provider = JWTAuth.create(vertx, config);
```

So in this example we configure the JWT to work with Keycloak token
format. In this case the claims will be checked under the path
`realm_access/roles` rather than `permissions`.

## Validating Tokens

When the method `authenticate` is invoked, the token is validated
against the `JWTOptions` provided during the initialization. The
validation performs the following steps:

1.  if `ignoreExpiration` (default is false) is false then the token is
    checked for expiration, this will check the fields: `exp`, `iat` and
    `nbf`. Since sometimes clocks are not reliable, it is possible to
    configure some `leeway` to be applied to the dates so we allow some
    grace period if the dates are outside the required limits.

2.  if `audience` is provided, then the token `aud` is checked against
    the configured one and all configured audiences must be in the
    token.

3.  if `issuer` is configured, then the tokens `iss` is checked against
    the configured one.

Once these validations complete a JWTUser object is then returned, the
object is configured with a reference to the permission claims key
provided in the configuration. This value is used later when doing
authorization. The value corresponds to the json path where authorities
should be checked.

## Customizing Token Generation

In the same way tokens are validated, the generation is initially
configured during the initialization.

When generating a token an optional extra parameter can be supplied to
control the token generation, this is a `JWTOptions` object. The token
signature algorithm (default HS256) can be configured using the property
`algorithm`. In this case a lookup for a key that corresponds to the
algorithm is performed and used to sign.

Token headers can be added by specifying any extra headers to be merged
with the default ones using the options `headers` property.

Sometimes it might be useful to issue tokens without a timestamp (test,
development time for example) in this case the property `noTimestamp`
should be set to true (default false). This means that there is no `iat`
field in the token.

Token expiration is controlled by the property `expiresInSeconds`, by
default there is no expiration. Other control fields `audience`,
`issuer` and `subject` are then picked from the config is available and
added to the token metadata.

Finally the token is signed and encoded in the correct format.

@author \<a href="mailto:julien@julienviet.com"\>Julien Viet\</a\>
@author \<a href="http://tfox.org"\>Tim Fox\</a\> @author \<a
href="mailto:pmlopes@gmail.com"\>Paulo Lopes\</a\>
