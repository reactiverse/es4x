# The Apache Shiro Auth provider implementation

This is an auth provider implementation that uses [Apache
Shiro](http://shiro.apache.org/). To use this project, add the following
dependency to the *dependencies* section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-auth-shiro</artifactId>
 <version>${maven.version}</version>
</dependency>
```

  - Gradle (in your `build.gradle` file):

<!-- end list -->

``` groovy
compile 'io.vertx:vertx-auth-shiro:${maven.version}'
```

We provide out of the box support for properties and LDAP based auth
using Shiro, and you can also plugin in any other Shiro Realm which
expects username and password for credentials.

To create an instance of the provider you use `ShiroAuth`. You specify
the type of Shiro auth provider that you want with `ShiroAuthRealmType`,
and you specify the configuration in a JSON object.

Here’s an example of creating a Shiro auth provider by specifying the
type:

``` java
import { ShiroAuth } from "@vertx/auth-shiro"

let config = {
  "properties_path" : "classpath:test-auth.properties"
};

let provider = ShiroAuth.create(vertx, new ShiroAuthOptions()
  .setType("PROPERTIES")
  .setConfig(config));
```

# Authentication

When authenticating using this implementation, it assumes `username` and
`password` fields are present in the authentication info:

``` java
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
permissions (they are just opaque strings), this implementation uses a
familiar user/role/permission model, where a user can have zero or more
roles and a role can have zero or more permissions.

If validating if a user has a particular permission simply pass the
permission into. `isAuthorised` as follows:

``` java
user.isAuthorized("newsletter:edit:13", (res) => {
  if (res.succeeded()) {
    let hasPermission = res.result();
  } else {
    // Failed to
  }
});
```

If validating that a user has a particular *role* then you should prefix
the argument with the role prefix.

``` java
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

## The Shiro properties auth provider

This auth provider implementation uses Apache Shiro to get
user/role/permission information from a properties file.

Note that roles are not available directly on the API due to the fact
that vertx-auth tries to be as portable as possible. However one can run
assertions on role by using the prefix `role:` or by specifying the
prefered prefix with `setRolePrefix`.

The implementation will, by default, look for a file called
`vertx-users.properties` on the classpath.

If you want to change this, you can use the `properties_path`
configuration element to define how the properties file is found.

The default value is `classpath:vertx-users.properties`.

If the value is prefixed with `classpath:` then the classpath will be
searched for a properties file of that name.

If the value is prefixed with `file:` then it specifies a file on the
file system.

If the value is prefixed with `url:` then it specifies a URL from where
to load the properties.

The properties file should have the following structure:

Each line should either contain the username, password and roles for a
user or the permissions in a role.

For a user line it should be of the form:

user.{username}={password},{roleName1},{roleName2},…​,{roleNameN}

For a role line it should be of the form:

role.{roleName}={permissionName1},{permissionName2},…​,{permissionNameN}

Here’s an example:

    user.tim = mypassword,administrator,developer
    user.bob = hispassword,developer
    user.joe = anotherpassword,manager
    role.administrator=*
    role.manager=play_golf,say_buzzwords
    role.developer=do_actual_work

When describing roles a wildcard `*` can be used to indicate that the
role has all permissions.

## The Shiro LDAP auth provider

The LDAP auth realm gets user/role/permission information from an LDAP
server.

The following configuration properties are used to configure the LDAP
realm:

  - `ldap_user_dn_template`  
    this is used to determine the actual lookup to use when looking up a
    user with a particular id. An example is
    `uid={0},ou=users,dc=foo,dc=com` - the element `{0}` is substituted
    with the user id to create the actual lookup. This setting is
    mandatory.

  - `ldap_url`  
    the url to the LDAP server. The url must start with `ldap://` and a
    port must be specified. An example is
    `ldap://myldapserver.mycompany.com:10389`

  - `ldap_authentication_mechanism`  
    Sets the type of LDAP authentication mechanism to use when
    connecting to the LDAP server.

  - `ldap_context_factory_class_name`  
    The name of the ContextFactory class to use. This defaults to the
    SUN LDAP JNDI implementation but can be overridden to use custom
    LDAP factories.

  - `ldap_pooling_enabled`  
    Sets whether or not connection pooling should be used when possible
    and appropriate.

  - `ldap_referral`  
    Sets the LDAP referral behavior when creating a connection. Defaults
    to `follow`. See the Sun/Oracle LDAP referral documentation for
    more:
    <http://java.sun.com/products/jndi/tutorial/ldap/referral/jndi.html>

  - `ldap_system_username`  
    Sets the system username that will be used when creating an LDAP
    connection used for authorization queries. The user must have the
    ability to query for authorization data for any application user.
    Note that setting this property is not required if the calling LDAP
    Realm does not perform authorization checks.

  - `ldap_system_password`  
    Sets the password of the that will be used when creating an LDAP
    connection used for authorization queries. Note that setting this
    property is not required if the calling LDAP Realm does not perform
    authorization checks.

For more information, refer to the documentation of
org.apache.shiro.realm.ldap.JndiLdapContextFactory.

## Using another Shiro Realm

It’s also possible to create an auth provider instance using a
pre-created Apache Shiro Realm object.

This is done as follows:

``` java
import { ShiroAuth } from "@vertx/auth-shiro"

let provider = ShiroAuth.create(vertx, realm);
```

The implementation currently assumes that user/password based
authentication is used.
