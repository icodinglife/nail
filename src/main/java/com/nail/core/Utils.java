package com.nail.core;

/**
 * Created by guofeng.qin on 2018/02/24.
 */
public class Utils {

    public static String getGroupName(Class<?> clazz) {
        if (clazz.isInterface() && clazz.getAnnotation(NailInterface.class) != null) {
            return clazz.getName();
        }

        Class<?>[] ifaces = clazz.getInterfaces();
        if (ifaces != null && ifaces.length > 0) {
            for (Class<?> iface : ifaces) {
                if (iface.getAnnotation(NailInterface.class) != null) {
                    return iface.getName();
                }
            }
        }

        return null;
    }
}
