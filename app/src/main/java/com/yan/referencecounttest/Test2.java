package com.yan.referencecounttest;

import com.yan.referencecount.dumps.ReferenceMgr;

/**
 * @author Bevan (Contact me: https://github.com/genius158)
 * @since 2020/11/20
 */
public class Test2 {
    Test3 test222 = ReferenceMgr.asyncOffer(new Test3(), "etest", "Estset", "SEtset");
    Test2 test;

    Test2(int one) {
        ReferenceMgr.asyncOffer(this, "etest", "Estset", "SEtset");
    }

    void test() {
        test = ReferenceMgr.asyncOffer(new Test2(1), "etest", "Estset", "SEtset");

    }

    void test2() {
    }
}
