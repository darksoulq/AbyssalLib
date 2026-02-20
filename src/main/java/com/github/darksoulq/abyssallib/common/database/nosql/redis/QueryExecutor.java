package com.github.darksoulq.abyssallib.common.database.nosql.redis;

import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class QueryExecutor {
    private final Database database;

    public QueryExecutor(Database database) {
        this.database = database;
    }

    public void set(String key, String value) {
        try (Jedis jedis = database.getResource()) {
            jedis.set(key, value);
        }
    }

    public void setex(String key, int seconds, String value) {
        try (Jedis jedis = database.getResource()) {
            jedis.setex(key, seconds, value);
        }
    }

    public String get(String key) {
        try (Jedis jedis = database.getResource()) {
            return jedis.get(key);
        }
    }

    public void del(String... keys) {
        try (Jedis jedis = database.getResource()) {
            jedis.del(keys);
        }
    }

    public void hset(String key, String field, String value) {
        try (Jedis jedis = database.getResource()) {
            jedis.hset(key, field, value);
        }
    }

    public void hset(String key, Map<String, String> hash) {
        try (Jedis jedis = database.getResource()) {
            jedis.hset(key, hash);
        }
    }

    public String hget(String key, String field) {
        try (Jedis jedis = database.getResource()) {
            return jedis.hget(key, field);
        }
    }

    public Map<String, String> hgetAll(String key) {
        try (Jedis jedis = database.getResource()) {
            return jedis.hgetAll(key);
        }
    }

    public void hdel(String key, String... fields) {
        try (Jedis jedis = database.getResource()) {
            jedis.hdel(key, fields);
        }
    }

    public void lpush(String key, String... values) {
        try (Jedis jedis = database.getResource()) {
            jedis.lpush(key, values);
        }
    }

    public void rpush(String key, String... values) {
        try (Jedis jedis = database.getResource()) {
            jedis.rpush(key, values);
        }
    }

    public List<String> lrange(String key, long start, long stop) {
        try (Jedis jedis = database.getResource()) {
            return jedis.lrange(key, start, stop);
        }
    }

    public void sadd(String key, String... members) {
        try (Jedis jedis = database.getResource()) {
            jedis.sadd(key, members);
        }
    }

    public Set<String> smembers(String key) {
        try (Jedis jedis = database.getResource()) {
            return jedis.smembers(key);
        }
    }

    public boolean sismember(String key, String member) {
        try (Jedis jedis = database.getResource()) {
            return jedis.sismember(key, member);
        }
    }

    public boolean exists(String key) {
        try (Jedis jedis = database.getResource()) {
            return jedis.exists(key);
        }
    }

    public void expire(String key, int seconds) {
        try (Jedis jedis = database.getResource()) {
            jedis.expire(key, seconds);
        }
    }

    public CompletableFuture<Void> setAsync(String key, String value) {
        return CompletableFuture.runAsync(() -> set(key, value), database.getAsyncPool());
    }

    public CompletableFuture<String> getAsync(String key) {
        return CompletableFuture.supplyAsync(() -> get(key), database.getAsyncPool());
    }

    public CompletableFuture<Void> hsetAsync(String key, String field, String value) {
        return CompletableFuture.runAsync(() -> hset(key, field, value), database.getAsyncPool());
    }

    public CompletableFuture<String> hgetAsync(String key, String field) {
        return CompletableFuture.supplyAsync(() -> hget(key, field), database.getAsyncPool());
    }

    public CompletableFuture<Map<String, String>> hgetAllAsync(String key) {
        return CompletableFuture.supplyAsync(() -> hgetAll(key), database.getAsyncPool());
    }

    public CompletableFuture<Void> delAsync(String... keys) {
        return CompletableFuture.runAsync(() -> del(keys), database.getAsyncPool());
    }

    public PipelineExecutor pipeline() {
        return new PipelineExecutor(database);
    }
}