package com.github.darksoulq.abyssallib.common.database.mysql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;

public class BatchQuery {
    private final Database database;
    private final String table;
    private final String[] columns;
    private final List<Object[]> records = new ArrayList<>();

    private enum Type { INSERT, REPLACE, INSERT_IGNORE }
    private Type type = Type.INSERT;

    public BatchQuery(Database database, String table, String... columns) {
        this.database = database;
        this.table = table;
        this.columns = columns;
    }

    public BatchQuery insert() { 
        this.type = Type.INSERT; 
        return this; 
    }
    
    public BatchQuery replace() { 
        this.type = Type.REPLACE; 
        return this; 
    }
    
    public BatchQuery insertIgnore() { 
        this.type = Type.INSERT_IGNORE; 
        return this; 
    }

    public BatchQuery add(Object... values) {
        if (values.length != columns.length) {
            throw new IllegalArgumentException("Column count mismatch. Expected " + columns.length + " but got " + values.length);
        }
        records.add(values);
        return this;
    }

    public int execute() {
        if (records.isEmpty()) return 0;

        String verb = switch (type) {
            case INSERT -> "INSERT INTO ";
            case REPLACE -> "REPLACE INTO ";
            case INSERT_IGNORE -> "INSERT IGNORE INTO ";
        };

        StringJoiner colNames = new StringJoiner(", ");
        StringJoiner placeholders = new StringJoiner(", ");

        for (String col : columns) {
            colNames.add(col);
            placeholders.add("?");
        }

        String sql = verb + table + " (" + colNames + ") VALUES (" + placeholders + ")";

        try (PreparedStatement stmt = database.getConnection().prepareStatement(sql)) {
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
        return CompletableFuture.supplyAsync(this::execute, database.getAsyncPool());
    }
}