package com.yan.referencecount.dumps

/**
 * @author Bevan (Contact me: https://github.com/genius158)
 * @since  2020/11/22
 */
interface OnDumpListener {
    fun onDump(classMap:HashMap<Class<*>, ArrayList<ReferenceWeak<Any?>>>)
}