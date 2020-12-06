package com.yan.referencecounttest;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.yan.router.TestRouterMgr;

import java.util.ArrayList;

/**
 * @author Bevan (Contact me: https://github.com/genius158)
 * @since 2020/11/20
 */
public class MainActivity extends Activity {
    static Application app;
    Test2 test;
    Test3 test3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = getApplication();
        test();
        test = new Test2(1);
        test.test();
        test3 = new Test3();
        test3.test3();
        ArrayList<String> packageNames = new ArrayList<>();
        packageNames.add(Test3.class.getName());

        TestRouterMgr.getRouter().sayTest();

    }

    static {
//      new Thread(new Runnable() {
//          @Override
//          public void run() {
//              while (true){
//                  try {
//                      Thread.sleep(4000);
//                      Debug.dumpHprofData(app.getExternalFilesDir("test").getAbsolutePath()+"/"+"test.txt");
//                  } catch (Exception e) {
//                      e.printStackTrace();
//                  }
//              }
//          }
//      }).start();
    }

    private void test() {

    }

}
