package com.yan.referencecount.dumps.view

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import androidx.annotation.Keep

/**
 * @author Bevan (Contact me: https://github.com/genius158)
 * @since  2020/11/15
 *
 * WindowPop(floating view manager) base on PopView
 *
 * demo :
 * val windowPop = WindowPop()
 * windowPop.pop.setContentView(mView)
 * windowPop.pop.setLayout
 * windowPop.pop.setDimAmount
 * ...
 */
class WindowPop(app: Context) {
    companion object {
        @JvmStatic
        internal fun floatingPermissionOk(app: Context): Boolean {
            //检查是否已经授予权限，大于6.0的系统适用，小于6.0系统默认打开，无需理会
            return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                    || Settings.canDrawOverlays(app)
        }


        private var windowPop: WindowPop? = null

        @Keep
        @JvmStatic
        fun attachDumpView(app: Application) {
            if (windowPop == null) {
                windowPop = WindowPop(app)
                ActivityLife(windowPop!!, app)
            }
            if (floatingPermissionOk(app)) {
                windowPop!!.attach()
            } else {
                app.startActivity(Intent(app, PermissionActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        }
    }

    private val windowManager = app.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val layoutParams = WindowManager.LayoutParams()

    private val popView = DumpView(app, windowManager, layoutParams)

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            //layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.format = PixelFormat.TRANSLUCENT
        layoutParams.flags = (
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)

        layoutParams.gravity = Gravity.TOP or Gravity.LEFT
        popView.addOnLayoutChangeListener { v, _, _, _, _, _, _, _, _ ->
            layoutParams.width = v.measuredWidth
            layoutParams.height = v.measuredHeight
            windowManager.updateViewLayout(v, layoutParams)
        }
    }


    fun attach() {
        if (popView.isShown) return
        windowManager.addView(popView, layoutParams)
    }

    fun detach() {
        if (!popView.isShown) return
        windowManager.removeView(popView)
    }

}