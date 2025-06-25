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
     * Establishes a connection to the underlying database engine.
     * <p>
     * This method must be called before attempting to execute any queries.
     * </p>
     *
     * @throws Exception if the connection could not be established
     */
    void connect() throws Exception;

    /**
     * Closes the connection to the database.
     * <p>
     * Once disconnected, the {@link #executor()} must not be used until {@link #connect()} is called again.
     * </p>
     *
     * @throws Exception if an error occurs while closing the connection
     */
    void disconnect() throws Exception;

    /**
     * Provides access to a {@link QueryExecutor} used for executing SQL queries and managing schema.
     * <p>
     * The returned executor is tied to this database instance and should only be used after
     * a successful {@link #connect()} call.
     * </p>
     *
     * @return a {@link QueryExecutor} associated with the current database connection
     * @throws IllegalStateException if called before the database has been connected
     */
    QueryExecutor executor();
}
