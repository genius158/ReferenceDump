package com.yan.referencecount.dumps

import android.os.Handler
import android.os.HandlerThread

/**
 * @author Bevan (Contact me: https://github.com/genius158)
 * @since  2020/11/22
 */
internal class ReferenceHandler {
    fun post(runnable: () -> Unit) {
        handler.post(runnable)
    }

    private val handlerThread: HandlerThread = HandlerThread("ReferenceMgr")
    private val handler: Handler

    init {
        handlerThread.start()
        handler = Handler(handlerThread.looper)
    }
}