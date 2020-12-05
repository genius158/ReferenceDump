package com.yan.referencecount.dumps.view

import android.util.Log
import com.yan.referencecount.dump.objectcalculate.ObjectCalculator
import com.yan.referencecount.dumps.OnDumpListener
import com.yan.referencecount.dumps.ReferenceWeak
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.ArrayList
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

    override fun onDump(referenceWeakMap: HashMap<Class<*>, ArrayList<ReferenceWeak<Any?>>>) {
        val withSize = pollDumpSize() ?: false
        Log.e("DumpRef", "-\n\n-")
        referenceWeakMap.map { it.value.size }
        Log.e("DumpRef", "START--------------------------------------------------")
        val entriesSort = ArrayList(referenceWeakMap.entries).sortedBy { e -> -e.value.size }

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
                if (withSize) {
                    group.value.forEach { weak -> curSize += dumpSize(weak.get()) }
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

}