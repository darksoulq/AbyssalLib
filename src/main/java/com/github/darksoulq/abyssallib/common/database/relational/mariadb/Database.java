package com.github.darksoulq.abyssallib.common.database.relational.mariadb;

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
 * A MariaDB database implementation managing remote JDBC connections.
 * <p>
 * This class includes a background scheduler to maintain connection stability
 * through periodic keep-alive checks and provides thread-safe access to the
 * underlying {@link Connection}.
 */
public class Database extends AbstractDatabase {
    /** The hostname of the MariaDB server. */
    private final String host;
    /** The port of the MariaDB server. */
    private final int port;
    /** The name of the target database. */
    private final String database;
    /** The database user. */
    private final String user;
    /** The database password. */
    private final String password;

    /** The active JDBC connection. */
    private Connection connection;
    /** The scheduler for keep-alive operations. */
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    /**
     * Constructs a new MariaDB Database instance.
     *
     * @param host     The server address.
     * @param port     The connection port.
     * @param database The database name.
     * @param user     The credential username.
     * @param password The credential password.
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
     * Establishes the connection and starts the 30-minute keep-alive interval.
     *
     * @throws Exception If connection fails.
     */
    public void connect() throws Exception {
        String url = "jdbc:mariadb://" + host + ":" + port + "/" + database;
        connection = DriverManager.getConnection(url, user, password);
        scheduler.scheduleAtFixedRate(this::keepAlive, 30, 30, TimeUnit.MINUTES);
    }

    /**
     * Internal task to validate the connection and attempt reconnection if invalid.
     */
    private void keepAlive() {
        try {
            if (connection != null && !connection.isValid(2)) {
                connect();
            }
        } catch (Exception ignored) {}
    }

    /**
     * Gracefully shuts down the scheduler, async pool, and JDBC connection.
     *
     * @throws Exception If an error occurs during disconnect.
     */
    public void disconnect() throws Exception {
        scheduler.shutdown();
        asyncPool.shutdown();
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    /**
     * Synchronized getter for the connection; performs a health check and
     * auto-reconnects if the connection is dead.
     *
     * @return A valid {@link Connection}.
     * @throws SQLException If the database is unreachable.
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
     * Spawns an executor for this database.
     *
     * @return A new {@link QueryExecutor}.
     */
    public QueryExecutor executor() {
        return new QueryExecutor(this);
    }

    /**
     * Executes a series of operations within a transactional scope.
     *
     * @param action The logic to execute.
     */
    public void transaction(Consumer<QueryExecutor> action) {
        super.executeTransaction(conn -> action.accept(new QueryExecutor(this)));
    }

    /**
     * Executes a series of operations within a transactional scope and returns a value.
     *
     * @param <T>    The result type.
     * @param action The logic to execute.
     * @return The result of type {@code T}.
     */
    public <T> T transactionResult(Function<QueryExecutor, T> action) {
        return super.executeTransactionResult(conn -> action.apply(new QueryExecutor(this)));
    }
}