# The OAuth2 auth provider

This component contains an out of the box OAuth2 (and to some extent
OpenID Connect) relying party implementation. To use this project, add
the following dependency to the *dependencies* section of your build
descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-auth-oauth2</artifactId>
 <version>${maven.version}</version>
</dependency>
```

  - Gradle (in your `build.gradle` file):

<!-- end list -->

``` groovy
compile 'io.vertx:vertx-auth-oauth2:${maven.version}'
```

OAuth2 lets users grant the access to the desired resources to third
party applications, giving them the possibility to enable and disable
those accesses whenever they want.

Vert.x OAuth2 supports the following flows.

  - Authorization Code Flow (for apps with servers that can store
    persistent information).

  - Password Credentials Flow (when previous flow can’t be used or
    during development).

  - Client Credentials Flow (the client can request an access token
    using only its client credentials)

The same code will work with OpenID Connect
<https://openid.net/connect/> servers and supports the Discovery
protocol as specified in
<http://openid.net/specs/openid-connect-discovery-1_0.html> .

## Authorization Code Flow

The authorization code grant type is used to obtain both access tokens
and refresh tokens and is optimized for confidential clients. As a
redirection-based flow, the client must be capable of interacting with
the resource owner’s user-agent (typically a web browser) and capable of
receiving incoming requests (via redirection) from the authorization
server.

For more details see [Oauth2 specification,
section 4.1](http://tools.ietf.org/html/draft-ietf-oauth-v2-31#section-4.1).

## Password Credentials Flow

The resource owner password credentials grant type is suitable in cases
where the resource owner has a trust relationship with the client, such
as the device operating system or a highly privileged application. The
authorization server should take special care when enabling this grant
type, and only allow it when other flows are not viable.

The grant type is suitable for clients capable of obtaining the resource
owner’s credentials (username and password, typically using an
interactive form). It is also used to migrate existing clients using
direct authentication schemes such as HTTP Basic or Digest
authentication to OAuth by converting the stored credentials to an
access token.

For more details see [Oauth2 specification,
section 4.3](http://tools.ietf.org/html/draft-ietf-oauth-v2-31#section-4.3).

## Client Credentials Flow

The client can request an access token using only its client credentials
(or other supported means of authentication) when the client is
requesting access to the protected resources under its control, or those
of another resource owner that have been previously arranged with the
authorization server (the method of which is beyond the scope of this
specification).

The client credentials grant type MUST only be used by confidential
clients.

For more details see [Oauth2 specification,
section 4.4](http://tools.ietf.org/html/draft-ietf-oauth-v2-31#section-4.4).

## Extensions

The provider supports RFC7523 an extension to allow server to server
authorization based on JWT.

## Getting Started

An example on how to use this provider and authenticate with GitHub can
be implemented as:

``` js
import { OAuth2Auth } from "@vertx/auth-oauth2"

let oauth2 = OAuth2Auth.create(vertx, 'AUTH_CODE', new OAuth2ClientOptions()
  .setClientID("YOUR_CLIENT_ID")
  .setClientSecret("YOUR_CLIENT_SECRET")
  .setSite("https://github.com/login")
  .setTokenPath("/oauth/access_token")
  .setAuthorizationPath("/oauth/authorize"));

// when there is a need to access a protected resource or call a protected method,
// call the authZ url for a challenge

let authorization_uri = oauth2.authorizeURL({
  "redirect_uri" : "http://localhost:8080/callback",
  "scope" : "notifications",
  "state" : "3(#0/!~"
});

// when working with web application use the above string as a redirect url

// in this case GitHub will call you back in the callback uri one should now complete the handshake as:


let code = "xxxxxxxxxxxxxxxxxxxxxxxx";

oauth2.authenticate({
  "code" : code,
  "redirect_uri" : "http://localhost:8080/callback"
}, (res, res_err) => {
  if (res.failed()) {
    // error, the code provided is not valid
  } else {
    // save the token and continue...
  }
});
```

### Authorization Code flow

The Authorization Code flow is made up from two parts. At first your
application asks to the user the permission to access their data. If the
user approves the OAuth2 server sends to the client an authorization
code. In the second part, the client POST the authorization code along
with its client secret to the authority server in order to get the
access token.

``` js
import { OAuth2Auth } from "@vertx/auth-oauth2"

// Set the client credentials and the OAuth2 server
let credentials = new OAuth2ClientOptions()
  .setClientID("<client-id>")
  .setClientSecret("<client-secret>")
  .setSite("https://api.oauth.com");


// Initialize the OAuth2 Library
let oauth2 = OAuth2Auth.create(vertx, 'AUTH_CODE', credentials);

// Authorization oauth2 URI
let authorization_uri = oauth2.authorizeURL({
  "redirect_uri" : "http://localhost:8080/callback",
  "scope" : "<scope>",
  "state" : "<state>"
});

// Redirect example using Vert.x
response.putHeader("Location", authorization_uri).setStatusCode(302).end();

let tokenConfig = {
  "code" : "<code>",
  "redirect_uri" : "http://localhost:3000/callback"
};

// Callbacks
// Save the access token
oauth2.authenticate(tokenConfig, (res, res_err) => {
  if (res.failed()) {
    console.error("Access Token Error: " + res.cause().getMessage());
  } else {
    // Get the access token object (the authorization code is given from the previous step).
    let token = res.result();
  }
});
```

### Password Credentials Flow

This flow is suitable when the resource owner has a trust relationship
with the client, such as its computer operating system or a highly
privileged application. Use this flow only when other flows are not
viable or when you need a fast way to test your application.

``` js
import { OAuth2Auth } from "@vertx/auth-oauth2"
import { AccessToken } from "@vertx/auth-oauth2"

// Initialize the OAuth2 Library
let oauth2 = OAuth2Auth.create(vertx, 'PASSWORD');

let tokenConfig = {
  "username" : "username",
  "password" : "password"
};

// Callbacks
// Save the access token
oauth2.authenticate(tokenConfig, (res, res_err) => {
  if (res.failed()) {
    console.error("Access Token Error: " + res.cause().getMessage());
  } else {
    // Get the access token object (the authorization code is given from the previous step).
    let token = res.result();

    token.fetch("/users", (res2, res2_err) => {
      // the user object should be returned here...
    });
  }
});
```

### Client Credentials Flow

This flow is suitable when client is requesting access to the protected
resources under its control.

``` js
import { OAuth2Auth } from "@vertx/auth-oauth2"

// Set the client credentials and the OAuth2 server
let credentials = new OAuth2ClientOptions()
  .setClientID("<client-id>")
  .setClientSecret("<client-secret>")
  .setSite("https://api.oauth.com");


// Initialize the OAuth2 Library
let oauth2 = OAuth2Auth.create(vertx, 'CLIENT', credentials);

let tokenConfig = {
};

// Callbacks
// Save the access token
oauth2.authenticate(tokenConfig, (res, res_err) => {
  if (res.failed()) {
    console.error("Access Token Error: " + res.cause().getMessage());
  } else {
    // Get the access token object (the authorization code is given from the previous step).
    let token = res.result();
  }
});
```

## OpenID Connect Discovery

There is limited support for OpenID Discovery servers. Using OIDC
Discovery will simplify the configuration of your auth module into a
single line of code, for example, consider setting up your auth using
Google:

``` js
import { OpenIDConnectAuth } from "@vertx/auth-oauth2"

OpenIDConnectAuth.discover(vertx, new OAuth2ClientOptions()
  .setSite("https://accounts.google.com")
  .setClientID("clientId"), (res, res_err) => {
  if (res.succeeded()) {
    // the setup call succeeded.
    // at this moment your auth is ready to use and
    // google signature keys are loaded so tokens can be decoded and verified.
  } else {
    // the setup failed.
  }
});
```

Behind the scenes a couple of actions are performed:

1.  HTTP get request to the `.well-known/openid-configuration` resource

2.  Validation of the response `issuer` field as mandated by the spec
    (the issuer value must match the request one)

3.  If the JWK uri is present, keys are loaded from the server and added
    to the auth keychain

4.  the auth module is configure and returned to the user.

A couple of well known OpenID Connect Discovery providers are:

  - Keycloak: `http://keycloakhost:keycloakport/auth/realms/{realm}`

  - Google: `https://accounts.google.com`

  - SalesForce: `https://login.salesforce.com`

  - Microsoft: `https://login.windows.net/common`

This and the given `client id` is enough to configure your auth provider
object.

## AccessToken object

When a token expires we need to refresh it. OAuth2 offers the
AccessToken class that add a couple of useful methods to refresh the
access token when it is expired.

``` js
// Check if the token is expired. If expired it is refreshed.
if (token.expired()) {
  // Callbacks
  token.refresh((res, res_err) => {
    if (res.succeeded()) {
      // success
    } else {
      // error handling...
    }
  });
}
```

When you’ve done with the token or you want to log out, you can revoke
the access token and refresh token.

``` js
// Revoke only the access token
token.revoke("access_token", (res, res_err) => {
  // Session ended. But the refresh_token is still valid.

  // Revoke the refresh_token
  token.revoke("refresh_token", (res1, res1_err) => {
    console.log("token revoked.");
  });
});
```

## Example configuration for common OAuth2 providers

For convenience there are several helpers to assist your with your
configuration. Currently we provide:

  - Azure Active Directory `AzureADAuth`

  - Box.com `BoxAuth`

  - Dropbox `DropboxAuth`

  - Facebook `FacebookAuth`

  - Foursquare `FoursquareAuth`

  - Github `GithubAuth`

  - Google `GoogleAuth`

  - Instagram `InstagramAuth`

  - Keycloak `KeycloakAuth`

  - LinkedIn `LinkedInAuth`

  - Mailchimp `MailchimpAuth`

  - Salesforce `SalesforceAuth`

  - Shopify `ShopifyAuth`

  - Soundcloud `SoundcloudAuth`

  - Stripe `StripeAuth`

  - Twitter `TwitterAuth`

### JBoss Keycloak

When using this Keycloak the provider has knowledge on how to parse
access tokens and extract grants from inside. This information is quite
valuable since it allows to do authorization at the API level, for
example:

``` js
import { KeycloakAuth } from "@vertx/auth-oauth2"
import { AccessToken } from "@vertx/auth-oauth2"
// you would get this config from the keycloak admin console
let keycloakJson = {
  "realm" : "master",
  "realm-public-key" : "MIIBIjANBgkqhk...wIDAQAB",
  "auth-server-url" : "http://localhost:9000/auth",
  "ssl-required" : "external",
  "resource" : "frontend",
  "credentials" : {
    "secret" : "2fbf5e18-b923-4a83-9657-b4ebd5317f60"
  }
};

// Initialize the OAuth2 Library
let oauth2 = KeycloakAuth.create(vertx, 'PASSWORD', keycloakJson);

// first get a token (authenticate)
oauth2.authenticate({
  "username" : "user",
  "password" : "secret"
}, (res, res_err) => {
  if (res.failed()) {
    // error handling...
  } else {
    let token = res.result();

    // now check for permissions
    token.isAuthorized("account:manage-account", (r, r_err) => {
      if (r.result()) {
        // this user is authorized to manage its account
      }
    });
  }
});
```

We also provide a helper class for Keycloak so that we can we can easily
retrieve decoded token and some necessary data (e.g.
`preferred_username`) from the Keycloak principal. For example:

``` js
import { KeycloakHelper } from "@vertx/auth-oauth2"
// you can get the decoded `id_token` from the Keycloak principal
let idToken = KeycloakHelper.idToken(principal);

// you can also retrieve some properties directly from the Keycloak principal
// e.g. `preferred_username`
let username = KeycloakHelper.preferredUsername(principal);
```

Please remember that Keycloak **does** implement OpenID Connect, so you
can configure it just by using it’s discovery url:

``` js
import { OpenIDConnectAuth } from "@vertx/auth-oauth2"

OpenIDConnectAuth.discover(vertx, new OAuth2ClientOptions()
  .setSite("http://server:port/auth/realms/your_realm")
  .setClientID("clientId"), (res, res_err) => {
  if (res.succeeded()) {
    // the setup call succeeded.
    // at this moment your auth is ready to use and
    // google signature keys are loaded so tokens can be decoded and verified.
  } else {
    // the setup failed.
  }
});
```

Since you can deploy your Keycloak server anywhere, just replace
`server:port` with the correct value and the `your_realm` value with
your application realm.

### Google Server to Server

The provider also supports Server to Server or the RFC7523 extension.
This is a feature present on Google with their service account.

## Token Introspection

Tokens can be introspected in order to assert that they are still valid.
Although there is RFC7662 for this purpose not many providers implement
it. Instead there are variations also known as `TokenInfo` end points.
The OAuth2 provider will accept both end points as a configuration.
Currently we are known to work with `Google` and `Keycloak`.

Token introspection assumes that tokens are opaque, so they need to be
validated on the provider server. Every time a token is validated it
requires a round trip to the provider. Introspection can be performed at
the OAuth2 level or at the User level:

``` js
// OAuth2Auth level
oauth2.introspectToken("opaque string", (res, res_err) => {
  if (res.succeeded()) {
    // token is valid!
    let accessToken = res.result();
  }
});

// User level
token.introspect((res, res_err) => {
  if (res.succeeded()) {
    // Token is valid!
  }
});
```

## Verifying JWT tokens

We’ve just covered how to introspect a token however when dealing with
JWT tokens one can reduce the amount of trips to the provider server
thus enhancing your overall response times. In this case tokens will be
verified using the JWT protocol at your application side only. Verifying
JWT tokens is cheaper and offers better performance, however due to the
stateless nature of JWTs it is not possible to know if a user is logged
out and a token is invalid. For this specific case one needs to use the
token introspection if the provider supports it.

``` js
// OAuth2Auth level
oauth2.decodeToken("jwt-token", (res, res_err) => {
  if (res.succeeded()) {
    // token is valid!
    let accessToken = res.result();
  }
});
```

Until now we covered mostly authentication, although the implementation
is relying party (that means that the real authentication happens
somewhere else), there is more you can do with the handler. For example
you can also do authorization if the provider is known to support JSON
web tokens. This is a common feature if your provider is a OpenId
Connect provider or if the provider does support \`access\_token\`s as
JWTs.

Such provider is Keycloak that is a OpenId Connect implementation. In
that case you will be able to perform authorization in a very easy way.

# Role Based Access Control

OAuth2 is an AuthN protocol, however OpenId Connect adds JWTs to the
token format which means that AuthZ can be encoded at the token level.
Currently there are 2 known JWT AuthZ known formats:

  - Keycloak

  - MicroProfile JWT 1.1 spec

## Keycloak JWT

Given that Keycloak does provide `JWT` \`access\_token\`s one can
authorize at two distinct levels:

  - role

  - authority

To distinct the two, the auth provider follows the same recommendations
from the base user class, i.e.: use the\`:\` as a separator for the two.
It should be noted that both role and authorities do not need to be
together, in the most simple case an authority is enough.

In order to map to keycloak’s token format the following checks are
performed:

1.  If no role is provided, it is assumed to the the provider realm name

2.  If the role is `realm` then the lookup happens in `realm_access`
    list

3.  If a role is provided then the lookup happends in the
    `resource_access` list under the role name

### Check for a specific authorities

Here is one example how you can perform authorization after the user has
been loaded from the oauth2 handshake, for example you want to see if
the user can `print` in the current application:

``` js
user.isAuthorized("print", (res, res_err) => {
  // in this case it is assumed that the role is the current application
  if (res.succeeded() && res.result()) {
    // Yes the user can print
  }
});
```

However this is quite specific, you might want to verify if the user can
`add-user` to the whole system (the realm):

``` js
user.isAuthorized("realm:add-user", (res, res_err) => {
  // the role is "realm"
  // the authority is "add-user"
  if (res.succeeded() && res.result()) {
    // Yes the user can add users to the application
  }
});
```

Or if the user can access the `year-report` in the `finance` department:

``` js
user.isAuthorized("finance:year-report", (res, res_err) => {
  // the role is "finance"
  // the authority is "year-report"
  if (res.succeeded() && res.result()) {
    // Yes the user can access the year report from the finance department
  }
});
```

## MicroProfile JWT 1.1 spec

Another format in the form of a spec is the MP-JWT 1.1. This spec
defines a JSON array of strings under the property name `groups` that
define the "groups" the token has an authority over.

In order to use this spec to assert AuthZ the right handler must be set:

``` js
import { MicroProfileRBAC } from "@vertx/auth-oauth2"
// use the MP-JWT 1.1 spec handler to
// handle Role-Based Access Control (AuthZ)
oauth2Auth.rbacHandler(MicroProfileRBAC.create());
```

# Token Management

## Check if it is expired

Tokens are usually fetched from the server and cached, in this case when
used later they might have already expired and be invalid, you can
verify if the token is still valid like this:

``` js
// internal validation against, expiration date
let isExpired = user.expired();
```

This call is totally offline, it could still happen that the Oauth2
server invalidated your token but you get a non expired token result.
The reason behind this is that the expiration is checked against the
token expiration dates, not before date and such values.

## Refresh token

There are times you know the token is about to expire and would like to
avoid to redirect the user again to the login screen. In this case you
can refresh the token. To refresh a token you need to have already a
user and call:

``` js
user.refresh((res, res_err) => {
  if (res.succeeded()) {
    // the refresh call succeeded
  } else {
    // the token was not refreshed, a best practise would be
    // to forcefully logout the user since this could be a
    // symptom that you're logged out by the server and this
    // token is not valid anymore.
  }
});
```

## Revoke token

Since tokens can be shared across various applications you might want to
disallow the usage of the current token by any application. In order to
do this one needs to revoke the token against the Oauth2 server:

``` js
user.revoke("access_token", (res, res_err) => {
  if (res.succeeded()) {
    // the refresh call succeeded
  } else {
    // the token was not refreshed, a best practise would be
    // to forcefully logout the user since this could be a
    // symptom that you're logged out by the server and this
    // token is not valid anymore.
  }
});
```

It is important to note that this call requires a token type. The reason
is because some providers will return more than one token e.g.:

  - id\_token

  - refresh\_token

  - access\_token

So one needs to know what token to invalidate. It should be obvious that
if you invalidate the `refresh_token` you’re still logged in but you
won’t be able to refresh anymore, which means that once the token
expires you need to redirect the user again to the login page.

## Introspect

Introspect a token is similar to a expiration check, however one needs
to note that this check is fully online. This means that the check
happens on the OAuth2 server.

``` js
user.introspect((res, res_err) => {
  if (res.succeeded()) {
    // the introspection call succeeded
  } else {
    // the token failed the introspection. You should proceed
    // to logout the user since this means that this token is
    // not valid anymore.
  }
});
```

Important note is that even if the `expired()` call is `true` the return
from the `introspect` call can still be an error. This is because the
OAuth2 might have received a request to invalidate the token or a
loggout in between.

## Logging out

Logging out is not a `Oauth2` feature but it is present on `OpenID
Connect` and most providers do support some sort of logging out. This
provider also covers this area if the configuration is enough to let it
make the call. For the user this is as simple as:

``` js
user.logout((res, res_err) => {
  if (res.succeeded()) {
    // the logout call succeeded
  } else {
    // the user might not have been logged out
    // to know why:
    console.log(res.cause());
  }
});
```
