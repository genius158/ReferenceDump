package com.yan.referencecount.dumps

import android.os.Handler
import android.os.HandlerThread
import android.util.Log

/**
 * @author Bevan (Contact me: https://github.com/genius158)
 * @since  2020/11/22
 */
internal class ReferenceHandler {
    fun post(runnable: ()->Unit) {
        handler.post(runnable)
    }

    private val handlerThread: HandlerThread
    private val handler: Handler

    init {
        handlerThread = object : HandlerThread("ReferenceMgr") {
            override fun run() {
                try {
                    super.run()
                } catch (e: Throwable) {
                    Log.e("ReferenceKernel", "ReferenceKernelError error --------------------")
                    e.printStackTrace()
                }
            }
        }

        handlerThread.start()
        handler = Handler(handlerThread.looper)
    }
}