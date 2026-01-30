package com.github.darksoulq.abyssallib.common.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * An abstract base class providing a fluent interface for table-level CRUD operations.
 * It supports standard INSERT, REPLACE, UPDATE, and DELETE operations, as well as complex
 * SELECT queries with mapping, ordering, and pagination.
 *
 * @param <T> The implementation type for fluent chaining.
 */
public abstract class AbstractTableQuery<T extends AbstractTableQuery<T>> {
    /** The JDBC connection for statement execution. */
    protected final Connection connection;
    /** The name of the table to query. */
    protected final String table;
    /** The thread pool for asynchronous query execution. */
    protected final ExecutorService asyncPool;

    /** Internal enumeration of the intended query operation. */
    protected enum Type { INSERT, REPLACE, UPDATE, DELETE }
    /** The current query mode. */
    protected Type type = Type.INSERT;

    /** Map of column names and their corresponding values for data modification. */
    protected final Map<String, Object> values = new LinkedHashMap<>();
    /** The SQL string used for filtering rows. */
    protected String whereClause = null;
    /** The parameters used to safely bind values to the {@link #whereClause}. */
    protected Object[] whereParams = new Object[0];
    /** The column name used for result sorting. */
    protected String orderByColumn = null;
    /** The direction of the sort (true for ASC, false for DESC). */
    protected boolean orderAscending = true;
    /** Maximum number of results to return. */
    protected Integer limit = null;
    /** Number of results to skip. */
    protected Integer offset = null;

    /**
     * Constructs a new AbstractTableQuery.
     *
     * @param connection The JDBC connection.
     * @param table      The target table name.
     * @param asyncPool  The execution pool for async operations.
     */
    public AbstractTableQuery(Connection connection, String table, ExecutorService asyncPool) {
        this.connection = connection;
        this.table = table;
        this.asyncPool = asyncPool;
    }

    /** Sets the query type to INSERT.
     * @return Fluent instance cast to {@code T}. */
    @SuppressWarnings("unchecked")
    public T insert() { this.type = Type.INSERT; return (T) this; }
    /** Sets the query type to REPLACE.
     * @return Fluent instance cast to {@code T}. */
    @SuppressWarnings("unchecked")
    public T replace() { this.type = Type.REPLACE; return (T) this; }
    /** Sets the query type to UPDATE.
     * @return Fluent instance cast to {@code T}. */
    @SuppressWarnings("unchecked")
    public T update() { this.type = Type.UPDATE; return (T) this; }
    /** Sets the query type to DELETE.
     *  @return Fluent instance cast to {@code T}. */
    @SuppressWarnings("unchecked")
    public T delete() { this.type = Type.DELETE; return (T) this; }

    /**
     * Adds a column-value pair for INSERT or UPDATE operations.
     *
     * @param column The name of the column.
     * @param value  The value to set.
     * @return Fluent instance cast to {@code T}.
     */
    @SuppressWarnings("unchecked")
    public T value(String column, Object value) {
        values.put(column, value);
        return (T) this;
    }

    /**
     * Configures a WHERE clause for the query.
     *
     * @param clause The SQL where clause (e.g., "name = ? AND active = ?").
     * @param params The objects to bind to the clause placeholders.
     * @return Fluent instance cast to {@code T}.
     */
    @SuppressWarnings("unchecked")
    public T where(String clause, Object... params) {
        this.whereClause = clause;
        this.whereParams = params;
        return (T) this;
    }

    /**
     * Sets result ordering.
     *
     * @param column    The column name to sort by.
     * @param ascending True for ASC, false for DESC.
     * @return Fluent instance cast to {@code T}.
     */
    @SuppressWarnings("unchecked")
    public T orderBy(String column, boolean ascending) {
        this.orderByColumn = column;
        this.orderAscending = ascending;
        return (T) this;
    }

    /** Sets a limit on the result set.
     * @param limit The max rows.
     * @return Fluent instance. */
    @SuppressWarnings("unchecked")
    public T limit(int limit) { this.limit = limit; return (T) this; }

    /** Sets an offset for pagination.
     * @param offset The starting row.
     * @return Fluent instance. */
    @SuppressWarnings("unchecked")
    public T offset(int offset) { this.offset = offset; return (T) this; }

    /** @return Dialect-specific INSERT verb. */
    protected abstract String getInsertVerb();
    /** @return Dialect-specific REPLACE verb. */
    protected abstract String getReplaceVerb();

    /**
     * Executes the built query (INSERT, REPLACE, UPDATE, or DELETE).
     *
     * @return The number of rows affected.
     * @throws RuntimeException If a {@link SQLException} occurs.
     */
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

    /**
     * Asynchronously executes the current operation.
     * @return A {@link CompletableFuture} with the rows affected.
     */
    public CompletableFuture<Integer> executeAsync() {
        return CompletableFuture.supplyAsync(this::execute, asyncPool);
    }

    /** Internal logic for execution.
     * @throws SQLException if SQL fails. */
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

    /**
     * Executes a COUNT(*) query based on the current configuration.
     * @return The count of rows matching the query.
     */
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

    /** @return True if at least one row matches the query criteria. */
    public boolean exists() { return count() > 0; }

    /**
     * Fetches the first row from the query results and maps it.
     *
     * @param <R>    The result type.
     * @param mapper The {@link ResultMapper} to convert the {@link ResultSet} row.
     * @return The mapped object, or {@code null} if no rows found.
     */
    public <R> R first(ResultMapper<R> mapper) {
        List<R> list = limit(1).select(mapper);
        return list.isEmpty() ? null : list.get(0);
    }

    /** Selects all columns and maps the results.
     * @param mapper The mapper.
     * @return List of mapped results. */
    public <R> List<R> select(ResultMapper<R> mapper) { return select(mapper, "*"); }

    /**
     * Selects specific columns and maps the results.
     *
     * @param <R>     The result type.
     * @param mapper  The mapper.
     * @param columns The column names to select.
     * @return List of results.
     */
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

    /** Asynchronous select.
     * @return Future list. */
    public <R> CompletableFuture<List<R>> selectAsync(ResultMapper<R> mapper) {
        return CompletableFuture.supplyAsync(() -> select(mapper), asyncPool);
    }

    /** Asynchronous select with columns.
     *  @return Future list. */
    public <R> CompletableFuture<List<R>> selectAsync(ResultMapper<R> mapper, String... columns) {
        return CompletableFuture.supplyAsync(() -> select(mapper, columns), asyncPool);
    }

    /** Appends WHERE clause to the provided StringBuilder. */
    protected void appendWhereClause(StringBuilder sql) {
        if (whereClause != null && !whereClause.isEmpty()) {
            sql.append(" WHERE ").append(whereClause);
        }
    }

    /** Binds params to PreparedStatement.
     * @throws SQLException if bind fails. */
    protected void setValues(PreparedStatement stmt, Object[] values) throws SQLException {
        if (values == null) return;
        for (int i = 0; i < values.length; i++) {
            stmt.setObject(i + 1, values[i]);
        }
    }
}