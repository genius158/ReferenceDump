package com.yan.referencecount.dump.objectcalculate

import android.content.Context
import android.os.Build
import android.util.Log
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
        loadJmvTI()

        return fullSizeOf(obj)
    }


    private val visitedCache = LinkedList<IdentityHashMap<Any, Any>>()
    private fun getVisited(): IdentityHashMap<Any, Any> {
        return synchronized(visitedCache) {
            val visited = visitedCache.pollFirst() ?: IdentityHashMap<Any, Any>()
            visited
        }
    }

    private fun cacheVisited(visited: IdentityHashMap<Any, Any>) {
        if (visitedCache.size > 10) return
        synchronized(visitedCache) {
            visited.clear()
            if (!visitedCache.contains(visited)) visitedCache.offer(visited)
        }
    }

    private val stackCache = LinkedList<LinkedList<Any>>()
    private fun getStack(): LinkedList<Any> {
        return synchronized(stackCache) {
            val stack = stackCache.pollFirst() ?: LinkedList<Any>()
            stack
        }
    }

    private fun cacheStack(stack: LinkedList<Any>) {
        if (stackCache.size > 10) return
        synchronized(stackCache) {
            stackCache.clear()
            if (!stackCache.contains(stack)) stackCache.offer(stack)
        }
    }

    private fun loadJmvTI() {
        if (!isLoad) {
            synchronized(ins) {
                if (!isLoad) {
                    try {
                        Log.d("loadJmvTI", "loadJmvTI init")
                        JVMTIHelper.init(context)
                        isLoad = true
                        Log.d("loadJmvTI", "loadJmvTI ok")
                    } catch (e: Throwable) {
                        e.printStackTrace()
                        Log.d("loadJmvTI", "loadJmvTI error")
                    }
                }
            }
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

    private fun fullSizeOf(obj: Any): Long {
        val visited = getVisited()
        val stack = getStack()
        var result = 0L

        try {
            result = internalSizeOf(obj, stack, visited)
            while (!stack.isEmpty()) {
                result += internalSizeOf(stack.pollFirst(), stack, visited)
            }
        } catch (ignore: Throwable) {
        }

        cacheVisited(visited)
        cacheStack(stack)
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