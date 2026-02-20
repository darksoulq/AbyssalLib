package com.github.darksoulq.abyssallib.common.database.nosql.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Database {
    private final String uri;
    private final String databaseName;
    private final ExecutorService asyncPool;
    private MongoClient client;
    private MongoDatabase database;

    public Database(String uri, String databaseName) {
        this.uri = uri;
        this.databaseName = databaseName;
        this.asyncPool = Executors.newCachedThreadPool();
    }

    public void connect() {
        client = MongoClients.create(uri);
        database = client.getDatabase(databaseName);
    }

    public void disconnect() {
        if (client != null) {
            client.close();
        }
        asyncPool.shutdown();
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public ExecutorService getAsyncPool() {
        return asyncPool;
    }

    public QueryExecutor executor() {
        return new QueryExecutor(this);
    }
}