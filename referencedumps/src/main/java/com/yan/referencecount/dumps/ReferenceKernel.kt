package com.yan.referencecount.dumps

import android.os.Handler
import android.os.HandlerThread
import android.util.Log

/**
 * @author Bevan (Contact me: https://github.com/genius158)
 * @since  2020/11/20
 */
internal class ReferenceKernel {
    private val handlerThread: HandlerThread

    private val handler: Handler

    private val referenceWeakMap: HashMap<Class<*>, ArrayList<ReferenceWeak<Any?>>> = HashMap(64)

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

    fun kernelAsyncOffer(obj: Any, classWho: Class<*>, methodWho: String?, methodDesWho: String?): Any {
        // 添加一个统计任务
        handler.post { kernelOffer(obj, classWho, methodWho, methodDesWho) }
        return obj
    }

    private fun kernelOffer(obj: Any, classWho: Class<*>, methodWho: String?, methodDesWho: String?) {
        val who = "${classWho.name}#$methodWho$methodDesWho"
        val clazz = obj::class.java
        val weaks = referenceWeakMap[clazz]
        if (weaks != null) {
            weaks.add(ReferenceWeak(obj, who))
            return
        } else {
            val tmpWeaks = ArrayList<ReferenceWeak<Any?>>()
            tmpWeaks.add(ReferenceWeak(obj, who))
            referenceWeakMap[clazz] = tmpWeaks
        }
    }

    fun kernelDump() {
        handler.post {
            Runtime.getRuntime().gc()
            kernelCleanUp()
            kernelDumpReference()
        }
    }

    /**
     * 清理空引用
     */
    private fun kernelCleanUp() {
        referenceWeakMap.forEach { entry ->
            val iterator = entry.value.iterator()
            while (iterator.hasNext()) {
                if (iterator.next().get() == null) iterator.remove()
            }
        }
    }

    /**
     * 打印引用信息
     */
    private fun kernelDumpReference() {
        Log.e("dumpReference", "-\n\n\n-")
        Log.e("dumpReference", "START--------------------------------------------------")
        var allCount = 0
        val entriesSort = ArrayList(referenceWeakMap.entries).sortedBy { e -> -e.value.size }
        for (entry in entriesSort) {
            val clazz = entry.key
            val weaks = entry.value
            if (weaks.isNullOrEmpty()) continue

            Log.e("dumpReference", "CLASS - ${clazz.name}  ${weaks.size}")
            val weakGroup = weaks.groupBy { weak -> weak.stack }.entries.sortedBy { e -> -e.value.size }
            if (weakGroup.isEmpty()) continue
            for (group in weakGroup) {
                val stack = group.key ?: continue
                allCount += group.value.size
                Log.e("dumpReference", "----> $stack : ${group.value.size}")
            }
            Log.e("dumpReference", "-")
        }

        Log.e("dumpReference", "AllCount ----> $allCount")
        Log.e("dumpReference", "END--------------------------------------------------")
        Log.e("dumpReference", "-\n\n\n-")
    }

}