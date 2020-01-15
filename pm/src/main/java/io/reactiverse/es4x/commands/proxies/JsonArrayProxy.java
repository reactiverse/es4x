package io.reactiverse.es4x.commands.proxies;

import org.objectweb.asm.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.IRETURN;

public class JsonArrayProxy extends ClassVisitor {

  public JsonArrayProxy() {
    super(ASM6, new ClassWriter(COMPUTE_FRAMES) {
      @Override
      protected String getCommonSuperClass(String type1, String type2) {
        // Because we can't load dependent classes, this pleases the frame computation algorithm
        return "java/lang/Object";
      }
    });
  }

  @Override
  public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
    String[] newInterfaces = new String[interfaces.length + 1];
    System.arraycopy(interfaces, 0, newInterfaces, 0, interfaces.length);
    newInterfaces[interfaces.length] = "org/graalvm/polyglot/proxy/ProxyArray";
    String newSignature = signature + "Lorg/graalvm/polyglot/proxy/ProxyArray;";
    super.visit(version, access, name, newSignature, superName, newInterfaces);
  }

  @Override
  public void visitEnd() {
    generateGet();
    generateSet();
    generateRemove();
    generateGetSize();
    super.visitEnd();
  }

  private void generateGet() {
    MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "get", "(J)Ljava/lang/Object;", null, null);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 0);
    mv.visitVarInsn(LLOAD, 1);
    mv.visitMethodInsn(INVOKESTATIC, "io/vertx/core/json/ProxyUtil", "get", "(Lio/vertx/core/json/JsonArray;J)Ljava/lang/Object;", false);
    mv.visitInsn(ARETURN);
    mv.visitMaxs(3, 3);
  }

  private void generateSet() {
    MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "set", "(JLorg/graalvm/polyglot/Value;)V", null, null);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 0);
    mv.visitVarInsn(LLOAD, 1);
    mv.visitVarInsn(ALOAD, 3);
    mv.visitMethodInsn(INVOKESTATIC, "io/vertx/core/json/ProxyUtil", "set", "(Lio/vertx/core/json/JsonArray;JLorg/graalvm/polyglot/Value;)V", false);
    mv.visitInsn(RETURN);
    mv.visitMaxs(4, 4);
  }

  private void generateGetSize() {
    MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "getSize", "()J", null, null);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKESTATIC, "io/vertx/core/json/ProxyUtil", "getSize", "(Lio/vertx/core/json/JsonArray;)J", false);
    mv.visitInsn(LRETURN);
    mv.visitMaxs(2, 1);
  }

  private void generateRemove() {
    MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "remove", "(J)Z", null, null);
    mv.visitCode();
    mv.visitVarInsn(LLOAD, 1);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKESTATIC, "io/vertx/core/json/ProxyUtil", "remove", "(Lio/vertx/core/json/JsonArray;J)Z", false);
    mv.visitInsn(IRETURN);
    mv.visitMaxs(3, 3);
    mv.visitEnd();
  }

  public byte[] rewrite(InputStream source) throws IOException {
    ClassReader classReader = new ClassReader(source);

    classReader.accept(this, 0);

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    out.write(((ClassWriter) cv).toByteArray());
    out.close();

    return out.toByteArray();
  }
}
