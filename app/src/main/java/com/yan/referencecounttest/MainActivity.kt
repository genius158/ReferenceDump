package com.yan.referencecounttest

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.yan.referencecounttest.databinding.ActivityMainBinding
import com.yan.router.TestRouterMgr.Companion.router
import java.util.*

/**
 * @author Bevan (Contact me: https://github.com/genius158)
 * @since 2020/11/20
 */
class MainActivity : Activity() {
    val thread = Thread(Runnable {  })
    var test: Test2? = null
    var test3: Test3? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        val rootView = binding.root
        setContentView(rootView)

        binding.flContent.setWillNotDraw(false)

        app = application
        test()
        test = Test2(1)
        test!!.test()
        test3 = Test3()
        test3!!.test3()
        val packageNames = ArrayList<String>()
        packageNames.add(Test3::class.java.name)
        router.sayTest()
    }

    companion object {
        var app: Application? = null
    }

    private fun test() {}
}