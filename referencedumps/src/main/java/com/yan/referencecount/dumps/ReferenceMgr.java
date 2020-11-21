package com.yan.referencecount.dumps;

import android.app.Application;
import android.content.Intent;

import com.yan.referencecount.dumps.view.ActivityLife;
import com.yan.referencecount.dumps.view.PermissionActivity;
import com.yan.referencecount.dumps.view.WindowPop;

/**
 * @author Bevan (Contact me: https://github.com/genius158)
 * @since 2020/11/21
 */
public class ReferenceMgr {
    private static ReferenceKernel reference = new ReferenceKernel();

    public static void dump() {
        reference.kernelDump();
    }

    static <T> T asyncOffer(Object obj, Class classWho, String methodWho, String methodDesWho) {
        if (obj == null) return (T) obj;
        return (T) reference.kernelAsyncOffer(obj, classWho, methodWho, methodDesWho);
    }


    private static WindowPop windowPop = null;

    public static void attachDumpView(Application app) {
        if (windowPop == null) {
            windowPop = new WindowPop(app);
            new ActivityLife(windowPop, app);
        }
        if (WindowPop.floatingPermissionOk(app)) {
            windowPop.attach();
        } else {
            app.startActivity(new Intent(app, PermissionActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

}