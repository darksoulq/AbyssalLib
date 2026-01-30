package com.github.darksoulq.abyssallib.common.database.mysql;

import java.sql.Statement;

/**
 * A utility class to facilitate the creation of MySQL table builders and queries.
 */
public class QueryExecutor {
    /** The MySQL database instance. */
    private final Database database;

    /**
     * @param database The {@link Database} manager to use for connections.
     */
    public QueryExecutor(Database database) {
        this.database = database;
    }

    /**
     * Creates a query builder for a table.
     * @param name The table name.
     * @return A {@link TableQuery} instance.
     */
    public TableQuery table(String name) {
        return new TableQuery(database, name);
    }

    /**
     * Creates a table creation builder.
     * @param name The table name.
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