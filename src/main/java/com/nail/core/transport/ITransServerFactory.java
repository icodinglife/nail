package com.nail.core.transport;

import com.nail.core.RemoteManager;

/**
 * Created by guofeng.qin on 2018/02/24.
 */
public interface ITransServerFactory {
    ITransServer buildTransServer(RemoteManager remoteManager);
}
