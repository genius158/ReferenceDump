package com.yan.referencecount;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

public final class ReferenceClassAdapter extends ClassVisitor {

    private String className;
    private ReferenceExtension referenceExtension;

    ReferenceClassAdapter(final ClassVisitor cv, ReferenceExtension referenceExtension) {
        super(Opcodes.ASM6, cv);
        this.referenceExtension = referenceExtension;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName,
                      String[] interfaces) {
        if (name != null) {
            this.className = name.replace("/", ".");
        }

        super.visit(version, access, name, signature, superName, interfaces);

    }

    @Override
    public MethodVisitor visitMethod(int access, final String name,
                                     final String desc, final String signature, final String[] exceptions) {
        // com.yan.referencecount.dumps.ReferenceMgr.asyncOffer
        // 强制Public
        if (referenceExtension.isLibAsyncOffer(className, name)) {
            access = access & ~ACC_PRIVATE;
            access = access | ACC_PUBLIC;
            return cv.visitMethod(access, name, desc, signature, exceptions);
        }

        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);

        // lib内的方法不需要处理
        if (referenceExtension.isLib(className)) return mv;

        return mv == null ? null
                : new ReferenceMethodAdapter(className, name, access, desc, mv, referenceExtension);
    }
}