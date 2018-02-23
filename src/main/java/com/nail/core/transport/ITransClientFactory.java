package com.nail.core.transport;

import java.util.concurrent.Executor;

public interface ITransClientFactory {
    ITransClient buildTransClient(String host, int port, Executor executor);
}
