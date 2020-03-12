# Vault Config Store

The Vault Store extends the Vert.x Configuration Retriever and provides
support for Vault (<https://www.vaultproject.io/>). So, configuration
(secrets) is retrieved from Vault.

> The secrets engines supported by this store are Vault Key/Value
> version 1 and version 2 engines
> (<https://www.vaultproject.io/docs/secrets/kv/index.html>). Other
> secrets engine are not supported.

## Using the Vault Config Store

To use the Vault Config Store, add the following dependency to the
*dependencies* section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-config-vault</artifactId>
 <version>${maven.version}</version>
</dependency>
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-config</artifactId>
 <version>${maven.version}</version>
</dependency>
```

  - Gradle (in your `build.gradle` file):

<!-- end list -->

``` groovy
compile 'io.vertx:vertx-config:${maven.version}'
compile 'io.vertx:vertx-config-vault:${maven.version}'
```

## Configuring the store

Once added to your classpath or dependencies, you need to configure the
`ConfigRetriever` to use this store:

``` js
import { ConfigRetriever } from "@vertx/config"
let store = new ConfigStoreOptions()
  .setType("vault")
  .setConfig(config);

let retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions()
  .setStores([store]));
```

To use the Vault config store, set the `type` to `vault`. The
configuration is provided as Json. It configures the access to Vault,
authentication and the path of the secret to retrieve:

``` js
import { ConfigRetriever } from "@vertx/config"
let vault_config = {
  "host" : "127.0.0.1",
  "port" : 8200,
  "ssl" : true
};

// Certificates
let certs = new PemKeyCertOptions()
  .setCertPaths(["target/vault/config/ssl/client-cert.pem"])
  .setKeyPaths(["target/vault/config/ssl/client-privatekey.pem"]);
vault_config.pemKeyCertOptions = certs;

// Truststore
let jks = new JksOptions()
  .setPath("target/vault/config/ssl/truststore.jks");
vault_config.trustStoreOptions = jks;

// Path to the secret to read.
vault_config.path = "secret/my-secret";

let store = new ConfigStoreOptions()
  .setType("vault")
  .setConfig(vault_config);

let retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions()
  .setStores([store]));
}
```

The `vault_config` object can contain the HTTP client / Web client
configuration such as trust stores, timeout, certificates, port and
host. The `path` and `host` entries are mandatory. The `path` indicates
the secret to retrieve. The `host` is the hostname of the Vault server.
By default the port 8200 is used. SSL is disabled by default, but you
should enable it for production settings.

Then, you need to use one of the following method to configure the token
to use or the authentication mechanism.

## Using an existing token

If you know the token to use, set the `token` entry in the
configuration:

``` js
import { ConfigRetriever } from "@vertx/config"
let vault_config = {
};

// ...

// Path to the secret to read.
vault_config.path = "secret/my-secret";

// The token
vault_config.token = token;

let store = new ConfigStoreOptions()
  .setType("vault")
  .setConfig(vault_config);

let retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions()
  .setStores([store]));
```

You can use the root token, but it’s not recommended. When the token is
revoked, the access to the secret is blocked. If the token is renewable,
the token is renewed when it expires.

## Generating a token

If you have a token allowing you to generate new token, you can request
the token generation:

``` js
import { ConfigRetriever } from "@vertx/config"
let vault_config = {
};

// ...

// Path to the secret to read.
vault_config.path = "secret/my-secret";

// Configure the token generation

// Configure the token request (https://www.vaultproject.io/docs/auth/token.html)
let tokenRequest = {
  "ttl" : "1h",
  "noDefault" : true,
  "token" : token
};

vault_config.auth-backend = "token".renew-window = 5000.token-request = tokenRequest;

let store = new ConfigStoreOptions()
  .setType("vault")
  .setConfig(vault_config);

let retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions()
  .setStores([store]));
```

When using this approach, no token must be provided in the root
configuration, the the token use to request the generation is passed in
the nested JSON structure. If the generated token is renewable, it will
be renewed automatically upon expiration. The `renew-window` is the time
window to add to the token validity to renew it. If the generated token
is revoked, the access to the secret is blocked.

## Using certificates

You can use TLS certificates as authentication mechanism. So, you don’t
need to know a token, the token is generated automatically.

``` js
import { ConfigRetriever } from "@vertx/config"
let vault_config = {
};

// ...

let certs = new PemKeyCertOptions()
  .setCertPaths(["target/vault/config/ssl/client-cert.pem"])
  .setKeyPaths(["target/vault/config/ssl/client-privatekey.pem"]);
vault_config.pemKeyCertOptions = certs;

let trust = new PemTrustOptions()
  .setCertPaths(["target/vault/config/ssl/cert.pem"]);
vault_config.pemTrustStoreOptions = trust;

let jks = new JksOptions()
  .setPath("target/vault/config/ssl/truststore.jks");
vault_config.trustStoreOptions = jks;

vault_config.auth-backend = "cert";

// Path to the secret to read.
vault_config.path = "secret/my-secret";

let store = new ConfigStoreOptions()
  .setType("vault")
  .setConfig(vault_config);

let retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions()
  .setStores([store]));
```

Check out the HTTP client and Web client configuration to pass the
certificates. If the generated token is renewable, it will be renewed.
If not, the store attempts to authenticate again.

## Using AppRole

`AppRole` is used when your application is known by Vault and you have
the `appRoleId` and `secretId`. You don’t need a token, the token being
generated automatically:

``` js
import { ConfigRetriever } from "@vertx/config"
let vault_config = {
};

// ...

vault_config.auth-backend = "approle".approle = {
  "role-id" : appRoleId,
  "secret-id" : secretId
};

// Path to the secret to read.
vault_config.path = "secret/my-secret";

let store = new ConfigStoreOptions()
  .setType("vault")
  .setConfig(vault_config);

let retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions()
  .setStores([store]));
```

If the generated token is renewable, it will be renewed. If not, the
store attempts to authenticate again.

## Using username and password

The `userpass` auth backend is used when the user / app is authenticated
using a username/password. You don’t need a token as the token is
generated during the authentication process:

``` js
import { ConfigRetriever } from "@vertx/config"
let vault_config = {
};

// ...

vault_config.auth-backend = "userpass".user-credentials = {
  "username" : username,
  "password" : password
};

// Path to the secret to read.
vault_config.path = "secret/my-secret";

let store = new ConfigStoreOptions()
  .setType("vault")
  .setConfig(vault_config);

let retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions()
  .setStores([store]));
```

If the generated token is renewable, it will be renewed. If not, the
store attempts to authenticate again.
