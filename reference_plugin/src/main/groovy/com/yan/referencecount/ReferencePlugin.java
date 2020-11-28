package com.yan.referencecount;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.BaseExtension;
import com.android.build.gradle.LibraryExtension;
import com.quinn.hunter.transform.RunVariant;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.UnknownDomainObjectException;

import java.util.Collections;

public class ReferencePlugin implements Plugin<Project> {
    static BaseExtension findExtension(Project project) {
        BaseExtension extension = null;
        try {
            extension = project.getExtensions().getByType(AppExtension.class);
        } catch (UnknownDomainObjectException e) {
            try {
                extension = project.getExtensions().getByType(LibraryExtension.class);
            } catch (UnknownDomainObjectException ignore) {
            }
        }
        return extension;
    }

    @Override
    public void apply(Project project) {
        BaseExtension extension = findExtension(project);

        if (extension == null) {
            throw new RuntimeException("error when BurialPlugin apply");
        }
        project.getDependencies().add("api", "com.yan.referencedumps:referencedumps:1.1.2");

        ReferenceExtension referenceExtension = project.getExtensions().create("referenceExt", ReferenceExtension.class);
        project.afterEvaluate(p -> {
            if (referenceExtension.runVariant == RunVariant.DEBUG) {
                project.getDependencies().add("implementation", "com.yan.referencedumps:referencedumpsview:1.0.5");
            }
        });

        ReferenceComponentFind.find(extension);
        extension.registerTransform(new ReferenceTransform(project, referenceExtension), Collections.EMPTY_LIST);
    }
}
