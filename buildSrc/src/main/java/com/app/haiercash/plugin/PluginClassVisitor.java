package com.app.haiercash.plugin;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.List;

/**
 * *@Author: Sun
 * *@Date :    2019/6/12
 * *@FileName: PluginClassVisitor
 * *@Description:
 */
public class PluginClassVisitor extends ClassVisitor implements Opcodes {

    private String mClassName;
    private List<String> method;

    public PluginClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM6, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.mClassName = PluginUtils.addSuffix(name);
        this.method = HookConfig.HOOK_CLASS.get(mClassName);
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (method != null && method.contains(name)) {
            if (HookConfig.HOOK_ACTIVITY_LIFECYCLE.equals(this.mClassName)
                    || HookConfig.HOOK_FRAGMENT_LIFECYCLE.equals(this.mClassName)) {
                //activity or fragment
                return new LifecycleVisitor(name, mv);
            } else if (HookConfig.HOOK_NET_REQUEST.equals(mClassName)) {
                //net request
                return new NetRequestVisitor(mv);
            } else if (HookConfig.HOOK_NET_RESPONSE.equals(mClassName)) {
                //net response
                return new NetResponseVisitor(name, mv);
            }
        }
        return mv;
    }


}
