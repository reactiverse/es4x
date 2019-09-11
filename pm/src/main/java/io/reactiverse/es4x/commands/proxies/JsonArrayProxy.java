package io.reactiverse.es4x.commands.proxies;

import org.objectweb.asm.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

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
    mv.visitVarInsn(ALOAD, 1);
    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(Ljava/lang/String;)Ljava/lang/Integer;", false);
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
    mv.visitVarInsn(ISTORE, 2);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitVarInsn(ILOAD, 2);
    mv.visitMethodInsn(INVOKEVIRTUAL, "io/vertx/core/json/JsonArray", "getValue", "(I)Ljava/lang/Object;", false);
    mv.visitInsn(ARETURN);
    mv.visitMaxs(2, 3);
    mv.visitEnd();
  }

  private void generateGetMemberKeys() {
    MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "getMemberKeys", "()Ljava/lang/Object;", null, null);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKEVIRTUAL, "io/vertx/core/json/JsonArray", "size", "()I", false);
    mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
    mv.visitVarInsn(ASTORE, 1);
    mv.visitInsn(ICONST_0);
    mv.visitVarInsn(ISTORE, 2);
    Label forLabel = new Label();
    mv.visitLabel(forLabel);
      mv.visitVarInsn(ILOAD, 2);
      mv.visitVarInsn(ALOAD, 1);
      mv.visitInsn(ARRAYLENGTH);
      Label maxLabel = new Label();
      mv.visitJumpInsn(IF_ICMPGE, maxLabel);
      mv.visitVarInsn(ALOAD, 1);
      mv.visitVarInsn(ILOAD, 2);
      mv.visitVarInsn(ILOAD, 2);
      mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "toString", "(I)Ljava/lang/String;", false);
      mv.visitInsn(AASTORE);
      mv.visitIincInsn(2, 1);
      mv.visitJumpInsn(GOTO, forLabel);
    mv.visitLabel(maxLabel);
    mv.visitVarInsn(ALOAD, 1);
    mv.visitInsn(ARETURN);
    mv.visitMaxs(3, 3);
    mv.visitEnd();
  }

  private void generateHasMember() {
    MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "hasMember", "(Ljava/lang/String;)Z", null, null);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 1);
    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(Ljava/lang/String;)Ljava/lang/Integer;", false);
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
    mv.visitVarInsn(ISTORE, 2);
    mv.visitVarInsn(ILOAD, 2);
    Label zeroLabel = new Label();
    mv.visitJumpInsn(IFGE, zeroLabel);
    mv.visitInsn(ICONST_0);
    mv.visitInsn(IRETURN);
    mv.visitLabel(zeroLabel);
    mv.visitVarInsn(ILOAD, 2);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKEVIRTUAL, "io/vertx/core/json/JsonArray", "size", "()I", false);
    Label maxLabel = new Label();
    mv.visitJumpInsn(IF_ICMPGE, maxLabel);
    mv.visitInsn(ICONST_1);
    mv.visitInsn(IRETURN);
    mv.visitLabel(maxLabel);
    mv.visitInsn(ICONST_0);
    mv.visitInsn(IRETURN);
    mv.visitMaxs(2, 3);
    mv.visitEnd();
  }

  private void generatePutMember() {
    MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "putMember", "(Ljava/lang/String;Lorg/graalvm/polyglot/Value;)V", null, null);
    mv.visitCode();
    mv.visitTypeInsn(NEW, "java/lang/UnsupportedOperationException");
    mv.visitInsn(DUP);
    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/UnsupportedOperationException", "<init>", "()V", false);
    mv.visitInsn(ATHROW);
    mv.visitMaxs(2, 3);
    mv.visitEnd();
  }

  private void generateRemoveMember() {
    MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "removeMember", "(Ljava/lang/String;)Z", null, null);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 1);
    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(Ljava/lang/String;)Ljava/lang/Integer;", false);
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
    mv.visitVarInsn(ISTORE, 2);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitVarInsn(ILOAD, 2);
    mv.visitMethodInsn(INVOKEVIRTUAL, "io/vertx/core/json/JsonArray", "remove", "(I)Ljava/lang/Object;", false);
    Label falseLabel = new Label();
    mv.visitJumpInsn(IFNULL, falseLabel);
    mv.visitLdcInsn(true);
    mv.visitInsn(IRETURN);
    mv.visitLabel(falseLabel);
    mv.visitLdcInsn(false);
    mv.visitInsn(IRETURN);
    mv.visitMaxs(2, 3);
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
