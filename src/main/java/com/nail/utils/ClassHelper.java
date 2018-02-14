package com.nail.utils;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.*;

public class ClassHelper {
    public static Map<String, Method> wrapInterfacesMthods(Class<?>... interfaces) {
        Objects.requireNonNull(interfaces, "Interfaces Can't Be Null");

        Map<String, Method> methodMap = new HashMap<>();

        for (Class<?> iface : interfaces) {
            Map<String, Method> methods = wrapInterfaceMethods(iface);
            methodMap.putAll(methods);
        }

        return methodMap;
    }

    private static Map<String, Method> wrapInterfaceMethods(Class<?> iface) {
        Map<String, Method> map = new HashMap<>();

        String ifaceName = iface.getSimpleName();

        Method[] methods = iface.getMethods();
        if (methods != null) {
            for (Method mtd : methods) {
                Class<?>[] params = mtd.getParameterTypes();
                List<String> paramsTypeName = new ArrayList<>();
                if (params != null && params.length > 0) {
                    for (Class<?> param : params) {
                        paramsTypeName.add(param.getSimpleName());
                    }
                }
                String methodKey = paramsTypeName.size() > 0 ? StringUtils.join(paramsTypeName, '_') : "";
                String key = StringUtils.join(new String[]{ifaceName, mtd.getName(), methodKey}, '.');
                map.put(key, mtd);
            }
        }

        return map;
    }

    public static void main(String[] args) {
        Map<String, Method> methodMap = wrapInterfacesMthods(TestIFace.class);
        for (Map.Entry<String, Method> entry : methodMap.entrySet()) {
            System.out.println(entry.getKey() + "::" + entry.getValue().toString());
        }
    }

    public interface TestIFace {
        void a(int b, String c);

        String s(String b, String c);

        String b();
    }
}
