Vert.x Core provides an API for parsing command line arguments passed to
programs. It’s also able to print help messages detailing the options
available for a command line tool. Even if such features are far from
the Vert.x core topics, this API is used in the `Launcher` class that
you can use in *fat-jar* and in the `vertx` command line tools. In
addition, it’s polyglot (can be used from any supported language) and is
used in Vert.x Shell.

Vert.x CLI provides a model to describe your command line interface, but
also a parser. This parser supports different types of syntax:

  - POSIX like options (ie. `tar -zxvf foo.tar.gz`)

  - GNU like long options (ie. `du --human-readable --max-depth=1`)

  - Java like properties (ie. `java -Djava.awt.headless=true
    -Djava.net.useSystemProxies=true Foo`)

  - Short options with value attached (ie. `gcc -O2 foo.c`)

  - Long options with single hyphen (ie. `ant -projecthelp`)

Using the CLI api is a 3-steps process:

1.  The definition of the command line interface

2.  The parsing of the user command line

3.  The query / interrogation

# Definition Stage

Each command line interface must define the set of options and arguments
that will be used. It also requires a name. The CLI API uses the
`Option` and `Argument` classes to describe options and arguments:

``` js
import { CLI } from "@vertx/core"
let cli = CLI.create("copy").setSummary("A command line interface to copy files.").addOption(new Option()
  .setLongName("directory")
  .setShortName("R")
  .setDescription("enables directory support")
  .setFlag(true)).addArgument(new Argument()
  .setIndex(0)
  .setDescription("The source")
  .setArgName("source")).addArgument(new Argument()
  .setIndex(1)
  .setDescription("The destination")
  .setArgName("target"));
```

As you can see, you can create a new `CLI` using `CLI.create`. The
passed string is the name of the CLI. Once created you can set the
summary and description. The summary is intended to be short (one line),
while the description can contain more details. Each option and argument
are also added on the `CLI` object using the `addArgument` and
`addOption` methods.

## Options

An `Option` is a command line parameter identified by a *key* present in
the user command line. Options must have at least a long name or a short
name. Long name are generally used using a `--` prefix, while short
names are used with a single `-`. Options can get a description
displayed in the usage (see below). Options can receive 0, 1 or several
values. An option receiving 0 values is a `flag`, and must be declared
using `setFlag`. By default, options receive a single value, however,
you can configure the option to receive several values using
`setMultiValued`:

``` js
import { CLI } from "@vertx/core"
let cli = CLI.create("some-name").setSummary("A command line interface illustrating the options valuation.").addOption(new Option()
  .setLongName("flag")
  .setShortName("f")
  .setFlag(true)
  .setDescription("a flag")).addOption(new Option()
  .setLongName("single")
  .setShortName("s")
  .setDescription("a single-valued option")).addOption(new Option()
  .setLongName("multiple")
  .setShortName("m")
  .setMultiValued(true)
  .setDescription("a multi-valued option"));
```

Options can be marked as mandatory. A mandatory option not set in the
user command line throws an exception during the parsing:

``` js
import { CLI } from "@vertx/core"
let cli = CLI.create("some-name").addOption(new Option()
  .setLongName("mandatory")
  .setRequired(true)
  .setDescription("a mandatory option"));
```

Non-mandatory options can have a *default value*. This value would be
used if the user does not set the option in the command line:

``` js
import { CLI } from "@vertx/core"
let cli = CLI.create("some-name").addOption(new Option()
  .setLongName("optional")
  .setDefaultValue("hello")
  .setDescription("an optional option with a default value"));
```

An option can be *hidden* using the `setHidden` method. Hidden option
are not listed in the usage, but can still be used in the user command
line (for power-users).

If the option value is contrained to a fixed set, you can set the
different acceptable choices:

``` js
import { CLI } from "@vertx/core"
let cli = CLI.create("some-name").addOption(new Option()
  .setLongName("color")
  .setDefaultValue("green")
  .setChoices(["blue", "red", "green"])
  .setDescription("a color"));
```

Options can also be instantiated from their JSON form.

## Arguments

Unlike options, arguments do not have a *key* and are identified by
their *index*. For example, in `java com.acme.Foo`, `com.acme.Foo` is an
argument.

Arguments do not have a name, there are identified using a 0-based
index. The first parameter has the index `0`:

``` js
import { CLI } from "@vertx/core"
let cli = CLI.create("some-name").addArgument(new Argument()
  .setIndex(0)
  .setDescription("the first argument")
  .setArgName("arg1")).addArgument(new Argument()
  .setIndex(1)
  .setDescription("the second argument")
  .setArgName("arg2"));
```

If you don’t set the argument indexes, it computes it automatically by
using the declaration order.

``` js
import { CLI } from "@vertx/core"
let cli = CLI.create("some-name").addArgument(new Argument()
  .setDescription("the first argument")
  .setArgName("arg1")).addArgument(new Argument()
  .setDescription("the second argument")
  .setArgName("arg2"));
```

The `argName` is optional and used in the usage message.

As options, `Argument` can:

  - be hidden using `setHidden`

  - be mandatory using `setRequired`

  - have a default value using `setDefaultValue`

  - receive several values using `setMultiValued` - only the last
    argument can be multi-valued.

Arguments can also be instantiated from their JSON form.

## Usage generation

Once your `CLI` instance is configured, you can generate the *usage*
message:

``` js
import { CLI } from "@vertx/core"
let cli = CLI.create("copy").setSummary("A command line interface to copy files.").addOption(new Option()
  .setLongName("directory")
  .setShortName("R")
  .setDescription("enables directory support")
  .setFlag(true)).addArgument(new Argument()
  .setIndex(0)
  .setDescription("The source")
  .setArgName("source")).addArgument(new Argument()
  .setIndex(0)
  .setDescription("The destination")
  .setArgName("target"));

let builder = new (Java.type("java.lang.StringBuilder"))();
cli.usage(builder);
```

It generates an usage message like this one:

    Usage: copy [-R] source target
    
    A command line interface to copy files.
    
     -R,--directory   enables directory support

If you need to tune the usage message, check the `UsageMessageFormatter`
class.

# Parsing Stage

Once your `CLI` instance is configured, you can parse the user command
line to evaluate each option and argument:

``` js
let commandLine = cli.parse(userCommandLineArguments);
```

The `parse` method returns a `CommandLine` object containing the values.
By default, it validates the user command line and checks that each
mandatory options and arguments have been set as well as the number of
values received by each option. You can disable the validation by
passing `false` as second parameter of `parse`. This is useful if you
want to check an argument or option is present even if the parsed
command line is invalid.

You can check whether or not the `CommandLine` is valid using `isValid`.

# Query / Interrogation Stage

Once parsed, you can retrieve the values of the options and arguments
from the `CommandLine` object returned by the `parse` method:

``` js
let commandLine = cli.parse(userCommandLineArguments);
let opt = commandLine.getOptionValue("my-option");
let flag = commandLine.isFlagEnabled("my-flag");
let arg0 = commandLine.getArgumentValue(0);
```

One of your option can have been marked as "help". If a user command
line enabled a "help" option, the validation won’t failed, but give you
the opportunity to check if the user asks for help:

``` js
import { CLI } from "@vertx/core"
let cli = CLI.create("test").addOption(new Option()
  .setLongName("help")
  .setShortName("h")
  .setFlag(true)
  .setHelp(true)).addOption(new Option()
  .setLongName("mandatory")
  .setRequired(true));

let line = cli.parse(Java.type("java.util.Collections").singletonList("-h"));

// The parsing does not fail and let you do:
if (!line.isValid() && line.isAskingForHelp()) {
  let builder = new (Java.type("java.lang.StringBuilder"))();
  cli.usage(builder);
  stream.print(builder.toString());
}
```
