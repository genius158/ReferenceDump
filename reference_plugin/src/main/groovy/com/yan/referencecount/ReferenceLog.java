package com.yan.referencecount;

public class ReferenceLog {
    public static String TAG = ReferenceLog.class.getSimpleName();

    static boolean logEnable = true;

    static void info(String msg) {
        if (logEnable) {
            System.out.println(TAG + ": " + msg);
        }
    }

    static void error(String msg) {
        if (logEnable) {
            System.err.println(TAG + ": " + msg);
        }
    }
}
