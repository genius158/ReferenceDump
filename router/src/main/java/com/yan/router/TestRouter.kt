package com.yan.router

import android.content.Context
import android.util.Log
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * @author Bevan (Contact me: https://github.com/genius158)
 * @since  2020/6/28
 */
class TestRouterMgr : TestRouter {

    companion object {
        @JvmStatic
        val router: TestRouterMgr by lazy { TestRouterMgr() }
    }

    private val real by lazy { getRouter(TestRouter::class) }
    override fun sayTest() {
        if (real==null) Log.e("sayTest","test no testimpl ")
        real?.sayTest()
    }

    override fun init(context: Context?) {
    }

}

interface TestRouter : IProvider {

    fun sayTest()

    companion object {
        const val ROUTER = "/Test/router"
    }
}