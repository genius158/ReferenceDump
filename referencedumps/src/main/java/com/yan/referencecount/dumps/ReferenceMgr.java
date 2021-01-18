package com.yan.referencecount.dumps;

/**
 * @author Bevan (Contact me: https://github.com/genius158)
 * @since 2020/11/21
 */
public class ReferenceMgr {
    private static ReferenceKernel reference = new ReferenceKernel();

    public static void dump() {
        reference.dump();
    }
    public static void dumpNow(OnDumpListener onDump) {
        reference.dumpNow(onDump);
    }

    public static void setOnDumpListener(OnDumpListener onDumpListener) {
        if (onDumpListener != null) reference.setOnDumpListener(onDumpListener);
    }

    /**
     * @hide
     */
    public static <T> T asyncOffer(Object obj, Class<?> classWho, String methodWho, String methodDesWho) {
        if (obj == null) return null;
        return (T) reference.asyncOffer(obj, classWho, methodWho, methodDesWho);
    }

}