package com.yan.referencecounttest;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.yan.referencecount.dumps.ReferenceMgr;

import java.util.ArrayList;

/**
 * @author Bevan (Contact me: https://github.com/genius158)
 * @since 2020/11/20
 */
public class MainActivity extends Activity {
    Test2 test;
    Test3 test3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ReferenceMgr.attachDumpView(getApplication());
        test();
        test = new Test2(1);
        test.test();
        test3 = new Test3();
        test3.test3();
        ArrayList<String> packageNames = new ArrayList<>();
        packageNames.add(Test3.class.getName());
    }

    private void test() {

    }

}
