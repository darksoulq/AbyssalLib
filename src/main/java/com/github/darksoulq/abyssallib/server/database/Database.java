package com.github.darksoulq.abyssallib.server.database;

/**
 * Represents a general-purpose database connection interface.
 * <p>
 * Implementations of this interface are responsible for managing connections
 * to various database engines such as SQLite, MySQL, or others.
 * </p>
 */
public interface Database {

    /**
     * Establishes a connection to the database.
     *
     * @throws Exception if the connection cannot be established
     */
    void connect() throws Exception;

    /**
     * Closes the connection to the database.
     *
     * @throws Exception if an error occurs during disconnection
     */
    void disconnect() throws Exception;

    /**
     * Retrieves a {@link QueryExecutor} for executing queries and managing tables.
     * <p>
     * This method should only be called after a successful {@link #connect()}.
     * </p>
     *
     * @return a {@link QueryExecutor} instance tied to this database connection
     */
    QueryExecutor executor();
}
