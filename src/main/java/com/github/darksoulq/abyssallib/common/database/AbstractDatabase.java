package com.github.darksoulq.abyssallib.common.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractDatabase {
    protected final ExecutorService asyncPool;

    public AbstractDatabase(ExecutorService asyncPool) {
        this.asyncPool = asyncPool;
    }

    public abstract Connection getConnection() throws SQLException;

    public ExecutorService getAsyncPool() {
        return asyncPool;
    }

    public void executeTransaction(Consumer<Connection> action) {
        executeTransactionResult(conn -> {
            action.accept(conn);
            return null;
        });
    }

    public <T> T executeTransactionResult(Function<Connection, T> action) {
        try {
            Connection conn = getConnection();
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                T result = action.apply(conn);
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