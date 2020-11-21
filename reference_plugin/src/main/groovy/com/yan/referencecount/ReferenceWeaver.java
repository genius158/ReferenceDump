package com.yan.referencecount;

import com.quinn.hunter.transform.asm.BaseWeaver;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

public final class ReferenceWeaver extends BaseWeaver {

    private ReferenceExtension referenceExtension;

    public ReferenceWeaver(ReferenceExtension referenceExtension) {
        this.referenceExtension = referenceExtension;
    }

    @Override
    public boolean isWeavableClass(String fullQualifiedClassName) {
        boolean superResult = super.isWeavableClass(fullQualifiedClassName);

        if (referenceExtension != null) {
            if (!referenceExtension.foreList.isEmpty()) {
                return referenceExtension.isInWhitelist(fullQualifiedClassName) && superResult;
            }

            if (referenceExtension.isInBlacklist(fullQualifiedClassName)) return !superResult;
        }
        return superResult;
    }

    @Override
    protected ClassVisitor wrapClassWriter(ClassWriter classWriter) {
        return new ReferenceClassAdapter(classWriter, referenceExtension);
    }
}
