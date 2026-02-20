package com.github.darksoulq.abyssallib.common.database.relational;

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
 * @param <T>
 * The implementation type used for fluent chaining, allowing methods to return the specific subclass type.
 */
public abstract class AbstractTableQuery<T extends AbstractTableQuery<T>> {

    /**
     * The active JDBC connection used for statement execution.
     */
    protected final Connection connection;

    /**
     * The name of the database table to be queried.
     */
    protected final String table;

    /**
     * The thread pool used for executing asynchronous query operations.
     */
    protected final ExecutorService asyncPool;

    /**
     * Internal enumeration representing the intended SQL operation mode.
     */
    protected enum Type {
        /** Represents a standard SQL INSERT operation. */
        INSERT,
        /** Represents a dialect-specific SQL REPLACE or UPSERT operation. */
        REPLACE,
        /** Represents a standard SQL UPDATE operation. */
        UPDATE,
        /** Represents a standard SQL DELETE operation. */
        DELETE
    }

    /**
     * The current operation mode of this query instance.
     */
    protected Type type = Type.INSERT;

    /**
     * Map of column names and their corresponding values for data modification operations.
     */
    protected final Map<String, Object> values = new LinkedHashMap<>();

    /**
     * The SQL string used for filtering rows in the query.
     */
    protected String whereClause = null;

    /**
     * The parameters used to safely bind values to the current {@link #whereClause}.
     */
    protected Object[] whereParams = new Object[0];

    /**
     * The column name utilized for sorting the result set.
     */
    protected String orderByColumn = null;

    /**
     * The direction of the sort; true for ASC (ascending), false for DESC (descending).
     */
    protected boolean orderAscending = true;

    /**
     * Maximum number of results to return from the database.
     */
    protected Integer limit = null;

    /**
     * Number of initial results to skip for pagination purposes.
     */
    protected Integer offset = null;

    /**
     * Constructs a new AbstractTableQuery with the required execution context.
     *
     * @param connection
     * The JDBC connection to be used for executing SQL statements.
     * @param table
     * The name of the target database table.
     * @param asyncPool
     * The executor service for handling asynchronous tasks.
     */
    public AbstractTableQuery(Connection connection, String table, ExecutorService asyncPool) {
        this.connection = connection;
        this.table = table;
        this.asyncPool = asyncPool;
    }

    /**
     * Sets the query operation mode to INSERT.
     *
     * @return
     * The fluent instance cast to the implementation type {@code T}.
     */
    @SuppressWarnings("unchecked")
    public T insert() {
        this.type = Type.INSERT;
        return (T) this;
    }

    /**
     * Sets the query operation mode to REPLACE.
     *
     * @return
     * The fluent instance cast to the implementation type {@code T}.
     */
    @SuppressWarnings("unchecked")
    public T replace() {
        this.type = Type.REPLACE;
        return (T) this;
    }

    /**
     * Sets the query operation mode to UPDATE.
     *
     * @return
     * The fluent instance cast to the implementation type {@code T}.
     */
    @SuppressWarnings("unchecked")
    public T update() {
        this.type = Type.UPDATE;
        return (T) this;
    }

    /**
     * Sets the query operation mode to DELETE.
     *
     * @return
     * The fluent instance cast to the implementation type {@code T}.
     */
    @SuppressWarnings("unchecked")
    public T delete() {
        this.type = Type.DELETE;
        return (T) this;
    }

    /**
     * Adds a column-value pair to be used for INSERT, REPLACE, or UPDATE operations.
     *
     * @param column
     * The name of the target column.
     * @param value
     * The value to be set for the specified column.
     * @return
     * The fluent instance cast to the implementation type {@code T}.
     */
    @SuppressWarnings("unchecked")
    public T value(String column, Object value) {
        values.put(column, value);
        return (T) this;
    }

    /**
     * Configures the filtering criteria for the query using a WHERE clause.
     *
     * @param clause
     * The SQL string for the where clause, using '?' for parameter placeholders.
     * @param params
     * The objects to bind to the placeholders in the provided clause.
     * @return
     * The fluent instance cast to the implementation type {@code T}.
     */
    @SuppressWarnings("unchecked")
    public T where(String clause, Object... params) {
        this.whereClause = clause;
        this.whereParams = params;
        return (T) this;
    }

    /**
     * Sets the ordering criteria for the resulting data set.
     *
     * @param column
     * The column name to sort by.
     * @param ascending
     * Set to true for ascending order, false for descending.
     * @return
     * The fluent instance cast to the implementation type {@code T}.
     */
    @SuppressWarnings("unchecked")
    public T orderBy(String column, boolean ascending) {
        this.orderByColumn = column;
        this.orderAscending = ascending;
        return (T) this;
    }

    /**
     * Sets the maximum number of rows to be returned by the query.
     *
     * @param limit
     * The maximum number of results.
     * @return
     * The fluent instance cast to the implementation type {@code T}.
     */
    @SuppressWarnings("unchecked")
    public T limit(int limit) {
        this.limit = limit;
        return (T) this;
    }

    /**
     * Sets the starting offset for the result set, primarily used for pagination.
     *
     * @param offset
     * The number of rows to skip.
     * @return
     * The fluent instance cast to the implementation type {@code T}.
     */
    @SuppressWarnings("unchecked")
    public T offset(int offset) {
        this.offset = offset;
        return (T) this;
    }

    /**
     * Retrieves the dialect-specific SQL verb used for INSERT operations.
     *
     * @return
     * A string containing the INSERT verb (e.g., "INSERT INTO ").
     */
    protected abstract String getInsertVerb();

    /**
     * Retrieves the dialect-specific SQL verb used for REPLACE operations.
     *
     * @return
     * A string containing the REPLACE verb (e.g., "REPLACE INTO ").
     */
    protected abstract String getReplaceVerb();

    /**
     * Executes the built data modification query synchronously.
     *
     * @return
     * The number of rows affected by the execution.
     * @throws RuntimeException
     * If a {@link SQLException} occurs during execution.
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
     * Asynchronously executes the currently built data modification operation.
     *
     * @return
     * A {@link CompletableFuture} containing the number of rows affected.
     */
    public CompletableFuture<Integer> executeAsync() {
        return CompletableFuture.supplyAsync(this::execute, asyncPool);
    }

    /**
     * Internal logic for executing INSERT and REPLACE statements.
     *
     * @param verb
     * The SQL verb to prepend to the statement.
     * @return
     * The number of rows affected.
     * @throws SQLException
     * If the SQL statement fails to execute.
     */
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

    /**
     * Internal logic for executing UPDATE statements.
     *
     * @return
     * The number of rows affected.
     * @throws SQLException
     * If the SQL statement fails to execute.
     */
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
            if (whereParams != null) {
                all.addAll(List.of(whereParams));
            }
            setValues(stmt, all.toArray());
            return stmt.executeUpdate();
        }
    }

    /**
     * Internal logic for executing DELETE statements.
     *
     * @return
     * The number of rows affected.
     * @throws SQLException
     * If the SQL statement fails to execute.
     */
    private int executeDelete() throws SQLException {
        StringBuilder sql = new StringBuilder("DELETE FROM " + table);
        appendWhereClause(sql);
        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            setValues(stmt, whereParams);
            return stmt.executeUpdate();
        }
    }

    /**
     * Executes a COUNT(*) query based on the current where clause configuration.
     *
     * @return
     * The total count of rows matching the query criteria.
     * @throws RuntimeException
     * If a {@link SQLException} occurs.
     */
    public long count() {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM " + table);
        appendWhereClause(sql);
        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            setValues(stmt, whereParams);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    /**
     * Determines if at least one row matches the query criteria.
     *
     * @return
     * True if the matching count is greater than zero, false otherwise.
     */
    public boolean exists() {
        return count() > 0;
    }

    /**
     * Fetches the first row from the result set and transforms it using the provided mapper.
     *
     * @param <R>
     * The type of the resulting object.
     * @param mapper
     * The {@link ResultMapper} used to convert the {@link ResultSet} row into type R.
     * @return
     * The mapped object, or {@code null} if no matching rows were found.
     */
    public <R> R first(ResultMapper<R> mapper) {
        List<R> list = limit(1).select(mapper);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * Selects all columns (*) from the table and maps the results.
     *
     * @param <R>
     * The type of the resulting objects.
     * @param mapper
     * The mapper used for object transformation.
     * @return
     * A list of mapped results.
     */
    public <R> List<R> select(ResultMapper<R> mapper) {
        return select(mapper, "*");
    }

    /**
     * Selects specific columns and maps the resulting rows into a list of objects.
     *
     * @param <R>
     * The type of the resulting objects.
     * @param mapper
     * The mapper used for object transformation.
     * @param columns
     * The column names to be included in the selection.
     * @return
     * A list of transformed result objects.
     * @throws RuntimeException
     * If an error occurs during execution or mapping.
     */
    public <R> List<R> select(ResultMapper<R> mapper, String... columns) {
        List<R> results = new ArrayList<>();
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

    /**
     * Asynchronously selects all columns and maps the results.
     *
     * @param <R>
     * The target result type.
     * @param mapper
     * The mapper for object conversion.
     * @return
     * A {@link CompletableFuture} containing the list of results.
     */
    public <R> CompletableFuture<List<R>> selectAsync(ResultMapper<R> mapper) {
        return CompletableFuture.supplyAsync(() -> select(mapper), asyncPool);
    }

    /**
     * Asynchronously selects specific columns and maps the results.
     *
     * @param <R>
     * The target result type.
     * @param mapper
     * The mapper for object conversion.
     * @param columns
     * The column names to select.
     * @return
     * A {@link CompletableFuture} containing the list of results.
     */
    public <R> CompletableFuture<List<R>> selectAsync(ResultMapper<R> mapper, String... columns) {
        return CompletableFuture.supplyAsync(() -> select(mapper, columns), asyncPool);
    }

    /**
     * Appends the WHERE clause to the provided SQL builder if criteria have been set.
     *
     * @param sql
     * The StringBuilder constructing the SQL statement.
     */
    protected void appendWhereClause(StringBuilder sql) {
        if (whereClause != null && !whereClause.isEmpty()) {
            sql.append(" WHERE ").append(whereClause);
        }
    }

    /**
     * Binds an array of parameters to a PreparedStatement.
     *
     * @param stmt
     * The statement to bind parameters to.
     * @param values
     * The values to be bound as parameters.
     * @throws SQLException
     * If binding an object to the statement fails.
     */
    protected void setValues(PreparedStatement stmt, Object[] values) throws SQLException {
        if (values == null) {
            return;
        }
        for (int i = 0; i < values.length; i++) {
            stmt.setObject(i + 1, values[i]);
        }
    }
}