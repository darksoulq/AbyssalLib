package com.github.darksoulq.abyssallib.common.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public abstract class AbstractBatchQuery<T extends AbstractBatchQuery<T>> {
    protected final Connection connection;
    protected final String table;
    protected final ExecutorService asyncPool;
    protected final String[] columns;
    protected final List<Object[]> records = new ArrayList<>();

    protected enum Type { INSERT, REPLACE, INSERT_IGNORE }
    protected Type type = Type.INSERT;

    public AbstractBatchQuery(Connection connection, String table, ExecutorService asyncPool, String... columns) {
        this.connection = connection;
        this.table = table;
        this.asyncPool = asyncPool;
        this.columns = columns;
    }

    @SuppressWarnings("unchecked")
    public T insert() { this.type = Type.INSERT; return (T) this; }
    @SuppressWarnings("unchecked")
    public T replace() { this.type = Type.REPLACE; return (T) this; }
    @SuppressWarnings("unchecked")
    public T insertIgnore() { this.type = Type.INSERT_IGNORE; return (T) this; }

    @SuppressWarnings("unchecked")
    public T add(Object... values) {
        if (values.length != columns.length) {
            throw new IllegalArgumentException("Column count mismatch. Expected " + columns.length + " but got " + values.length);
        }
        records.add(values);
        return (T) this;
    }

    protected abstract String getInsertVerb();
    protected abstract String getReplaceVerb();
    protected abstract String getInsertIgnoreVerb();

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

    public CompletableFuture<Integer> executeAsync() {
        return CompletableFuture.supplyAsync(this::execute, asyncPool);
    }
}