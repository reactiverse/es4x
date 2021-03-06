package io.reactiverse.es4x;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.junit.Test;

public class FormatterTest {

  private final Logger log = LoggerFactory.getLogger(FormatterTest.class);

  @Test
  public void testColors() {
    log.fatal("Fatal message");
    log.error("Error message");
    log.warn("Warn message");
    log.info("Info message");
    log.debug("Debug message");
    log.trace("Trace message");
  }
}
