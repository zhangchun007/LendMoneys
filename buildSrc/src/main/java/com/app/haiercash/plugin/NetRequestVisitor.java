package com.app.haiercash.plugin;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * *@Author:    Sun
 * *@Date  :    2019/6/14
 * *@FileName: NetRequestVisitor
 * *@Description:
 */
public class NetRequestVisitor extends MethodVisitor {
    public NetRequestVisitor(MethodVisitor mv) {
        super(Opcodes.ASM6, mv);
    }


    @Override
    public void visitCode() {
        super.visitCode();
        mv.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
        mv.visitInsn(Opcodes.DUP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitLdcInsn("\u53d1\u8d77\u8bf7\u6c42");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, HookConfig.INSERT_CLASS, "insertEvent", "(Ljava/lang/String;)V", false);
    }
}
