package com.yan.referencecount.dumps.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatTextView
import com.yan.referencecount.dumps.ReferenceMgr
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * @author Bevan (Contact me: https://github.com/genius158)
 * @since  2020/11/21
 */
internal class DumpView(ctx: Context, private val windowManager: WindowManager, private val lpWindow: WindowManager.LayoutParams) : AppCompatTextView(ctx) {
    private val touchSlop = ViewConfiguration.get(ctx).scaledTouchSlop

    private val screenSizeHelper = ScreenSizeHelper(windowManager)

    private val rect = RectF()
    private val paint = Paint().also { it.color = Color.parseColor("#6102EE") }

    override fun onDraw(canvas: Canvas?) {
        canvas ?: return
        rect.set(0F, 0F, width.toFloat(), height.toFloat())
        val radius = width.coerceAtMost(height) / 2F
        canvas.drawRoundRect(rect, radius, radius, paint)
        super.onDraw(canvas)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        runCatching { dellEvent(ev) }
        super.dispatchTouchEvent(ev)
        return true
    }

    /**
     * 刚开始触摸事件的位置
     */
    private val downEvent = floatArrayOf(0F, 0F)

    /**
     * 开始移动前的点击位置
     */
    private var eventPreMovedXY: FloatArray? = null

    /**
     * 开始移动前的window位置
     */
    private var windowPreMoveXY: IntArray? = null

    private var isMoving = false

    private fun dellEvent(ev: MotionEvent) {

        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                reset()
                downEvent[0] = ev.rawX
                downEvent[1] = ev.rawY
            }
            // 多点触控，重置移动参数，屏蔽感知
            // 相当于重新开始移动
            MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_POINTER_DOWN -> {
                eventPreMovedXY = null
                windowPreMoveXY = null
            }
            MotionEvent.ACTION_MOVE -> {
                val ex = ev.rawX
                val ey = ev.rawY

                if (!isMoving && sqrt(((ex - downEvent[0]).pow(2) + (ey - downEvent[1]).pow(2)).toDouble()) > touchSlop) {
                    isMoving = true
                }

                // 移动状态
                if (isMoving) {
                    if (eventPreMovedXY == null) eventPreMovedXY = floatArrayOf(ex, ey)
                    if (windowPreMoveXY == null) windowPreMoveXY = intArrayOf(lpWindow.x, lpWindow.y)

                    moving(windowPreMoveXY!!, eventPreMovedXY!!, ex, ey)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                reset()
            }
        }
    }

    /**
     * 重置
     */
    private fun reset() {
        eventPreMovedXY = null
        windowPreMoveXY = null
        isMoving = false
    }

    /**
     * 相对位置逻辑，保证移动顺滑
     */
    private fun moving(windowPerMovedXY: IntArray, eventXYPreMoved: FloatArray, ex: Float, ey: Float) {
        val relativeX = ex - eventXYPreMoved[0]
        val relativeY = ey - eventXYPreMoved[1]

        var wx = windowPerMovedXY[0] + relativeX
        var wy = windowPerMovedXY[1] + relativeY

        val screenSize = screenSizeHelper.screenSize

        wx = (screenSize.widthPixels - measuredWidth.toFloat()).coerceAtMost(wx)
        wx = 0F.coerceAtLeast(wx)
        wy = (screenSize.heightPixels - measuredHeight.toFloat()).coerceAtMost(wy)
        wy = 0F.coerceAtLeast(wy)

        lpWindow.x = wx.toInt()
        lpWindow.y = wy.toInt()

        windowManager.updateViewLayout(this, layoutParams)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = screenSizeHelper.screenSize.widthPixels.coerceAtMost(screenSizeHelper.screenSize.heightPixels) / 6
        super.onMeasure(MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY))
    }

    init {
        text = "dump"
        setTextColor(Color.WHITE)
        gravity = Gravity.CENTER
        setOnClickListener { ReferenceMgr.dump() }

        post {
            moving(intArrayOf(lpWindow.x, lpWindow.y), floatArrayOf(0F, 0F)
                    , screenSizeHelper.screenSize.widthPixels.toFloat()
                    , screenSizeHelper.screenSize.heightPixels / 2F)
        }
    }
}