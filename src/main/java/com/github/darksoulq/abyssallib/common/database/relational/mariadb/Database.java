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

public class Database extends AbstractDatabase {
    private final String host;
    private final int port;
    private final String database;
    private final String user;
    private final String password;

    private Connection connection;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public Database(String host, int port, String database, String user, String password) {
        super(Executors.newCachedThreadPool());
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    public void connect() throws Exception {
        String url = "jdbc:mariadb://" + host + ":" + port + "/" + database;
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

    public QueryExecutor executor() {
        return new QueryExecutor(this);
    }

    public void transaction(Consumer<QueryExecutor> action) {
        super.executeTransaction(conn -> action.accept(new QueryExecutor(this)));
    }

    public <T> T transactionResult(Function<QueryExecutor, T> action) {
        return super.executeTransactionResult(conn -> action.apply(new QueryExecutor(this)));
    }
}