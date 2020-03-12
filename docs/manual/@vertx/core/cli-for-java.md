# Typed options and arguments

The described `Option` and `Argument` classes are *untyped*, meaning
that the only get String values. `TypedOption` and `TypedArgument` let
you specify a *type*, so the (String) raw value is converted to the
specified type.

Instead of `Option` and `Argument`, use `TypedOption` and
`TypedArgument` in the `CLI` definition:

``` java
CLI cli = CLI.create("copy")
    .setSummary("A command line interface to copy files.")
    .addOption(new TypedOption<Boolean>()
        .setType(Boolean.class)
        .setLongName("directory")
        .setShortName("R")
        .setDescription("enables directory support")
        .setFlag(true))
    .addArgument(new TypedArgument<File>()
        .setType(File.class)
        .setIndex(0)
        .setDescription("The source")
        .setArgName("source"))
    .addArgument(new TypedArgument<File>()
        .setType(File.class)
        .setIndex(0)
        .setDescription("The destination")
        .setArgName("target"));
```

Then you can retrieve the converted values as follows:

``` java
CommandLine commandLine = cli.parse(userCommandLineArguments);
boolean flag = commandLine.getOptionValue("R");
File source = commandLine.getArgumentValue("source");
File target = commandLine.getArgumentValue("target");
```

The vert.x CLI is able to convert to classes:

  - having a constructor with a single `String` argument, such as `File`
    or `JsonObject`

  - with a static `from` or `fromString` method

  - with a static `valueOf` method, such as primitive types and
    enumeration

In addition, you can implement your own `Converter` and instruct the CLI
to use this converter:

``` java
CLI cli = CLI.create("some-name")
    .addOption(new TypedOption<Person>()
        .setType(Person.class)
        .setConverter(new PersonConverter())
        .setLongName("person"));
```

For booleans, the boolean values are evaluated to `true`: `on`, `yes`,
`1`, `true`.

If one of your option has an `enum` as type, it computes the set of
choices automatically.

# Using annotations

You can also define your CLI using annotations. Definition is done using
annotation on the class and on *setter* methods:

``` java
&#64;Name("some-name")
&#64;Summary("some short summary.")
&#64;Description("some long description")
public class AnnotatedCli {

 private boolean flag;
 private String name;
 private String arg;

&#64;Option(shortName = "f", flag = true)
public void setFlag(boolean flag) {
  this.flag = flag;
}

&#64;Option(longName = "name")
public void setName(String name) {
  this.name = name;
}

&#64;Argument(index = 0)
public void setArg(String arg) {
 this.arg = arg;
}
}
```

Once annotated, you can define the `CLI` and inject the values using:

``` java
CLI cli = CLI.create(AnnotatedCli.class);
CommandLine commandLine = cli.parse(userCommandLineArguments);
AnnotatedCli instance = new AnnotatedCli();
CLIConfigurator.inject(commandLine, instance);
```
