package com.github.darksoulq.abyssallib.common.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * An abstract base class representing a database connection provider and transaction manager.
 * It manages an {@link ExecutorService} for off-thread operations and provides robust
 * transaction handling with automatic rollback capability.
 */
public abstract class AbstractDatabase {
    /** The thread pool used for executing asynchronous database queries. */
    protected final ExecutorService asyncPool;

    /**
     * Initializes the database manager with an execution pool.
     *
     * @param asyncPool The {@link ExecutorService} to use for background tasks.
     */
    public AbstractDatabase(ExecutorService asyncPool) {
        this.asyncPool = asyncPool;
    }

    /**
     * Retrieves a connection from the database or connection pool.
     *
     * @return A valid {@link Connection} object.
     * @throws SQLException If a database access error occurs.
     */
    public abstract Connection getConnection() throws SQLException;

    /**
     * Provides access to the asynchronous executor pool.
     *
     * @return The current {@link ExecutorService}.
     */
    public ExecutorService getAsyncPool() {
        return asyncPool;
    }

    /**
     * Executes a database operation within a transaction that does not return a result.
     * The transaction is automatically committed on success and rolled back on failure.
     *
     * @param action A {@link Consumer} receiving the {@link Connection} to perform tasks.
     * @throws RuntimeException If the transaction fails or a database error occurs.
     */
    public void executeTransaction(Consumer<Connection> action) {
        executeTransactionResult(conn -> {
            action.accept(conn);
            return null;
        });
    }

    /**
     * Executes a database operation within a transaction and returns a value.
     * Handles manual commit/rollback and ensures the auto-commit state is restored.
     *
     * @param <T>    The type of the result returned by the transaction.
     * @param action A {@link Function} receiving the {@link Connection} and returning a value of type {@code T}.
     * @return The result produced by the action.
     * @throws RuntimeException If the transaction fails or a database error occurs.
     */
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