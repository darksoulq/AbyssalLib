package com.github.darksoulq.abyssallib.common.database.relational;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * An abstract base class for handling batch database operations such as INSERT, REPLACE, or INSERT IGNORE.
 * This class provides a fluent API to build a collection of records and execute them in a single batch
 * to optimize performance and reduce database round-trips.
 *
 * @param <T> The specific implementation type, allowing for fluent method chaining.
 */
public abstract class AbstractBatchQuery<T extends AbstractBatchQuery<T>> {
    /** The active JDBC connection used to execute the statements. */
    protected final Connection connection;
    /** The name of the database table where the records will be added. */
    protected final String table;
    /** The thread pool used to handle asynchronous execution via {@link CompletableFuture}. */
    protected final ExecutorService asyncPool;
    /** An array of column names that define the structure of the batch query. */
    protected final String[] columns;
    /** A list containing the rows of data, where each {@code Object[]} matches the length of {@link #columns}. */
    protected final List<Object[]> records = new ArrayList<>();

    /**
     * Enumeration of supported SQL batch operation types.
     */
    protected enum Type {
        /** Standard SQL INSERT operation. */
        INSERT,
        /** SQL REPLACE operation (typically used in MySQL/SQLite). */
        REPLACE,
        /** SQL INSERT IGNORE operation to skip duplicate key errors. */
        INSERT_IGNORE
    }

    /** The current operation type for this query instance, defaults to {@link Type#INSERT}. */
    protected Type type = Type.INSERT;

    /**
     * Constructs a new AbstractBatchQuery instance.
     *
     * @param connection The JDBC {@link Connection} to be used.
     * @param table      The name of the target table.
     * @param asyncPool  The {@link ExecutorService} for asynchronous tasks.
     * @param columns    The specific column names to be populated in this batch.
     */
    public AbstractBatchQuery(Connection connection, String table, ExecutorService asyncPool, String... columns) {
        this.connection = connection;
        this.table = table;
        this.asyncPool = asyncPool;
        this.columns = columns;
    }

    /**
     * Sets the batch operation mode to standard INSERT.
     * @return The current instance cast to type {@code T} for chaining.
     */
    @SuppressWarnings("unchecked")
    public T insert() { this.type = Type.INSERT; return (T) this; }

    /**
     * Sets the batch operation mode to REPLACE.
     * @return The current instance cast to type {@code T} for chaining.
     */
    @SuppressWarnings("unchecked")
    public T replace() { this.type = Type.REPLACE; return (T) this; }

    /**
     * Sets the batch operation mode to INSERT IGNORE.
     * @return The current instance cast to type {@code T} for chaining.
     */
    @SuppressWarnings("unchecked")
    public T insertIgnore() { this.type = Type.INSERT_IGNORE; return (T) this; }

    /**
     * Adds a single row of data to the internal batch list.
     *
     * @param values The values to be inserted. The length must match the number of columns defined.
     * @return The current instance cast to type {@code T} for chaining.
     * @throws IllegalArgumentException If the number of values does not match the number of columns.
     */
    @SuppressWarnings("unchecked")
    public T add(Object... values) {
        if (values.length != columns.length) {
            throw new IllegalArgumentException("Column count mismatch. Expected " + columns.length + " but got " + values.length);
        }
        records.add(values);
        return (T) this;
    }

    /**
     * Returns the database-specific SQL verb for an INSERT operation.
     * @return A string such as "INSERT INTO ".
     */
    protected abstract String getInsertVerb();

    /**
     * Returns the database-specific SQL verb for a REPLACE operation.
     * @return A string such as "REPLACE INTO ".
     */
    protected abstract String getReplaceVerb();

    /**
     * Returns the database-specific SQL verb for an INSERT IGNORE operation.
     * @return A string such as "INSERT IGNORE INTO ".
     */
    protected abstract String getInsertIgnoreVerb();

    /**
     * Compiles the SQL statement and executes the batch against the database.
     *
     * @return The sum of all rows affected by the batch operation.
     * @throws RuntimeException If a {@link SQLException} occurs during execution.
     */
    public int execute() {
        if (records.isEmpty()) return 0;

        String verb = switch (type) {
            case INSERT -> getInsertVerb();
            case REPLACE -> getReplaceVerb();
            case INSERT_IGNORE -> getInsertIgnoreVerb();
        };

        StringJoiner colNames = new StringJoiner(", ");
        StringJoiner placeholders = new StringJoiner(", ");

        for (String col : columns) {
            colNames.add(col);
            placeholders.add("?");
        }

        String sql = verb + table + " (" + colNames + ") VALUES (" + placeholders + ")";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (Object[] row : records) {
                for (int i = 0; i < row.length; i++) {
                    stmt.setObject(i + 1, row[i]);
                }
                stmt.addBatch();
            }
            int[] results = stmt.executeBatch();
            return Arrays.stream(results).sum();
        } catch (SQLException e) {
            throw new RuntimeException("Batch execution failed", e);
        }
    }

    /**
     * Executes the batch operation asynchronously using the internal thread pool.
     *
     * @return A {@link CompletableFuture} that resolves to the total number of affected rows.
     */
    public CompletableFuture<Integer> executeAsync() {
        return CompletableFuture.supplyAsync(this::execute, asyncPool);
    }
}