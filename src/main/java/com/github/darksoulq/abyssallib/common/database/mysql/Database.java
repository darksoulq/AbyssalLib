package com.github.darksoulq.abyssallib.common.database.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

public class Database {
    private final String host;
    private final int port;
    private final String database;
    private final String user;
    private final String password;

    private Connection connection;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final java.util.concurrent.ExecutorService asyncPool = Executors.newCachedThreadPool();

    public Database(String host, int port, String database, String user, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    public void connect() throws Exception {
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database +
            "?useSSL=false&allowPublicKeyRetrieval=true&autoReconnect=true&characterEncoding=utf8";
        connection = DriverManager.getConnection(url, user, password);
        scheduler.scheduleAtFixedRate(this::keepAlive, 30, 30, TimeUnit.MINUTES);
    }

    private void keepAlive() {
        try {
            if (connection != null && !connection.isValid(2)) {
                connect();
            }
        } catch (Exception ignored) {}
    }

    public void disconnect() throws Exception {
        scheduler.shutdown();
        asyncPool.shutdown();
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

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

    public java.util.concurrent.ExecutorService getAsyncPool() {
        return asyncPool;
    }

    public QueryExecutor executor() {
        return new QueryExecutor(this);
    }

    public void transaction(Consumer<QueryExecutor> action) {
        transactionResult(executor -> {
            action.accept(executor);
            return null;
        });
    }

    public <T> T transactionResult(Function<QueryExecutor, T> action) {
        try {
            Connection conn = getConnection();
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try {
                T result = action.apply(new QueryExecutor(this));
                conn.commit();
                return result;
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Transaction failed, rolled back.", e);
            } finally {
                conn.setAutoCommit(originalAutoCommit);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during transaction", e);
        }
    }
}