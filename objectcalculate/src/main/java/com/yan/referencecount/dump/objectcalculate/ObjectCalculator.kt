package com.yan.referencecount.dump.objectcalculate

import android.content.Context
import android.os.Build
import com.dodola.jvmtilib.JVMTIHelper
import java.lang.reflect.Modifier
import java.util.*

/**
 * @author Bevan (Contact me: https://github.com/genius158)
 * @since  2020/12/5
 */

class ObjectCalculator {
    companion object {
        @JvmStatic
        val ins = ObjectCalculator()
    }

    private var isLoad = false
    fun objectSize(obj: Any): Long {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return -1
        }
        if (!isLoad) {
            JVMTIHelper.init(context)
            isLoad = true
        }
        return try {
            fullSizeOf(obj)
        } catch (ignore: Throwable) {
            -1
        }
    }

    private lateinit var context: Context
    fun loadCtx(ctx: Context) {
        this.context = ctx
    }


    /* from https://github.com/zjw-swun/JVMTI_Demo/tree/master/app */

    /**
     * Calculates full size of object iterating over
     * its hierarchy graph.
     * @param obj object to calculate size of
     * @return object size
     */
    private val visited = IdentityHashMap<Any, Any>()
    private val stack = LinkedList<Any>()

    private fun fullSizeOf(obj: Any): Long {
        visited.clear()
        stack.clear()
        var result = internalSizeOf(obj, stack, visited)
        while (!stack.isEmpty()) {
            result += internalSizeOf(stack.pop(), stack, visited)
        }
        // help gc
        visited.clear()
        stack.clear()
        return result
    }

    private fun skipObject(obj: Any?, visited: Map<Any?, Any?>): Boolean {
        if (obj is String) {//这个if是bug，应当去掉--teasp
            // skip interned string
            if (obj === obj.intern()) {
                return true
            }
        }
        return obj == null || visited.containsKey(obj)
    }

    private fun internalSizeOf(obj: Any?, stack: LinkedList<Any>, visited: MutableMap<Any?, Any?>): Long {
        if (skipObject(obj, visited)) {
            return 0
        }
        visited[obj] = null

        var result: Long = 0
        // get size of object + primitive variables + member pointers
        result += JVMTIHelper.getObjectSize(obj)

        // process all array elements
        var clazz: Class<*>? = obj!!.javaClass
        if (clazz!!.isArray) {
            if (clazz.name.length != 2) {// skip primitive type array
                val length = java.lang.reflect.Array.getLength(obj)
                for (i in 0 until length) {
                    stack.add(java.lang.reflect.Array.get(obj, i))
                }
            }
            return result
        }

        // process all fields of the object
        while (clazz != null) {
            val fields = clazz.declaredFields
            for (i in fields.indices) {
                if (!Modifier.isStatic(fields[i].modifiers)) {
                    if (fields[i].type.isPrimitive) {
                        continue // skip primitive fields
                    } else {
                        fields[i].isAccessible = true
                        try {
                            // objects to be estimated are put to stack
                            val objectToAdd = fields[i].get(obj)
                            if (objectToAdd != null) {
                                stack.add(objectToAdd)
                            }
                        } catch (ex: IllegalAccessException) {
                            ex.printStackTrace()
                        }

                    }
                }
            }
            clazz = clazz.superclass
        }
        return result
    }


}