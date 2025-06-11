package com.github.darksoulq.abyssallib.server.database.impl.mysql;

import com.github.darksoulq.abyssallib.server.database.Database;
import com.github.darksoulq.abyssallib.server.database.QueryExecutor;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Implementation of the {@link Database} interface for MySQL.
 * Manages a JDBC connection and provides a query executor.
 */
public class MySQLDatabase implements Database {
    /**
     * The MySQL host (including port if necessary, e.g., localhost:3306).
     */
    private final String host;
    /**
     * The name of the database to connect to.
     */
    private final String database;
    /**
     * The username used to authenticate with the MySQL server.
     */
    private final String username;
    /**
     * The password used to authenticate with the MySQL server.
     */
    private final String password;
    /**
     * The current JDBC connection to the MySQL database.
     */
    private Connection connection;

    /**
     * Constructs a new {@link MySQLDatabase} instance.
     *
     * @param host     the MySQL host (e.g., "localhost" or "localhost:3306")
     * @param database the name of the database
     * @param username the username
     * @param password the password
     */
    public MySQLDatabase(String host, String database, String username, String password) {
        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    /**
     * Establishes a connection to the MySQL database using JDBC.
     *
     * @throws Exception if a connection cannot be established
     */
    @Override
    public void connect() throws Exception {
        String url = "jdbc:mysql://" + host + "/" + database;
        connection = DriverManager.getConnection(url, username, password);
    }

    /**
     * Closes the connection to the MySQL database, if open.
     *
     * @throws Exception if an error occurs while closing the connection
     */
    @Override
    public void disconnect() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    /**
     * Returns a {@link QueryExecutor} instance bound to this database connection.
     *
     * @return a new {@link MySQLQueryExecutor}
     */
    @Override
    public QueryExecutor executor() {
        return new MySQLQueryExecutor(connection);
    }
}
