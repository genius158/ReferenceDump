package com.yan.referencecount.dumps

import java.lang.ref.WeakReference

/**
 * @author Bevan (Contact me: https://github.com/genius158)
 * @since  2020/11/21
 */
class ReferenceWeak<T>(target: T, val stack: String?) : WeakReference<T>(target) {
    var extra: Any? = null

    override fun hashCode(): Int {
        return get()?.hashCode() ?: 0
    }

    override fun equals(other: Any?): Boolean {
        val equals = super.equals(other)
        return equals || get() == (other as? ReferenceWeak<*>)?.get()
    }
}