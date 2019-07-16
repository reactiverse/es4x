package io.reactiverse.es4x.codegen.generator;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;

import java.io.PrintWriter;
import java.util.ArrayList;

public class UtilTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();

  @Rule public final Timeout globalTimeout = new Timeout(10000);

  /* testedClasses: Util */
  // Test written by Diffblue Cover.
  @Test
  public void generateLicenseInputNullOutputNullPointerException() {

    // Arrange
    final PrintWriter writer = null;

    // Act
    thrown.expect(NullPointerException.class);
    Util.generateLicense(writer);

    // Method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.

  @Test
  public void genGenericInput0OutputNotNull() {

    // Arrange
    final ArrayList params = new ArrayList();

    // Act
    final String actual = Util.genGeneric(params);

    // Assert result
    Assert.assertEquals("", actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void genGenericInput1OutputClassCastException() {

    // Arrange
    final ArrayList params = new ArrayList();
    params.add(0);

    // Act
    thrown.expect(ClassCastException.class);
    Util.genGeneric(params);

    // Method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.
  @Test
  public void genGenericInput1OutputNullPointerException() {

    // Arrange
    final ArrayList params = new ArrayList();
    params.add(null);

    // Act
    thrown.expect(NullPointerException.class);
    Util.genGeneric(params);

    // Method is not expected to return due to exception thrown
  }
}
