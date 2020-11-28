package com.yan.referencecount.dumps

/**
 * @author Bevan (Contact me: https://github.com/genius158)
 * @since  2020/11/20
 */
internal class ReferenceKernel : OnDumpListener {
    var onDumpListener: OnDumpListener = OnDefaultDumpListener()
    private val referenceHandler = ReferenceHandler()
    private val referenceWeakMap: HashMap<Class<*>, ArrayList<ReferenceWeak<Any?>>> = HashMap(64)

    fun asyncOffer(obj: Any, classWho: Class<*>, methodWho: String?, methodDesWho: String?): Any {
        // 添加一个统计任务
        referenceHandler.post { offer(obj, classWho, methodWho, methodDesWho) }
        return obj
    }

    private fun offer(obj: Any, classWho: Class<*>, methodWho: String?, methodDesWho: String?) {
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

    fun dump() {
        referenceHandler.post {
//            Runtime.getRuntime().gc()
            cleanUp()
            onDump(referenceWeakMap)
        }
    }

    /**
     * 清理空引用
     */
    private fun cleanUp() {
        val mapIterator = referenceWeakMap.iterator()
        while (mapIterator.hasNext()) {
            val entity = mapIterator.next()
            val list = entity.value

            val iterator = list.iterator()
            while (iterator.hasNext()) {
                val weak = iterator.next()
                if (weak.get() == null) iterator.remove()
            }

            if (list.isEmpty()) mapIterator.remove()
        }
    }

    override fun onDump(classMap: HashMap<Class<*>, ArrayList<ReferenceWeak<Any?>>>) {
        onDumpListener.onDump(classMap)
    }

}