package com.yan.referencecount.dumps.view

import android.os.Build
import android.util.Log
import com.yan.referencecount.dump.objectcalculate.ObjectCalculator
import com.yan.referencecount.dumps.OnDumpListener
import com.yan.referencecount.dumps.ReferenceWeak
import java.util.*
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.concurrent.withLock

/**
 * @author Bevan (Contact me: https://github.com/genius158)
 * @since  2020/12/5
 */
internal class OnDumpWithSizeListener private constructor() : OnDumpListener {

    companion object {
        val ins = OnDumpWithSizeListener()
    }

    private val dumpSize = LinkedList<Boolean>()

    private val dumpSizeLock = ReentrantLock()
    fun dumpWithSize(withSize: Boolean) {
        dumpSizeLock.withLock { dumpSize.offer(withSize) }
    }

    private fun pollDumpSize(): Boolean? {
        return dumpSizeLock.withLock { dumpSize.pollFirst() }
    }

    private fun dumpSize(obj: Any?): Long {
        obj ?: return 0
        return ObjectCalculator.ins.objectSize(obj).coerceAtLeast(0)
    }

    override fun onDump(classMap: HashMap<Class<*>, ArrayList<ReferenceWeak<Any?>>>) {
        val withSize = pollDumpSize() ?: false
        if (withSize && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            doDumpWithSize(HashMap(classMap))
        } else {
            dump(classMap, false)
        }
    }

    private fun dump(classMap: HashMap<Class<*>, ArrayList<ReferenceWeak<Any?>>>, dumpWithSize: Boolean) {
        Log.e("DumpRef", "-\n\n-")
        Log.e("DumpRef", "START--------------------------------------------------")
        val entriesSort = ArrayList(classMap.entries).sortedBy { e -> -e.value.size }

        var refCount = 0
        entriesSort.forEach { entry -> refCount += entry.value.size }
        Log.e("DumpRef", "RefCount ---->| $refCount |<----")

        var curTotalSize = 0L
        Log.e("DumpRef", "-")
        for (entry in entriesSort) {
            val clazz = entry.key
            val weaks = entry.value
            if (weaks.isNullOrEmpty()) continue

            Log.e("DumpRef", "CLASS - ${clazz.name} count: ${weaks.size} ")
            var curGroupSize = 0L
            val weakGroup = weaks.groupBy { weak -> weak.stack }.entries.sortedBy { e -> -e.value.size }
            if (weakGroup.isEmpty()) continue
            for (group in weakGroup) {
                val stack = group.key ?: continue
                var curSize = 0L
                if (dumpWithSize) {
                    for (weak in group.value) {
                        curSize += ((weak.extra as? Long) ?: 0L)
                    }
                }
                curGroupSize += curSize
                Log.e("DumpRef", "----> size(byte): $curSize count: ${group.value.size}  $stack ")
            }
            Log.e("DumpRef", "----> size(byte): $curGroupSize *")
            Log.e("DumpRef", "-")

            curTotalSize += curGroupSize
        }

        Log.e("DumpRef", "totalSize(byte): $curTotalSize")
        Log.e("DumpRef", "END--------------------------------------------------")
        Log.e("DumpRef", "-\n\n-")
    }


    private val executor = ThreadPoolExecutor(8, 16, 60, TimeUnit.SECONDS, LinkedBlockingDeque())

    private fun putDumpSizeTask(dumpSizeTask: DumpSizeTask) {
        if (dumpSizeTasks.size > 150) return
        synchronized(dumpSizeTasks) {
            dumpSizeTask.referenceWeakMap = null
            dumpSizeTask.referenceWeak = null
            dumpSizeTask.countAtomic = null
            dumpSizeTasks.offer(dumpSizeTask)
        }
    }

    private fun doDumpWithSize(classMap: HashMap<Class<*>, ArrayList<ReferenceWeak<Any?>>>) {
        var refCount = 0
        val classMapIterator = classMap.iterator()
        while (classMapIterator.hasNext()) {
            val entry = classMapIterator.next()
            refCount += entry.value.size
        }
        val countAtomic = AtomicInteger(refCount)
        val classMapValueIterator = classMap.values.iterator()
        while (classMapValueIterator.hasNext()) {
            val refIterator = classMapValueIterator.next().iterator()
            while (refIterator.hasNext()) {
                val rw = refIterator.next()
                executor.execute(getDumpSizeTask(rw, countAtomic, classMap))
            }
        }
    }

    private val dumpSizeTasks = LinkedList<DumpSizeTask>()
    private fun getDumpSizeTask(referenceWeak: ReferenceWeak<Any?>, countAtomic: AtomicInteger, referenceWeakMap: HashMap<Class<*>, ArrayList<ReferenceWeak<Any?>>>): Runnable {
        return synchronized(dumpSizeTasks) {
            val task = dumpSizeTasks.pollFirst() ?: DumpSizeTask()
            task.referenceWeakMap = referenceWeakMap
            task.referenceWeak = referenceWeak
            task.countAtomic = countAtomic
            task
        }
    }

    inner class DumpSizeTask : Runnable {
        var referenceWeakMap: HashMap<Class<*>, ArrayList<ReferenceWeak<Any?>>>? = null
        var referenceWeak: ReferenceWeak<Any?>? = null
        var countAtomic: AtomicInteger? = null
        override fun run() {
            referenceWeak?.let { rw ->
                val size = dumpSize(rw.get())
                rw.extra = size
            }
            if (countAtomic?.decrementAndGet() ?: 1 <= 0) {
                referenceWeakMap?.let { rwm -> dump(rwm, true) }
            }
            putDumpSizeTask(this)
        }
    }

}