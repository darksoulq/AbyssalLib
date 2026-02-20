package com.github.darksoulq.abyssallib.common.database.nosql.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class PipelineExecutor {
    private final Database database;
    private final List<Consumer<Pipeline>> operations = new ArrayList<>();

    public PipelineExecutor(Database database) {
        this.database = database;
    }

    public PipelineExecutor set(String key, String value) {
        operations.add(p -> p.set(key, value));
        return this;
    }

    public PipelineExecutor setex(String key, int seconds, String value) {
        operations.add(p -> p.setex(key, seconds, value));
        return this;
    }

    public PipelineExecutor del(String... keys) {
        operations.add(p -> p.del(keys));
        return this;
    }

    public PipelineExecutor hset(String key, String field, String value) {
        operations.add(p -> p.hset(key, field, value));
        return this;
    }

    public PipelineExecutor hset(String key, Map<String, String> hash) {
        operations.add(p -> p.hset(key, hash));
        return this;
    }

    public PipelineExecutor hdel(String key, String... fields) {
        operations.add(p -> p.hdel(key, fields));
        return this;
    }

    public PipelineExecutor lpush(String key, String... values) {
        operations.add(p -> p.lpush(key, values));
        return this;
    }

    public PipelineExecutor rpush(String key, String... values) {
        operations.add(p -> p.rpush(key, values));
        return this;
    }

    public PipelineExecutor sadd(String key, String... members) {
        operations.add(p -> p.sadd(key, members));
        return this;
    }

    public PipelineExecutor srem(String key, String... members) {
        operations.add(p -> p.srem(key, members));
        return this;
    }

    public PipelineExecutor expire(String key, int seconds) {
        operations.add(p -> p.expire(key, seconds));
        return this;
    }

    public void execute() {
        if (operations.isEmpty()) return;
        try (Jedis jedis = database.getResource()) {
            Pipeline pipeline = jedis.pipelined();
            for (Consumer<Pipeline> op : operations) {
                op.accept(pipeline);
            }
            pipeline.sync();
        }
    }

    public CompletableFuture<Void> executeAsync() {
        return CompletableFuture.runAsync(this::execute, database.getAsyncPool());
    }
}