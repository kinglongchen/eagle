package com.iris.dlock.spring;

import com.iris.annotations.Dlock;
import com.iris.annotations.LockKey;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by chenjinlong on 17/4/26.
 */
@Slf4j
public class DlockSpringTest {
    @Dlock
    public void dlock4Test(String p1,@LockKey ComplexKey p2,String p3) {
        log.info(Thread.currentThread().getName()+"===>");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info(Thread.currentThread().getName()+"<===");
    }
}
