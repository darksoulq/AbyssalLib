package com.github.darksoulq.abyssallib.server.energy.impl;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

public final class DirectBufferPool {
    private static final int BUFFER_SIZE = 32;
    private static final int POOL_SIZE = 8;
    private static final ByteBuffer[] POOL = new ByteBuffer[POOL_SIZE];
    private static final AtomicInteger INDEX = new AtomicInteger(0);

    static {
        for (int i = 0; i < POOL_SIZE; i++) POOL[i] = ByteBuffer.allocateDirect(BUFFER_SIZE);
    }

    private DirectBufferPool() {}

    public static ByteBuffer acquire() {
        int idx = INDEX.getAndIncrement();
        ByteBuffer b = POOL[idx & (POOL_SIZE - 1)];
        if (b != null) {
            b.clear();
            return b;
        }
        return ByteBuffer.allocateDirect(BUFFER_SIZE);
    }

    public static void release(ByteBuffer buf) {
        if (buf == null || buf.capacity() != BUFFER_SIZE) return;
        int idx = INDEX.getAndIncrement() & (POOL_SIZE - 1);
        POOL[idx] = buf;
    }
}
