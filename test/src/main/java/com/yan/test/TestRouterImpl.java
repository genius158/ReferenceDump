package com.yan.test;

import android.content.Context;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yan.router.Test2RouterMgr;
import com.yan.router.TestRouter;

/**
 * @author Bevan (Contact me: https://github.com/genius158)
 * @since 2020/12/5
 */
@Route(path = TestRouter.ROUTER)
public class TestRouterImpl implements TestRouter {
    private Object test = new Object();

    @Override
    public void sayTest() {
        String msg = "test sayTestsayTestsayTestsayTestsayTestsayTest";
        Log.e("sayTest", msg);

        Test2RouterMgr.getROUTER().sayTest();
    }

    @Override
    public void init(Context context) {

    }
}
