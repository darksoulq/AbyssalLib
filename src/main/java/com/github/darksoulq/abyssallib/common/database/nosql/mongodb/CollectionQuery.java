package com.github.darksoulq.abyssallib.common.database.nosql.mongodb;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A fluent builder for executing CRUD operations on a single MongoDB collection.
 */
public class CollectionQuery {
    /** The parent database instance. */
    private final Database database;
    /** The name of the collection being queried. */
    private final String collectionName;

    /** Supported operation types. */
    private enum Type { INSERT, REPLACE, UPDATE, DELETE }
    /** The current operation type, defaults to INSERT. */
    private Type type = Type.INSERT;

    /** The values/data to be written to the database. */
    private final Document values = new Document();
    /** The filter criteria used to select documents. */
    private final Document filter = new Document();
    /** The sorting criteria. */
    private Document sort = null;
    /** Maximum number of documents to return. */
    private Integer limit = null;
    /** Number of documents to skip in the result set. */
    private Integer skip = null;
    /** Whether to perform an upsert (insert if not found) during update/replace. */
    private boolean upsert = false;

    /**
     * Constructs a new CollectionQuery.
     *
     * @param database       The target {@link Database}.
     * @param collectionName The name of the collection.
     */
    public CollectionQuery(Database database, String collectionName) {
        this.database = database;
        this.collectionName = collectionName;
    }

    /** Sets operation type to INSERT. @return this. */
    public CollectionQuery insert() { this.type = Type.INSERT; return this; }
    /** Sets operation type to REPLACE. @return this. */
    public CollectionQuery replace() { this.type = Type.REPLACE; return this; }
    /** Sets operation type to UPDATE. @return this. */
    public CollectionQuery update() { this.type = Type.UPDATE; return this; }
    /** Sets operation type to DELETE. @return this. */
    public CollectionQuery delete() { this.type = Type.DELETE; return this; }

    /** Adds a key-value pair to the write payload. @return this. */
    public CollectionQuery value(String key, Object value) { this.values.put(key, value); return this; }
    /** Adds all fields from a document to the write payload. @return this. */
    public CollectionQuery values(Document doc) { this.values.putAll(doc); return this; }

    /** Adds a filter criterion. @return this. */
    public CollectionQuery filter(String key, Object value) { this.filter.put(key, value); return this; }
    /** Adds multiple filter criteria. @return this. */
    public CollectionQuery filter(Document doc) { this.filter.putAll(doc); return this; }

    /**
     * Sets the sort order.
     * @param key The field to sort by.
     * @param ascending True for 1, false for -1.
     * @return this.
     */
    public CollectionQuery sort(String key, boolean ascending) {
        if (this.sort == null) this.sort = new Document();
        this.sort.put(key, ascending ? 1 : -1);
        return this;
    }

    /** Sets the result limit. @return this. */
    public CollectionQuery limit(int limit) { this.limit = limit; return this; }
    /** Sets the result skip count. @return this. */
    public CollectionQuery skip(int skip) { this.skip = skip; return this; }
    /** Sets the upsert flag. @return this. */
    public CollectionQuery upsert(boolean upsert) { this.upsert = upsert; return this; }

    /**
     * Transitions into a batch operation for bulk writes.
     *
     * @return A new {@link BatchQuery} instance.
     */
    public BatchQuery batch() {
        return new BatchQuery(database, collectionName);
    }

    /**
     * Executes the configured operation synchronously.
     *
     * @return The count of affected documents.
     */
    public long execute() {
        MongoCollection<Document> collection = database.getDatabase().getCollection(collectionName);
        switch (type) {
            case INSERT -> {
                collection.insertOne(values);
                return 1;
            }
            case REPLACE -> {
                return collection.replaceOne(filter, values, new ReplaceOptions().upsert(upsert)).getModifiedCount();
            }
            case UPDATE -> {
                return collection.updateMany(filter, new Document("$set", values), new UpdateOptions().upsert(upsert)).getModifiedCount();
            }
            case DELETE -> {
                return collection.deleteMany(filter).getDeletedCount();
            }
        }
        return 0;
    }

    /**
     * Executes the configured operation asynchronously.
     *
     * @return A future containing the affected document count.
     */
    public CompletableFuture<Long> executeAsync() {
        return CompletableFuture.supplyAsync(this::execute, database.getAsyncPool());
    }

    /**
     * Counts documents matching the filter.
     *
     * @return Total count.
     */
    public long count() {
        return database.getDatabase().getCollection(collectionName).countDocuments(filter);
    }

    /**
     * Checks if any documents match the filter.
     *
     * @return True if count > 0.
     */
    public boolean exists() {
        return count() > 0;
    }

    /**
     * Retrieves the first matching document and maps it.
     *
     * @param <R>    Result type.
     * @param mapper The mapper logic.
     * @return The mapped object or null.
     */
    public <R> R first(DocumentMapper<R> mapper) {
        List<R> list = limit(1).select(mapper);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * Retrieves all matching documents and maps them to a list.
     *
     * @param <R>    Result type.
     * @param mapper The mapper logic.
     * @return A list of mapped results.
     */
    public <R> List<R> select(DocumentMapper<R> mapper) {
        List<R> results = new ArrayList<>();
        FindIterable<Document> find = database.getDatabase().getCollection(collectionName).find(filter);
        if (sort != null) find.sort(sort);
        if (skip != null) find.skip(skip);
        if (limit != null) find.limit(limit);

        for (Document doc : find) {
            try {
                results.add(mapper.map(doc));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return results;
    }

    /**
     * Retrieves all matching documents asynchronously.
     *
     * @param <R>    Result type.
     * @param mapper The mapper logic.
     * @return A future containing the result list.
     */
    public <R> CompletableFuture<List<R>> selectAsync(DocumentMapper<R> mapper) {
        return CompletableFuture.supplyAsync(() -> select(mapper), database.getAsyncPool());
    }
}