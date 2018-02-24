package com.nail.test.service.impl;

import com.alibaba.fastjson.JSON;
import com.nail.test.service.ITestService;

/**
 * Created by guofeng.qin on 2018/02/24.
 */
public class TestServiceImpl implements ITestService {
    @Override
    public String testStr(String str) {
        System.out.println("TestStr " + str);
        return "Test: " + str;
    }

    @Override
    public void testvoid() {
        System.out.println("TestVoid");
    }

    @Override
    public int testInt(int i) {
        System.out.println("TestInt: " + i);
        return 100 + i;
    }

    @Override
    public TestEntity testObj(TestEntity entity) {
        System.out.println("TestInt: " + JSON.toJSONString(entity));

        entity.setValue(entity.getValue() + " Test");

        return entity;
    }
}
