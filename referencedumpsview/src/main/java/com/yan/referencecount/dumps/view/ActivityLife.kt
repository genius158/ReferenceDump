package com.yan.referencecount.dumps.view

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.util.Pools

/**
 * @author Bevan (Contact me: https://github.com/genius158)
 * @since  2020/11/21
 */
internal class ActivityLife(private val windowPop: WindowPop, private val app: Application) {

    private val handler = Handler(Looper.getMainLooper())

    init {
        var resumeCount = 0
        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityResumed(activity: Activity) {
                resumeCount++
                trigger(resumeCount > 0)
            }

            override fun onActivityPaused(activity: Activity) {
                resumeCount--
                trigger(resumeCount > 0, 150L)
            }

            override fun onActivityStopped(activity: Activity) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityDestroyed(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
        })
    }


    /**
     * 当前的显示隐藏任务
     */
    private var curShowHideRun: ShowHide? = null
    private fun trigger(needShow: Boolean, delay: Long = 0) {
        curShowHideRun?.let { t ->
            handler.removeCallbacks(t)
            poll.release(t)
        }

        val showHide = poll.acquire() ?: ShowHide()
        curShowHideRun = showHide
        showHide.needShow = needShow
        handler.postDelayed(showHide, delay)
    }

    private val poll = Pools.SimplePool<ShowHide>(2)

    inner class ShowHide : Runnable {
        var needShow: Boolean = false
        override fun run() {
            if (!WindowPop.floatingPermissionOk(app)) return
            if (needShow) {
                windowPop.attach()
            } else {
                windowPop.detach()
            }
        }

    }

}