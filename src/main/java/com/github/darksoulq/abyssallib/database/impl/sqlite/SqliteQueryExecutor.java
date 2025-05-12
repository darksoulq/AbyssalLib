package com.github.darksoulq.abyssallib.database.impl.sqlite;

import com.github.darksoulq.abyssallib.database.QueryExecutor;
import com.github.darksoulq.abyssallib.database.TableQuery;

import java.sql.Connection;
import java.sql.Statement;

/**
 * SQLite implementation of {@link QueryExecutor}.
 * Provides methods to execute SQL queries and obtain {@link TableQuery} objects.
 */
public class SqliteQueryExecutor implements QueryExecutor {
    /**
     * The JDBC connection to the SQLite database.
     */
    private final Connection connection;

    /**
     * Constructs a new {@code SqliteQueryExecutor} using the given SQLite connection.
     *
     * @param connection the SQLite database connection
     */
    public SqliteQueryExecutor(Connection connection) {
        this.connection = connection;
    }

    /**
     * Returns a new {@link TableQuery} instance for the specified table name.
     *
     * @param name the name of the table to operate on
     * @return a {@link SqliteTableQuery} instance for the table
     */
    @Override
    public TableQuery table(String name) {
        return new SqliteTableQuery(connection, name);
    }

    /**
     * Executes a raw SQL statement directly against the SQLite connection.
     *
     * @param sql the raw SQL string to execute
     * @throws Exception if an error occurs while executing the SQL
     */
    @Override
    public void executeRaw(String sql) throws Exception {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }
}
