package io.reactiverse.es4x.codegen.generator;

import io.vertx.core.json.JsonObject;

import java.io.PrintWriter;
import java.lang.reflect.*;
import java.util.Arrays;

import static io.reactiverse.es4x.codegen.generator.Util.*;

public class JVMClass {

  private static String getSimpleName(Class<?> clazz) {
    String name = clazz.getName();
    int idx = name.lastIndexOf('.');
    return name.substring(idx + 1);
  }

  public static void generateJS(PrintWriter writer, String fqcn) {

    final Class<?> clazz;

    try {
      clazz = Class.forName(fqcn);
    } catch (ClassNotFoundException e) {
      System.err.println("Can't process: " + fqcn);
      return;
    }

    writer.printf("  %s: Java.type('%s'),\n", getSimpleName(clazz), clazz.getName());
  }

  public static void generateMJS(PrintWriter writer, String fqcn) {

    final Class<?> clazz;

    try {
      clazz = Class.forName(fqcn);
    } catch (ClassNotFoundException e) {
      System.err.println("Can't process: " + fqcn);
      return;
    }

    writer.printf("export const %s = Java.type('%s');\n", getSimpleName(clazz), clazz.getName());
  }

  public static void generateDTS(PrintWriter writer, String fqcn) {

    final Class<?> clazz;

    try {
      clazz = Class.forName(fqcn);
    } catch (ClassNotFoundException e) {
      System.err.println("Can't process: " + fqcn);
      return;
    }

    JsonObject includes = getIncludes(getSimpleName(clazz));

    if (includes.containsKey("import<d.ts>")) {
      writer.printf("%s\n", includes.getString("import<d.ts>"));
    }

    boolean isInterface = clazz.isInterface();
    boolean isAbstract = !clazz.isInterface() && Modifier.isAbstract(clazz.getModifiers());

    // verify if we need to "change" from interface to abstract class, this is needed when "static" methods exist
    // on interfaces
    if (isInterface) {
      // Get the metadata or information of all the methods of the class using getDeclaredMethods()
      for (Method method : clazz.getMethods()) {
        if (isExcluded(getSimpleName(clazz), method.getName(), Arrays.asList(method.getParameterTypes()))) {
          continue;
        }
        if (Modifier.isPublic(method.getModifiers()) && Modifier.isStatic(method.getModifiers())) {
          isInterface = false;
          isAbstract = true;
          break;
        }
      }
    }

    writer.printf("/** Auto-generated from %s %s %s %s\n", Modifier.toString(clazz.getModifiers()), clazz.getName(), clazz.isInterface() ? "" : "extends", clazz.getSuperclass() != null ? clazz.getSuperclass().getName() : "");
    // Get the list of implemented interfaces in the form of Class array using getInterface() method
    Class<?>[] clazzInterfaces = clazz.getInterfaces();
    for (Class<?> c : clazzInterfaces) {
      writer.printf(" * implements %s\n", c.getName());
    }
    if (clazz.isAnnotationPresent(Deprecated.class)) {
      writer.println(" * @deprecated");
    }
    writer.println(" */");
    writer.printf("export %s%s %s {\n", isAbstract ? "abstract " : "", isInterface ? "interface" : "class", getSimpleName(clazz));
    writer.println();

    // Get the metadata of all the fields of the class

    for (Field field : clazz.getFields()) {
      if (isExcluded(getSimpleName(clazz), field.getName(), null)) {
        continue;
      }
      if (Modifier.isPublic(field.getModifiers())) {
        if (field.isAnnotationPresent(Deprecated.class)) {
          writer.println("  /** @deprecated */");
        }
        writer.printf("  %s%s : %s;\n", Modifier.isStatic(field.getModifiers()) ? "static " : "", field.getName(), genType(field.getGenericType().getTypeName()));
        writer.println();
      }
    }

    // Get all the constructor information in the Constructor array
    for (Constructor<?> constructor : clazz.getConstructors()) {
      if (isExcluded(getSimpleName(clazz), "<ctor>", Arrays.asList(constructor.getParameterTypes()))) {
        continue;
      }
      if (Modifier.isPublic(constructor.getModifiers())) {
        writer.printf("  /** Auto-generated from %s#%s\n", clazz.getName(), constructor.getName());
        // Get and print exception thrown by the method
        for (Class<?> exception : constructor.getExceptionTypes()) {
          writer.printf("   * @throws %s\n", exception.getName());
        }
        if (constructor.isAnnotationPresent(Deprecated.class)) {
          writer.println("   * @deprecated");
        }
        writer.println("   */");
        // Print all name of each constructor
        writer.printf("  constructor(%s);\n", getParamDefinition(constructor.getGenericParameterTypes()));
        writer.println();
      }
    }

    // Get the metadata or information of all the methods of the class using getDeclaredMethods()
    for (Method method : clazz.getMethods()) {
      if (isExcluded(getSimpleName(clazz), method.getName(), Arrays.asList(method.getParameterTypes()))) {
        continue;
      }
      if (Modifier.isPublic(method.getModifiers())) {

        writer.printf("  /** Auto-generated from %s#%s\n", clazz.getName(), method.getName());
        // Get and print exception thrown by the method
        for (Class<?> exception : method.getExceptionTypes()) {
          writer.printf("   * @throws %s\n", exception.getName());
        }
        if (method.isAnnotationPresent(Deprecated.class)) {
          writer.println("   * @deprecated");
        }
        writer.println("   */");

        writer.printf("  %s%s(%s) : %s;\n", Modifier.isStatic(method.getModifiers()) ? "static " : "", method.getName(), getParamDefinition(method.getParameterTypes()), genType(method.getGenericReturnType().getTypeName()));
        writer.println();
      }
    }

    writer.println("}");
    writer.println();
  }

  private static CharSequence getParamDefinition(Type[] params) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < params.length; i++) {
      if (i != 0) {
        sb.append(", ");
      }
      Type param = params[i];
      sb.append("arg");
      sb.append(i);
      sb.append(": ");
      sb.append(genType(param.getTypeName()));
    }

    return sb;
  }
}
