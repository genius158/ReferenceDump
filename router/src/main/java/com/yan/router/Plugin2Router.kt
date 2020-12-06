package com.yan.router

import android.content.Context
import android.util.Log
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * @author Bevan (Contact me: https://github.com/genius158)
 * @since  2020/6/28
 */
class PluginRouterMgr : Plugin2Router {

    companion object {
        @JvmStatic
        val router: PluginRouterMgr by lazy { PluginRouterMgr() }
    }

    private val real by lazy { getRouter(Plugin2Router::class) }
    override fun sayTest() {
        if (real==null)Log.e("sayTest","no pluginimpl ")
        real?.sayTest()
    }

    override fun init(context: Context?) {
    }

}

interface Plugin2Router : IProvider {

    fun sayTest()

    companion object {
        const val ROUTER = "/plugin/router"
    }
}