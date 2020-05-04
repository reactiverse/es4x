package io.reactiverse.es4x.codegen.generator;

import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static io.reactiverse.es4x.codegen.generator.Util.genType;

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

    writer.printf("  %s: Java.type('%s')", getSimpleName(clazz), clazz.getName());
  }

  public static void generateMJS(PrintWriter writer, String fqcn) {

    final Class<?> clazz;

    try {
      clazz = Class.forName(fqcn);
    } catch (ClassNotFoundException e) {
      System.err.println("Can't process: " + fqcn);
      return;
    }

    writer.printf("export const %s = Java.type('%s');", getSimpleName(clazz), clazz.getName());
  }

  public static void generateDTS(PrintWriter writer, String fqcn) {

    final Class<?> clazz;

    try {
      clazz = Class.forName(fqcn);
    } catch (ClassNotFoundException e) {
      System.err.println("Can't process: " + fqcn);
      return;
    }

    writer.printf("/** Auto-generated from %s %s extends %s\n", Modifier.toString(clazz.getModifiers()), clazz.getName(), clazz.getSuperclass().getName());
    // Get the list of implemented interfaces in the form of Class array using getInterface() method
    Class<?>[] clazzInterfaces = clazz.getInterfaces();
    for (Class<?> c : clazzInterfaces) {
      writer.printf(" * implements %s\n", c.getName());
    }
    if (clazz.isAnnotationPresent(Deprecated.class)) {
      writer.println(" * @deprecated");
    }
    writer.println(" */");
    writer.printf("export %s%s %s {\n", Modifier.isAbstract(clazz.getModifiers()) ? "abstract " : "", clazz.isInterface() ? "interface" : "class", getSimpleName(clazz));
    writer.println();

    // Get the metadata of all the fields of the class

    for (Field field : clazz.getFields()) {
      if (Modifier.isPublic(field.getModifiers())) {
        if (field.isAnnotationPresent(Deprecated.class)) {
          writer.println("  /** @deprecated */");
        }
        writer.printf("  %s%s : %s;\n", Modifier.isStatic(field.getModifiers()) ? "static " : "", field.getName(), genType(field.getType()));
        writer.println();
      }
    }

    // Get all the constructor information in the Constructor array
    for (Constructor<?> constructor : clazz.getConstructors()) {
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
        writer.printf("  constructor(%s);\n", getParamDefinition(constructor.getParameterTypes()));
        writer.println();
      }
    }

    // Get the metadata or information of all the methods of the class using getDeclaredMethods()
    for (Method method : clazz.getMethods()) {
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

        writer.printf("  %s%s(%s) : %s;\n", Modifier.isStatic(method.getModifiers()) ? "static " : "", method.getName(), getParamDefinition(method.getParameterTypes()), genType(method.getReturnType()));
        writer.println();
      }
    }

    writer.println("}");
    writer.println();
  }

  private static CharSequence getParamDefinition(Class<?>[] params) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < params.length; i++) {
      if (i != 0) {
        sb.append(", ");
      }
      Class<?> param = params[i];
      sb.append("arg");
      sb.append(i);
      sb.append(" : ");
      sb.append(genType(param));
    }

    return sb;
  }
}
