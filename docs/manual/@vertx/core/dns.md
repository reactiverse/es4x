# DNS client

Often you will find yourself in situations where you need to obtain DNS
informations in an asynchronous fashion. Unfortunally this is not
possible with the API that is shipped with the Java Virtual Machine
itself. Because of this Vert.x offers it’s own API for DNS resolution
which is fully asynchronous.

To obtain a DnsClient instance you will create a new via the Vertx
instance.

``` js
let client = vertx.createDnsClient(53, "10.0.0.1");
```

You can also create the client with options and configure the query
timeout.

``` js
let client = vertx.createDnsClient(new DnsClientOptions()
  .setPort(53)
  .setHost("10.0.0.1")
  .setQueryTimeout(10000));
```

Creating the client with no arguments or omitting the server address
will use the address of the server used internally for non blocking
address resolution.

``` js
let client1 = vertx.createDnsClient();

// Just the same but with a different query timeout
let client2 = vertx.createDnsClient(new DnsClientOptions()
  .setQueryTimeout(10000));
```

## lookup

Try to lookup the A (ipv4) or AAAA (ipv6) record for a given name. The
first which is returned will be used, so it behaves the same way as you
may be used from when using "nslookup" on your operation system.

To lookup the A / AAAA record for "vertx.io" you would typically use it
like:

``` js
let client = vertx.createDnsClient(53, "9.9.9.9");
client.lookup("vertx.io", (ar) => {
  if (ar.succeeded()) {
    console.log(ar.result());
  } else {
    console.log("Failed to resolve entry" + ar.cause());
  }
});
```

## lookup4

Try to lookup the A (ipv4) record for a given name. The first which is
returned will be used, so it behaves the same way as you may be used
from when using "nslookup" on your operation system.

To lookup the A record for "vertx.io" you would typically use it like:

``` js
let client = vertx.createDnsClient(53, "9.9.9.9");
client.lookup4("vertx.io", (ar) => {
  if (ar.succeeded()) {
    console.log(ar.result());
  } else {
    console.log("Failed to resolve entry" + ar.cause());
  }
});
```

## lookup6

Try to lookup the AAAA (ipv6) record for a given name. The first which
is returned will be used, so it behaves the same way as you may be used
from when using "nslookup" on your operation system.

To lookup the A record for "vertx.io" you would typically use it like:

``` js
let client = vertx.createDnsClient(53, "9.9.9.9");
client.lookup6("vertx.io", (ar) => {
  if (ar.succeeded()) {
    console.log(ar.result());
  } else {
    console.log("Failed to resolve entry" + ar.cause());
  }
});
```

## resolveA

Try to resolve all A (ipv4) records for a given name. This is quite
similar to using "dig" on unix like operation systems.

To lookup all the A records for "vertx.io" you would typically do:

``` js
let client = vertx.createDnsClient(53, "9.9.9.9");
client.resolveA("vertx.io", (ar) => {
  if (ar.succeeded()) {
    let records = ar.result();
    records.forEach(record => {
      console.log(record);
    });
  } else {
    console.log("Failed to resolve entry" + ar.cause());
  }
});
```

## resolveAAAA

Try to resolve all AAAA (ipv6) records for a given name. This is quite
similar to using "dig" on unix like operation systems.

To lookup all the AAAAA records for "vertx.io" you would typically do:

``` js
let client = vertx.createDnsClient(53, "9.9.9.9");
client.resolveAAAA("vertx.io", (ar) => {
  if (ar.succeeded()) {
    let records = ar.result();
    records.forEach(record => {
      console.log(record);
    });
  } else {
    console.log("Failed to resolve entry" + ar.cause());
  }
});
```

## resolveCNAME

Try to resolve all CNAME records for a given name. This is quite similar
to using "dig" on unix like operation systems.

To lookup all the CNAME records for "vertx.io" you would typically do:

``` js
let client = vertx.createDnsClient(53, "9.9.9.9");
client.resolveCNAME("vertx.io", (ar) => {
  if (ar.succeeded()) {
    let records = ar.result();
    records.forEach(record => {
      console.log(record);
    });
  } else {
    console.log("Failed to resolve entry" + ar.cause());
  }
});
```

## resolveMX

Try to resolve all MX records for a given name. The MX records are used
to define which Mail-Server accepts emails for a given domain.

To lookup all the MX records for "vertx.io" you would typically do:

``` js
let client = vertx.createDnsClient(53, "9.9.9.9");
client.resolveMX("vertx.io", (ar) => {
  if (ar.succeeded()) {
    let records = ar.result();
    records.forEach(record => {
      console.log(record);
    });
  } else {
    console.log("Failed to resolve entry" + ar.cause());
  }
});
```

Be aware that the List will contain the `MxRecord` sorted by the
priority of them, which means MX records with smaller priority coming
first in the List.

The `MxRecord` allows you to access the priority and the name of the MX
record by offer methods for it like:

``` js
record.priority();
record.name();
```

## resolveTXT

Try to resolve all TXT records for a given name. TXT records are often
used to define extra informations for a domain.

To resolve all the TXT records for "vertx.io" you could use something
along these lines:

``` js
let client = vertx.createDnsClient(53, "9.9.9.9");
client.resolveTXT("vertx.io", (ar) => {
  if (ar.succeeded()) {
    let records = ar.result();
    records.forEach(record => {
      console.log(record);
    });
  } else {
    console.log("Failed to resolve entry" + ar.cause());
  }
});
```

## resolveNS

Try to resolve all NS records for a given name. The NS records specify
which DNS Server hosts the DNS informations for a given domain.

To resolve all the NS records for "vertx.io" you could use something
along these lines:

``` js
let client = vertx.createDnsClient(53, "9.9.9.9");
client.resolveNS("vertx.io", (ar) => {
  if (ar.succeeded()) {
    let records = ar.result();
    records.forEach(record => {
      console.log(record);
    });
  } else {
    console.log("Failed to resolve entry" + ar.cause());
  }
});
```

## resolveSRV

Try to resolve all SRV records for a given name. The SRV records are
used to define extra informations like port and hostname of services.
Some protocols need this extra informations.

To lookup all the SRV records for "vertx.io" you would typically do:

``` js
let client = vertx.createDnsClient(53, "9.9.9.9");
client.resolveSRV("vertx.io", (ar) => {
  if (ar.succeeded()) {
    let records = ar.result();
    records.forEach(record => {
      console.log(record);
    });
  } else {
    console.log("Failed to resolve entry" + ar.cause());
  }
});
```

Be aware that the List will contain the SrvRecords sorted by the
priority of them, which means SrvRecords with smaller priority coming
first in the List.

The `SrvRecord` allows you to access all informations contained in the
SRV record itself:

``` js
record.priority();
record.name();
record.weight();
record.port();
record.protocol();
record.service();
record.target();
```

Please refer to the API docs for the exact details.

## resolvePTR

Try to resolve the PTR record for a given name. The PTR record maps an
ipaddress to a name.

To resolve the PTR record for the ipaddress 10.0.0.1 you would use the
PTR notion of "1.0.0.10.in-addr.arpa"

``` js
let client = vertx.createDnsClient(53, "9.9.9.9");
client.resolvePTR("1.0.0.10.in-addr.arpa", (ar) => {
  if (ar.succeeded()) {
    let record = ar.result();
    console.log(record);
  } else {
    console.log("Failed to resolve entry" + ar.cause());
  }
});
```

## reverseLookup

Try to do a reverse lookup for an ipaddress. This is basically the same
as resolve a PTR record, but allows you to just pass in the ipaddress
and not a valid PTR query string.

To do a reverse lookup for the ipaddress 10.0.0.1 do something similar
like this:

``` js
let client = vertx.createDnsClient(53, "9.9.9.9");
client.reverseLookup("10.0.0.1", (ar) => {
  if (ar.succeeded()) {
    let record = ar.result();
    console.log(record);
  } else {
    console.log("Failed to resolve entry" + ar.cause());
  }
});
```

## Error handling

As you saw in previous sections the DnsClient allows you to pass in a
Handler which will be notified with an AsyncResult once the query was
complete. In case of an error it will be notified with a DnsException
which will hole a {@link io.vertx.core.dns.DnsResponseCode} that
indicate why the resolution failed. This DnsResponseCode can be used to
inspect the cause in more detail.

Possible DnsResponseCodes are:

  - {@link io.vertx.core.dns.DnsResponseCode\#NOERROR} No record was
    found for a given query

  - {@link io.vertx.core.dns.DnsResponseCode\#FORMERROR} Format error

  - {@link io.vertx.core.dns.DnsResponseCode\#SERVFAIL} Server failure

  - {@link io.vertx.core.dns.DnsResponseCode\#NXDOMAIN} Name error

  - {@link io.vertx.core.dns.DnsResponseCode\#NOTIMPL} Not implemented
    by DNS Server

  - {@link io.vertx.core.dns.DnsResponseCode\#REFUSED} DNS Server
    refused the query

  - {@link io.vertx.core.dns.DnsResponseCode\#YXDOMAIN} Domain name
    should not exist

  - {@link io.vertx.core.dns.DnsResponseCode\#YXRRSET} Resource record
    should not exist

  - {@link io.vertx.core.dns.DnsResponseCode\#NXRRSET} RRSET does not
    exist

  - {@link io.vertx.core.dns.DnsResponseCode\#NOTZONE} Name not in zone

  - {@link io.vertx.core.dns.DnsResponseCode\#BADVERS} Bad extension
    mechanism for version

  - {@link io.vertx.core.dns.DnsResponseCode\#BADSIG} Bad signature

  - {@link io.vertx.core.dns.DnsResponseCode\#BADKEY} Bad key

  - {@link io.vertx.core.dns.DnsResponseCode\#BADTIME} Bad timestamp

All of those errors are "generated" by the DNS Server itself.

You can obtain the DnsResponseCode from the DnsException like:

``` java
{@link docoverride.dns.Examples#example16}
```
