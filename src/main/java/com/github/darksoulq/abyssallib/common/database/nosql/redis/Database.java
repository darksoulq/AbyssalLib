package com.github.darksoulq.abyssallib.common.database.nosql.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Manages the connection lifecycle and pooling for a Redis NoSQL database.
 * <p>
 * This class wraps a {@link JedisPool} to provide thread-safe access to Redis
 * and maintains an internal thread pool for executing non-blocking asynchronous commands.
 */
public class Database {
    /** The hostname of the Redis server. */
    private final String host;
    /** The port of the Redis server. */
    private final int port;
    /** The authentication password for the Redis server. */
    private final String password;
    /** The thread pool used for asynchronous database operations. */
    private final ExecutorService asyncPool;
    /** The underlying Jedis connection pool. */
    private JedisPool pool;

    /**
     * Constructs a new Redis Database handler.
     *
     * @param host     The server hostname.
     * @param port     The server port.
     * @param password The server password (can be null or empty for no auth).
     */
    public Database(String host, int port, String password) {
        this.host = host;
        this.port = port;
        this.password = password;
        this.asyncPool = Executors.newCachedThreadPool();
    }

    /**
     * Initializes the Jedis pool with a maximum of 128 connections.
     * <p>
     * Sets a timeout of 2000ms for connection attempts.
     */
    public void connect() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(128);
        if (password != null && !password.isEmpty()) {
            pool = new JedisPool(config, host, port, 2000, password);
        } else {
            pool = new JedisPool(config, host, port, 2000);
        }
    }

    /**
     * Gracefully shuts down the Jedis pool and the asynchronous thread pool.
     */
    public void disconnect() {
        if (pool != null) {
            pool.close();
        }
        asyncPool.shutdown();
    }

    /**
     * Borrows a Jedis instance from the pool.
     * <p>
     * <b>Note:</b> The returned resource must be closed (returned to pool) after use,
     * typically via a try-with-resources block.
     *
     * @return A {@link Jedis} resource.
     */
    public Jedis getResource() {
        return pool.getResource();
    }

    /**
     * Gets the thread pool used for async tasks.
     *
     * @return The {@link ExecutorService} instance.
     */
    public ExecutorService getAsyncPool() {
        return asyncPool;
    }

    /**
     * Creates a new QueryExecutor for performing Redis commands.
     *
     * @return A new {@link QueryExecutor}.
     */
    public QueryExecutor executor() {
        return new QueryExecutor(this);
    }
}