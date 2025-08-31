package com.github.darksoulq.abyssallib.common.database;

/**
 * Provides methods to execute database operations such as creating and manipulating tables,
 * as well as executing raw SQL statements.
 * <p>
 * Each {@code QueryExecutor} instance is tied to a specific {@link Database} connection.
 * </p>
 */
public interface QueryExecutor {

    /**
     * Creates or accesses a query builder for the given table name.
     * <p>
     * The returned {@link TableQuery} allows for creating, inserting, updating, and deleting rows,
     * as well as defining table structure.
     * </p>
     *
     * @param name the name of the table
     * @return a {@link TableQuery} for the specified table
     */
    TableQuery table(String name);

    /**
     * Executes a raw SQL statement directly against the database.
     * <p>
     * This is useful for advanced operations not covered by the query abstraction layer.
     * </p>
     *
     * @param sql the raw SQL string to execute
     * @throws Exception if an error occurs during execution
     */
    void executeRaw(String sql) throws Exception;
}
