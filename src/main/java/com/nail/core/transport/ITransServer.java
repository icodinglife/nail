package com.nail.core.transport;

import java.io.Closeable;
import java.io.IOException;

public interface ITransServer extends Closeable {
    boolean start(int port) throws IOException;
}
