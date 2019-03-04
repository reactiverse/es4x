package io.reactiverse.es4x.commands;

import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class VersionsCommandTest {

  private final VersionsCommand command = new VersionsCommand();

  @Test
  public void shouldPrintTheVersions() {
    command.run();
  }
}
