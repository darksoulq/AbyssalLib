package com.github.darksoulq.abyssallib.common.database.relational.mariadb;

import java.sql.Statement;

/**
 * A MariaDB-aware query bridge that handles statement execution and
 * builder initialization.
 */
public class QueryExecutor {
    /** The parent database instance. */
    private final Database database;

    /**
     * Constructs a QueryExecutor linked to a MariaDB database.
     *
     * @param database The {@link Database} instance.
     */
    public QueryExecutor(Database database) {
        this.database = database;
    }

    /**
     * Initiates a query on a specific table.
     *
     * @param name The table name.
     * @return A {@link TableQuery} instance.
     */
    public TableQuery table(String name) {
        return new TableQuery(database, name);
    }

    /**
     * Initiates a table creation builder.
     *
     * @param name The table name.
     * @return A {@link TableBuilder} instance.
     */
    public TableBuilder create(String name) {
        return new TableBuilder(database, name);
    }

    /**
     * Executes a raw SQL statement string.
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