package com.github.darksoulq.abyssallib.common.database.mysql;

import com.github.darksoulq.abyssallib.common.database.AbstractDatabase;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A MySQL database implementation that handles remote connectivity, connection pooling,
 * and a keep-alive scheduler to prevent connection timeouts.
 */
public class Database extends AbstractDatabase {
    /** The hostname or IP address of the MySQL server. */
    private final String host;
    /** The port number of the MySQL server (typically 3306). */
    private final int port;
    /** The name of the specific database/schema. */
    private final String database;
    /** The username for authentication. */
    private final String user;
    /** The password for authentication. */
    private final String password;

    /** The active JDBC connection. */
    private Connection connection;
    /** A single-threaded scheduler used to run periodic keep-alive checks. */
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    /**
     * Constructs a new MySQL Database manager.
     *
     * @param host     The server host.
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
     * Connects to the MySQL server using JDBC.
     * Configures SSL, public key retrieval, auto-reconnect, and UTF-8 encoding.
     * Starts a heartbeat task that runs every 30 minutes.
     *
     * @throws Exception If the connection fails.
     */
    public void connect() throws Exception {
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database +
            "?useSSL=false&allowPublicKeyRetrieval=true&autoReconnect=true&characterEncoding=utf8";
        connection = DriverManager.getConnection(url, user, password);
        scheduler.scheduleAtFixedRate(this::keepAlive, 30, 30, TimeUnit.MINUTES);
    }

    /**
     * Performs a validity check on the connection.
     * If the connection is invalid or null, it attempts to reconnect.
     */
    private void keepAlive() {
        try {
            if (connection != null && !connection.isValid(2)) {
                connect();
            }
        } catch (Exception ignored) {}
    }

    /**
     * Shuts down the keep-alive scheduler, the async thread pool, and closes the SQL connection.
     *
     * @throws Exception If an error occurs during disconnection.
     */
    public void disconnect() throws Exception {
        scheduler.shutdown();
        asyncPool.shutdown();
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    /**
     * Provides the active SQL connection.
     * If the connection is closed or invalid, it attempts to reconnect synchronously.
     *
     * @return A valid {@link Connection}.
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
     * Creates a {@link QueryExecutor} for this database.
     * @return A new executor instance.
     */
    public QueryExecutor executor() {
        return new QueryExecutor(this);
    }

    /**
     * Executes a transaction using a {@link QueryExecutor}.
     * @param action The consumer logic.
     */
    public void transaction(Consumer<QueryExecutor> action) {
        super.executeTransaction(conn -> action.accept(new QueryExecutor(this)));
    }

    /**
     * Executes a transaction that returns a result using a {@link QueryExecutor}.
     *
     * @param <T>    The return type.
     * @param action The function logic.
     * @return The transaction result.
     */
    public <T> T transactionResult(Function<QueryExecutor, T> action) {
        return super.executeTransactionResult(conn -> action.apply(new QueryExecutor(this)));
    }
}