package com.nail.core.transport;

public interface ITransClientFactory {
    ITransClient buildTransClient(String host, int port);
}
