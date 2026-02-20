package com.github.darksoulq.abyssallib.common.database.nosql.mongodb;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CollectionQuery {
    private final Database database;
    private final String collectionName;

    private enum Type { INSERT, REPLACE, UPDATE, DELETE }
    private Type type = Type.INSERT;

    private final Document values = new Document();
    private final Document filter = new Document();
    private Document sort = null;
    private Integer limit = null;
    private Integer skip = null;
    private boolean upsert = false;

    public CollectionQuery(Database database, String collectionName) {
        this.database = database;
        this.collectionName = collectionName;
    }

    public CollectionQuery insert() { this.type = Type.INSERT; return this; }
    public CollectionQuery replace() { this.type = Type.REPLACE; return this; }
    public CollectionQuery update() { this.type = Type.UPDATE; return this; }
    public CollectionQuery delete() { this.type = Type.DELETE; return this; }

    public CollectionQuery value(String key, Object value) { this.values.put(key, value); return this; }
    public CollectionQuery values(Document doc) { this.values.putAll(doc); return this; }

    public CollectionQuery filter(String key, Object value) { this.filter.put(key, value); return this; }
    public CollectionQuery filter(Document doc) { this.filter.putAll(doc); return this; }

    public CollectionQuery sort(String key, boolean ascending) {
        if (this.sort == null) this.sort = new Document();
        this.sort.put(key, ascending ? 1 : -1);
        return this;
    }

    public CollectionQuery limit(int limit) { this.limit = limit; return this; }
    public CollectionQuery skip(int skip) { this.skip = skip; return this; }
    public CollectionQuery upsert(boolean upsert) { this.upsert = upsert; return this; }

    public BatchQuery batch() {
        return new BatchQuery(database, collectionName);
    }

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

    public CompletableFuture<Long> executeAsync() {
        return CompletableFuture.supplyAsync(this::execute, database.getAsyncPool());
    }

    public long count() {
        return database.getDatabase().getCollection(collectionName).countDocuments(filter);
    }

    public boolean exists() {
        return count() > 0;
    }

    public <R> R first(DocumentMapper<R> mapper) {
        List<R> list = limit(1).select(mapper);
        return list.isEmpty() ? null : list.get(0);
    }

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

    public <R> CompletableFuture<List<R>> selectAsync(DocumentMapper<R> mapper) {
        return CompletableFuture.supplyAsync(() -> select(mapper), database.getAsyncPool());
    }
}