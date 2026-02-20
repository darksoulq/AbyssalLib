package com.github.darksoulq.abyssallib.common.database.nosql.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * A fluent builder for executing multiple Redis commands in a single batch (Pipelining).
 * <p>
 * Pipelining significantly improves performance by sending multiple commands to
 * the server without waiting for individual replies.
 */
public class PipelineExecutor {
    /** The parent database instance. */
    private final Database database;
    /** The list of queued operations to be executed on the pipeline. */
    private final List<Consumer<Pipeline>> operations = new ArrayList<>();

    /**
     * Constructs a new PipelineExecutor.
     *
     * @param database The target {@link Database}.
     */
    public PipelineExecutor(Database database) {
        this.database = database;
    }

    /**
     * Queues a SET command.
     *
     * @param key   The key.
     * @param value The value.
     * @return This executor instance.
     */
    public PipelineExecutor set(String key, String value) {
        operations.add(p -> p.set(key, value));
        return this;
    }

    /**
     * Queues a SETEX (Set with Expiry) command.
     *
     * @param key     The key.
     * @param seconds Expiration in seconds.
     * @param value   The value.
     * @return This executor instance.
     */
    public PipelineExecutor setex(String key, int seconds, String value) {
        operations.add(p -> p.setex(key, seconds, value));
        return this;
    }

    /**
     * Queues a DEL command for one or more keys.
     *
     * @param keys The keys to delete.
     * @return This executor instance.
     */
    public PipelineExecutor del(String... keys) {
        operations.add(p -> p.del(keys));
        return this;
    }

    /**
     * Queues a Hash SET command for a single field.
     *
     * @param key   The hash key.
     * @param field The field name.
     * @param value The field value.
     * @return This executor instance.
     */
    public PipelineExecutor hset(String key, String field, String value) {
        operations.add(p -> p.hset(key, field, value));
        return this;
    }

    /**
     * Queues a Hash SET command for multiple fields.
     *
     * @param key  The hash key.
     * @param hash A map of fields and values.
     * @return This executor instance.
     */
    public PipelineExecutor hset(String key, Map<String, String> hash) {
        operations.add(p -> p.hset(key, hash));
        return this;
    }

    /**
     * Queues a Hash DEL command.
     *
     * @param key    The hash key.
     * @param fields The fields to remove.
     * @return This executor instance.
     */
    public PipelineExecutor hdel(String key, String... fields) {
        operations.add(p -> p.hdel(key, fields));
        return this;
    }

    /**
     * Queues a List Left Push command.
     *
     * @param key    The list key.
     * @param values The values to push.
     * @return This executor instance.
     */
    public PipelineExecutor lpush(String key, String... values) {
        operations.add(p -> p.lpush(key, values));
        return this;
    }

    /**
     * Queues a List Right Push command.
     *
     * @param key    The list key.
     * @param values The values to push.
     * @return This executor instance.
     */
    public PipelineExecutor rpush(String key, String... values) {
        operations.add(p -> p.rpush(key, values));
        return this;
    }

    /**
     * Queues a Set Add command.
     *
     * @param key     The set key.
     * @param members The members to add.
     * @return This executor instance.
     */
    public PipelineExecutor sadd(String key, String... members) {
        operations.add(p -> p.sadd(key, members));
        return this;
    }

    /**
     * Queues a Set Remove command.
     *
     * @param key     The set key.
     * @param members The members to remove.
     * @return This executor instance.
     */
    public PipelineExecutor srem(String key, String... members) {
        operations.add(p -> p.srem(key, members));
        return this;
    }

    /**
     * Queues an EXPIRE command.
     *
     * @param key     The key.
     * @param seconds Time to live in seconds.
     * @return This executor instance.
     */
    public PipelineExecutor expire(String key, int seconds) {
        operations.add(p -> p.expire(key, seconds));
        return this;
    }

    /**
     * Executes all queued operations synchronously.
     */
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

    /**
     * Executes all queued operations asynchronously using the database thread pool.
     *
     * @return A {@link CompletableFuture} representing the pending execution.
     */
    public CompletableFuture<Void> executeAsync() {
        return CompletableFuture.runAsync(this::execute, database.getAsyncPool());
    }
}