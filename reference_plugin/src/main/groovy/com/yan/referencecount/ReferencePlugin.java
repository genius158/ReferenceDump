package com.yan.referencecount;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.BaseExtension;
import com.android.build.gradle.LibraryExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.UnknownDomainObjectException;

import java.util.Collections;

public class ReferencePlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getDependencies().add("api", "com.yan.referencedumps:referencedumps:1.0.1");

        ReferenceComponentFind.find(project);

        BaseExtension extension = null;
        try {
            extension = project.getExtensions().getByType(AppExtension.class);
        } catch (UnknownDomainObjectException e1) {
            e1.printStackTrace();
            try {
                extension = project.getExtensions().getByType(LibraryExtension.class);
            } catch (UnknownDomainObjectException e2) {
                e2.printStackTrace();
            }
        }
        if (extension == null) {
            throw new RuntimeException("error when BurialPlugin apply");
        }
        extension.registerTransform(new ReferenceTransform(project), Collections.EMPTY_LIST);
    }
}
