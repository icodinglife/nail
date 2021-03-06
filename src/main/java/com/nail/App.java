package com.nail;

import co.paralleluniverse.actors.BasicActor;
import co.paralleluniverse.actors.behaviors.ProxyServerActor;
import co.paralleluniverse.actors.behaviors.Server;
import co.paralleluniverse.fibers.*;
import com.alibaba.fastjson.JSON;
import com.google.common.net.HostAndPort;
import com.nail.core.NailConfig;
import com.nail.core.NailContext;
import com.nail.core.registry.Registry;
import com.nail.core.registry.Service;
import com.nail.core.registry.ServiceDiscovery;
import com.nail.core.registry.zk.ZKRegistry;
import com.nail.utils.NailComponent;
import com.nail.utils.SpringBeanPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class App {
    /*
    static public Integer doAll() throws ExecutionException, InterruptedException {
        final IntChannel increasingToEcho = Channels.newIntChannel(0); // Synchronizing channel (buffer = 0)
        final IntChannel echoToIncreasing = Channels.newIntChannel(0); // Synchronizing channel (buffer = 0)

        Fiber<Integer> increasing = new Fiber<>("INCREASER", new SuspendableCallable<Integer>() {
            @Override
            public Integer run() throws SuspendExecution, InterruptedException {
                ////// The following is enough to test instrumentation of synchronizing methods
                // synchronized(new Object()) {}

                int curr = 0;
                for (int i = 0; i < 10; i++) {
                    Fiber.sleep(10);
                    System.out.println("INCREASER sending: " + curr);
                    increasingToEcho.send(curr);
                    curr = echoToIncreasing.receive();
                    System.out.println("INCREASER received: " + curr);
                    curr++;
                    System.out.println("INCREASER now: " + curr);
                }
                System.out.println("INCREASER closing channel and exiting");
                increasingToEcho.close();
                return curr;
            }
        }).start();

        Fiber<Void> echo = new Fiber<Void>("ECHO", new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution, InterruptedException {
                Integer curr;
                while (true) {
                    Fiber.sleep(1000);
                    curr = increasingToEcho.receive();
                    System.out.println("ECHO received: " + curr);

                    if (curr != null) {
                        System.out.println("ECHO sending: " + curr);
                        echoToIncreasing.send(curr);
                    } else {
                        System.out.println("ECHO detected closed channel, closing and exiting");
                        echoToIncreasing.close();
                        return;
                    }
                }
            }
        }).start();

        try {
            increasing.join();
            echo.join();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return increasing.get();
    }
*/

    static class Message {
        String name;
        String value;

        public Message() {
        }

        public Message(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }

    public interface AT {
        String call(String param);
    }

    static class ActorTest extends BasicActor<Message, Void> {
        @Override
        protected Void doRun() throws InterruptedException, SuspendExecution {
            while (true) {
                Message msg = receive();
                System.out.println(msg.name + ":" + msg.value);
            }
        }
    }

    @NailComponent
    public static class CallTest implements AT {
        @Suspendable
        @Override
        public String call(String param) {
//            System.out.println("Before Fiber: " + param + " " + Fiber.currentFiber().toString() + "  " + Thread.currentThread().toString());
//            try {
//                Fiber.sleep(5 * 1000L);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (SuspendExecution suspendExecution) {
//                suspendExecution.printStackTrace();
//            }
//            System.out.println("After Fiber: " + param + " " + Fiber.currentFiber().toString() + "  " + Thread.currentThread().toString());
            return "Call:" + param;
        }
    }

    static void testProxyServer() {
        FiberScheduler fiberScheduler = new FiberForkJoinScheduler("AA", 1, null, false);

        CallTest ct = new CallTest();
        ProxyServerActor psActor = new ProxyServerActor("test actor", false, ct);
        Server<?, ?, ?> server = psActor.spawn((FiberFactory) fiberScheduler);

        Fiber<Void> fiber = new Fiber<>(fiberScheduler, () -> {
            String res = ((AT) server).call("TestFunc");
            System.out.println("Call Result: " + res);
        });
        fiber.start();

        Fiber<Void> fiber1 = new Fiber<>(fiberScheduler, () -> {
            String res = ((AT) server).call("TestFunc1");
            System.out.println("Call Result: " + res);
        });
        fiber1.start();

        try {
            fiber.join();
            fiber1.join();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Configuration
    public static class Cfg {
//        @Bean
//        public AT getCallTest() {
//            CallTest ct = new CallTest();
//            ProxyServerActor psActor = new ProxyServerActor("test actor", false, ct);
//            Server<?, ?, ?> server = psActor.spawn();
//            return (AT) server;
//        }

        @Bean
        public BeanPostProcessor getBeanPostProcessor() {
            return new SpringBeanPostProcessor();
        }
    }

    private static ApplicationContext appCtx;

    static public void main(String[] args) throws ExecutionException, InterruptedException {
//        doAll();
        testProxyServer();
//         appCtx = SpringApplication.run(App.class, args);
//         testAll();
//
//         try {
//             System.in.read();
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
    }

    private static void testAll() {
        String zone = "";
        String nodeName = "test";
        String host = "127.0.0.1";
        int port = 16666;
        NailConfig config = new NailConfig(zone, nodeName, host, port, 2, "127.0.0.1:2181");
        NailContext ctx = new NailContext();
        ctx.init(config);

        Registry registry = new ZKRegistry();
        List<HostAndPort> zkHosts = new ArrayList<>();
        zkHosts.add(HostAndPort.fromParts("127.0.0.1", 2181));
        registry.init(zkHosts);

        ServiceDiscovery serviceDiscovery = new ServiceDiscovery();
        serviceDiscovery.init(registry, null, zone, nodeName, host, port);

        Service service = new Service("TestService", "TestService1", nodeName);
        serviceDiscovery.registerService(zone, service);

        new Thread(() -> {
            try {
                Thread.sleep(5 * 1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(JSON.toJSONString(serviceDiscovery.getNode(nodeName)));
            System.out.println(JSON.toJSONString(serviceDiscovery.getService("TestService", "TestService1")));
        }).start();
    }
}
