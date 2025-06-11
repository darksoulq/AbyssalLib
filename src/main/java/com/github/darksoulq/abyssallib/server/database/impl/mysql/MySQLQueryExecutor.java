package com.github.darksoulq.abyssallib.server.database.impl.mysql;

import com.github.darksoulq.abyssallib.server.database.QueryExecutor;
import com.github.darksoulq.abyssallib.server.database.TableQuery;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Implementation of the {@link QueryExecutor} interface for MySQL.
 * Provides methods to execute queries and interact with tables in a MySQL database.
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
     *
     * @param sql the raw SQL query to execute
     * @throws Exception if an error occurs during the execution of the SQL statement
     */
    @Override
    public void executeRaw(String sql) throws Exception {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }
}
