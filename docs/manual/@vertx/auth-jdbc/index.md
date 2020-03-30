# JDBC Auth Provider implementation

We provide an implementation of `AuthProvider` which uses the Vert.x
`JDBCClient` to perform authentication and authorisation against any
JDBC compliant database. To use this project, add the following
dependency to the *dependencies* section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-auth-jdbc</artifactId>
 <version>${maven.version}</version>
</dependency>
```

  - Gradle (in your `build.gradle` file):

<!-- end list -->

``` groovy
compile 'io.vertx:vertx-auth-jdbc:${maven.version}'
```

To create an instance you first need an instance of `JDBCClient`. To
learn how to create one of those please consult the documentation for
the JDBC client.

Once you’ve got one of those you can create a `JDBCAuth` instance as
follows:

``` js
import { JDBCClient } from "@vertx/jdbc-client"
import { JDBCAuth } from "@vertx/auth-jdbc"

let jdbcClient = JDBCClient.createShared(vertx, jdbcClientConfig);

let authProvider = JDBCAuth.create(vertx, jdbcClient);
```

Once you’ve got your instance you can authenticate and authorise with it
just like any `AuthProvider`.

The out of the box config assumes certain queries for authentication and
authorisation, these can easily be changed with the operations
`setAuthenticationQuery`, `setPermissionsQuery` and `setRolesQuery`, if
you want to use them with a different database schema.

The default implementation assumes that the password is stored in the
database as a SHA-512 hash after being concatenated with a salt. It also
assumes the salt is stored in the table too.

The basic data definition for the storage should look like this:

``` sql
--
-- Take this script with a grain of salt and adapt it to your RDBMS
--
CREATE TABLE `user` (
 `username` VARCHAR(255) NOT NULL,
 `password` VARCHAR(255) NOT NULL,
 `password_salt` VARCHAR(255) NOT NULL
);

CREATE TABLE `user_roles` (
 `username` VARCHAR(255) NOT NULL,
 `role` VARCHAR(255) NOT NULL
);

CREATE TABLE `roles_perms` (
 `role` VARCHAR(255) NOT NULL,
 `perm` VARCHAR(255) NOT NULL
);

ALTER TABLE user ADD CONSTRAINT `pk_username` PRIMARY KEY (username);
ALTER TABLE user_roles ADD CONSTRAINT `pk_user_roles` PRIMARY KEY (username, role);
ALTER TABLE roles_perms ADD CONSTRAINT `pk_roles_perms` PRIMARY KEY (role);

ALTER TABLE user_roles ADD CONSTRAINT fk_username FOREIGN KEY (username) REFERENCES user(username);
ALTER TABLE user_roles ADD CONSTRAINT fk_roles FOREIGN KEY (role) REFERENCES roles_perms(role);
```

The current password hashing strategy is based on the SHA-512 algorithm.
OWASP as of 2018-01-08 recommends the usage of stronger algorithms, for
this case you can use the PBKDF2 strategy (OWASP recommendation).

> **Warning**
> 
> If you already have a running application switching the strategies
> will make break your existing passwords, so you will need to migrate
> the passwords from one algorithm to the second.

If you want to override this behaviour you can do so by providing an
alternative hash strategy and setting it with `setHashStrategy`.

> **Warning**
> 
> It is advised to always store your passwords as hashes in your
> database tables which have been created with a salt which should be
> stored in the row too. A strong hashing algorithm should be used. It
> is strongly advised never to store your passwords as plain text.

# Vertx Auth JDBC and GDPR

GDPR is a regulation from the common European Union law. It
overrides/supercedes national data protection laws and extents the
previously existing directives. This section of the manual is by no
means a thorough walkthrough of the regulation, it is just a small
summary how this component adheres to the requirements. Companies not
adhering to the requirements can be fined on 4% of the turnover or 20
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
own data schema and use the username column as a foreign key to your
data. As a tip you should have a boolean flag to mark the personal data
as restricted to comply to the right to restriction of processing which
means that if you need to handle the data, e.g.: send a bulk email from
a mailing list you are not allowed to do so if the flag is true.

The right to erasure does not mean that you must wipe all records from
your application, e.g.: in a bank this right cannot be used to erase a
running loan or debt. You are allowed to keep your application data but
must erase the personal data. In case of Vert.x Auth JDBC you should
delete your table but can still use a foreign key to the username as
long as is not possible to link the username to the personal data.

Important note is that this must survive backups\! As a tip backup the
data, and data erasure on different archives so they can be replayed
individually.

# Hashing passwords

Like any application there will be a time where you need to store new
users into the database. Has you have learn passwords are not stored in
plain text but hashed according to the hashing strategy. The same
strategy is required to hash new password before storing it to the
database. Doing it is a 3 step task.

1.  Generate a salt string

2.  Hash the password given the salt string

3.  Store it to the database

<!-- end list -->

``` js
let salt = auth.generateSalt();
let hash = auth.computeHash("sausages", salt);
// save to the database
conn.updateWithParams("INSERT INTO user VALUES (?, ?, ?)", [
  "tim",
  hash,
  salt
], (res) => {
  if (res.succeeded()) {
    // success!
  }
});
```

> **Warning**
> 
> Hashing user password with salt can be not enough, this approach his
> good enough for avoiding rainbow tables attacks or precomputed table
> attacks but if the attacker gets the database it will be easier to
> setup a brute force attack. This kind of attack is slower but all
> required information is given: the hash and the salt.

To make the hash attack more complex the default strategy allows you to
provide an application level list of nonces to be used in the
computation. This list should not be stored in the database since it add
an extra variable to the computation that is unknown, making the brute
force attack as potentially the only way to crack the hash. You might
want to refresh the nonces now and then so you should add and never
remove entries to the list, for example:

``` js
auth.setNonces([
  "random_hash_1",
  "random_hash_1"
]);
```

In order to decode there is no change required to the code, however to
generate a new user you must specify which nonce (by it’s index) you
want to use. If you look at the previous example, the usage is quite
similar:

1.  Generate a salt string

2.  Hash the password given the salt string and choosen nonce

3.  Store it to the database

<!-- end list -->

``` js
auth.setNonces([
  "random_hash_1",
  "random_hash_1"
]);

let salt = auth.generateSalt();
// we will pick the second nonce
let hash = auth.computeHash("sausages", salt, 1);
// save to the database
conn.updateWithParams("INSERT INTO user VALUES (?, ?, ?)", [
  "tim",
  hash,
  salt
], (res) => {
  if (res.succeeded()) {
    // success!
  }
});
```

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
user.isAuthorized("role:manager", (res) => {
  if (res.succeeded()) {
    let hasRole = res.result();
  } else {
    // Failed to
  }
});
```

The default role prefix is `role:`. You can change this with
`setRolePrefix`.

@author \<a href="mailto:julien@julienviet.com"\>Julien Viet\</a\>
@author \<a href="http://tfox.org"\>Tim Fox\</a\>
