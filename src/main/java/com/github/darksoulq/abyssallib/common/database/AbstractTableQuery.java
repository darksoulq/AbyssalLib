package com.github.darksoulq.abyssallib.common.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public abstract class AbstractTableQuery<T extends AbstractTableQuery<T>> {
    protected final Connection connection;
    protected final String table;
    protected final ExecutorService asyncPool;

    protected enum Type { INSERT, REPLACE, UPDATE, DELETE }
    protected Type type = Type.INSERT;

    protected final Map<String, Object> values = new LinkedHashMap<>();
    protected String whereClause = null;
    protected Object[] whereParams = new Object[0];
    protected String orderByColumn = null;
    protected boolean orderAscending = true;
    protected Integer limit = null;
    protected Integer offset = null;

    public AbstractTableQuery(Connection connection, String table, ExecutorService asyncPool) {
        this.connection = connection;
        this.table = table;
        this.asyncPool = asyncPool;
    }

    @SuppressWarnings("unchecked")
    public T insert() { this.type = Type.INSERT; return (T) this; }
    @SuppressWarnings("unchecked")
    public T replace() { this.type = Type.REPLACE; return (T) this; }
    @SuppressWarnings("unchecked")
    public T update() { this.type = Type.UPDATE; return (T) this; }
    @SuppressWarnings("unchecked")
    public T delete() { this.type = Type.DELETE; return (T) this; }

    @SuppressWarnings("unchecked")
    public T value(String column, Object value) {
        values.put(column, value);
        return (T) this;
    }
    @SuppressWarnings("unchecked")
    public T where(String clause, Object... params) {
        this.whereClause = clause;
        this.whereParams = params;
        return (T) this;
    }
    @SuppressWarnings("unchecked")
    public T orderBy(String column, boolean ascending) {
        this.orderByColumn = column;
        this.orderAscending = ascending;
        return (T) this;
    }
    @SuppressWarnings("unchecked")
    public T limit(int limit) { this.limit = limit; return (T) this; }
    @SuppressWarnings("unchecked")
    public T offset(int offset) { this.offset = offset; return (T) this; }

    protected abstract String getInsertVerb();
    protected abstract String getReplaceVerb();

    public int execute() {
        try {
            return switch (type) {
                case INSERT -> executeInsert(getInsertVerb());
                case REPLACE -> executeInsert(getReplaceVerb());
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

    private int executeInsert(String verb) throws SQLException {
        StringBuilder sql = new StringBuilder(verb).append(table).append(" (");
        StringJoiner cols = new StringJoiner(", ");
        StringJoiner placeholders = new StringJoiner(", ");
        for (String col : values.keySet()) {
            cols.add(col);
            placeholders.add("?");
        }
        sql.append(cols).append(") VALUES (").append(placeholders).append(")");
        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
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
        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            List<Object> all = new ArrayList<>(values.values());
            if (whereParams != null) all.addAll(List.of(whereParams));
            setValues(stmt, all.toArray());
            return stmt.executeUpdate();
        }
    }

    private int executeDelete() throws SQLException {
        StringBuilder sql = new StringBuilder("DELETE FROM " + table);
        appendWhereClause(sql);
        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            setValues(stmt, whereParams);
            return stmt.executeUpdate();
        }
    }

    public long count() {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM " + table);
        appendWhereClause(sql);
        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            setValues(stmt, whereParams);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
    public boolean exists() { return count() > 0; }

    public <R> R first(ResultMapper<R> mapper) {
        List<R> list = limit(1).select(mapper);
        return list.isEmpty() ? null : list.get(0);
    }

    public <R> List<R> select(ResultMapper<R> mapper) { return select(mapper, "*"); }
    public <R> List<R> select(ResultMapper<R> mapper, String... columns) {
        List<R> results = new ArrayList<>();
        String colStr = (columns == null || columns.length == 0) ? "*" : String.join(", ", columns);
        StringBuilder sql = new StringBuilder("SELECT " + colStr + " FROM " + table);
        appendWhereClause(sql);
        if (orderByColumn != null) {
            sql.append(" ORDER BY ").append(orderByColumn).append(orderAscending ? " ASC" : " DESC");
        }
        if (limit != null) sql.append(" LIMIT ").append(limit);
        if (offset != null) sql.append(" OFFSET ").append(offset);

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
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

    public <R> CompletableFuture<List<R>> selectAsync(ResultMapper<R> mapper) {
        return CompletableFuture.supplyAsync(() -> select(mapper), asyncPool);
    }
    public <R> CompletableFuture<List<R>> selectAsync(ResultMapper<R> mapper, String... columns) {
        return CompletableFuture.supplyAsync(() -> select(mapper, columns), asyncPool);
    }

    protected void appendWhereClause(StringBuilder sql) {
        if (whereClause != null && !whereClause.isEmpty()) {
            sql.append(" WHERE ").append(whereClause);
        }
    }
    protected void setValues(PreparedStatement stmt, Object[] values) throws SQLException {
        if (values == null) return;
        for (int i = 0; i < values.length; i++) {
            stmt.setObject(i + 1, values[i]);
        }
    }
}