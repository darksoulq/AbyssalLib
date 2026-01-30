package com.github.darksoulq.abyssallib.common.database.sql;

import java.sql.Connection;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;

/**
 * A bridge class used to spawn table builders and queries.
 * This class simplifies access to table-specific operations using the provided connection and pool.
 */
public class QueryExecutor {
    /** The connection used for all operations spawned by this executor. */
    private final Connection connection;
    /** The thread pool used for any asynchronous tasks spawned by the queries. */
    private final ExecutorService asyncPool;

    /**
     * Constructs a new QueryExecutor.
     *
     * @param connection The JDBC {@link Connection}.
     * @param asyncPool  The {@link ExecutorService}.
     */
    public QueryExecutor(Connection connection, ExecutorService asyncPool) {
        this.connection = connection;
        this.asyncPool = asyncPool;
    }

    /**
     * Starts a fluent query builder for a specific table.
     *
     * @param name The name of the table to query.
     * @return A new {@link TableQuery} instance.
     */
    public TableQuery table(String name) {
        return new TableQuery(connection, name, asyncPool);
    }

    /**
     * Starts a fluent table builder to define a new schema.
     *
     * @param name The name of the table to create.
     * @return A new {@link TableBuilder} instance.
     */
    public TableBuilder create(String name) {
        return new TableBuilder(connection, name);
    }

    /**
     * Executes a raw SQL string that does not require a result set.
     *
     * @param sql The raw SQL statement to execute.
     * @throws Exception If a database access error occurs.
     */
    public void executeRaw(String sql) throws Exception {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }
}