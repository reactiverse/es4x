Vert.x client for sending SMTP emails via a local mail server (e.g.
postfix) or by external mail server (e.g. googlemail or aol).

The client supports a few additional auth methods like DIGEST-MD5 and
has full support for TLS and SSL and is completely asynchronous. The
client supports connection pooling to keep connections open to be
reused.

To use this project, add the following dependency to the *dependencies*
section of your build descriptor:

  - Maven (in your `pom.xml`):

<!-- end list -->

``` xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-mail-client</artifactId>
 <version>${maven.version}</version>
</dependency>
```

  - Gradle (in your `build.gradle` file):

<!-- end list -->

``` groovy
compile 'io.vertx:vertx-mail-client:${maven.version}'
```

# Creating a client

You can send mails by creating a client that opens SMTP connections from
the local jvm.

The client uses a configuration object, the default config is created as
empty object and will connect to localhost port 25, which should be ok
in a standard Linux environment where you have Postfix or similar mail
server running on the local machine. For all possible properties of the
config object, see below.

The client can use a connection pool of the SMTP connections to get rid
of the overhead of connecting each time to the server, negotiating TLS
and login (this function can be turned off by setting keepAlive =
false). A client can either be shared or non-shared, if it is shared,
the same connection pool will be used for all clients using the same
identifier.

``` js
import { MailClient } from "@vertx/mail"
let config = new MailConfig();
let mailClient = MailClient.createShared(vertx, config, "exampleclient");
```

The first call to MailClient.createShared will actually create the pool
with the specified config. Subsequent calls will return a new client
instance that uses the same pool, so the configuration won’t be used.

If you leave out the pool identifier, a default pool will be created.
Note that the clients are shared in the scope of a vertx instance only
(so two different vertx will have different pools with the same
identifier).

The unshared client can be created the same way leaving out the
identifier.

``` js
import { MailClient } from "@vertx/mail"
let config = new MailConfig();
let mailClient = MailClient.create(vertx, config);
```

A more elaborate example using a mailserver that requires login via TLS

``` js
import { MailClient } from "@vertx/mail"
let config = new MailConfig();
config.hostname = "mail.example.com";
config.port = 587;
config.starttls = "REQUIRED";
config.username = "user";
config.password = "password";
let mailClient = MailClient.create(vertx, config);
```

# Sending mails

Once the client object is created, you can use it to send mails. Since
the sending of the mails works asynchronous in vert.x, the result
handler will be called when the mail operation finishes. You can start
many mail send operations in parallel, the connection pool will limit
the number of concurrent operations so that new operations will wait in
queue if no slots are available.

A mail message is constructed as JSON. The MailMessage object has
properties from, to, cc, bcc, subject, text, html etc. Depending on
which values are set, the format of the generated MIME message will
vary. The recipient address properties can either be a single address or
a list of addresses.

The MIME encoder supports us-ascii (7bit) headers/messages and utf8
(usually quoted-printable) headers/messages

``` js
let message = new MailMessage();
message.from = "user@example.com (Example User)";
message.to = "recipient@example.org";
message.cc = "Another User <another@example.net>";
message.text = "this is the plain message text";
message.html = "this is html text <a href=\"http://vertx.io\">vertx.io</a>";
```

Attachments can be created by the MailAttachment object using data
stored in a Buffer, this supports base64 attachments.

``` js
import { Buffer } from "@vertx/core"
let attachment = new MailAttachment();
attachment.contentType = "text/plain";
attachment.data = Buffer.buffer("attachment file");

message.attachment = attachment;
```

When using inline attachments (usually images), it is possible to
reference the images within a html message to display html with the
images included in the mail. Images can be referenced as \<img
src="cid:contentid@domain"\> in the html text, the corresponding image
has Disposition: inline and the Content-ID header as
"\<contentid@domain\>". Please note that RFC 2392 requires Content-ID
values to be structured like a Message-ID with angle brackets and a
local and domain part using URL compatible encoding. None of this is not
enforced and most mail clients supports IDs without angle brackets or
without domain part, the best practice is to use the strict format. A
valid example for a Content-ID value is
"\<<filename%201.jpg@example.org>\>"

``` js
import { Buffer } from "@vertx/core"
let attachment = new MailAttachment();
attachment.contentType = "image/jpeg";
attachment.data = Buffer.buffer("image data");
attachment.disposition = "inline";
attachment.contentId = "<image1@example.com>";

message.inlineAttachment = attachment;
```

When sending the mail, you can provide a AsyncResult\<MailResult\>
handler that will be called when the send operation is finished or it
failed.

A mail is sent as follows:

``` js
mailClient.sendMail(message, (result, result_err) => {
  if (result.succeeded()) {
    console.log(result.result());
  } else {
    result.cause().printStackTrace();
  }
});
```

# Mail-client data objects

## MailMessage properties

Email fields are Strings using the common formats for email with or
without real name

  - `username@example.com`

  - `username@example.com (Firstname Lastname)`

  - `Firstname Lastname <username@example.com>`

The MailMessage object has the following properties

  - `from` String representing the From address and the MAIL FROM field

  - `to` String or list of String representing the To addresses and the
    RCPT TO fields

  - `cc` same as to

  - `bcc` same as to

  - `bounceAddress` String representing the error address (MAIL FROM),
    if not set from is used

  - `text` String representing the text/plain part of the mail

  - `html` String representing the text/html part of the mail

  - `attachment` MailAttachment or list of MailAttachment attachments of
    the message

  - `inlineAttachment` MailAttachment or list of MailAttachment of
    inline attachments of the message (usually images)

  - `headers` MultiMap representing headers to be added in addition to
    the headers necessary for the MIME Message

  - `fixedHeaders` boolean if true, only the headers provided as headers
    property will be set in the generated message

the last two properties allow manipulating the generate messages with
custom headers, e.g. providing a message-id chosen by the calling
program or setting different headers than would be generated by default.
Unless you know what you are doing, this may generate invalid messages.

## MailAttachment properties

The MailAttachment object has the following properties

  - `data` Buffer containing the binary data of the attachment

  - `contentType` String of the Content-Type of the attachment (e.g.
    text/plain or text/plain; charset="UTF8", default is
    application/octet-stream)

  - `description` String describing the attachment (this is put in the
    description header of the attachment), optional

  - `disposition` String describing the disposition of the attachment
    (this is either "inline" or "attachment", default is attachment)

  - `name` String filename of the attachment (this is put into the
    disposition and in the Content-Type headers of the attachment),
    optional

  - `contentId` String describing the Content-Id of the attachment (this
    is used to identify inline images), optional

  - `headers` MultiMap of headers for the attachment in addition to the
    default ones, optional

## MailConfig options

The configuration has the following properties

  - `hostname` the hostname of the smtp server to connect to (default is
    localhost)

  - `port` the port of the smtp server to connect to (default is 25)

  - `startTLS` StartTLSOptions either DISABLED, OPTIONAL or REQUIRED,
    default is OPTIONAL

  - `login` LoginOption either DISABLED, NONE or REQUIRED, default is
    NONE

  - `username` String of the username to be used for login (required
    only when LoginOption is REQUIRED)

  - `password` String of the password to be used for login (required
    only when LoginOption is REQUIRED)

  - `ssl` boolean whether to use ssl on connect to the mail server
    (default is false), set this to use a port 465 ssl connection
    (default is false)

  - `ehloHostname` String to used in EHLO and for creating the
    message-id, if not set, the own hostname will be used, which may not
    be a good choice if it doesn’t contain a FQDN or is localhost
    (optional)

  - `authMethods` String space separated list of allowed auth methods,
    this can be used to disallow some auth methods or define one
    required auth method (optional)

  - `keepAlive` boolean if connection pooling is enabled (default is
    true)

  - `maxPoolSize` int max number of open connections kept in the pool or
    to be opened at one time (regardless if pooling is enabled or not),
    default is 10

  - `trustAll` boolean whether to accept all certs from the server
    (default is false)

  - `keyStore` String the key store filename, this can be used to trust
    a server cert that is custom generated (optional)

  - `keyStorePassword` String password used to decrypt the key store
    (optional)

  - `allowRcptErrors` boolean if true, sending continues if a recipient
    address is not accepted and the mail will be sent if at least one
    address is accepted (default false)

  - `disableEsmtp` boolean if true, ESMTP-related commands will not be
    used (set if your smtp server doesn’t even give a proper error
    response code for the EHLO command) (default false)

  - `userAgent` String represents the Mail User Agent(MUA) name used to
    generate email boundaries for multipart emails and message-id,
    default is `vertxmail`.

## MailResult object

The MailResult object has the following members

  - `messageID` the Message-ID of the generated mail

  - `recipients` the list of recipients the mail was sent to (if
    allowRcptErrors is true, this may be fewer than the intended
    recipients)
