package com.nail.test;

import com.nail.core.NailApp;
import com.nail.core.NailConfig;
import com.nail.core.ServiceManager;
import com.nail.core.quasar.RemoteProxy;
import com.nail.core.transport.ITransClientFactory;
import com.nail.core.transport.ITransServerFactory;
import com.nail.core.transport.grpc.GrpcClientFactory;
import com.nail.core.transport.grpc.GrpcServerFactory;
import com.nail.test.service.ITestService;
import com.nail.test.service.impl.TestServiceImpl;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

/**
 * Created by guofeng.qin on 2018/02/24.
 */
@SpringBootApplication
public class TestApp {
    public static void main(String[] args) {
        NailConfig nailConfig = new NailConfig("", "TestNode", "192.168.7.178", 6666, 3, "127.0.0.1:2181");
        ITransServerFactory serverFactory = new GrpcServerFactory();
        ITransClientFactory clientFactory = new GrpcClientFactory();

        NailApp app = new NailApp(nailConfig, serverFactory, clientFactory);

        ITestService testService = new TestServiceImpl();

        ServiceManager serviceManager = app.getServiceManager();

        serviceManager.deployService(testService);

//        ITestService remoteService = RemoteProxy.make(ITestService.class);
//        remoteService.testvoid();

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
