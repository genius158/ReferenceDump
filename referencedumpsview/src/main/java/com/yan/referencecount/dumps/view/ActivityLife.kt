package com.yan.referencecount.dumps.view

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * @author Bevan (Contact me: https://github.com/genius158)
 * @since  2020/11/21
 */
internal class ActivityLife(private val windowPop: WindowPop, private val app: Application) {
    init {
        var resumeCount = 0
        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityStopped(activity: Activity) {
                resumeCount--
                trigger(resumeCount > 0)
            }

            override fun onActivityResumed(activity: Activity) {
                resumeCount++
                trigger(resumeCount > 0)
            }

            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityDestroyed(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
        })
    }

    private fun trigger(needShow: Boolean) {
        if (!WindowPop.floatingPermissionOk(app)) return
        if (needShow) {
            windowPop.attach()
        } else {
            windowPop.detach()
        }
    }
}