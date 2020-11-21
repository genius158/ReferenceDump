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
import java.util.Set;

public final class ReferenceTransform extends HunterTransform {
  private ReferenceExtension referenceExtension;

  public ReferenceTransform(Project project) {
    super(project);
    referenceExtension = project.getExtensions().create("referenceExt", ReferenceExtension.class);
    ReferenceLog.logEnable = referenceExtension.logEnable;

    this.bytecodeWeaver = new ReferenceWeaver(referenceExtension);
    ReferenceLog.info("BurialExtension:" + referenceExtension.toString());
  }

  @Override
  public void transform(Context context, Collection<TransformInput> inputs,
      Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider,
      boolean isIncremental) throws IOException, TransformException, InterruptedException {
    bytecodeWeaver.setExtension(referenceExtension);
    ReferenceLog.info("BurialExtension:" + referenceExtension.toString());
    super.transform(context, inputs, referencedInputs, outputProvider, isIncremental);
  }

  protected RunVariant getRunVariant() {
    return referenceExtension.runVariant;
  }

  @Override public Set<QualifiedContent.Scope> getScopes() {
    if (referenceExtension == null) return super.getScopes();
    Set<QualifiedContent.Scope> extScopes = referenceExtension.getScopes();
    if (extScopes != null) {
      return extScopes;
    }
    return super.getScopes();
  }

  @Override
  protected boolean inDuplcatedClassSafeMode() {
    return true;
  }
}
