Vert.x Shell is a command line interface for the Vert.x runtime
available from regular terminals using different protocols.

Vert.x Shell provides a variety of commands for interacting live with
Vert.x services.

Vert.x Shell can be extended with custom commands in any language
supported by Vert.x

# Using Vert.x Shell

Vert.x Shell is a Vert.x Service and can be started programmatically via
the `ShellService` or deployed as a service.

## Shell service

The shell can be started as a service directly either from the command
line or as a the Vert.x deployment:

**Starting a shell service available via Telnet.**

    vertx run -conf '{"telnetOptions":{"port":5000}}' maven:io.vertx:vertx-shell:${maven.version}

or

**Starting a shell service available via SSH.**

    # create a key pair for the SSH server
    keytool -genkey -keyalg RSA -keystore ssh.jks -keysize 2048 -validity 1095 -dname CN=localhost -keypass secret -storepass secret
    # create the auth config
    echo user.admin=password > auth.properties
    # start the shell
    vertx run -conf '{"sshOptions":{"port":4000,"keyPairOptions":{"path":"ssh.jks","password":"secret"},"authOptions":{"provider":"shiro","config":{"properties_path":"file:auth.properties"}}}}' maven:io.vertx:vertx-shell:${maven.version}

or

**Starting a shell service available via HTTP.**

    # create a certificate for the HTTP server
    keytool -genkey -keyalg RSA -keystore keystore.jks -keysize 2048 -validity 1095 -dname CN=localhost -keypass secret -storepass secret
    # create the auth config
    echo user.admin=password > auth.properties
    vertx run -conf '{"httpOptions":{"port":8080,"ssl":true,"keyStoreOptions":{"path":"keystore.jks","password":"secret"},"authOptions":{"provider":""shiro,"config":{"properties_path":"file:auth.properties"}}}}' maven:io.vertx:vertx-shell:${maven.version}

You can also deploy this service inside your own verticle:

``` js
vertx.deployVerticle("maven:{maven-groupId}:{maven-artifactId}:{maven-version}", new DeploymentOptions()
  .setConfig({
    "telnetOptions" : {
      "host" : "localhost",
      "port" : 4000
    }
  }));
```

or

``` js
vertx.deployVerticle("maven:{maven-groupId}:{maven-artifactId}:{maven-version}", new DeploymentOptions()
  .setConfig({
    "sshOptions" : {
      "host" : "localhost",
      "port" : 5000,
      "keyPairOptions" : {
        "path" : "src/test/resources/ssh.jks",
        "password" : "wibble"
      },
      "authOptions" : {
        "provider" : "shiro",
        "config" : {
          "properties_path" : "file:/path/to/my/auth.properties"
        }
      }
    }
  }));
```

or

``` js
vertx.deployVerticle("maven:{maven-groupId}:{maven-artifactId}:{maven-version}", new DeploymentOptions()
  .setConfig({
    "httpOptions" : {
      "host" : "localhost",
      "port" : 8080,
      "ssl" : true,
      "keyPairOptions" : {
        "path" : "src/test/resources/server-keystore.jks",
        "password" : "wibble"
      },
      "authOptions" : {
        "provider" : "shiro",
        "config" : {
          "properties_path" : "file:/path/to/my/auth.properties"
        }
      }
    }
  }));
```

> **Note**
> 
> when Vert.x Shell is already on your classpath you can use
> `service:io.vertx.ext.shell` instead or
> `maven:io.vertx:vertx-shell:${maven.version}`

## Programmatic service

The `ShellService` takes care of starting an instance of Vert.x Shell.

Starting a shell service available via SSH:

``` js
import { ShellService } from "@vertx/shell"
let service = ShellService.create(vertx, new ShellServiceOptions()
  .setSSHOptions(new SSHTermOptions()
    .setHost("localhost")
    .setPort(5000)
    .setKeyPairOptions(new JksOptions()
      .setPath("server-keystore.jks")
      .setPassword("wibble"))
    .setAuthOptions(new ShiroAuthOptions()
      .setType("PROPERTIES")
      .setConfig({
        "properties_path" : "file:/path/to/my/auth.properties"
      }))));
service.start();
```

Starting a shell service available via Telnet:

``` js
import { ShellService } from "@vertx/shell"
let service = ShellService.create(vertx, new ShellServiceOptions()
  .setTelnetOptions(new TelnetTermOptions()
    .setHost("localhost")
    .setPort(4000)));
service.start();
```

The `TelnetTermOptions` extends the Vert.x Core `NetServerOptions` as
the Telnet server implementation is based on a `NetServer`.

> **Caution**
> 
> Telnet does not provide any authentication nor encryption at all.

Starting a shell service available via HTTP:

``` js
import { ShellService } from "@vertx/shell"
let service = ShellService.create(vertx, new ShellServiceOptions()
  .setHttpOptions(new HttpTermOptions()
    .setHost("localhost")
    .setPort(8080)));
service.start();
```

# Authentication

The SSH and HTTP connectors provide both authentication built on top of
*vertx-auth* with the following supported providers:

  - *shiro* : provides `.properties` and *LDAP* backend as seen in the
    ShellService presentation

  - *jdbc* : JDBC backend

  - *mongo* : MongoDB backend

These options can be created directly using directly `AuthOptions`:

  - `ShiroAuthOptions` for Shiro

  - `JDBCAuthOptions` for JDBC

  - `MongoAuthOptions` for Mongo

As for external service configuration in Json, the `authOptions` uses
the `provider` property to distinguish:

    {
     ...
     "authOptions": {
       "provider":"shiro",
       "config": {
         "properties_path":"file:auth.properties"
       }
     }
     ...
    }

# Telnet term configuration

Telnet terms are configured by `setTelnetOptions`, the
`TelnetTermOptions` extends the `NetServerOptions` so they have the
exact same configuration.

# SSH term configuration

SSH terms are configured by `setSSHOptions`:

  - `setPort`: port

  - `setHost`: host

Only username/password authentication is supported at the moment, it can
be configured with property file or LDAP, see Vert.x Auth for more info:

  - `setAuthOptions`: configures user authentication

The server key configuration reuses the key pair store configuration
scheme provided by *Vert.x Core*:

  - `setKeyPairOptions`: set `.jks` key pair store

  - `setPfxKeyPairOptions`: set `.pfx` key pair store

  - `setPemKeyPairOptions`: set `.pem` key pair store

**Deploying the Shell Service on SSH with Mongo authentication.**

``` js
vertx.deployVerticle("maven:{maven-groupId}:{maven-artifactId}:{maven-version}", new DeploymentOptions()
  .setConfig({
    "sshOptions" : {
      "host" : "localhost",
      "port" : 5000,
      "keyPairOptions" : {
        "path" : "src/test/resources/ssh.jks",
        "password" : "wibble"
      },
      "authOptions" : {
        "provider" : "mongo",
        "config" : {
          "connection_string" : "mongodb://localhost:27018"
        }
      }
    }
  }));
```

**Running the Shell Service on SSH with Mongo authentication.**

``` js
import { ShellService } from "@vertx/shell"
let service = ShellService.create(vertx, new ShellServiceOptions()
  .setSSHOptions(new SSHTermOptions()
    .setHost("localhost")
    .setPort(5000)
    .setKeyPairOptions(new JksOptions()
      .setPath("server-keystore.jks")
      .setPassword("wibble"))
    .setAuthOptions(new MongoAuthOptions()
      .setConfig({
        "connection_string" : "mongodb://localhost:27018"
      }))));
service.start();
```

**Deploying the Shell Service on SSH with JDBC authentication.**

``` js
vertx.deployVerticle("maven:{maven-groupId}:{maven-artifactId}:{maven-version}", new DeploymentOptions()
  .setConfig({
    "sshOptions" : {
      "host" : "localhost",
      "port" : 5000,
      "keyPairOptions" : {
        "path" : "src/test/resources/ssh.jks",
        "password" : "wibble"
      },
      "authOptions" : {
        "provider" : "jdbc",
        "config" : {
          "url" : "jdbc:hsqldb:mem:test?shutdown=true",
          "driver_class" : "org.hsqldb.jdbcDriver"
        }
      }
    }
  }));
```

**Running the Shell Service on SSH with JDBC authentication.**

``` js
import { ShellService } from "@vertx/shell"
let service = ShellService.create(vertx, new ShellServiceOptions()
  .setSSHOptions(new SSHTermOptions()
    .setHost("localhost")
    .setPort(5000)
    .setKeyPairOptions(new JksOptions()
      .setPath("server-keystore.jks")
      .setPassword("wibble"))
    .setAuthOptions(new JDBCAuthOptions()
      .setConfig({
        "url" : "jdbc:hsqldb:mem:test?shutdown=true",
        "driver_class" : "org.hsqldb.jdbcDriver"
      }))));
service.start();
```

# HTTP term configuration

HTTP terms are configured by `setHttpOptions`, the http options extends
the `HttpServerOptions` so they expose the exact same configuration.

In addition there are extra options for configuring an HTTP term:

  - `setAuthOptions`: configures user authentication

  - `setSockJSHandlerOptions`: configures SockJS

  - `setSockJSPath`: the SockJS path in the router

**Deploying the Shell Service on HTTP with Mongo authentication.**

``` js
vertx.deployVerticle("maven:{maven-groupId}:{maven-artifactId}:{maven-version}", new DeploymentOptions()
  .setConfig({
    "httpOptions" : {
      "host" : "localhost",
      "port" : 8080,
      "ssl" : true,
      "keyPairOptions" : {
        "path" : "src/test/resources/server-keystore.jks",
        "password" : "wibble"
      },
      "authOptions" : {
        "provider" : "mongo",
        "config" : {
          "connection_string" : "mongodb://localhost:27018"
        }
      }
    }
  }));
```

**Running the Shell Service on HTTP with Mongo authentication.**

``` js
import { ShellService } from "@vertx/shell"
let service = ShellService.create(vertx, new ShellServiceOptions()
  .setHttpOptions(new HttpTermOptions()
    .setHost("localhost")
    .setPort(8080)
    .setAuthOptions(new MongoAuthOptions()
      .setConfig({
        "connection_string" : "mongodb://localhost:27018"
      }))));
service.start();
```

**Deploying the Shell Service on HTTP with JDBC authentication.**

``` js
vertx.deployVerticle("maven:{maven-groupId}:{maven-artifactId}:{maven-version}", new DeploymentOptions()
  .setConfig({
    "httpOptions" : {
      "host" : "localhost",
      "port" : 8080,
      "ssl" : true,
      "keyPairOptions" : {
        "path" : "src/test/resources/server-keystore.jks",
        "password" : "wibble"
      },
      "authOptions" : {
        "provider" : "jdbc",
        "config" : {
          "url" : "jdbc:hsqldb:mem:test?shutdown=true",
          "driver_class" : "org.hsqldb.jdbcDriver"
        }
      }
    }
  }));
```

**Running the Shell Service on HTTP with JDBC authentication.**

``` js
import { ShellService } from "@vertx/shell"
let service = ShellService.create(vertx, new ShellServiceOptions()
  .setHttpOptions(new HttpTermOptions()
    .setHost("localhost")
    .setPort(8080)
    .setAuthOptions(new JDBCAuthOptions()
      .setConfig({
        "url" : "jdbc:hsqldb:mem:test?shutdown=true",
        "driver_class" : "org.hsqldb.jdbcDriver"
      }))));
service.start();
```

# Keymap configuration

The shell uses a default keymap configuration that can be overriden
using the `inputrc` property of the various term configuration object:

  - `setIntputrc`

  - `setIntputrc`

  - `setIntputrc`

The `inputrc` must point to a file available via the classloader or the
filesystem.

The `inputrc` only function bindings and the available functions are:

  - *backward-char*

  - *forward-char*

  - *next-history*

  - *previous-history*

  - *backward-delete-char*

  - *backward-delete-char*

  - *backward-word*

  - *end-of-line*

  - *beginning-of-line*

  - *delete-char*

  - *delete-char*

  - *complete*

  - *accept-line*

  - *accept-line*

  - *kill-line*

  - *backward-word*

  - *forward-word*

  - *backward-kill-word*

> **Note**
> 
> Extra functions can be added, however this is done by implementing
> functions of the `Term.d` project on which Vert.x Shell is based, for
> instance the [reverse
> function](https://github.com/termd/termd/blob/c1629623c8a3add4bde7778640bf8cc233a7c98f/src/examples/java/examples/readlinefunction/ReverseFunction.java)
> can be implemented and then declared in a
> `META-INF/services/io.termd.core.readline.Function` to be loaded by
> the shell.

# Base commands

To find out the available commands you can use the *help* builtin
command:

1.  Verticle commands
    
    1.  verticle-ls: list all deployed verticles
    
    2.  verticle-undeploy: undeploy a verticle
    
    3.  verticle-deploy: deploys a verticle with deployment options as
        JSON string
    
    4.  verticle-factories: list all known verticle factories

2.  File system commands
    
    1.  ls
    
    2.  cd
    
    3.  pwd

3.  Bus commands
    
    1.  bus-tail: display all incoming messages on an event bus address
    
    2.  bus-send: send a message on the event bus

4.  Net commands
    
    1.  net-ls: list all available net servers, including HTTP servers

5.  Shared data commands
    
    1.  local-map-put
    
    2.  local-map-get
    
    3.  local-map-rm

6.  Various commands
    
    1.  echo
    
    2.  sleep
    
    3.  help
    
    4.  exit
    
    5.  logout

7.  Job control
    
    1.  fg
    
    2.  bg
    
    3.  jobs

> **Note**
> 
> this command list should evolve in next releases of Vert.x Shell.
> Other Vert.x project may provide commands to extend Vert.x Shell, for
> instance Dropwizard Metrics.

# Extending Vert.x Shell

Vert.x Shell can be extended with custom commands in any of the
languages supporting code generation.

A command is created by the `CommandBuilder.command` method: the command
process handler is called by the shell when the command is executed,
this handler can be set with the `processHandler` method:

``` js
import { CommandBuilder } from "@vertx/shell"
import { CommandRegistry } from "@vertx/shell"

let builder = CommandBuilder.command("my-command");
builder.processHandler((process) => {

  // Write a message to the console
  process.write("Hello World");

  // End the process
  process.end();
});

// Register the command
let registry = CommandRegistry.getShared(vertx);
registry.registerCommand(builder.build(vertx));
```

After a command is created, it needs to be registed to a
`CommandRegistry`. The command registry holds all the commands for a
Vert.x instance.

A command is registered until it is unregistered with the
`unregisterCommand`. When a command is registered from a Verticle, this
command is unregistered when this verticle is undeployed.

> **Note**
> 
> Command callbacks are invoked in the {@literal io.vertx.core.Context}
> when the command is registered in the registry. Keep this in mind if
> you maintain state in a command.

The `CommandProcess` object can be used for interacting with the shell.

## Command arguments

The `args` returns the command arguments:

``` js
command.processHandler((process) => {

  process.args().forEach(arg => {
    // Print each argument on the console
    process.write("Argument " + arg);
  });

  process.end();
});
```

Besides it is also possible to create commands using `Vert.x CLI`: it
makes easier to write command line argument parsing:

  - *option* and *argument* parsing

  - argument *validation*

  - generation of the command *usage*

<!-- end list -->

``` js
import { CLI } from "@vertx/core"
import { CommandBuilder } from "@vertx/shell"
let cli = CLI.create("my-command").addArgument(new Argument()
  .setArgName("my-arg")).addOption(new Option()
  .setShortName("m")
  .setLongName("my-option"));
let command = CommandBuilder.command(cli);
command.processHandler((process) => {

  let commandLine = process.commandLine();

  let argValue = commandLine.getArgumentValue(0);
  let optValue = commandLine.getOptionValue("my-option");
  process.write("The argument is " + argValue + " and the option is " + optValue);

  process.end();
});
```

When an option named *help* is added to the CLI object, the shell will
take care of generating the command usage when the option is activated:

``` js
import { CLI } from "@vertx/core"
import { CommandBuilder } from "@vertx/shell"
let cli = CLI.create("my-command").addArgument(new Argument()
  .setArgName("my-arg")).addOption(new Option()
  .setArgName("help")
  .setShortName("h")
  .setLongName("help"));
let command = CommandBuilder.command(cli);
command.processHandler((process) => {
  // ...
});
```

When the command executes the `process` is provided for interacting with
the shell. A `CommandProcess` extends `Tty` which is used for
interacting with the terminal.

## Terminal usage

### terminal I/O

The `stdinHandler` handler is used to be notified when the terminal
receives data, e.g the user uses his keyboard:

``` js
tty.stdinHandler((data) => {
  console.log("Received " + data);
});
```

A command can use the `write` to write to the standard output.

``` js
tty.write("Hello World");
```

### Terminal size

The current terminal size can be obtained using `width` and `height`.

``` js
tty.write("Current terminal size: (" + tty.width() + ", " + tty.height() + ")");
```

### Resize event

When the size of the terminal changes the `resizehandler` is called, the
new terminal size can be obtained with `width` and `height`.

``` js
tty.resizehandler((v) => {
  console.log("terminal resized : " + tty.width() + " " + tty.height());
});
```

### Terminal type

The terminal type is useful for sending escape codes to the remote
terminal: `type` returns the current terminal type, it can be null if
the terminal has not advertised the value.

``` js
console.log("terminal type : " + tty.type());
```

## Shell session

The shell is a connected service that naturally maintains a session with
the client, this session can be used in commands to scope data. A
command can get the session with `session`:

``` js
command.processHandler((process) => {

  let session = process.session();

  if ((session.get("my_key") === null || session.get("my_key") === undefined)) {
    session.put("my key", "my value");
  }

  process.end();
});
```

## Process termination

Calling `end` ends the current process. It can be called directly in the
invocation of the command handler or any time later:

``` js
command.processHandler((process) => {
  let vertx = process.vertx();

  // Set a timer
  vertx.setTimer(1000, (id) => {

    // End the command when the timer is fired
    process.end();
  });
});
```

## Process events

A command can subscribe to a few process events.

### Interrupt event

The `interruptHandler` is called when the process is interrupted, this
event is fired when the user press *Ctrl+C* during the execution of a
command. This handler can be used for interrupting commands *blocking*
the CLI and gracefully ending the command process:

``` js
command.processHandler((process) => {
  let vertx = process.vertx();

  // Every second print a message on the console
  let periodicId = vertx.setPeriodic(1000, (id) => {
    process.write("tick\n");
  });

  // When user press Ctrl+C: cancel the timer and end the process
  process.interruptHandler((v) => {
    vertx.cancelTimer(periodicId);
    process.end();
  });
});
```

When no interrupt handler is registered, pressing *Ctrl+C* will have no
effect on the current process and the event will be delayed and will
likely be handled by the shell, like printing a new line on the console.

### Suspend/resume events

The `suspendHandler` is called when the process is running and the user
press *Ctrl+Z*, the command is *suspended*:

  - the command can receive the suspend event when it has registered an
    handler for this event

  - the command will not receive anymore data from the standard input

  - the shell prompt the user for input

  - the command can receive interrupts event or end events

The `resumeHandler` is called when the process is resumed, usually when
the user types *fg*:

  - the command can receive the resume event when it has registered an
    handler for this event

  - the command will receive again data from the standard input when it
    has registered an stdin handler

<!-- end list -->

``` js
command.processHandler((process) => {

  // Command is suspended
  process.suspendHandler((v) => {
    console.log("Suspended");
  });

  // Command is resumed
  process.resumeHandler((v) => {
    console.log("Resumed");
  });
});
```

### End events

The `endHandler` (io.vertx.core.Handler)} is called when the process is
running or suspended and the command terminates, for instance the shell
session is closed, the command is *terminated*.

``` js
command.processHandler((process) => {

  // Command terminates
  process.endHandler((v) => {
    console.log("Terminated");
  });
});
```

The end handler is called even when the command invokes `end`.

This handler is useful for cleaning up resources upon command
termination, for instance closing a client or a timer.

## Command completion

A command can provide a completion handler when it wants to provide
contextual command line interface completion.

Like the process handler, the `completion
handler` is non blocking because the implementation may use Vert.x
services, e.g the file system.

The `lineTokens` returns a list of `tokens` from the beginning of the
line to the cursor position. The list can be empty if the cursor when
the cursor is at the beginning of the line.

The `rawLine` returns the current completed from the beginning of the
line to the cursor position, in raw format, i.e without any char escape
performed.

Completion ends with a call to `complete`.

# Shell server

The Shell service is a convenient facade for starting a preconfigured
shell either programmatically or as a Vert.x service. When more
flexibility is needed, a `ShellServer` can be used instead of the
service.

For instance the shell http term can be configured to use an existing
router instead of starting its own http server.

Using a shell server requires explicit configuration but provides full
flexiblity, a shell server is setup in a few steps:

``` js
import { ShellServer } from "@vertx/shell"
import { Router } from "@vertx/web"
import { TermServer } from "@vertx/shell"
import { CommandResolver } from "@vertx/shell"

let server = ShellServer.create(vertx);

let shellRouter = Router.router(vertx);
router.mountSubRouter("/shell", shellRouter);
let httpTermServer = TermServer.createHttpTermServer(vertx, router);

let sshTermServer = TermServer.createSSHTermServer(vertx);

server.registerTermServer(httpTermServer);
server.registerTermServer(sshTermServer);

server.registerCommandResolver(CommandResolver.baseCommands(vertx));

server.listen();
```

  - create a the shell server

  - create an HTTP term server mounted on an existing router

  - create an SSH term server

  - register term servers

  - register all base commands

  - finally start the shell server

Besides, the shell server can also be used for creating in process shell
session: it provides a programmatic interactive shell.

In process shell session can be created with `createShell`:

``` js
// Create a shell ession
let shell = shellServer.createShell();
```

The main use case is running or testing a command:

``` js
import { Pty } from "@vertx/shell"

// Create a shell
let shell = shellServer.createShell();

// Create a job fo the command
let job = shell.createJob("my-command 1234");

// Create a pseudo terminal
let pty = Pty.create();
pty.stdoutHandler((data) => {
  console.log("Command wrote " + data);
});

// Run the command
job.setTty(pty.slave());
job.statusUpdateHandler((status) => {
  console.log("Command terminated with status " + status);
});
```

The `Pty` pseudo terminal is the main interface for interacting with the
command when it’s running:

  - uses standard input/output for writing or reading strings

  - resize the terminal

The `close` closes the shell, it will terminate all jobs in the current
shell session.

# Terminal servers

Vert.x Shell also provides bare terminal servers for those who need to
write pure terminal applications.

A `Term` handler must be set on a term server before starting it. This
handler will handle each term when the user connects.

An `AuthOptions` can be set on `SSHTermOptions` and `HttpTermOptions`.
Alternatively, an `AuthProvider` can be `set` directly on the term
server before starting it.

## SSH term

The terminal server `Term` handler accepts incoming terminal
connections. When a remote terminal connects, the `Term` can be used to
interact with connected terminal.

``` js
import { TermServer } from "@vertx/shell"
let server = TermServer.createSSHTermServer(vertx, new SSHTermOptions()
  .setPort(5000)
  .setHost("localhost"));
server.termHandler((term) => {
  term.stdinHandler((line) => {
    term.write(line);
  });
});
server.listen();
```

The `Term` is also a `Tty`, this section explains how to use the tty.

## Telnet term

``` js
import { TermServer } from "@vertx/shell"
let server = TermServer.createTelnetTermServer(vertx, new TelnetTermOptions()
  .setPort(5000)
  .setHost("localhost"));
server.termHandler((term) => {
  term.stdinHandler((line) => {
    term.write(line);
  });
});
server.listen();
```

## HTTP term

The `TermServer.createHttpTermServer` method creates an HTTP term
server, built on top of Vert.x Web using the SockJS protocol.

``` js
import { TermServer } from "@vertx/shell"
let server = TermServer.createHttpTermServer(vertx, new HttpTermOptions()
  .setPort(5000)
  .setHost("localhost"));
server.termHandler((term) => {
  term.stdinHandler((line) => {
    term.write(line);
  });
});
server.listen();
```

An HTTP term can start its own HTTP server, or it can reuse an existing
Vert.x Web `Router`.

The shell can be found at `/shell.html`.

``` js
import { TermServer } from "@vertx/shell"
let server = TermServer.createHttpTermServer(vertx, router, new HttpTermOptions()
  .setPort(5000)
  .setHost("localhost"));
server.termHandler((term) => {
  term.stdinHandler((line) => {
    term.write(line);
  });
});
server.listen();
```

The later option is convenient when the HTTP shell is integrated in an
existing HTTP server.

The HTTP term server by default is configured for serving:

  - the `shell.html` page

  - the `term.js` client library

  - the `vertxshell.js` client library

The `vertxshell.js` integrates `term.js` is the client side part of the
HTTP term.

It integrates `term.js` with SockJS and needs the URL of the HTTP term
server endpoint:

``` javascript
window.addEventListener('load', function () {
 var url = 'http://localhost/shell';
 new VertxTerm(url, {
   cols: 80,
   rows: 24
  });
});
```

Straight websockets can also be used, if so, the remote term URL should
be suffixed with `/websocket`:

``` javascript
window.addEventListener('load', function () {
 var url = 'ws://localhost/shell/websocket';
 new VertxTerm(url, {
   cols: 80,
   rows: 24
  });
});
```

For customization purpose these resources can be copied and customized,
they are available in the Vert.x Shell jar under the
`io.vertx.ext.shell` packages.

# Command discovery

The command discovery can be used when new commands need to be added to
Vert.x without an explicit registration.

For example, the *Dropwizard* metrics service, adds specific metrics
command to the shell service on the fly.

It can be achieved via the `java.util.ServiceLoader` of a
`CommandResolverFactory`.

``` java
public class CustomCommands implements CommandResolverFactory {

 public void resolver(Vertx vertx, Handler<AsyncResult<CommandResolver>> resolverHandler) {
   resolverHandler.handler(() -> Arrays.asList(myCommand1, myCommand2));
 }
}
```

The `resolver` method is async, because the resolver may need to wait
some condition before commands are resolved.

The shell service discovery using the service loader mechanism:

**The service provider file
`META-INF/services/io.vertx.ext.shell.spi.CommandResolverFactory`.**

    my.CustomCommands

This is only valid for the `ShellService`. `ShellServer` don’t use this
mechanism.

# Command pack

A command pack is a jar that provides new Vert.x Shell commands.

Such jar just need to be present on the classpath and it is discovered
by Vertx. Shell.

``` java
public class CommandPackExample implements CommandResolverFactory {

  @Override
  public void resolver(Vertx vertx, Handler<AsyncResult<CommandResolver>> resolveHandler) {
    List<Command> commands = new ArrayList<>();

    // Add commands
    commands.add(Command.create(vertx, JavaCommandExample.class));

    // Add another command
    commands.add(CommandBuilder.command("another-command").processHandler(process -> {
      // Handle process
    }).build(vertx));

    // Resolve with the commands
    resolveHandler.handle(Future.succeededFuture(() -> commands));
  }
}
```

The command pack uses command discovery mechanism, so it needs the
descriptor:

**`META-INF/services/io.vertx.ext.shell.spi.CommandResolverFactory`
descriptor.**

    examples.pack.CommandPackExample
