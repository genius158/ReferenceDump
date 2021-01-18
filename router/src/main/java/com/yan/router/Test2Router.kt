package com.yan.router

import android.content.Context
import android.util.Log
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * @author Bevan (Contact me: https://github.com/genius158)
 * @since  2020/6/28
 */
class Test2RouterMgr : Test2Router {

    companion object {
        @JvmStatic
        val ROUTER: Test2RouterMgr by lazy { Test2RouterMgr() }
    }

    private val real by lazy { getRouter(Test2Router::class) }
    override fun sayTest() {
        if (real == null) Log.e("sayTest", "test2 no pluginimpl ")
        real?.sayTest()
    }

    override fun init(context: Context?) {
    }

}

interface Test2Router : IProvider {

    fun sayTest()

    companion object {
        const val ROUTER = "/plugin/router"
    }
}