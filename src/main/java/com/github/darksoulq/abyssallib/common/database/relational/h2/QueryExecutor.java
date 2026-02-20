package com.github.darksoulq.abyssallib.common.database.relational.h2;

import java.sql.Statement;

/**
 * A bridge for executing queries and builders against an H2 database.
 */
public class QueryExecutor {
    /** The parent H2 database instance. */
    private final Database database;

    /**
     * Constructs a QueryExecutor for H2.
     *
     * @param database The {@link Database} instance.
     */
    public QueryExecutor(Database database) {
        this.database = database;
    }

    /**
     * Starts a fluent query on a specific table.
     *
     * @param name Table name.
     * @return A {@link TableQuery} instance.
     */
    public TableQuery table(String name) {
        return new TableQuery(database, name);
    }

    /**
     * Starts a fluent builder to create a table.
     *
     * @param name Table name.
     * @return A {@link TableBuilder} instance.
     */
    public TableBuilder create(String name) {
        return new TableBuilder(database, name);
    }

    /**
     * Executes a raw SQL statement.
     *
     * @param sql The SQL string.
     * @throws Exception If execution fails.
     */
    public void executeRaw(String sql) throws Exception {
        try (Statement stmt = database.getConnection().createStatement()) {
            stmt.execute(sql);
        }
    }
}