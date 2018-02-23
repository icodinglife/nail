package com.nail.core.transport;

import com.nail.core.entity.RemoteMessage;

import java.io.Closeable;
import java.util.concurrent.CompletableFuture;

public interface ITransClient<V> extends Closeable {
    CompletableFuture<V> trans(RemoteMessage.Type type, byte[] data);
}
