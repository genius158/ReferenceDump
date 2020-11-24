package com.yan.referencecount;

import com.android.build.api.transform.QualifiedContent;
import com.quinn.hunter.transform.asm.BaseWeaver;

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

        if (referenceExtension != null) {
            if (!referenceExtension.foreList.isEmpty()) {
                return referenceExtension.isInWhitelist(fullQualifiedClassName) && superResult;
            }

            if (referenceExtension.isInBlacklist(fullQualifiedClassName)) return !superResult;

            if (!referenceExtension.librariesOnly.isEmpty() &&
                    input.getScopes().contains(QualifiedContent.Scope.EXTERNAL_LIBRARIES)) {
                boolean libResult = superResult && referenceExtension.isInLibrariesOnly(fullQualifiedClassName);
                if (libResult) {
                    ReferenceLog.info("librariesOnly  " + input.getFile() + "   " + fullQualifiedClassName);
                }
                return libResult;
            }
        }

        return superResult;
    }

    @Override
    protected ClassVisitor wrapClassWriter(ClassWriter classWriter) {
        return new ReferenceClassAdapter(classWriter, referenceExtension);
    }
}
