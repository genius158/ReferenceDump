package com.yan.referencecount;

public class ReferenceLog {

    static boolean logEnable = true;

    static void info(String msg) {
        if (logEnable) {
            System.out.println("BurialPlugin: " + msg);
        }
    }
}
