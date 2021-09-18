package io.reactiverse.es4x.codetrans;

import io.vertx.codetrans.CodeBuilder;
import io.vertx.codetrans.Lang;
import io.vertx.codetrans.Script;

public class EcmaScript implements Lang {
  @Override
  public String id() {
    return "es4x";
  }

  @Override
  public Script loadScript(ClassLoader classLoader, String s, String s1) throws Exception {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getExtension() {
    return "js";
  }

  @Override
  public CodeBuilder codeBuilder() {
    return new EcmaScriptCodeBuilder();
  }
}
