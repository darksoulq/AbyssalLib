package com.github.darksoulq.abyssallib.common.database.sql;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

public class Database {
    private final File file;
    private Connection connection;
    private final ExecutorService asyncPool = Executors.newCachedThreadPool();

    public Database(File file) {
        this.file = file;
    }

    public void connect() throws Exception {
        file.getParentFile().mkdirs();
        connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath() + "?foreign_keys=on");
    }

    public void disconnect() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        asyncPool.shutdown();
    }

    public QueryExecutor executor() {
        return new QueryExecutor(connection, asyncPool);
    }

    public void transaction(Consumer<QueryExecutor> action) {
        transactionResult(executor -> {
            action.accept(executor);
            return null;
        });
    }

    public <T> T transactionResult(Function<QueryExecutor, T> action) {
        try {
            boolean originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try {
                T result = action.apply(executor());
                connection.commit();
                return result;
            } catch (Exception e) {
                connection.rollback();
                throw new RuntimeException("Transaction failed, rolled back.", e);
            } finally {
                connection.setAutoCommit(originalAutoCommit);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during transaction", e);
        }
    }

    public ExecutorService getAsyncPool() {
        return asyncPool;
    }
}