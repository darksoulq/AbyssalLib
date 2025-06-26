package com.github.darksoulq.abyssallib.server.database.impl.mysql;

import com.github.darksoulq.abyssallib.server.database.QueryExecutor;
import com.github.darksoulq.abyssallib.server.database.TableQuery;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Implementation of the {@link QueryExecutor} interface for MySQL.
 * Provides methods to execute queries and interact with tables in a MySQL database.
 * <p>
 * Note: This class is not thread-safe. Each thread should obtain its own executor
 * or ensure external synchronization when executing statements.
 */
public class MySQLQueryExecutor implements QueryExecutor {

    /**
     * The JDBC connection to the MySQL database.
     */
    private final Connection connection;

    /**
     * Constructs a new {@link MySQLQueryExecutor} instance.
     *
     * @param connection the JDBC connection to the MySQL database
     */
    public MySQLQueryExecutor(Connection connection) {
        this.connection = connection;
    }

    /**
     * Returns a {@link TableQuery} instance for interacting with a specific table.
     *
     * @param name the name of the table to interact with
     * @return a {@link MySQLTableQuery} instance for the given table
     */
    @Override
    public TableQuery table(String name) {
        return new MySQLTableQuery(connection, name);
    }

    /**
     * Executes a raw SQL statement on the MySQL database.
     * <p>
     * Example usage:
     * <pre>{@code
     * executor.executeRaw("DELETE FROM users WHERE id = 1");
     * }</pre>
     *
     * @param sql the raw SQL query to execute
     * @throws SQLException if an error occurs during execution
     * @throws IllegalStateException if the database connection is closed
     */
    @Override
    public void executeRaw(String sql) throws Exception {
        if (connection == null || connection.isClosed()) {
            throw new IllegalStateException("Cannot execute SQL: connection is closed or null.");
        }

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException ex) {
            throw new SQLException("Failed to execute SQL: " + sql, ex);
        }
    }
}
