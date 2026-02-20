package com.github.darksoulq.abyssallib.common.database

import com.github.darksoulq.abyssallib.common.database.nosql.mongodb.Database as MongoDatabase
import com.github.darksoulq.abyssallib.common.database.nosql.mongodb.CollectionQuery
import com.github.darksoulq.abyssallib.common.database.nosql.mongodb.BatchQuery
import com.github.darksoulq.abyssallib.common.database.nosql.mongodb.DocumentMapper
import com.github.darksoulq.abyssallib.common.database.nosql.redis.Database as RedisDatabase
import com.github.darksoulq.abyssallib.common.database.nosql.redis.QueryExecutor
import com.github.darksoulq.abyssallib.common.database.nosql.redis.PipelineExecutor
import org.bson.Document
import kotlinx.coroutines.future.await

/**
 * Top-level factory function to initialize a MongoDB connection using Kotlin DSL.
 *
 * @param uri The MongoDB Connection String (e.g., "mongodb://user:pass@localhost:27017").
 * @param databaseName The target database name to perform operations on.
 * @param init A lambda with receiver [KMongoDatabase] for immediate configuration or connection.
 * @return A new [KMongoDatabase] wrapper instance.
 */
fun mongodb(uri: String, databaseName: String, init: KMongoDatabase.() -> Unit = {}): KMongoDatabase {
    val db = MongoDatabase(uri, databaseName)
    return KMongoDatabase(db).apply(init)
}

/**
 * Kotlin-idiomatic wrapper for the Java-based MongoDB [MongoDatabase].
 *
 * @property handle The internal Java [MongoDatabase] instance that manages the MongoClient.
 */
class KMongoDatabase(val handle: MongoDatabase) {

    /**
     * Triggers the connection logic of the underlying driver.
     * Starts the MongoClient and initializes connection pools.
     */
    fun connect() = handle.connect()

    /**
     * Closes the MongoClient connection and releases all associated resources.
     */
    fun disconnect() = handle.disconnect()

    /**
     * Resolves a specific MongoDB collection for querying.
     *
     * @param name The name of the collection in the database.
     * @return A [KMongoCollection] wrapper for DSL-based CRUD operations.
     */
    fun collection(name: String): KMongoCollection {
        return KMongoCollection(handle.executor().collection(name))
    }
}

/**
 * A DSL builder for performing operations on a MongoDB collection.
 * * This class wraps the [CollectionQuery] and provides both synchronous and
 * coroutine-based asynchronous methods.
 *
 * @property handle The underlying Java [CollectionQuery] builder used to construct the BSON command.
 */
class KMongoCollection(val handle: CollectionQuery) {

    /**
     * Inserts a new document into the collection synchronously.
     *
     * @param block A lambda to build the BSON document using [KMongoValues].
     * @return The number of documents affected (typically 1 on success).
     */
    fun insert(block: KMongoValues.() -> Unit): Long {
        handle.insert()
        val vals = KMongoValues().apply(block).doc
        handle.values(vals)
        return handle.execute()
    }

    /**
     * Inserts a new document into the collection asynchronously using Kotlin Coroutines.
     *
     * @param block A lambda to build the BSON document using [KMongoValues].
     * @return A [Long] result from the server via [await].
     */
    suspend fun insertAsync(block: KMongoValues.() -> Unit): Long {
        handle.insert()
        val vals = KMongoValues().apply(block).doc
        handle.values(vals)
        return handle.executeAsync().await()
    }

    /**
     * Replaces a document entirely (excluding _id) synchronously.
     *
     * @param upsert If true, inserts the document if no matching document is found.
     * @param filter A lambda to build the query document used to find the target.
     * @param block A lambda to build the new document content.
     * @return The number of documents matched/replaced.
     */
    fun replace(upsert: Boolean = false, filter: KMongoValues.() -> Unit, block: KMongoValues.() -> Unit): Long {
        handle.replace()
        handle.filter(KMongoValues().apply(filter).doc)
        handle.values(KMongoValues().apply(block).doc)
        handle.upsert(upsert)
        return handle.execute()
    }

    /**
     * Replaces a document entirely (excluding _id) asynchronously using Kotlin Coroutines.
     *
     * @param upsert If true, inserts the document if no matching document is found.
     * @param filter A lambda to build the query document used to find the target.
     * @param block A lambda to build the new document content.
     * @return The number of documents matched/replaced.
     */
    suspend fun replaceAsync(upsert: Boolean = false, filter: KMongoValues.() -> Unit, block: KMongoValues.() -> Unit): Long {
        handle.replace()
        handle.filter(KMongoValues().apply(filter).doc)
        handle.values(KMongoValues().apply(block).doc)
        handle.upsert(upsert)
        return handle.executeAsync().await()
    }

    /**
     * Performs a partial update on matching documents synchronously using the $set operator.
     *
     * @param upsert If true, creates a new document if the filter finds no matches.
     * @param filter A lambda defining which documents should be updated.
     * @param block A lambda defining the fields to be set or modified.
     * @return The count of modified documents.
     */
    fun update(upsert: Boolean = false, filter: KMongoValues.() -> Unit, block: KMongoValues.() -> Unit): Long {
        handle.update()
        handle.filter(KMongoValues().apply(filter).doc)
        handle.values(KMongoValues().apply(block).doc)
        handle.upsert(upsert)
        return handle.execute()
    }

    /**
     * Performs a partial update on matching documents asynchronously using Kotlin Coroutines.
     *
     * @param upsert If true, creates a new document if the filter finds no matches.
     * @param filter A lambda defining which documents should be updated.
     * @param block A lambda defining the fields to be set or modified.
     * @return The count of modified documents.
     */
    suspend fun updateAsync(upsert: Boolean = false, filter: KMongoValues.() -> Unit, block: KMongoValues.() -> Unit): Long {
        handle.update()
        handle.filter(KMongoValues().apply(filter).doc)
        handle.values(KMongoValues().apply(block).doc)
        handle.upsert(upsert)
        return handle.executeAsync().await()
    }

    /**
     * Deletes documents matching the filter synchronously.
     *
     * @param filter A lambda defining the selection criteria for deletion.
     * @return The number of documents removed.
     */
    fun delete(filter: KMongoValues.() -> Unit): Long {
        handle.delete()
        handle.filter(KMongoValues().apply(filter).doc)
        return handle.execute()
    }

    /**
     * Deletes documents matching the filter asynchronously using Kotlin Coroutines.
     *
     * @param filter A lambda defining the selection criteria for deletion.
     * @return The number of documents removed.
     */
    suspend fun deleteAsync(filter: KMongoValues.() -> Unit): Long {
        handle.delete()
        handle.filter(KMongoValues().apply(filter).doc)
        return handle.executeAsync().await()
    }

    /**
     * Counts the number of documents in the collection matching a filter.
     *
     * @param filter An optional lambda for query criteria; if null, counts all documents.
     * @return The total count of matching documents.
     */
    fun count(filter: (KMongoValues.() -> Unit)? = null): Long {
        if (filter != null) handle.filter(KMongoValues().apply(filter).doc)
        return handle.count()
    }

    /**
     * Checks if at least one document exists matching the filter.
     *
     * @param filter An optional lambda for query criteria.
     * @return True if document count > 0, false otherwise.
     */
    fun exists(filter: (KMongoValues.() -> Unit)? = null): Boolean {
        if (filter != null) handle.filter(KMongoValues().apply(filter).doc)
        return handle.exists()
    }

    /**
     * Finds and maps multiple documents from the collection synchronously.
     *
     * @param T The type to which the BSON [Document] will be mapped.
     * @param filter Optional search criteria.
     * @param sortKey Optional field name to sort the results by.
     * @param asc Direction of the sort; true for ascending, false for descending.
     * @param limit Optional maximum number of documents to return.
     * @param skip Optional number of documents to skip (for pagination).
     * @param mapper A lambda that converts a raw [Document] into an instance of [T].
     * @return A [List] of mapped objects.
     */
    fun <T> select(
        filter: (KMongoValues.() -> Unit)? = null,
        sortKey: String? = null,
        asc: Boolean = true,
        limit: Int? = null,
        skip: Int? = null,
        mapper: (Document) -> T
    ): List<T> {
        if (filter != null) handle.filter(KMongoValues().apply(filter).doc)
        if (sortKey != null) handle.sort(sortKey, asc)
        if (limit != null) handle.limit(limit)
        if (skip != null) handle.skip(skip)
        return handle.select(DocumentMapper<T> { mapper(it) })
    }

    /**
     * Finds and maps multiple documents from the collection asynchronously using Kotlin Coroutines.
     *
     * @param T The type to which the BSON [Document] will be mapped.
     * @param filter Optional search criteria.
     * @param sortKey Optional field name to sort the results by.
     * @param asc Direction of the sort; true for ascending, false for descending.
     * @param limit Optional maximum number of documents to return.
     * @param skip Optional number of documents to skip (for pagination).
     * @param mapper A lambda that converts a raw [Document] into an instance of [T].
     * @return A [List] of mapped objects.
     */
    suspend fun <T> selectAsync(
        filter: (KMongoValues.() -> Unit)? = null,
        sortKey: String? = null,
        asc: Boolean = true,
        limit: Int? = null,
        skip: Int? = null,
        mapper: (Document) -> T
    ): List<T> {
        if (filter != null) handle.filter(KMongoValues().apply(filter).doc)
        if (sortKey != null) handle.sort(sortKey, asc)
        if (limit != null) handle.limit(limit)
        if (skip != null) handle.skip(skip)
        return handle.selectAsync(DocumentMapper<T> { mapper(it) }).await()
    }

    /**
     * Finds the first document matching the filter and maps it to a Kotlin object.
     *
     * @param T The result type.
     * @param filter Optional search criteria.
     * @param mapper Transformation logic from BSON to [T].
     * @return The mapped object of type [T], or null if no document was found.
     */
    fun <T> first(filter: (KMongoValues.() -> Unit)? = null, mapper: (Document) -> T): T? {
        if (filter != null) handle.filter(KMongoValues().apply(filter).doc)
        return handle.first(DocumentMapper<T> { mapper(it) })
    }

    /**
     * Groups multiple write operations (inserts, updates, deletes) into a single batch execution synchronously.
     *
     * @param init A lambda with receiver [KMongoBatch] to queue various operations.
     * @return The total number of documents affected by the batch.
     */
    fun batch(init: KMongoBatch.() -> Unit): Long {
        val b = KMongoBatch(handle.batch())
        b.init()
        return b.execute()
    }

    /**
     * Groups multiple write operations (inserts, updates, deletes) into a single batch execution
     * asynchronously using Kotlin Coroutines.
     *
     * @param init A lambda with receiver [KMongoBatch] to queue various operations.
     * @return The total number of documents affected by the batch.
     */
    suspend fun batchAsync(init: KMongoBatch.() -> Unit): Long {
        val b = KMongoBatch(handle.batch())
        b.init()
        return b.executeAsync()
    }
}

/**
 * A builder utility used to construct MongoDB [Document] objects using idiomatic Kotlin.
 *
 * @property doc The underlying BSON [Document] being built.
 */
class KMongoValues {

    /** * The internal BSON storage for this builder context.
     */
    val doc = Document()

    /**
     * Map-like infix function to add a field to the document.
     * usage: `"name" to "John"`
     *
     * @param value The object to store (must be compatible with BSON serialization).
     */
    infix fun String.to(value: Any?) { doc.put(this, value) }

    /**
     * Operator overload to add a field to the document.
     * usage: `doc["age"] = 25`
     *
     * @param key The field name.
     * @param value The object to store.
     */
    operator fun set(key: String, value: Any?) { doc.put(key, value) }
}

/**
 * A DSL builder for performing bulk/batch operations in MongoDB.
 *
 * @property handle The internal Java [BatchQuery] that manages the bulk write buffer.
 */
class KMongoBatch(val handle: BatchQuery) {

    /**
     * Queues an insertion operation into the batch.
     *
     * @param block Lambda to build the document to insert.
     */
    fun insert(block: KMongoValues.() -> Unit) {
        handle.insert(KMongoValues().apply(block).doc)
    }

    /**
     * Queues a replacement operation into the batch.
     *
     * @param upsert If true, inserts a new document if the filter doesn't match.
     * @param filter Criteria to find the document.
     * @param update The new document data.
     */
    fun replace(upsert: Boolean = false, filter: KMongoValues.() -> Unit, update: KMongoValues.() -> Unit) {
        handle.replace(KMongoValues().apply(filter).doc, KMongoValues().apply(update).doc, upsert)
    }

    /**
     * Queues an update ($set) operation into the batch.
     *
     * @param upsert If true, inserts a new document if the filter doesn't match.
     * @param filter Criteria to find the target documents.
     * @param update The fields to modify.
     */
    fun update(upsert: Boolean = false, filter: KMongoValues.() -> Unit, update: KMongoValues.() -> Unit) {
        handle.update(KMongoValues().apply(filter).doc, KMongoValues().apply(update).doc, upsert)
    }

    /**
     * Queues a deletion operation into the batch.
     *
     * @param filter Criteria for documents to remove.
     */
    fun delete(filter: KMongoValues.() -> Unit) {
        handle.delete(KMongoValues().apply(filter).doc)
    }

    /**
     * Sends all queued operations to the server in a single bulk request synchronously.
     *
     * @return Total count of documents affected.
     */
    fun execute(): Long = handle.execute()

    /**
     * Sends all queued operations to the server in a single bulk request
     * asynchronously using Kotlin Coroutines.
     *
     * @return Total count of documents affected.
     */
    suspend fun executeAsync(): Long = handle.executeAsync().await()
}

/**
 * Top-level factory function to initialize a Redis connection using the Kotlin DSL.
 *
 * @param host The hostname or IP address of the Redis server.
 * @param port The port number the Redis server is listening on.
 * @param password The authentication password for the Redis server. Defaults to an empty string for no auth.
 * @param init A lambda with receiver [KRedisDatabase] for immediate configuration or connection logic.
 * @return A new [KRedisDatabase] wrapper instance.
 */
fun redis(host: String, port: Int, password: String = "", init: KRedisDatabase.() -> Unit = {}): KRedisDatabase {
    val db = RedisDatabase(host, port, password)
    return KRedisDatabase(db).apply(init)
}

/**
 * Kotlin-idiomatic wrapper for the Java-based Redis [RedisDatabase] class.
 *
 * @property handle The underlying Java [RedisDatabase] instance managing the Jedis/Lettuce client.
 */
class KRedisDatabase(val handle: RedisDatabase) {

    /**
     * Initializes the connection to the Redis server and sets up the internal client.
     */
    fun connect() = handle.connect()

    /**
     * Gracefully closes the Redis connection and releases associated resources.
     */
    fun disconnect() = handle.disconnect()

    /**
     * Provides access to the synchronous [QueryExecutor] for immediate command execution.
     * * @return The underlying Java [QueryExecutor] instance.
     */
    fun executor(): QueryExecutor = handle.executor()

    /**
     * Executes a group of commands as a single atomic-like batch using Redis Pipelining.
     * This method blocks until the pipeline execution is complete.
     *
     * @param init A lambda with receiver [KRedisPipeline] to queue commands.
     */
    fun pipeline(init: KRedisPipeline.() -> Unit) {
        val p = KRedisPipeline(handle.executor().pipeline())
        p.init()
        p.execute()
    }

    /**
     * Executes a group of commands as a single batch using Redis Pipelining asynchronously.
     * This utilizes Kotlin Coroutines to avoid blocking the calling thread.
     *
     * @param init A lambda with receiver [KRedisPipeline] to queue commands.
     */
    suspend fun pipelineAsync(init: KRedisPipeline.() -> Unit) {
        val p = KRedisPipeline(handle.executor().pipeline())
        p.init()
        p.executeAsync()
    }
}

/**
 * DSL wrapper for the [PipelineExecutor], allowing for fluent command batching.
 *
 * @property handle The underlying Java [PipelineExecutor] that buffers the commands.
 */
class KRedisPipeline(val handle: PipelineExecutor) {

    /**
     * Queues a SET command to store a string value at a specific key.
     * * @param key The key identifier.
     * @param value The string data to store.
     */
    fun set(key: String, value: String) { handle.set(key, value) }

    /**
     * Queues a SETEX command to store a string value with a specific TTL.
     * * @param key The key identifier.
     * @param seconds The time-to-live in seconds.
     * @param value The string data to store.
     */
    fun setex(key: String, seconds: Int, value: String) { handle.setex(key, seconds, value) }

    /**
     * Queues a DEL command to remove one or more keys.
     * * @param keys A variable number of keys to be deleted.
     */
    fun del(vararg keys: String) { handle.del(*keys) }

    /**
     * Queues an HSET command to set a single field in a hash.
     * * @param key The hash key.
     * @param field The specific field within the hash.
     * @param value The value to assign to the field.
     */
    fun hset(key: String, field: String, value: String) { handle.hset(key, field, value) }

    /**
     * Queues an HSET command to set multiple fields in a hash from a Map.
     * * @param key The hash key.
     * @param hash A [Map] containing field-value pairs to store.
     */
    fun hset(key: String, hash: Map<String, String>) { handle.hset(key, hash) }

    /**
     * Queues an HDEL command to remove specific fields from a hash.
     * * @param key The hash key.
     * @param fields A variable number of fields to remove.
     */
    fun hdel(key: String, vararg fields: String) { handle.hdel(key, *fields) }

    /**
     * Queues an LPUSH command to insert values at the head of a list.
     * * @param key The list key.
     * @param values A variable number of strings to push.
     */
    fun lpush(key: String, vararg values: String) { handle.lpush(key, *values) }

    /**
     * Queues an RPUSH command to insert values at the tail of a list.
     * * @param key The list key.
     * @param values A variable number of strings to push.
     */
    fun rpush(key: String, vararg values: String) { handle.rpush(key, *values) }

    /**
     * Queues an SADD command to add members to a set.
     * * @param key The set key.
     * @param members A variable number of members to add.
     */
    fun sadd(key: String, vararg members: String) { handle.sadd(key, *members) }

    /**
     * Queues an SREM command to remove members from a set.
     * * @param key The set key.
     * @param members A variable number of members to remove.
     */
    fun srem(key: String, vararg members: String) { handle.srem(key, *members) }

    /**
     * Queues an EXPIRE command to set a timeout on a key.
     * * @param key The key identifier.
     * @param seconds The number of seconds until expiration.
     */
    fun expire(key: String, seconds: Int) { handle.expire(key, seconds) }

    /**
     * Flushes all queued commands in the pipeline to the Redis server synchronously.
     */
    fun execute() = handle.execute()

    /**
     * Flushes all queued commands in the pipeline to the Redis server asynchronously.
     * This uses [await] to suspend the coroutine until the server acknowledges.
     */
    suspend fun executeAsync() { handle.executeAsync().await() }
}