package com.yan.referencecount.dumps.view

import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager

/**
 * @author Bevan (Contact me: https://github.com/genius158)
 * @since  2020/11/18
 *
 * @param onRelayout 屏幕旋转后回调
 */
internal class ScreenSizeHelper(private val windowManager: WindowManager) : View.OnLayoutChangeListener {
    val screenSize = DisplayMetrics()

    init {
        determineScreenSize()
    }

    override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
        determineScreenSize()
    }

    private fun determineScreenSize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            windowManager.defaultDisplay?.getRealMetrics(screenSize)
        } else {
            windowManager.defaultDisplay?.getMetrics(screenSize)
        }
    }

}