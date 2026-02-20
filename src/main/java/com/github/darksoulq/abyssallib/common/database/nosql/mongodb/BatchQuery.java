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
 * This class allows queuing multiple different operation types (insert, replace, update, delete)
 * and executing them in a single network round-trip to optimize database performance.
 */
public class BatchQuery {

    /**
     * The parent database instance providing connection and pool context.
     */
    private final Database database;

    /**
     * The name of the collection to perform batch operations on.
     */
    private final String collectionName;

    /**
     * The list of write operations to be performed during the bulk execution.
     */
    private final List<WriteModel<Document>> operations = new ArrayList<>();

    /**
     * Constructs a new BatchQuery for a specific collection.
     *
     * @param database
     * The target {@link Database} instance.
     * @param collectionName
     * The name of the collection where operations will be applied.
     */
    public BatchQuery(Database database, String collectionName) {
        this.database = database;
        this.collectionName = collectionName;
    }

    /**
     * Queues an insert operation for a single document.
     *
     * @param document
     * The BSON {@link Document} to be inserted into the collection.
     * @return
     * This {@link BatchQuery} instance for fluent method chaining.
     */
    public BatchQuery insert(Document document) {
        operations.add(new InsertOneModel<>(document));
        return this;
    }

    /**
     * Queues a replace operation that swaps a document matching the filter with a new one.
     *
     * @param filter
     * The BSON {@link Document} defining the criteria to find the document to replace.
     * @param replacement
     * The new BSON {@link Document} that will take the place of the matched document.
     * @param upsert
     * If true, a new document is created if no document matches the filter.
     * @return
     * This {@link BatchQuery} instance for fluent method chaining.
     */
    public BatchQuery replace(Document filter, Document replacement, boolean upsert) {
        operations.add(new ReplaceOneModel<>(filter, replacement, new ReplaceOptions().upsert(upsert)));
        return this;
    }

    /**
     * Queues an update operation to modify multiple documents matching the filter.
     *
     * @param filter
     * The BSON {@link Document} defining which documents should be updated.
     * @param update
     * The BSON {@link Document} containing the fields and values to set.
     * @param upsert
     * If true, a new document is created if no documents match the filter.
     * @return
     * This {@link BatchQuery} instance for fluent method chaining.
     */
    public BatchQuery update(Document filter, Document update, boolean upsert) {
        operations.add(new UpdateManyModel<>(filter, new Document("$set", update), new UpdateOptions().upsert(upsert)));
        return this;
    }

    /**
     * Queues a delete operation to remove all documents matching the filter.
     *
     * @param filter
     * The BSON {@link Document} defining the criteria for documents to be removed.
     * @return
     * This {@link BatchQuery} instance for fluent method chaining.
     */
    public BatchQuery delete(Document filter) {
        operations.add(new DeleteManyModel<>(filter));
        return this;
    }

    /**
     * Executes all queued bulk write operations synchronously.
     *
     * @return
     * The combined total count of documents that were inserted, modified, or deleted.
     */
    public long execute() {
        if (operations.isEmpty()) {
            return 0;
        }
        MongoCollection<Document> collection = database.getDatabase().getCollection(collectionName);
        BulkWriteResult result = collection.bulkWrite(operations);
        return result.getInsertedCount() + result.getModifiedCount() + result.getDeletedCount();
    }

    /**
     * Executes all queued bulk write operations asynchronously using the database thread pool.
     *
     * @return
     * A {@link CompletableFuture} that will yield the total affected document count upon completion.
     */
    public CompletableFuture<Long> executeAsync() {
        return CompletableFuture.supplyAsync(this::execute, database.getAsyncPool());
    }
}