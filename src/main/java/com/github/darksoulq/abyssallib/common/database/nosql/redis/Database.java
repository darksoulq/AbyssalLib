package com.github.darksoulq.abyssallib.common.database.nosql.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Database {
    private final String host;
    private final int port;
    private final String password;
    private final ExecutorService asyncPool;
    private JedisPool pool;

    public Database(String host, int port, String password) {
        this.host = host;
        this.port = port;
        this.password = password;
        this.asyncPool = Executors.newCachedThreadPool();
    }

    public void connect() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(128);
        if (password != null && !password.isEmpty()) {
            pool = new JedisPool(config, host, port, 2000, password);
        } else {
            pool = new JedisPool(config, host, port, 2000);
        }
    }

    public void disconnect() {
        if (pool != null) {
            pool.close();
        }
        asyncPool.shutdown();
    }

    public Jedis getResource() {
        return pool.getResource();
    }

    public ExecutorService getAsyncPool() {
        return asyncPool;
    }

    public QueryExecutor executor() {
        return new QueryExecutor(this);
    }
}