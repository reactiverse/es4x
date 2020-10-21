package io.reactiverse.es4x.codegen.generator;

import org.junit.Assert;
import org.junit.Test;

import java.io.PrintWriter;
import java.util.ArrayList;

public class UtilTest {

  /* testedClasses: Util */
  // Test written by Diffblue Cover.
  @Test(timeout = 10_000, expected = NullPointerException.class)
  public void generateLicenseInputNullOutputNullPointerException() {

    // Arrange
    final PrintWriter writer = null;

    // Act
    Util.generateLicense(writer);
    // Method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.

  @Test(timeout = 10_000)
  public void genGenericInput0OutputNotNull() {

    // Arrange
    final ArrayList params = new ArrayList();

    // Act
    final String actual = Util.genGeneric(params);

    // Assert result
    Assert.assertEquals("", actual);
  }

  // Test written by Diffblue Cover.
  @Test(timeout = 10_000, expected = ClassCastException.class)
  public void genGenericInput1OutputClassCastException() {

    // Arrange
    final ArrayList params = new ArrayList();
    params.add(0);

    // Act
    Util.genGeneric(params);
    // Method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.
  @Test(timeout = 10_000, expected = NullPointerException.class)
  public void genGenericInput1OutputNullPointerException() {

    // Arrange
    final ArrayList params = new ArrayList();
    params.add(null);

    // Act
    Util.genGeneric(params);
    // Method is not expected to return due to exception thrown
  }
}
