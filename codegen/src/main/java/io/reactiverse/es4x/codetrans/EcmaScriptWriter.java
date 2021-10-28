package io.reactiverse.es4x.codetrans;

import com.sun.source.tree.LambdaExpressionTree;
import io.vertx.codegen.type.ApiTypeInfo;
import io.vertx.codegen.type.ClassTypeInfo;
import io.vertx.codegen.type.EnumTypeInfo;
import io.vertx.codegen.type.TypeInfo;
import io.vertx.codetrans.CodeModel;
import io.vertx.codetrans.CodeWriter;
import io.vertx.codetrans.MethodSignature;
import io.vertx.codetrans.TypeArg;
import io.vertx.codetrans.expression.*;
import io.vertx.codetrans.statement.StatementModel;

import javax.lang.model.element.TypeElement;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

class EcmaScriptWriter extends CodeWriter {

  final EcmaScriptCodeBuilder builder;

  EcmaScriptWriter(EcmaScriptCodeBuilder builder) {
    super(builder);
    this.builder = builder;
  }

  private String capitalize(String string) {
    return string.substring(0, 1).toUpperCase() + string.substring(1);
  }

  @Override
  public void renderBinary(BinaryExpressionModel expression) {
    ExpressionModel left = expression.getLeft();
    ExpressionModel right = expression.getRight();
    String op = expression.getOp();
    switch (op) {
      case "==":
        op = "===";
        if (right instanceof NullLiteralModel) {
          ExpressionModel tmp = right;
          right = left;
          left = tmp;
        }
        if (left instanceof NullLiteralModel) {
          // Todo find a way to suppress these when not needed i.e (( xxx ))
          append("(");
          right.render(this);
          append(" === null || ");
          right.render(this);
          append(" === undefined)");
          return;
        }
        break;
      case "!=":
        if (right instanceof NullLiteralModel) {
          ExpressionModel tmp = right;
          right = left;
          left = tmp;
        }
        if (left instanceof NullLiteralModel) {
          // Todo find a way to suppress these when not needed i.e (( xxx ))
          append("(");
          right.render(this);
          append(" !== null && ");
          right.render(this);
          append(" !== undefined)");
          return;
        }
        op = "!==";
        break;
    }
    super.renderBinary(new BinaryExpressionModel(builder, expression.getLeft(), op, expression.getRight()));
  }

  @Override
  public void renderStatement(StatementModel statement) {
    statement.render(this);
    // In javascript, conditional structure should not have an ending ;. This generates an empty instruction.
    if (statement instanceof StatementModel.Expression) {
      append(";");
    }
    append("\n");
  }

  @Override
  public void renderTryCatch(StatementModel tryBlock, StatementModel catchBlock) {
    append("try {\n");
    indent();
    tryBlock.render(this);
    unindent();
    append("} catch(err) {\n");
    indent();
    catchBlock.render(this);
    unindent();
    append("}\n");
  }

  @Override
  public void renderThis() {
    append("this");
  }

  @Override
  public void renderNewMap() {
    append("{}");
  }

  @Override
  public void renderNewList() {
    append("[]");
  }

  public void renderDataObject(DataObjectLiteralModel model) {
    append("new ").append(model.getType().getSimpleName()).append("()");
    boolean dataModelHasMembers = model.getMembers().iterator().hasNext();
    if (dataModelHasMembers) {
      append("\n");
      indent();
    }
    final AtomicBoolean needsNL = new AtomicBoolean();
    model.getMembers().forEach(member -> {
      if (needsNL.get()) {
        append("\n");
      }
      append(".set").append(capitalize(member.getName())).append("(");
      if (member instanceof Member.Single) {
        ((Member.Single) member).getValue().render(this);
      } else if (member instanceof Member.Sequence) {
        append("[");
        IntStream.range(0, ((Member.Sequence) member).getValues().size()).forEach(i -> {
          if (i > 0)
            append(", ");
          ((Member.Sequence) member).getValues().get(i).render(this);
        });
        append("]");
      } else if (member instanceof Member.Entries) {
        append("todo-renderDataObject-entries");
      }
      append(")");
      needsNL.set(true);
    });
    if (dataModelHasMembers) {
      unindent();
    }
  }

  @Override
  public void renderDataObjectToJson(IdentifierModel model) {
    model.render(this);
  }

  @Override
  public void renderToDataObject(JsonObjectModel model, ClassTypeInfo type) {
    model.render(this);
  }

  public void renderJsonObject(JsonObjectLiteralModel jsonObject) {
    renderJsonObject(jsonObject.getMembers());
  }

  public void renderJsonArray(JsonArrayLiteralModel jsonArray) {
    renderJsonArray(jsonArray.getValues());
  }

  private void renderJsonObject(Iterable<Member> members) {
    append("{\n");
    indent();
    for (Iterator<Member> iterator = members.iterator(); iterator.hasNext(); ) {
      Member member = iterator.next();
      String name = member.getName();
      append("\"");
      renderChars(name);
      append("\" : ");
      if (member instanceof Member.Single) {
        ((Member.Single) member).getValue().render(this);
      } else if (member instanceof Member.Sequence) {
        renderJsonArray(((Member.Sequence) member).getValues());
      } else if (member instanceof Member.Entries) {
        renderJsonObject(((Member.Entries) member).entries());
      }
      if (iterator.hasNext()) {
        append(',');
      }
      append('\n');
    }
    unindent().append("}");
  }

  private void renderJsonArray(List<ExpressionModel> values) {
    append("[\n").indent();
    for (int i = 0; i < values.size(); i++) {
      values.get(i).render(this);
      if (i < values.size() - 1) {
        append(',');
      }
      append('\n');
    }
    unindent().append(']');
  }

  @Override
  public void renderJsonObjectAssign(ExpressionModel expression, String name, ExpressionModel value) {
    expression.render(this);
    append('.');
    append(name);
    append(" = ");
    value.render(this);
  }

  @Override
  public void renderJsonArrayAdd(ExpressionModel expression, ExpressionModel value) {
    expression.render(this);
    append(".push(");
    value.render(this);
    append(")");
  }

  @Override
  public void renderDataObjectAssign(ExpressionModel expression, String name, ExpressionModel value) {
    renderJsonObjectAssign(expression, name, value);
  }

  @Override
  public void renderJsonObjectMemberSelect(ExpressionModel expression, Class<?> type, String name) {
    expression.render(this);
    append('.');
    append(name);
  }

  @Override
  public void renderJsonObjectToString(ExpressionModel expression) {
    append("JSON.stringify(");
    expression.render(this);
    append(")");
  }

  @Override
  public void renderJsonArrayToString(ExpressionModel expression) {
    append("JSON.stringify(");
    expression.render(this);
    append(")");
  }

  @Override
  public void renderDataObjectMemberSelect(ExpressionModel expression, String name) {
    renderJsonObjectMemberSelect(expression, Object.class, name);
  }

  @Override
  public void renderJsonObjectSize(ExpressionModel expression) {
    append("Object.keys(");
    expression.render(this);
    append(").length");
  }

  @Override
  public void renderJsonArraySize(ExpressionModel expression) {
    expression.render(this);
    append(".length");
  }

  @Override
  public void renderLambda(LambdaExpressionTree.BodyKind bodyKind, List<TypeInfo> parameterTypes, List<String> parameterNames, CodeModel body) {
    append("(");
    for (int i = 0; i < parameterNames.size(); i++) {
      if (i > 0) {
        append(", ");
      }
      append(parameterNames.get(i));
    }
    append(") => {\n");
    indent();
    body.render(this);
    if (bodyKind == LambdaExpressionTree.BodyKind.EXPRESSION) {
      append(";\n");
    }
    unindent();
    append("}");
  }

  @Override
  public void renderEnumConstant(EnumTypeInfo type, String constant) {
    append(type.getSimpleName()).append('.').append(constant);
  }

  @Override
  public void renderThrow(String throwableType, ExpressionModel reason) {
    if (reason == null) {
      append("throw ").append("\"an error occurred\"");
    } else {
      append("throw ");
      reason.render(this);
    }
  }

  @Override
  public void renderSystemOutPrintln(ExpressionModel expression) {
    append("console.log(");
    expression.render(this);
    append(")");
  }

  @Override
  public void renderSystemErrPrintln(ExpressionModel expression) {
    append("console.error(");
    expression.render(this);
    append(")");
  }

  @Override
  public void renderListAdd(ExpressionModel list, ExpressionModel value) {
    list.render(this);
    append(".push(");
    value.render(this);
    append(")");
  }

  @Override
  public void renderListSize(ExpressionModel list) {
    list.render(this);
    append(".length");
  }

  @Override
  public void renderListGet(ExpressionModel list, ExpressionModel index) {
    list.render(this);
    append("[");
    index.render(this);
    append("]");
  }

  @Override
  public void renderListLiteral(List<ExpressionModel> arguments) {
    append("[");
    for (Iterator<ExpressionModel> it = arguments.iterator(); it.hasNext(); ) {
      it.next().render(this);
      if (it.hasNext()) {
        append(", ");
      }
    }
    append("]");
  }

  @Override
  public void renderNewArray(String s, List<ExpressionModel> list) {
    renderListLiteral(list);
  }

  @Override
  public void renderMapGet(ExpressionModel map, ExpressionModel key) {
    map.render(this);
    append('[');
    key.render(this);
    append(']');
  }

  @Override
  public void renderMapPut(ExpressionModel map, ExpressionModel key, ExpressionModel value) {
    map.render(this);
    append('[');
    key.render(this);
    append("] = ");
    value.render(this);
  }

  @Override
  public void renderMapForEach(ExpressionModel map, String keyName, TypeInfo keyType, String valueName, TypeInfo valueType, LambdaExpressionTree.BodyKind bodyKind, CodeModel block) {
    map.render(this);
    append(".forEach(");
    renderLambda(bodyKind, Arrays.asList(valueType, keyType), Arrays.asList(valueName, keyName), block);
    append(")");
  }

  @Override
  public void renderMethodReference(ExpressionModel expression, MethodSignature signature) {
    if (!(expression instanceof ThisModel)) {
      expression.render(this);
      append('.');
    }
    append(signature.getName());
  }

  @Override
  public void renderApiType(ApiTypeInfo apiType) {
    append(apiType.getSimpleName());
  }

  @Override
  public void renderJavaType(ClassTypeInfo javaType) {
    append("Java.type(\"").append(javaType.getName()).append("\")");
  }

  @Override
  public void renderAsyncResultSucceeded(TypeInfo resultType, String name) {
    append(name).append(".succeeded()");
  }

  @Override
  public void renderAsyncResultFailed(TypeInfo resultType, String name) {
    append(name).append(".failed()");
  }

  @Override
  public void renderAsyncResultCause(TypeInfo resultType, String name) {
    append(name).append(".cause()");
  }

  @Override
  public void renderAsyncResultValue(TypeInfo resultType, String name) {
    append(name).append(".result()");
  }

  @Override
  public void renderMethodInvocation(ExpressionModel expression, TypeInfo receiverType, MethodSignature method, TypeInfo returnType, List<TypeArg> typeArguments, List<ExpressionModel> argumentModels, List<TypeInfo> argumentTypes) {
    List<TypeInfo> parameterTypes = method.getParameterTypes();
    for (int i = 0; i < parameterTypes.size(); i++) {
      TypeInfo parameterType = parameterTypes.get(i);
      TypeInfo argumentType = argumentTypes.get(i);
      if (io.vertx.codetrans.Helper.isHandler(parameterType) && io.vertx.codetrans.Helper.isInstanceOfHandler(argumentType)) {
        ExpressionModel expressionModel = argumentModels.get(i);
        argumentModels.set(i, builder.render(expressionModel::render));
      }
    }

    //
    if (!(expression instanceof ThisModel)) {
      expression.render(this);
      append('.');
    }
    append(method.getName());
    append('(');
    for (int i = 0; i < argumentModels.size(); i++) {
      if (i > 0) {
        append(", ");
      }
      argumentModels.get(i).render(this);
    }
    append(')');
  }

  @Override
  public void renderNew(ExpressionModel expression, TypeInfo type, List<ExpressionModel> argumentModels) {
    append("new (");
    expression.render(this);
    append(")");
    append('(');
    for (int i = 0; i < argumentModels.size(); i++) {
      if (i > 0) {
        append(", ");
      }
      argumentModels.get(i).render(this);
    }
    append(')');
  }

  @Override
  public void renderInstanceOf(ExpressionModel expression, TypeElement type) {
    expression.render(this);
    append(" instanceof ");
    append(type.getSimpleName());
  }
}
