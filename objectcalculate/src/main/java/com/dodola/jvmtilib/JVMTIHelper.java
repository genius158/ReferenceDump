package com.dodola.jvmtilib;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Debug;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by dodola on 2018/12/16.
 */
public class JVMTIHelper {
    private static String packageCodePath = "";

    public static void init(Context context) throws Throwable {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageCodePath = context.getPackageCodePath();
            ClassLoader classLoader = context.getClassLoader();
            Method findLibrary = ClassLoader.class.getDeclaredMethod("findLibrary", String.class);
            String jvmtiAgentLibPath = (String) findLibrary.invoke(classLoader, "jvmti_agent");
            File filesDir = context.getFilesDir();
            File jvmtiLibDir = new File(filesDir, "jvmti");
            if (!jvmtiLibDir.exists()) {
                jvmtiLibDir.mkdirs();

            }
            File agentLibSo = new File(jvmtiLibDir, "agent.so");
            if (agentLibSo.exists()) {
                agentLibSo.delete();
            }
            Files.copy(Paths.get(new File(jvmtiAgentLibPath).getAbsolutePath()), Paths.get((agentLibSo).getAbsolutePath()));

            Debug.attachJvmtiAgent(agentLibSo.getAbsolutePath(), null, classLoader);
            System.loadLibrary("jvmti_agent");
        }

    }

    public static native void retransformClasses(Class[] classes);

    public static native long getObjectSize(Object obj);

    public static native int redefineClass(Class targetClass, byte[] dexBytes);


    public static void printEnter(String log) {
    }

    public static void printEnter(Activity context, String log) {
    }

}
