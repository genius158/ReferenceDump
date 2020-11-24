package com.yan.referencecount;

import com.android.build.api.transform.Context;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformOutputProvider;
import com.quinn.hunter.transform.HunterTransform;
import com.quinn.hunter.transform.RunVariant;

import org.gradle.api.Project;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class ReferenceTransform extends HunterTransform {
    private ReferenceExtension referenceExtension;

    public ReferenceTransform(Project project, ReferenceExtension referenceExtension) {
        super(project);
        this.referenceExtension = referenceExtension;

        this.bytecodeWeaver = new ReferenceWeaver(this.referenceExtension);
        ReferenceLog.info("BurialExtension:" + this.referenceExtension.toString());
    }

    @Override
    public void transform(Context context, Collection<TransformInput> inputs,
                          Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider,
                          boolean isIncremental) throws IOException, TransformException, InterruptedException {
        bytecodeWeaver.setExtension(referenceExtension);
        ReferenceLog.logEnable = referenceExtension.logEnable;
        ReferenceLog.info("BurialExtension:" + referenceExtension.toString());
        super.transform(context, inputs, referencedInputs, outputProvider, isIncremental);
    }

    protected RunVariant getRunVariant() {
        return referenceExtension.runVariant;
    }

    private static final Set<QualifiedContent.Scope> SCOPES = new HashSet<>();

    static {
        SCOPES.add(QualifiedContent.Scope.PROJECT);
        SCOPES.add(QualifiedContent.Scope.SUB_PROJECTS);
    }

    private boolean isPrint = false;

    @Override
    public Set<QualifiedContent.Scope> getScopes() {
        if (referenceExtension == null) return SCOPES;
        Set<QualifiedContent.Scope> extScopes = referenceExtension.getScopes();
        if (extScopes != null) {
            if (!isPrint && extScopes.contains(QualifiedContent.Scope.EXTERNAL_LIBRARIES)) {
                isPrint = true;
                ReferenceLog.error("");
                ReferenceLog.error("EXTERNAL_LIBRARIES may not work will in ReferencePlugin");
                ReferenceLog.error("");
            }
            return extScopes;
        }
        return SCOPES;
    }
}
