/*
 * Copyright 2019 Red Hat, Inc.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */
package io.reactiverse.es4x.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.Opcodes.*;

public class FutureBase extends ClassVisitor {

  public FutureBase() {
    super(ASM7, new ClassWriter(COMPUTE_FRAMES) {
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
    newInterfaces[interfaces.length] = "io/reactiverse/es4x/Thenable";
    String newSignature = signature + "Lio/reactiverse/es4x/Thenable;";
    super.visit(version, access, name, newSignature, superName, newInterfaces);
  }

  @Override
  public void visitEnd() {
    generateThen();
    super.visitEnd();
  }

  private void generateThen() {
    MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "then", "(Lorg/graalvm/polyglot/Value;Lorg/graalvm/polyglot/Value;)V", null, null);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 0);
    mv.visitVarInsn(ALOAD, 1);
    mv.visitVarInsn(ALOAD, 2);
    mv.visitMethodInsn(INVOKESTATIC, "io/reactiverse/es4x/impl/ProxyUtil", "then", "(Lio/vertx/core/Future;Lorg/graalvm/polyglot/Value;Lorg/graalvm/polyglot/Value;)V", false);
    mv.visitInsn(RETURN);
    mv.visitMaxs(3, 3);
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
