package io.reactiverse.es4x.docgen.generator;

import io.reactiverse.es4x.codetrans.EcmaScript;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.codegen.type.*;
import io.vertx.codetrans.CodeTranslator;
import io.vertx.docgen.Coordinate;
import io.vertx.docgen.DocGenerator;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static io.reactiverse.es4x.codegen.generator.Util.getNPMScope;

public class ES4XDocGenerator implements DocGenerator {

  private CodeTranslator translator;
  private Elements elementUtils;
  private Types typeUtils;

  @Override
  public void init(ProcessingEnvironment processingEnv) {
    translator = new CodeTranslator(processingEnv);
    elementUtils = processingEnv.getElementUtils();
    typeUtils = processingEnv.getTypeUtils();
  }

  @Override
  public String getName() {
    return "js";
  }

  @Override
  public String renderSource(ExecutableElement elt, String source) {
    try {
      return translator.translate(elt, new EcmaScript());
    } catch (Exception e) {
      System.out.println("Cannot generate " + elt.getEnclosingElement().getSimpleName() + "#" + elt.getSimpleName() + " : " + e.getMessage());
      return "Code not translatable";
    }
  }

  @Override
  public String resolveTypeLink(TypeElement elt, Coordinate coordinate) {
    try {
      TypeMirrorFactory factory = new TypeMirrorFactory(elementUtils, typeUtils);
      TypeInfo type = factory.create(elt.asType());

      String baselink = getNPMScope(type.getRaw().getModule());
      String kind;

      switch (type.getKind()) {
        case ENUM:
          kind = "enums";
          break;
        case HANDLER:
        case ASYNC_RESULT:
          kind = "interfaces";
          break;
        case API:
          boolean concrete = elt.getAnnotation(VertxGen.class) == null || elt.getAnnotation(VertxGen.class).concrete();
          if (concrete) {
            kind = "classes";
          } else {
            kind = "interfaces";
          }
          break;
        case OTHER:
          if (type.getDataObject() != null) {
            kind = "classes";
            break;
          } else {
            return null;
          }
        default:
          System.err.println("Could not resolve doc link for kind " + type.getKind());
          return null;
      }

      return "/es4x/" + baselink + "/" + kind + "/" + elt.getSimpleName().toString().toLowerCase() + ".html";
    } catch (Exception e) {
      System.out.println("Could not resolve doc link for type " + elt.getQualifiedName() + ": " + e.getMessage());
      return null;
    }
  }

  @Override
  public String resolveMethodLink(ExecutableElement elt, Coordinate coordinate) {
    TypeElement typeElt = (TypeElement) elt.getEnclosingElement();
    String link =  resolveTypeLink(typeElt, coordinate);
    if (link != null) {
      link += '#' + elt.getSimpleName().toString().toLowerCase();
    }
    return link;
  }

  @Override
  public String resolveLabel(Element elt, String defaultLabel) {
    return defaultLabel;
  }

  @Override
  public String resolveConstructorLink(ExecutableElement elt, Coordinate coordinate) {
    TypeElement typeElt = (TypeElement) elt.getEnclosingElement();
    String link =  resolveTypeLink(typeElt, coordinate);
    if (link != null) {
      link += '#' + elt.getSimpleName().toString().toLowerCase();
    }
    return link;
  }

  @Override
  public String resolveFieldLink(VariableElement elt, Coordinate coordinate) {
    TypeElement typeElt = (TypeElement) elt.getEnclosingElement();
    String link =  resolveTypeLink(typeElt, coordinate);
    if (link != null) {
      link += '#' + elt.getSimpleName().toString().toLowerCase();
    }
    return link;
  }
}
