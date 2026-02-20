package com.github.darksoulq.abyssallib.common.database.nosql.mongodb;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BatchQuery {
    private final Database database;
    private final String collectionName;
    private final List<WriteModel<Document>> operations = new ArrayList<>();

    public BatchQuery(Database database, String collectionName) {
        this.database = database;
        this.collectionName = collectionName;
    }

    public BatchQuery insert(Document document) {
        operations.add(new InsertOneModel<>(document));
        return this;
    }

    public BatchQuery replace(Document filter, Document replacement, boolean upsert) {
        operations.add(new ReplaceOneModel<>(filter, replacement, new ReplaceOptions().upsert(upsert)));
        return this;
    }

    public BatchQuery update(Document filter, Document update, boolean upsert) {
        operations.add(new UpdateManyModel<>(filter, new Document("$set", update), new UpdateOptions().upsert(upsert)));
        return this;
    }

    public BatchQuery delete(Document filter) {
        operations.add(new DeleteManyModel<>(filter));
        return this;
    }

    public long execute() {
        if (operations.isEmpty()) return 0;
        MongoCollection<Document> collection = database.getDatabase().getCollection(collectionName);
        BulkWriteResult result = collection.bulkWrite(operations);
        return result.getInsertedCount() + result.getModifiedCount() + result.getDeletedCount();
    }

    public CompletableFuture<Long> executeAsync() {
        return CompletableFuture.supplyAsync(this::execute, database.getAsyncPool());
    }
}