package com.iris.dlock.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjinlong on 17/4/26.
 */
public class MainTest {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("test.dlock.xml");
        DlockSpringTest obj = ctx.getBean("dlockSpringTest",DlockSpringTest.class);
        ComplexKey complexKey = new ComplexKey();
        List<String> list = new ArrayList<String>();
        list.add("key1");
        list.add("key2ddddd");
        complexKey.setList(list);
        complexKey.setValue("key3");
        obj.dlock4Test("p1v1",complexKey,"p3v1");
    }
}
