package com.yan.referencecount;

import com.android.build.api.transform.QualifiedContent;
import com.quinn.hunter.rctransform.asm.BaseWeaver;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

public final class ReferenceWeaver extends BaseWeaver {

    private ReferenceExtension referenceExtension;

    public ReferenceWeaver(ReferenceExtension referenceExtension) {
        this.referenceExtension = referenceExtension;
    }

    @Override
    public boolean isWeavableClass(QualifiedContent input, String fullQualifiedClassName) {
        boolean superResult = super.isWeavableClass(input, fullQualifiedClassName);
        if (!superResult) return false;

        if (referenceExtension != null) {
            if (referenceExtension.isJvmHelper(fullQualifiedClassName)) {
                return false;
            }
            if (referenceExtension.isObjectCalculator(fullQualifiedClassName)) {
                return false;
            }

            if (!referenceExtension.foreList.isEmpty()) {
                return referenceExtension.isInWhitelist(fullQualifiedClassName);
            }

            if (referenceExtension.isInBlacklist(fullQualifiedClassName)) {
                return false;
            }

            if (!referenceExtension.librariesOnly.isEmpty() && input.getScopes().contains(QualifiedContent.Scope.EXTERNAL_LIBRARIES)) {
                boolean libResult = referenceExtension.isInLibrariesOnly(fullQualifiedClassName);
                if (libResult) {
                    ReferenceLog.info("librariesOnly  " + input.getFile() + "   " + fullQualifiedClassName);
                }
                return libResult;
            }
        }

        return true;
    }

    @Override
    protected ClassVisitor wrapClassWriter(ClassWriter classWriter) {
        return new ReferenceClassAdapter(classWriter, referenceExtension);
    }
}
