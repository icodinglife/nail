package com.nail.debug;

/**
 * Created by guofeng.qin on 2018/02/08.
 */
public class Debug {
    private static boolean debugable = false;

    public static void debug(Runnable runnable) {
        if (debugable) {
            runnable.run();
        }
    }

    public static void setDebugable(boolean debugable) {
        Debug.debugable = debugable;
    }
}
