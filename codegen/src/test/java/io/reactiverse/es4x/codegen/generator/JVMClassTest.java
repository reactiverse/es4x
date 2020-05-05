package io.reactiverse.es4x.codegen.generator;

import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

public class JVMClassTest {

  @Test
  public void test() {
    StringWriter sw = new StringWriter();
    JVMClass.generateDTS(new PrintWriter(sw), "java.lang.String");
    System.out.println(sw.toString());
  }

  @Test
  public void testGenericConvert() {
    assertEquals("{ [key: string]: /* graphql.schema.TypeResolver */ any }", Util.genType("java.util.Map<java.lang.String, graphql.schema.TypeResolver>"));
    assertEquals("string[]", Util.genType("java.util.List<java.lang.String>"));
    assertEquals("{ [key: string]: { [key: string]: /* graphql.schema.DataFetcher */ any } }", Util.genType("java.util.Map<java.lang.String, java.util.Map<java.lang.String, graphql.schema.DataFetcher>>"));
  }
}
