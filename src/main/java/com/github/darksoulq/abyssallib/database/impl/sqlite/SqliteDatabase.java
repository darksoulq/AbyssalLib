package com.github.darksoulq.abyssallib.database.impl.sqlite;

import com.github.darksoulq.abyssallib.database.Database;
import com.github.darksoulq.abyssallib.database.QueryExecutor;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * SQLite implementation of the {@link Database} interface.
 * Manages the lifecycle of a SQLite database connection.
 */
public class SqliteDatabase implements Database {
    /**
     * The file location of the SQLite database.
     */
    private final File file;
    /**
     * The active JDBC connection to the SQLite database.
     */
    private Connection connection;

    /**
     * Constructs a new SQLite database instance using the specified file.
     *
     * @param file the file representing the SQLite database location
     */
    public SqliteDatabase(File file) {
        this.file = file;
    }

    /**
     * Connects to the SQLite database. If the file or its parent directories
     * do not exist, they will be created.
     *
     * @throws Exception if the connection fails
     */
    @Override
    public void connect() throws Exception {
        file.getParentFile().mkdirs();
        connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
    }

    /**
     * Disconnects from the SQLite database if the connection is open.
     *
     * @throws Exception if closing the connection fails
     */
    @Override
    public void disconnect() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    /**
     * Provides a {@link QueryExecutor} to run queries on this SQLite database.
     *
     * @return a new instance of {@link SqliteQueryExecutor}
     */
    @Override
    public QueryExecutor executor() {
        return new SqliteQueryExecutor(connection);
    }
}
