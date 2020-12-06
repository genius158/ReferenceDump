package com.yan.referencecounttest

import android.app.Application
import android.content.Context
import android.util.Log
import com.alibaba.android.arouter.launcher.ARouter
import com.yan.burial.method.timer.BurialTimer

//import com.yan.burial.method.timer.BurialTimer

/**
 * @author Bevan (Contact me: https://github.com/genius158)
 * @since  2020/12/5
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            ARouter.openDebug()
            ARouter.openLog()
        }
        ARouter.init(this)

        BurialTimer.getTimer().setListener { ignore, className, methodName, des, cost ->
            if (cost > 0) {
                stringBuilder.clear()
                stringBuilder.append(cost).append("   ").append(className).append("  ").append(methodName)
                Log.e("BurialTimer", stringBuilder.toString())
                stringBuilder.clear()
            }
        }
    }

    private val stringBuilder = StringBuilder()

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }
}