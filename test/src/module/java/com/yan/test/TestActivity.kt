package com.yan.test

import android.app.Activity
import android.os.Bundle
import com.yan.router.Test2RouterMgr

/**
 * @author Bevan (Contact me: https://github.com/genius158)
 * @since  2020/12/5
 */
class TestActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Test2RouterMgr.ROUTER.sayTest()
    }
}