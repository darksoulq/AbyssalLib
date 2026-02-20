package com.github.darksoulq.abyssallib.common.database.nosql.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Manages the connection lifecycle for a MongoDB NoSQL database.
 * <p>
 * This class handles the initialization of the {@link MongoClient}, provides access
 * to specific {@link MongoDatabase} instances, and maintains an asynchronous
 * thread pool for non-blocking operations.
 */
public class Database {
    /** The MongoDB connection URI (e.g., "mongodb://localhost:27017"). */
    private final String uri;
    /** The name of the database to interact with. */
    private final String databaseName;
    /** The executor service used for asynchronous query execution. */
    private final ExecutorService asyncPool;
    /** The underlying MongoDB client. */
    private MongoClient client;
    /** The specific MongoDB database instance. */
    private MongoDatabase database;

    /**
     * Constructs a new MongoDB Database handler.
     *
     * @param uri          The connection string.
     * @param databaseName The target database name.
     */
    public Database(String uri, String databaseName) {
        this.uri = uri;
        this.databaseName = databaseName;
        this.asyncPool = Executors.newCachedThreadPool();
    }

    /**
     * Establishes a connection to the MongoDB server and initializes the database instance.
     */
    public void connect() {
        client = MongoClients.create(uri);
        database = client.getDatabase(databaseName);
    }

    /**
     * Closes the MongoDB client connection and shuts down the asynchronous thread pool.
     */
    public void disconnect() {
        if (client != null) {
            client.close();
        }
        asyncPool.shutdown();
    }

    /**
     * Retrieves the active MongoDatabase instance.
     *
     * @return The {@link MongoDatabase} instance.
     */
    public MongoDatabase getDatabase() {
        return database;
    }

    /**
     * Gets the thread pool used for asynchronous operations.
     *
     * @return The {@link ExecutorService} instance.
     */
    public ExecutorService getAsyncPool() {
        return asyncPool;
    }

    /**
     * Creates a new QueryExecutor to perform operations on collections.
     *
     * @return A new {@link QueryExecutor}.
     */
    public QueryExecutor executor() {
        return new QueryExecutor(this);
    }
}