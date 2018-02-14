package com.nail.utils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class KryoHelper {
    private static final Logger logger = LoggerFactory.getLogger(KryoHelper.class);

    private static ThreadLocal<KryoEntry> localKryo = new ThreadLocal<>();
    private static final int DefaultBufferSize = 10 * 1024;
    private static final int MaxSize = 100 * 1024;

    private static final List<Consumer<Kryo>> kryoInitHook = new ArrayList<>();

    static {
        registerKryoInitHook((kryo) -> {
            // add hook for RuntimeException serializer.
            JavaSerializer javaSeri = new JavaSerializer();
            kryo.addDefaultSerializer(Throwable.class, new JavaSerializer());
            UnmodifiableCollectionsSerializer.registerSerializers(kryo);
        });
    }

    public static byte[] writeClassAndObject(Object obj) {
        KryoEntry kryoEntry = localKryo.get();
        if (kryoEntry == null) {
            kryoEntry = new KryoEntry(new Kryo(), new Output(DefaultBufferSize));
            localKryo.set(kryoEntry);
        }
        Kryo kryo = kryoEntry.kryo;
        Output out = kryoEntry.out;
        out.clear();

        int retryCount = 0;
        while (retryCount <= 10) {
            retryCount++;
            try {
                kryo.writeClassAndObject(out, obj);
                return out.toBytes();
            } catch (KryoException e) {
                // e.printStackTrace();
                Output temp = out;
                int total = temp.position();
                if (total >= MaxSize) {
                    logger.error("kryo seri reach max size !!!!!!");
                    return null;
                }
                out = new Output(total * 2);
                kryoEntry.out = out;
                temp.clear();

            }
        }
        logger.error("kryo seri error, size: {}, {}", retryCount, out.position());
        return null;
    }

    public static <T> T readClassAndObject(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        KryoEntry kryoEntry = localKryo.get();
        if (kryoEntry == null) {
            kryoEntry = new KryoEntry(new Kryo(), new Output(DefaultBufferSize));
            localKryo.set(kryoEntry);
        }
        Kryo kryo = kryoEntry.kryo;
        Input input = new Input(bytes);
        try {
            return (T) kryo.readClassAndObject(input);
        } catch (KryoException e) {
            logger.error("Kryo descri Error", e);
        }
        logger.error("kryo deseri error !!!!!!");
        return null;
    }

    public static void registerKryoInitHook(Consumer<Kryo> hook) {
        kryoInitHook.add(hook);
    }

    static final class KryoEntry {
        public Kryo kryo;
        public Output out;

        public KryoEntry(Kryo kryo, Output out) {
            this.kryo = kryo;
            this.out = out;
            initKryo();
        }

        private void initKryo() {
            if (kryoInitHook.size() > 0) {
                for (Consumer<Kryo> hook : kryoInitHook) {
                    hook.accept(kryo);
                }
            }
        }
    }
}
