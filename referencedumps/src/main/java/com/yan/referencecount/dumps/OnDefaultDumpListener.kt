package com.yan.referencecount.dumps

import android.util.Log

/**
 * @author Bevan (Contact me: https://github.com/genius158)
 * @since  2020/11/22
 *
 * 打印引用信息
 */
internal class OnDefaultDumpListener : OnDumpListener {
    override fun onDump(referenceWeakMap: HashMap<Class<*>, ArrayList<ReferenceWeak<Any?>>>) {
        Log.e("DumpRef", "-\n\n-")
        referenceWeakMap.map { it.value.size }
        Log.e("DumpRef", "START--------------------------------------------------")
        val entriesSort = ArrayList(referenceWeakMap.entries).sortedBy { e -> -e.value.size }

        var refCount = 0
        entriesSort.forEach { entry -> refCount += entry.value.size }
        Log.e("DumpRef", "RefCount ---->| $refCount |<----")

        Log.e("DumpRef", "-")
        for (entry in entriesSort) {
            val clazz = entry.key
            val weaks = entry.value
            if (weaks.isNullOrEmpty()) continue

            Log.e("DumpRef", "CLASS - ${clazz.name}  ${weaks.size}")
            val weakGroup = weaks.groupBy { weak -> weak.stack }.entries.sortedBy { e -> -e.value.size }
            if (weakGroup.isEmpty()) continue
            for (group in weakGroup) {
                val stack = group.key ?: continue
                Log.e("DumpRef", "----> $stack : ${group.value.size}")
            }
            Log.e("DumpRef", "-")
        }

        Log.e("DumpRef", "END--------------------------------------------------")
        Log.e("DumpRef", "-\n\n-")
    }

}