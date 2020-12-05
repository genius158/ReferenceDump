package com.yan.referencecount.dumps.view

import android.content.Context
import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import android.view.*
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.TextViewCompat
import com.yan.referencecount.dumps.ReferenceMgr
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * @author Bevan (Contact me: https://github.com/genius158)
 * @since  2020/11/21
 */
internal class DumpView(ctx: Context, private val windowManager: WindowManager, private val lpWindow: WindowManager.LayoutParams) : LinearLayout(ctx) {
    private val touchSlop = ViewConfiguration.get(ctx).scaledTouchSlop

    private val screenSizeHelper = ScreenSizeHelper(windowManager)

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

    private val viewHeight by lazy { screenSizeHelper.screenSize.widthPixels.coerceAtMost(screenSizeHelper.screenSize.heightPixels) / 6 }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = viewHeight * 2 / 3
        if (width != measuredWidth) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY))
        }
        setMeasuredDimension(width, viewHeight)
    }

    private val countOnly = providerTxtView()
    private val withSize = providerTxtView()

    private fun providerTxtView(): AppCompatTextView {
        return object : AppCompatTextView(context) {
            override fun requestLayout() {}
        }.also { tv ->
            addView(tv, -1, -1)
            tv.setTextColor(Color.WHITE)
            (tv.layoutParams as LayoutParams).weight = 1F
            val padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4F, resources.displayMetrics).toInt()
            tv.setPadding(padding, 0, padding, 0)
            tv.setLines(1)
            TextViewCompat.setAutoSizeTextTypeWithDefaults(tv, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM)
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(tv, 8, 14, 1, TypedValue.COMPLEX_UNIT_DIP)
            tv.gravity = Gravity.CENTER
        }
    }

    init {
        orientation = VERTICAL

        countOnly.text = "num"
        withSize.text = "mem"

        countOnly.setOnClickListener {
            OnDumpWithSizeListener.ins.dumpWithSize(false)
            ReferenceMgr.dump()
        }

        val countBg = ColorDrawable(0xFFEE9A00.toInt())
        countOnly.background = countBg
        val sizeBg = ColorDrawable(0xFF4169E1.toInt())
        withSize.background = sizeBg

        withSize.setOnClickListener {
            OnDumpWithSizeListener.ins.dumpWithSize(true)
            ReferenceMgr.dump()
        }

        post {
            moving(intArrayOf(lpWindow.x, lpWindow.y), floatArrayOf(0F, 0F)
                    , screenSizeHelper.screenSize.widthPixels.toFloat()
                    , screenSizeHelper.screenSize.heightPixels / 2F)
        }

        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View?, outline: Outline?) {
                outline?.setRoundRect(0, 0, measuredWidth, measuredHeight, (measuredWidth / 3).toFloat())
            }
        }
        clipToOutline = true
        setWillNotDraw(false)
    }
}