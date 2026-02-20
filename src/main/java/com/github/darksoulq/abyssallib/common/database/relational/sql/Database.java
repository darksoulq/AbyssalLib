package com.github.darksoulq.abyssallib.common.database.relational.sql;

import com.github.darksoulq.abyssallib.common.database.relational.AbstractDatabase;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A SQLite database implementation that manages the file lifecycle and connection state.
 * It uses a cached thread pool for asynchronous operations and enforces foreign key constraints by default.
 */
public class Database extends AbstractDatabase {
    /** The SQLite database file on the local file system. */
    private final File file;
    /** The active JDBC connection to the SQLite database. */
    private Connection connection;

    /**
     * Constructs a new Database instance and initializes a cached thread pool for async tasks.
     *
     * @param file The {@link File} representing the SQLite database storage.
     */
    public Database(File file) {
        super(Executors.newCachedThreadPool());
        this.file = file;
    }

    /**
     * Establishes a connection to the SQLite database.
     * Creates parent directories if they do not exist and enables foreign key support.
     *
     * @throws Exception If a directory creation or database connection error occurs.
     */
    public void connect() throws Exception {
        file.getParentFile().mkdirs();
        connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath() + "?foreign_keys=on");
    }

    /**
     * Closes the database connection and shuts down the asynchronous thread pool.
     *
     * @throws Exception If a database access error occurs during closure.
     */
    public void disconnect() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        asyncPool.shutdown();
    }

    /**
     * Retrieves the currently active SQLite connection.
     *
     * @return The {@link Connection} instance.
     */
    @Override
    public Connection getConnection() {
        return connection;
    }

    /**
     * Creates a new QueryExecutor instance tied to this database's connection and thread pool.
     *
     * @return A new {@link QueryExecutor} for performing database operations.
     */
    public QueryExecutor executor() {
        return new QueryExecutor(connection, asyncPool);
    }

    /**
     * Executes a set of operations within a SQL transaction.
     * The operations are performed via a {@link QueryExecutor}.
     *
     * @param action A {@link Consumer} providing a {@link QueryExecutor} within the transaction scope.
     */
    public void transaction(Consumer<QueryExecutor> action) {
        super.executeTransaction(conn -> action.accept(new QueryExecutor(conn, asyncPool)));
    }

    /**
     * Executes a set of operations within a SQL transaction and returns a result.
     *
     * @param <T>    The type of the result.
     * @param action A {@link Function} providing a {@link QueryExecutor} and returning a value of type {@code T}.
     * @return The result of the transaction.
     */
    public <T> T transactionResult(Function<QueryExecutor, T> action) {
        return super.executeTransactionResult(conn -> action.apply(new QueryExecutor(conn, asyncPool)));
    }
}