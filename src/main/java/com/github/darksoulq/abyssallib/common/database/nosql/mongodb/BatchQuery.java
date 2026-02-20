package com.github.darksoulq.abyssallib.common.database.nosql.mongodb;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Handles high-performance bulk write operations in MongoDB.
 * <p>
 * This class allows queuing multiple different operation types (insert, replace, etc.)
 * and executing them in a single network round-trip.
 */
public class BatchQuery {
    /** The parent database instance. */
    private final Database database;
    /** The name of the collection to perform batch operations on. */
    private final String collectionName;
    /** The list of write operations to be performed. */
    private final List<WriteModel<Document>> operations = new ArrayList<>();

    /**
     * Constructs a new BatchQuery.
     *
     * @param database       The target {@link Database}.
     * @param collectionName The collection name.
     */
    public BatchQuery(Database database, String collectionName) {
        this.database = database;
        this.collectionName = collectionName;
    }

    /** Queues an insert operation. @return this. */
    public BatchQuery insert(Document document) {
        operations.add(new InsertOneModel<>(document));
        return this;
    }

    /** Queues a replace operation. @return this. */
    public BatchQuery replace(Document filter, Document replacement, boolean upsert) {
        operations.add(new ReplaceOneModel<>(filter, replacement, new ReplaceOptions().upsert(upsert)));
        return this;
    }

    /** Queues an update operation. @return this. */
    public BatchQuery update(Document filter, Document update, boolean upsert) {
        operations.add(new UpdateManyModel<>(filter, new Document("$set", update), new UpdateOptions().upsert(upsert)));
        return this;
    }

    /** Queues a delete operation. @return this. */
    public BatchQuery delete(Document filter) {
        operations.add(new DeleteManyModel<>(filter));
        return this;
    }

    /**
     * Executes all queued bulk write operations synchronously.
     *
     * @return Total count of documents inserted, modified, and deleted.
     */
    public long execute() {
        if (operations.isEmpty()) return 0;
        MongoCollection<Document> collection = database.getDatabase().getCollection(collectionName);
        BulkWriteResult result = collection.bulkWrite(operations);
        return result.getInsertedCount() + result.getModifiedCount() + result.getDeletedCount();
    }

    /**
     * Executes all queued bulk write operations asynchronously.
     *
     * @return A future containing the total affected document count.
     */
    public CompletableFuture<Long> executeAsync() {
        return CompletableFuture.supplyAsync(this::execute, database.getAsyncPool());
    }
}