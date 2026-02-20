package com.github.darksoulq.abyssallib.common.database.nosql.redis;

import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Provides a high-level API for executing standard Redis commands.
 * <p>
 * This class handles resource management (try-with-resources) for every call
 * and provides both synchronous and asynchronous variants of common commands.
 */
public class QueryExecutor {
    /** The parent database instance providing resources. */
    private final Database database;

    /**
     * Constructs a new QueryExecutor.
     *
     * @param database The target {@link Database}.
     */
    public QueryExecutor(Database database) {
        this.database = database;
    }

    /**
     * Sets a key to a specific value.
     *
     * @param key   The key name.
     * @param value The string value.
     */
    public void set(String key, String value) {
        try (Jedis jedis = database.getResource()) {
            jedis.set(key, value);
        }
    }

    /**
     * Sets a key with an expiration time.
     *
     * @param key     The key name.
     * @param seconds TTL in seconds.
     * @param value   The string value.
     */
    public void setex(String key, int seconds, String value) {
        try (Jedis jedis = database.getResource()) {
            jedis.setex(key, seconds, value);
        }
    }

    /**
     * Retrieves the value of a key.
     *
     * @param key The key name.
     * @return The string value, or null if not found.
     */
    public String get(String key) {
        try (Jedis jedis = database.getResource()) {
            return jedis.get(key);
        }
    }

    /**
     * Deletes one or more keys.
     *
     * @param keys The keys to remove.
     */
    public void del(String... keys) {
        try (Jedis jedis = database.getResource()) {
            jedis.del(keys);
        }
    }

    /**
     * Sets a field in a Redis hash.
     *
     * @param key   The hash key.
     * @param field The field name.
     * @param value The value.
     */
    public void hset(String key, String field, String value) {
        try (Jedis jedis = database.getResource()) {
            jedis.hset(key, field, value);
        }
    }

    /**
     * Sets multiple fields in a Redis hash using a map.
     *
     * @param key  The hash key.
     * @param hash The map of fields and values.
     */
    public void hset(String key, Map<String, String> hash) {
        try (Jedis jedis = database.getResource()) {
            jedis.hset(key, hash);
        }
    }

    /**
     * Gets a field value from a hash.
     *
     * @param key   The hash key.
     * @param field The field name.
     * @return The value, or null.
     */
    public String hget(String key, String field) {
        try (Jedis jedis = database.getResource()) {
            return jedis.hget(key, field);
        }
    }

    /**
     * Retrieves all fields and values from a hash.
     *
     * @param key The hash key.
     * @return A map of all entries in the hash.
     */
    public Map<String, String> hgetAll(String key) {
        try (Jedis jedis = database.getResource()) {
            return jedis.hgetAll(key);
        }
    }

    /**
     * Deletes fields from a hash.
     *
     * @param key    The hash key.
     * @param fields The fields to delete.
     */
    public void hdel(String key, String... fields) {
        try (Jedis jedis = database.getResource()) {
            jedis.hdel(key, fields);
        }
    }

    /**
     * Pushes values to the front (left) of a list.
     *
     * @param key    The list key.
     * @param values The values to push.
     */
    public void lpush(String key, String... values) {
        try (Jedis jedis = database.getResource()) {
            jedis.lpush(key, values);
        }
    }

    /**
     * Pushes values to the end (right) of a list.
     *
     * @param key    The list key.
     * @param values The values to push.
     */
    public void rpush(String key, String... values) {
        try (Jedis jedis = database.getResource()) {
            jedis.rpush(key, values);
        }
    }

    /**
     * Retrieves a range of elements from a list.
     *
     * @param key   The list key.
     * @param start The start index (0-based).
     * @param stop  The end index.
     * @return A list of strings within the range.
     */
    public List<String> lrange(String key, long start, long stop) {
        try (Jedis jedis = database.getResource()) {
            return jedis.lrange(key, start, stop);
        }
    }

    /**
     * Adds members to a set.
     *
     * @param key     The set key.
     * @param members The members to add.
     */
    public void sadd(String key, String... members) {
        try (Jedis jedis = database.getResource()) {
            jedis.sadd(key, members);
        }
    }

    /**
     * Retrieves all members of a set.
     *
     * @param key The set key.
     * @return A set of members.
     */
    public Set<String> smembers(String key) {
        try (Jedis jedis = database.getResource()) {
            return jedis.smembers(key);
        }
    }

    /**
     * Checks if a value is a member of a set.
     *
     * @param key    The set key.
     * @param member The value to check.
     * @return True if the member exists in the set.
     */
    public boolean sismember(String key, String member) {
        try (Jedis jedis = database.getResource()) {
            return jedis.sismember(key, member);
        }
    }

    /**
     * Checks if a key exists in the database.
     *
     * @param key The key to check.
     * @return True if it exists.
     */
    public boolean exists(String key) {
        try (Jedis jedis = database.getResource()) {
            return jedis.exists(key);
        }
    }

    /**
     * Sets a timeout on a key.
     *
     * @param key     The key.
     * @param seconds Timeout in seconds.
     */
    public void expire(String key, int seconds) {
        try (Jedis jedis = database.getResource()) {
            jedis.expire(key, seconds);
        }
    }

    /**
     * Sets a key value asynchronously.
     *
     * @return A future.
     */
    public CompletableFuture<Void> setAsync(String key, String value) {
        return CompletableFuture.runAsync(() -> set(key, value), database.getAsyncPool());
    }

    /**
     * Gets a key value asynchronously.
     *
     * @return A future containing the string.
     */
    public CompletableFuture<String> getAsync(String key) {
        return CompletableFuture.supplyAsync(() -> get(key), database.getAsyncPool());
    }

    /**
     * Sets a hash field asynchronously.
     *
     * @return A future.
     */
    public CompletableFuture<Void> hsetAsync(String key, String field, String value) {
        return CompletableFuture.runAsync(() -> hset(key, field, value), database.getAsyncPool());
    }

    /**
     * Gets a hash field value asynchronously.
     *
     * @return A future containing the string.
     */
    public CompletableFuture<String> hgetAsync(String key, String field) {
        return CompletableFuture.supplyAsync(() -> hget(key, field), database.getAsyncPool());
    }

    /**
     * Gets all fields from a hash asynchronously.
     *
     * @return A future containing the map.
     */
    public CompletableFuture<Map<String, String>> hgetAllAsync(String key) {
        return CompletableFuture.supplyAsync(() -> hgetAll(key), database.getAsyncPool());
    }

    /**
     * Deletes keys asynchronously.
     *
     * @return A future.
     */
    public CompletableFuture<Void> delAsync(String... keys) {
        return CompletableFuture.runAsync(() -> del(keys), database.getAsyncPool());
    }

    /**
     * Creates a new PipelineExecutor for batching operations starting from this executor.
     *
     * @return A new {@link PipelineExecutor}.
     */
    public PipelineExecutor pipeline() {
        return new PipelineExecutor(database);
    }
}