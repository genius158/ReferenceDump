package com.yan.referencecount;

import com.android.build.api.transform.QualifiedContent;
import com.quinn.hunter.rctransform.RunVariant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReferenceExtension {
    static final String JAVA_JVM_HELPER = "com.dodola.jvmtilib.JVMTIHelper";
    static final String PLUGIN_OBJECT_CALCULATER = "com.yan.referencecount.dump.objectcalculate";

    static final String PLUGIN_LIBRARY = "com.yan.referencecount.dumps";
    static final String PLUGIN_LIBRARY_REFERENCE = "com/yan/referencecount/dumps/ReferenceMgr";
    static final String PLUGIN_LIBRARY_REFERENCE_ASYNCOFFER = "asyncOffer";
    static final String PLUGIN_LIBRARY_REFERENCE_ASYNCOFFER_DES = "(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object;";

    /**
     * 如果不为空，埋点只插入这个配置上的类
     */
    public List<String> foreList = new ArrayList<>();
    /**
     * 这个配置那些不需要插入代码的类
     */
    public List<String> ignoreList = new ArrayList<>();

    /**
     * 有些第三次jar包混淆后由些特殊符号，在EXTERNAL_LIBRARIES模式下
     * 只包插桩到这些
     */
    public List<String> librariesOnly = new ArrayList<>();

    /**
     * 那种编译状态下触发
     */
    public RunVariant runVariant = RunVariant.DEBUG;

    /**
     * 作用预
     */
    public List<String> scopes = new ArrayList<>();

    public boolean logEnable = false;

    @Override
    public String toString() {
        return "ReferenceExtension{" +
                "runVariant=" + runVariant +
                ", scopes=" + scopes +
                ", logEnable=" + logEnable +
                '}';
    }

    /**
     * Only the project content
     * PROJECT(0x01),
     * <p>
     * Only the sub-projects.
     * SUB_PROJECTS(0x04),
     * <p>
     * Only the external libraries
     * EXTERNAL_LIBRARIES(0x10),
     * <p>
     * Code that is being tested by the current variant, including dependencies
     * TESTED_CODE(0x20),
     * <p>
     * Local or remote dependencies that are provided-only
     * PROVIDED_ONLY(0x40),
     */
    Set<QualifiedContent.Scope> scopeSet = new HashSet<>();

    Set<QualifiedContent.Scope> getScopes() {
        if (scopes == null || scopes.isEmpty()) return null;
        if (!scopeSet.isEmpty()) return scopeSet;
        for (String s : scopes) {
            if (s == null) continue;
            if (s.equalsIgnoreCase("PROJECT")) {
                scopeSet.add(QualifiedContent.Scope.PROJECT);
            } else if (s.equalsIgnoreCase("SUB_PROJECTS")) {
                scopeSet.add(QualifiedContent.Scope.SUB_PROJECTS);
            } else if (s.equalsIgnoreCase("EXTERNAL_LIBRARIES")) {
                scopeSet.add(QualifiedContent.Scope.EXTERNAL_LIBRARIES);
            } else if (s.equalsIgnoreCase("TESTED_CODE")) {
                scopeSet.add(QualifiedContent.Scope.TESTED_CODE);
            } else if (s.equalsIgnoreCase("PROVIDED_ONLY")) {
                scopeSet.add(QualifiedContent.Scope.PROVIDED_ONLY);
            }
        }
        if (scopeSet.isEmpty()) return null;
        return scopeSet;
    }

    public boolean isLibAsyncOffer(String className, String methodName) {
        return isLib(className) && methodName.equals(PLUGIN_LIBRARY_REFERENCE_ASYNCOFFER);
    }

    public boolean isLib(String className) {
        return className.contains(PLUGIN_LIBRARY);
    }

    public boolean isJvmHelper(String className) {
        return className.contains(JAVA_JVM_HELPER);
    }

    public boolean isObjectCalculator(String className) {
        return className.contains(PLUGIN_OBJECT_CALCULATER);
    }


    boolean isInWhitelist(String fullQualifiedClassName) {
        boolean inWhiteList = false;
        for (String item : foreList) {
            if (fullQualifiedClassName.contains(item)) {
                inWhiteList = true;
                break;
            }
        }
        return inWhiteList;
    }

    boolean isInLibrariesOnly(String fullQualifiedClassName) {
        boolean inWhiteList = false;
        for (String item : librariesOnly) {
            if (fullQualifiedClassName.contains(item)) {
                inWhiteList = true;
                break;
            }
        }
        return inWhiteList;
    }

    boolean isInBlacklist(String fullQualifiedClassName) {
        boolean inBlacklist = false;
        for (String item : ignoreList) {
            if (fullQualifiedClassName.contains(item)) {
                inBlacklist = true;
                break;
            }
        }
        return inBlacklist;
    }

    public boolean isConstructor(String methodName) {
        return methodName.contains("<init>");
    }


}
