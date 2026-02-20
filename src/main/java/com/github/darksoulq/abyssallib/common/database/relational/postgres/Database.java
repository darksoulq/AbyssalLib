package com.github.darksoulq.abyssallib.common.database.relational.postgres;

import com.github.darksoulq.abyssallib.common.database.relational.AbstractDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A PostgreSQL database implementation that manages remote connections.
 * <p>
 * This class handles persistent connections to a PostgreSQL server, featuring
 * an automated keep-alive scheduler to prevent connection timeouts and
 * thread-safe connection retrieval.
 */
public class Database extends AbstractDatabase {
    /** The hostname or IP address of the PostgreSQL server. */
    private final String host;
    /** The port number the PostgreSQL server is listening on. */
    private final int port;
    /** The name of the specific database to connect to. */
    private final String database;
    /** The username for authentication. */
    private final String user;
    /** The password for authentication. */
    private final String password;

    /** The active JDBC connection to the PostgreSQL server. */
    private Connection connection;
    /** A single-threaded scheduler used for periodic keep-alive checks. */
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    /**
     * Constructs a new PostgreSQL Database instance.
     *
     * @param host     The server hostname.
     * @param port     The server port.
     * @param database The database name.
     * @param user     The login username.
     * @param password The login password.
     */
    public Database(String host, int port, String database, String user, String password) {
        super(Executors.newCachedThreadPool());
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    /**
     * Establishes a connection to the PostgreSQL database and starts the keep-alive task.
     *
     * @throws Exception If the driver is missing or the connection fails.
     */
    public void connect() throws Exception {
        String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;
        connection = DriverManager.getConnection(url, user, password);
        scheduler.scheduleAtFixedRate(this::keepAlive, 30, 30, TimeUnit.MINUTES);
    }

    /**
     * Periodically verifies the connection validity and attempts to reconnect if dropped.
     */
    private void keepAlive() {
        try {
            if (connection != null && !connection.isValid(2)) {
                connect();
            }
        } catch (Exception ignored) {}
    }

    /**
     * Shuts down the keep-alive scheduler, the async pool, and closes the JDBC connection.
     *
     * @throws Exception If a database access error occurs during closure.
     */
    public void disconnect() throws Exception {
        scheduler.shutdown();
        asyncPool.shutdown();
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    /**
     * Retrieves the active connection, automatically reconnecting if it has been closed or invalidated.
     *
     * @return The active {@link Connection}.
     * @throws SQLException If reconnection fails.
     */
    @Override
    public synchronized Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                connect();
            }
        } catch (Exception e) {
            throw new SQLException("Failed to reconnect to database", e);
        }
        return connection;
    }

    /**
     * Creates a new QueryExecutor instance for this database.
     *
     * @return A new {@link QueryExecutor}.
     */
    public QueryExecutor executor() {
        return new QueryExecutor(this);
    }

    /**
     * Executes an operation within a SQL transaction using a QueryExecutor.
     *
     * @param action A {@link Consumer} that performs the transaction logic.
     */
    public void transaction(Consumer<QueryExecutor> action) {
        super.executeTransaction(conn -> action.accept(new QueryExecutor(this)));
    }

    /**
     * Executes an operation within a SQL transaction and returns a computed result.
     *
     * @param <T>    The return type.
     * @param action A {@link Function} that performs the transaction logic and returns a result.
     * @return The result of the transaction.
     */
    public <T> T transactionResult(Function<QueryExecutor, T> action) {
        return super.executeTransactionResult(conn -> action.apply(new QueryExecutor(this)));
    }
}