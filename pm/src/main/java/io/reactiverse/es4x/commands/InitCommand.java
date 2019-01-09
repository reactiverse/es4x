package io.reactiverse.es4x.commands;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.vertx.core.cli.CLIException;
import io.vertx.core.cli.annotations.Name;
import io.vertx.core.cli.annotations.Summary;
import io.vertx.core.spi.launcher.DefaultCommand;

import java.io.*;
import java.util.*;

@Name("init")
@Summary("Initializes the 'package.json' to work with ES4X.")
public class InitCommand extends DefaultCommand {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  static {
    MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    MAPPER.configure(SerializationFeature.INDENT_OUTPUT, true);
  }

  @Override
  public void run() throws CLIException {
    try {

      final File file = new File("package.json");

      if (!file.exists()) {
        throw new IllegalStateException("'package.json' doesn't exists!");
      }

      Map npm = MAPPER.readValue(file, Map.class);
      Map scripts = (Map) npm.get("scripts");

      if (scripts == null) {
        scripts = new LinkedHashMap();
        npm.put("scripts", scripts);
      }

      String main = (String) npm.get("main");
      if (main == null) {
        main = "index.js";
      }
      String test = main;
      if (test.endsWith(".js")) {
        test = test.substring(0, test.length() - 3) + ".test.js";
      }

      scripts.put("postinstall", "es4x-pm install");
      scripts.put("start", "es4x run js:" + main);
      scripts.put("test", "es4x test js:" + test);

      MAPPER.writeValue(file, npm);

    } catch (IOException e) {
      throw new CLIException(e.getMessage(), e);
    }
  }
}
