package com.yan.referencecount;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.yan.referencecount.ReferenceExtension.PLUGIN_LIBRARY_REFERENCE;
import static com.yan.referencecount.ReferenceExtension.PLUGIN_LIBRARY_REFERENCE_ASYNCOFFER;
import static com.yan.referencecount.ReferenceExtension.PLUGIN_LIBRARY_REFERENCE_ASYNCOFFER_DES;

public final class ReferenceMethodAdapter extends LocalVariablesSorter implements Opcodes {

    private String classNamePath;
    private String methodDes;
    private String className;
    private String methodName;
    private ReferenceExtension referenceExtension;
    private HashSet<String> newClazzSet = new HashSet<>();

    public ReferenceMethodAdapter(String className, String methodName, int access, String desc,
                                  MethodVisitor mv, ReferenceExtension referenceExtension) {
        super(Opcodes.ASM5, access, desc, mv);
        this.classNamePath = className.replace(".", "/");
        this.className = className;
        this.methodName = methodName;
        this.methodDes = desc;
        this.referenceExtension = referenceExtension;
    }


    @Override
    public void visitTypeInsn(int opcode, String type) {
        if (opcode == NEW) {
            newClazzSet.add(type);
        }
        super.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if (referenceExtension.isConstructor(name) && newClazzSet.contains(owner) && opcode == INVOKESPECIAL) {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            applyAsyncOffer();
            mv.visitTypeInsn(CHECKCAST, owner);
        } else {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }

    private void applyAsyncOffer() {
        ReferenceLog.info(" applyAsyncOffer " + newClazzSet.toString());

        mv.visitLdcInsn(Type.getType("L" + classNamePath + ";"));
        mv.visitLdcInsn(methodName);
        mv.visitLdcInsn(reset(methodDes));
        mv.visitMethodInsn(INVOKESTATIC, PLUGIN_LIBRARY_REFERENCE,
                PLUGIN_LIBRARY_REFERENCE_ASYNCOFFER,
                PLUGIN_LIBRARY_REFERENCE_ASYNCOFFER_DES, false);
    }


    private String reset(String methodDes) {
        if (methodDes == null) return null;
        Pattern pattern = Pattern.compile("L[^;]+;");
        Matcher matcher = pattern.matcher(methodDes);
        while (matcher.find()) {
            String packagePath = matcher.group();
            if (packagePath.contains("/")) {
                methodDes = methodDes.replace(packagePath, "L" +
                        packagePath.substring(packagePath.lastIndexOf("/") + 1));
            }
        }
        return methodDes;
    }

    @Override
    public void visitInsn(int opcode) {
        if (((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW)) {
            if (ReferenceComponentFind.activities.contains(className)
                    || ReferenceComponentFind.providers.contains(className)
                    || ReferenceComponentFind.services.contains(className)
                    || ReferenceComponentFind.broadcasts.contains(className)
            )
                if (referenceExtension.isConstructor(methodName)) {
                    mv.visitVarInsn(ALOAD, 0);
                    applyAsyncOffer();
//                    mv.visitInsn(POP);
                }
        }
        super.visitInsn(opcode);
    }

}
