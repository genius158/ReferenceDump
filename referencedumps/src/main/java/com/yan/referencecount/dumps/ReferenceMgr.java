package com.yan.referencecount.dumps;

import android.app.Application;

import java.lang.reflect.Method;

/**
 * @author Bevan (Contact me: https://github.com/genius158)
 * @since 2020/11/21
 */
public class ReferenceMgr {
    private static ReferenceKernel reference = new ReferenceKernel();

    public static void dump() {
        reference.kernelDump();
    }

    /**
     * @hide
     */
    public static <T> T asyncOffer(Object obj, Class classWho, String methodWho, String methodDesWho) {
        if (obj == null) return null;
        return (T) reference.kernelAsyncOffer(obj, classWho, methodWho, methodDesWho);
    }


    public static void attachDumpView(Application app) {
        try {
            Class<?> windowPopClass = Class.forName("com.yan.referencecount.dumps.view.WindowPop");
            Method attachDumpViewMethod = windowPopClass.getMethod("attachDumpView", Application.class);
            attachDumpViewMethod.invoke(windowPopClass, app);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}