package com.nail.core.transport;

import com.nail.core.entity.RemoteMessage;

import java.io.Closeable;

public interface ITransClient extends Closeable {
    boolean trans(RemoteMessage.Type type, byte[] data);
}
