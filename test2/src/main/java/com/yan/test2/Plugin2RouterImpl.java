package com.yan.test2;

import android.content.Context;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yan.router.Test2Router;

/**
 * @author Bevan (Contact me: https://github.com/genius158)
 * @since 2020/12/5
 */
@Route(path = Test2Router.ROUTER)
public class Plugin2RouterImpl implements Test2Router {
    @Override
    public void sayTest() {
        Log.e("sayTest", "test2 plugin plugin plugin plugin plugin plugin ");
    }

    @Override
    public void init(Context context) {

    }
}
