package com.github.darksoulq.abyssallib.common.database.nosql.mongodb;

/**
 * A bridge class for accessing MongoDB collection-specific operations.
 */
public class QueryExecutor {
    /** The parent database instance. */
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
     * Starts a fluent query builder for a specific collection.
     *
     * @param name The name of the MongoDB collection.
     * @return A new {@link CollectionQuery} instance.
     */
    public CollectionQuery collection(String name) {
        return new CollectionQuery(database, name);
    }
}