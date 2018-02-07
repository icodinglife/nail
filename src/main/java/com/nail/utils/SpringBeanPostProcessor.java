package com.nail.utils;

import co.paralleluniverse.actors.behaviors.ProxyServerActor;
import co.paralleluniverse.actors.behaviors.Server;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class SpringBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        NailComponent nailComponent = bean.getClass().getAnnotation(NailComponent.class);
        if (nailComponent != null) {
            ProxyServerActor psActor = new ProxyServerActor(bean.getClass().getName() + "@" + bean.hashCode(), false, bean);
            Server<?, ?, ?> server = psActor.spawn();
            System.out.println("After Init..." + beanName + ": " + bean.toString());
            return server;
        }
        return bean;
    }
}
