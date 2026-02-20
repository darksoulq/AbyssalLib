package com.github.darksoulq.abyssallib.common.database.relational.postgres;

import java.sql.Statement;

/**
 * A PostgreSQL-aware query dispatcher.
 * <p>
 * This class acts as a factory for table queries and builders, ensuring that
 * all operations are performed using the latest valid connection from the
 * associated {@link Database}.
 */
public class QueryExecutor {
    /** The PostgreSQL database instance providing the connection and thread pool. */
    private final Database database;

    /**
     * Constructs a new QueryExecutor.
     *
     * @param database The parent {@link Database} instance.
     */
    public QueryExecutor(Database database) {
        this.database = database;
    }

    /**
     * Starts a fluent query builder for a specific table.
     *
     * @param name The table name.
     * @return A new {@link TableQuery} instance.
     */
    public TableQuery table(String name) {
        return new TableQuery(database, name);
    }

    /**
     * Starts a fluent table schema builder.
     *
     * @param name The name of the table to create.
     * @return A new {@link TableBuilder} instance.
     */
    public TableBuilder create(String name) {
        return new TableBuilder(database, name);
    }

    /**
     * Executes a raw SQL string. This is typically used for administrative
     * tasks or non-standard queries.
     *
     * @param sql The raw SQL to execute.
     * @throws Exception If a database access error occurs.
     */
    public void executeRaw(String sql) throws Exception {
        try (Statement stmt = database.getConnection().createStatement()) {
            stmt.execute(sql);
        }
    }
}