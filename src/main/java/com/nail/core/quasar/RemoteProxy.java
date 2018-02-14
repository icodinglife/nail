package com.nail.core.quasar;

import co.paralleluniverse.fibers.Suspendable;
import com.alibaba.fastjson.JSON;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RemoteProxy {
    private static final Logger logger = LoggerFactory.getLogger(RemoteProxy.class);

    private static final Map<Class<?>, Class<?>> classes = new ConcurrentHashMap<>();
    private static final RemoteProxyImpl remoteProxyImpl = new RemoteProxyImpl();

    public static <T> T make(Class<T> iface) {
        Class<?> clazz = classes.get(iface);
        if (clazz == null) {
            clazz = new ByteBuddy()
                    .subclass(RemoteProxyTarget.class)
                    .implement(iface)
                    .method(ElementMatchers.isDeclaredBy(ElementMatchers.anyOf(iface)))
                    .intercept(InvocationHandlerAdapter.of(remoteProxyImpl))
                    .make()
                    .load(RemoteProxy.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER).getLoaded();
            Class<?> old = classes.putIfAbsent(iface, clazz);
            if (old != null) {
                clazz = old;
            }
        }

        try {
            return (T) clazz.getConstructor(Class.class).newInstance(iface);
        } catch (Exception e) {
            logger.error(iface.getName() + " RemoteProxy make error.", e);
        }

        return null;
    }

    public static class RemoteProxyTarget {
        private Class<?> target;

        public RemoteProxyTarget(Class<?> target) {
            this.target = target;
        }

        public Class<?> getTarget() {
            return target;
        }
    }

    private static class RemoteProxyImpl implements InvocationHandler, Serializable {

        @Override
        @Suspendable
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println(((RemoteProxyTarget) proxy).getTarget().toString() + " " + method.toString() + " Invoked: " + JSON.toJSONString(args));
            // TODO ... Send The Method Invoke Request To Remote...
            return null;
        }
    }

    public static interface TestFace {
        String test1(String abc);

        void test2();

        String test3(String abc, int bcd);
    }

    public static void main(String[] args) {
        TestFace tf = make(TestFace.class);
        tf.test1("test1");
        tf.test2();
        tf.test3("test3", 3);
    }
}
