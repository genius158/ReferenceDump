package com.yan.test2;

import android.content.Context;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yan.router.Plugin2Router;

/**
 * @author Bevan (Contact me: https://github.com/genius158)
 * @since 2020/12/5
 */
@Route(path = Plugin2Router.ROUTER)
public class Plugin2RouterImpl implements Plugin2Router {
    @Override
    public void sayTest() {
        Log.e("sayTest", "plugin plugin plugin plugin plugin plugin ");
    }

    @Override
    public void init(Context context) {

    }
}
