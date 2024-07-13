package com.app.haiercash.plugin;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * *@Author:    Sun
 * *@Date  :    2019/6/14
 * *@FileName: NetResponseVisitor
 * *@Description:
 */
public class NetResponseVisitor extends MethodVisitor {

    private String methodName;

    public NetResponseVisitor(String method, MethodVisitor mv) {
        super(Opcodes.ASM6, mv);
        this.methodName = method;
    }

    @Override
    public void visitCode() {
        super.visitCode();
        mv.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
        mv.visitInsn(Opcodes.DUP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
        mv.visitLdcInsn(methodName + "  ");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitVarInsn(Opcodes.ALOAD, 2);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, HookConfig.INSERT_CLASS, "insertEvent", "(Ljava/lang/String;)V", false);
    }
}
