package com.github.darksoulq.abyssallib.common.database.relational.h2;

import com.github.darksoulq.abyssallib.common.database.relational.AbstractDatabase;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * An H2 database implementation managing local file-based storage.
 * <p>
 * This class handles connection lifecycles for H2, automatically managing
 * directory creation and filename sanitization for the MVStore format.
 */
public class Database extends AbstractDatabase {
    /** The base file for the H2 database. */
    private final File file;
    /** The active JDBC connection. */
    private Connection connection;

    /**
     * Constructs a new H2 Database instance.
     *
     * @param file The {@link File} representing the database location.
     */
    public Database(File file) {
        super(Executors.newCachedThreadPool());
        this.file = file;
    }

    /**
     * Establishes a connection to the H2 database.
     * <p>
     * Automatically creates parent directories and strips the ".mv.db" suffix
     * from the path if present to satisfy JDBC URL requirements.
     * Configured with {@code MODE=MySQL} for compatibility and {@code AUTO_SERVER=TRUE}
     * for multi-connection support.
     *
     * @throws Exception If connection or directory creation fails.
     */
    public void connect() throws Exception {
        file.getParentFile().mkdirs();
        String path = file.getAbsolutePath();
        if (path.endsWith(".mv.db")) {
            path = path.substring(0, path.length() - 6);
        }
        connection = DriverManager.getConnection("jdbc:h2:" + path + ";MODE=MySQL;AUTO_SERVER=TRUE");
    }

    /**
     * Closes the database connection and shuts down the asynchronous pool.
     *
     * @throws Exception If a database access error occurs.
     */
    public void disconnect() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        asyncPool.shutdown();
    }

    /**
     * Retrieves the current connection, attempting to reconnect if it has been closed.
     *
     * @return A valid {@link Connection}.
     * @throws SQLException If reconnection is unsuccessful.
     */
    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connect();
            } catch (Exception e) {
                throw new SQLException("Failed to reconnect to H2 database", e);
            }
        }
        return connection;
    }

    /**
     * @return A new {@link QueryExecutor} linked to this database.
     */
    public QueryExecutor executor() {
        return new QueryExecutor(this);
    }

    /**
     * Executes logic within a transaction.
     *
     * @param action A {@link Consumer} receiving a transactional {@link QueryExecutor}.
     */
    public void transaction(Consumer<QueryExecutor> action) {
        super.executeTransaction(conn -> action.accept(new QueryExecutor(this)));
    }

    /**
     * Executes logic within a transaction and returns a result.
     *
     * @param <T>    The result type.
     * @param action A {@link Function} receiving a transactional {@link QueryExecutor}.
     * @return The result of the transaction.
     */
    public <T> T transactionResult(Function<QueryExecutor, T> action) {
        return super.executeTransactionResult(conn -> action.apply(new QueryExecutor(this)));
    }
}