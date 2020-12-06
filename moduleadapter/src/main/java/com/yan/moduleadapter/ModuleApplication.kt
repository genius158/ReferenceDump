package com.yan.moduleadapter

import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter

/**
 * @author Bevan (Contact me: https://github.com/genius158)
 * @since  2020/12/5
 */
class ModuleApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        ARouter.init(this)
    }

}