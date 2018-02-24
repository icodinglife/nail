package com.nail.test.service;

/**
 * Created by guofeng.qin on 2018/02/24.
 */
public interface ITestService {
    String testStr(String str);

    void testvoid();

    int testInt(int i);

    TestEntity testObj(TestEntity entity);

    public static class TestEntity {
        private String name;
        private String value;

        public TestEntity() {
        }

        public TestEntity(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String v) {
            this.value = v;
        }
    }
}
