package io.reactiverse.es4x.commands.proxies;

import org.objectweb.asm.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.IRETURN;

public class JsonObjectProxy extends ClassVisitor {

  public JsonObjectProxy() {
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
    newInterfaces[interfaces.length] = "org/graalvm/polyglot/proxy/ProxyObject";
    String newSignature = signature + "Lorg/graalvm/polyglot/proxy/ProxyObject;";
    super.visit(version, access, name, newSignature, superName, newInterfaces);
  }

  @Override
  public void visitEnd() {
    generateGetMember();
    generateGetMemberKeys();
    generateHasMember();
    generatePutMember();
    generateRemoveMember();
    super.visitEnd();
  }

  private void generateGetMember() {
    MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "getMember", "(Ljava/lang/String;)Ljava/lang/Object;", null, null);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 0);
    mv.visitVarInsn(ALOAD, 1);
    mv.visitMethodInsn(INVOKEVIRTUAL, "io/vertx/core/json/JsonObject", "getValue", "(Ljava/lang/String;)Ljava/lang/Object;", false);
    mv.visitInsn(ARETURN);
    mv.visitMaxs(2, 2);
    mv.visitEnd();
  }

  private void generateGetMemberKeys() {
    MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "getMemberKeys", "()Ljava/lang/Object;", null, null);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKEVIRTUAL, "io/vertx/core/json/JsonObject", "fieldNames", "()Ljava/util/Set;", false);
    mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "toArray", "()[Ljava/lang/Object;", true);
    mv.visitInsn(ARETURN);
    mv.visitMaxs(1, 1);
    mv.visitEnd();
  }

  private void generateHasMember() {
    MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "hasMember", "(Ljava/lang/String;)Z", null, null);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 0);
    mv.visitVarInsn(ALOAD, 1);
    mv.visitMethodInsn(INVOKEVIRTUAL, "io/vertx/core/json/JsonObject", "containsKey", "(Ljava/lang/String;)Z", false);
    mv.visitInsn(IRETURN);
    mv.visitMaxs(2, 2);
    mv.visitEnd();
  }

  private void generatePutMember() {
    MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "putMember", "(Ljava/lang/String;Lorg/graalvm/polyglot/Value;)V", null, null);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 0);
    mv.visitVarInsn(ALOAD, 1);
    mv.visitVarInsn(ALOAD, 2);
    mv.visitLdcInsn(Type.getType(Object.class));
    mv.visitMethodInsn(INVOKEVIRTUAL, "org/graalvm/polyglot/Value", "as", "(Ljava/lang/Class;)Ljava/lang/Object;", false);
    mv.visitMethodInsn(INVOKEVIRTUAL, "io/vertx/core/json/JsonObject", "put", "(Ljava/lang/String;Ljava/lang/Object;)Lio/vertx/core/json/JsonObject;", false);
    mv.visitInsn(POP);
    mv.visitInsn(RETURN);
    mv.visitMaxs(3, 3);
    mv.visitEnd();
  }

  private void generateRemoveMember() {
    MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "removeMember", "(Ljava/lang/String;)Z", null, null);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 0);
    mv.visitVarInsn(ALOAD, 1);
    mv.visitMethodInsn(INVOKEVIRTUAL, "io/vertx/core/json/JsonObject", "remove", "(Ljava/lang/String;)Ljava/lang/Object;", false);
    Label falseLabel = new Label();
    mv.visitJumpInsn(IFNULL, falseLabel);
    mv.visitLdcInsn(true);
    mv.visitInsn(IRETURN);
    mv.visitLabel(falseLabel);
    mv.visitLdcInsn(false);
    mv.visitInsn(IRETURN);
    mv.visitMaxs(2, 2);
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
