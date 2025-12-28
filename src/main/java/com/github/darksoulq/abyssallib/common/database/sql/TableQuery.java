package com.github.darksoulq.abyssallib.common.database.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class TableQuery {
    private final Connection conn;
    private final String table;
    private final ExecutorService asyncPool;

    private enum Type { INSERT, REPLACE, UPDATE, DELETE }
    private Type type = Type.INSERT;

    private final Map<String, Object> values = new LinkedHashMap<>();
    private String whereClause = null;
    private Object[] whereParams = new Object[0];

    private String orderByColumn = null;
    private boolean orderAscending = true;
    private Integer limit = null;
    private Integer offset = null;

    public TableQuery(Connection conn, String table, ExecutorService asyncPool) {
        this.conn = conn;
        this.table = table;
        this.asyncPool = asyncPool;
    }

    public TableQuery insert() { this.type = Type.INSERT; return this; }
    public TableQuery replace() { this.type = Type.REPLACE; return this; }
    public TableQuery update() { this.type = Type.UPDATE; return this; }
    public TableQuery delete() { this.type = Type.DELETE; return this; }
    public TableBuilder create() { return new TableBuilder(conn, table); }

    public TableQuery value(String column, Object value) {
        values.put(column, value);
        return this;
    }

    public TableQuery where(String clause, Object... params) {
        this.whereClause = clause;
        this.whereParams = params;
        return this;
    }

    public TableQuery orderBy(String column, boolean ascending) {
        this.orderByColumn = column;
        this.orderAscending = ascending;
        return this;
    }

    public TableQuery limit(int limit) {
        this.limit = limit;
        return this;
    }

    public TableQuery offset(int offset) {
        this.offset = offset;
        return this;
    }

    public int execute() {
        try {
            return switch (type) {
                case INSERT -> executeInsert(false);
                case REPLACE -> executeInsert(true);
                case UPDATE -> executeUpdate();
                case DELETE -> executeDelete();
            };
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<Integer> executeAsync() {
        return CompletableFuture.supplyAsync(this::execute, asyncPool);
    }

    private int executeInsert(boolean replace) throws SQLException {
        String cmd = replace ? "INSERT OR REPLACE INTO " : "INSERT INTO ";
        StringBuilder sql = new StringBuilder(cmd).append(table).append(" (");

        StringJoiner cols = new StringJoiner(", ");
        StringJoiner placeholders = new StringJoiner(", ");

        for (String col : values.keySet()) {
            cols.add(col);
            placeholders.add("?");
        }

        sql.append(cols).append(") VALUES (").append(placeholders).append(")");

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            setValues(stmt, values.values().toArray());
            return stmt.executeUpdate();
        }
    }

    private int executeUpdate() throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE " + table + " SET ");
        StringJoiner updates = new StringJoiner(", ");
        for (String col : values.keySet()) {
            updates.add(col + " = ?");
        }
        sql.append(updates);
        appendWhereClause(sql);

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            List<Object> all = new ArrayList<>(values.values());
            if (whereParams != null) all.addAll(List.of(whereParams));
            setValues(stmt, all.toArray());
            return stmt.executeUpdate();
        }
    }

    private int executeDelete() throws SQLException {
        StringBuilder sql = new StringBuilder("DELETE FROM " + table);
        appendWhereClause(sql);

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            setValues(stmt, whereParams);
            return stmt.executeUpdate();
        }
    }

    public <T> List<T> select(ResultMapper<T> mapper) {
        return select(mapper, "*");
    }

    public <T> List<T> select(ResultMapper<T> mapper, String... columns) {
        List<T> results = new ArrayList<>();

        String colStr = (columns == null || columns.length == 0) ? "*" : String.join(", ", columns);
        StringBuilder sql = new StringBuilder("SELECT " + colStr + " FROM " + table);

        appendWhereClause(sql);

        if (orderByColumn != null) {
            sql.append(" ORDER BY ").append(orderByColumn).append(orderAscending ? " ASC" : " DESC");
        }
        if (limit != null) {
            sql.append(" LIMIT ").append(limit);
        }
        if (offset != null) {
            sql.append(" OFFSET ").append(offset);
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            setValues(stmt, whereParams);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapper.map(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error executing select on " + table, e);
        }

        return results;
    }

    public <T> CompletableFuture<List<T>> selectAsync(ResultMapper<T> mapper) {
        return CompletableFuture.supplyAsync(() -> select(mapper), asyncPool);
    }

    public <T> CompletableFuture<List<T>> selectAsync(ResultMapper<T> mapper, String... columns) {
        return CompletableFuture.supplyAsync(() -> select(mapper, columns), asyncPool);
    }

    private void appendWhereClause(StringBuilder sql) {
        if (whereClause != null && !whereClause.isEmpty()) {
            sql.append(" WHERE ").append(whereClause);
        }
    }

    private void setValues(PreparedStatement stmt, Object[] values) throws SQLException {
        if (values == null) return;
        for (int i = 0; i < values.length; i++) {
            stmt.setObject(i + 1, values[i]);
        }
    }
}