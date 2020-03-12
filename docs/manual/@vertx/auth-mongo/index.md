# Mongo Auth Provider implementation

We provide an implementation of `AuthProvider` which uses the Vert.x
`MongoClient` to perform authentication and authorisation against a
MongoDb.

To use this project, add the following dependency to the *dependencies*
section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-auth-mongo</artifactId>
 <version>${maven.version}</version>
</dependency>
```

  - Gradle (in your `build.gradle` file):

<!-- end list -->

``` groovy
compile 'io.vertx:vertx-auth-mongo:${maven.version}'
```

To create an instance you first need an instance of `MongoClient`. To
learn how to create one of those please consult the documentation for
the MongoClient.

Once you’ve got one of those you can create a `MongoAuth` instance as
follows:

``` js
import { MongoClient } from "@vertx/mongo-client"
import { MongoAuth } from "@vertx/auth-mongo"
let client = MongoClient.createShared(vertx, mongoClientConfig);
let authProperties = {
};
let authProvider = MongoAuth.create(client, authProperties);
```

Once you’ve got your instance you can authenticate and authorise with it
just like any `AuthProvider`.

The out of the box config assumes the usage of the collection with name
"user", the username stored and read by field "username" some others.

In order to avoid duplicates of user names your "user" collection should
have a unique index on "username". In order to do this you should run
the following snippet on your mongo server:

    db.user.createIndex( { username: 1 }, { unique: true } )

The reason you should add the index is that due to the nature of mongo
doing a query first to verify if a username is already taken and then
insert a document cannot be run as an atomic action. Using the index the
code will try to insert the row and fail if duplicate.

You can also change all the defaults for the mongo collection and column
names using any of the methods:

`setCollectionName` `setUsernameField` `setPasswordField`
`setPermissionField` `setRoleField` if you want to adapt that to your
needs.

The default implementation assumes that the password is stored in the
database as a SHA-512 hash after being concatenated with a salt. It also
assumes the salt is stored in the table too. The field, where the salt
is stored can be set by `setSaltField`, the default is "salt". You are
able to change this behaviour by using `setSaltStyle`. The HashStrategy
you can retrieve by `getHashStrategy`. By using this, you are able to
set: `NO_SALT` by which passwords are not crypted and stored in
cleartext. ( see the warning below\! ) `COLUMN`, which will create a
salt per user and store this inside the defined column of the user. (
see the warning below\! ) `EXTERNAL`, which will store only the crypted
password in the database and will use a salt from external, which you
will have to set by `setExternalSalt`

If you want to override this behaviour you can do so by providing an
alternative hash strategy and setting it with `setHashStrategy`

> **Warning**
>
> It is strongly advised to use the `EXTERNAL` option. The NO\_SALT
> option is existing for development phase only and even the COLUMN
> option is not recommended, cause salt and password are stored inside
> the same place\!

> **Warning**
>
> As of 2018 OWASP recommends the usage of stronger encryption
> algorithms to hash user passwords for this case you can change from
> the default (preserved for backwards-compatibility) to PBKDF2. For new
> projects this should be the standard.

``` js
import { MongoClient } from "@vertx/mongo-client"
import { MongoAuth } from "@vertx/auth-mongo"
let client = MongoClient.createShared(vertx, mongoClientConfig);
let authProperties = {
};
let authProvider = MongoAuth.create(client, authProperties);
authProvider.setHashAlgorithm('PBKDF2');
```

# Vertx Auth JDBC and GDPR

GDPR is a regulation from the common European Union law. It
overrides/supercedes national data protection laws and extents the
previously existing directives. This section of the manual is by no
means a thorough walkthrough of the regulation, it is just a small
summary how this component adheres to the requirements. Companies not
adhering to the equirements can be fined on 4% of the turnover or 20
million euro. Therefore we want to make sure that as a user of Vert.x
Auth JDBC you’re are on the good track to comply.

The law defines certain terminology:

  - Data Subject - Person whose personal data is processed (e.g.: User)

  - Personal Data - Any data about an identifiable or identified person

  - Data Processing - Any operation (manual or automated) on personal
    data

  - Controller - The entity (company) that requests and uses the data

  - Processors - Any entity that processes data on behalf of a
    controller (e.g.: cloud service provider)

GDPR defines the following functionality:

  - "Forget me" - Right to erasure

  - Mark profile as restricted - Right to restriction of processing

  - Export data - Right to portability

  - Allow profile editing - Right to rectification

  - See all my data - Right to access

  - Consent checkboxes

  - Age checks

  - Data destruction - Data minimization principle

This module complies to the GDPR law by not storing any identifiable
information about a data subject. The only reference is the username
which is not linked to any personal data.

In order to add personal data to your application you should create your
own data schema and use the username column as a reference to your data.
As a tip you should have a boolean flag to mark the personal data as
restricted to comply to the right to restriction of processing which
means that if you need to handle the data, e.g.: send a bulk email from
a mailing list you are not allowed to do so if the flag is true.

The right to erasure does not mean that you must wipe all records from
your application, e.g.: in a bank this right cannot be used to erase a
running loan or debt. You are allowed to keep your application data but
must erase the personal data. In case of Vert.x Auth JDBC you should
delete your table but can still use a reference to the username as long
as is not possible to link the username to the personal data.

Important note is that this must survive backups\! As a tip backup the
data, and data erasure on different archives so they can be replayed
individually.

# Authentication

When authenticating using this implementation, it assumes `username` and
`password` fields are present in the authentication info:

``` js
let authInfo = {
  "username" : "tim",
  "password" : "sausages"
};
authProvider.authenticate(authInfo, (res) => {
  if (res.succeeded()) {
    let user = res.result();
  } else {
    // Failed!
  }
});
```

Instead of the `username` and `password` field names used in the
previous snippet, you should use: `setUsernameCredentialField` and
`setPasswordCredentialField`

# Authorisation - Permission-Role Model

Although Vert.x auth itself does not mandate any specific model of
permissions (they are just opaque strings), this implementation assumes
a familiar user/role/permission model, where a user can have zero or
more roles and a role can have zero or more permissions.

If validating if a user has a particular permission simply pass the
permission into. `isAuthorised` as follows:

``` js
user.isAuthorized("commit_code", (res) => {
  if (res.succeeded()) {
    let hasPermission = res.result();
  } else {
    // Failed to
  }
});
```

If validating that a user has a particular *role* then you should prefix
the argument with the role prefix.

``` js
import { MongoAuth } from "@vertx/auth-mongo"

user.isAuthorized(MongoAuth.ROLE_PREFIX + "manager", (res) => {
  if (res.succeeded()) {
    let hasRole = res.result();
  } else {
    // Failed to
  }
});
```
