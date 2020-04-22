package io.reactiverse.es4x.codetrans;

import com.sun.source.tree.LambdaExpressionTree;
import io.vertx.codegen.type.*;
import io.vertx.codetrans.*;
import io.vertx.codetrans.expression.*;
import io.vertx.codetrans.statement.StatementModel;

import java.util.Collections;
import java.util.LinkedHashSet;

import static io.reactiverse.es4x.codegen.generator.Util.getNPMScope;

class EcmaScriptCodeBuilder implements CodeBuilder {

  LinkedHashSet<ClassTypeInfo> modules = new LinkedHashSet<>();

  @Override
  public CodeWriter newWriter() {
    return new EcmaScriptWriter(this);
  }

  @Override
  public String render(RunnableCompilationUnit unit, RenderMode renderMode) {
    CodeWriter writer = newWriter();
    for (ClassTypeInfo module : modules) {
      writer
        .append("import { ")
        .append(module.getSimpleName())
        .append(" } from \"")
        .append(      getNPMScope(module.getModule()));

      switch (module.getKind()) {
        case OTHER:
          if (module.getDataObject() != null) {
            writer.append("/options");
          }
          break;
        case ENUM:
          writer.append("/enums");
          break;
      }

      writer
        .append("\"\n");
    }
    unit.getMain().render(writer);
    return writer.getBuffer().toString();
  }

  @Override
  public ApiTypeModel apiType(ApiTypeInfo type) {
    modules.add(type);
    return CodeBuilder.super.apiType(type);
  }

  @Override
  public ExpressionModel toDataObjectValue(EnumFieldExpressionModel enumField) {
    return new StringLiteralModel(this, enumField.identifier);
  }

  @Override
  public ExpressionModel asyncResultHandler(LambdaExpressionTree.BodyKind bodyKind, ParameterizedTypeInfo resultType, String resultName, CodeModel body, CodeModel succeededBody, CodeModel failedBody) {
    return new LambdaExpressionModel(this, bodyKind, Collections.singletonList(resultType), Collections.singletonList(resultName), body);
  }

  @Override
  public StatementModel variableDecl(VariableScope scope, TypeInfo type, String name, ExpressionModel initializer) {
    return StatementModel.render(renderer -> {
      renderer.append("let ").append(name);
      if (initializer != null) {
        renderer.append(" = ");
        initializer.render(renderer);
      }
    });
  }

  @Override
  public StatementModel enhancedForLoop(String variableName, ExpressionModel expression, StatementModel body) {
    return StatementModel.render((renderer) -> {
      expression.render(renderer);
      renderer.append(".forEach(").append(variableName).append(" => {\n");
      renderer.indent();
      body.render(renderer);
      renderer.unindent();
      renderer.append("})");
    });
  }

  @Override
  public StatementModel forLoop(StatementModel initializer, ExpressionModel condition, ExpressionModel update, StatementModel body) {
    return StatementModel.conditional((renderer) -> {
      renderer.append("for (");
      initializer.render(renderer);
      renderer.append("; ");
      condition.render(renderer);
      renderer.append("; ");
      update.render(renderer);
      renderer.append(") {\n");
      renderer.indent();
      body.render(renderer);
      renderer.unindent();
      renderer.append("}");
    });
  }

  @Override
  public StatementModel sequenceForLoop(String variableName, ExpressionModel fromValue, ExpressionModel toValue, StatementModel body) {
    return StatementModel.conditional((renderer) -> {
      renderer.append("for (let ").append(variableName).append(" = ");
      fromValue.render(renderer);
      renderer.append(";").append(variableName).append(" < ");
      toValue.render(renderer);
      renderer.append(";").append(variableName).append("++) {\n");
      renderer.indent();
      body.render(renderer);
      renderer.unindent();
      renderer.append("}");
    });
  }
}
