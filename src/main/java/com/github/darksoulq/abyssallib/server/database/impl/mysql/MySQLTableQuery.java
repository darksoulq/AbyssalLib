package com.github.darksoulq.abyssallib.server.database.impl.mysql;

import com.github.darksoulq.abyssallib.server.database.ResultMapper;
import com.github.darksoulq.abyssallib.server.database.TableBuilder;
import com.github.darksoulq.abyssallib.server.database.TableQuery;

import java.sql.*;
import java.util.*;

/**
 * MySQL implementation of {@link TableQuery}. Supports INSERT, UPDATE, DELETE, SELECT, and CREATE operations.
 */
public class MySQLTableQuery implements TableQuery {
    /** The database connection. */
    private final Connection conn;

    /** The name of the table being queried. */
    private final String table;

    /** The type of operation to execute ("INSERT", "UPDATE", "DELETE"). */
    private String type = "INSERT";

    /** The values to insert or update, mapped by column name. */
    private final Map<String, Object> values = new LinkedHashMap<>();

    /** Optional WHERE clause used in UPDATE, DELETE, or SELECT operations. */
    private String whereClause = null;

    /** Parameters to bind in the WHERE clause. */
    private Object[] whereParams = new Object[0];

    /**
     * Constructs a new {@link MySQLTableQuery} instance for the given table and connection.
     *
     * @param conn  JDBC connection
     * @param table table name to interact with
     * @throws IllegalArgumentException if table is null or empty
     */
    public MySQLTableQuery(Connection conn, String table) {
        if (conn == null || table == null || table.isBlank()) {
            throw new IllegalArgumentException("Connection and table name must not be null/empty.");
        }
        this.conn = conn;
        this.table = table;
    }

    /** {@inheritDoc} */
    @Override
    public TableQuery insert() {
        this.type = "INSERT";
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public TableQuery update() {
        this.type = "UPDATE";
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public TableQuery delete() {
        this.type = "DELETE";
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public TableBuilder create() {
        return new MySQLTableBuilder(conn, table);
    }

    /**
     * Adds a column-value pair to the current query.
     *
     * @param column the column name
     * @param value  the value to set
     * @return this query instance
     * @throws IllegalArgumentException if column is null or blank
     */
    @Override
    public TableQuery value(String column, Object value) {
        if (column == null || column.isBlank()) {
            throw new IllegalArgumentException("Column name must not be null/empty.");
        }
        values.put(column, value);
        return this;
    }

    /**
     * Adds a WHERE clause to the current query.
     *
     * @param clause SQL WHERE clause (e.g., "id = ?")
     * @param params parameters to bind to the clause
     * @return this query instance
     * @throws IllegalArgumentException if clause is null or blank
     */
    @Override
    public TableQuery where(String clause, Object... params) {
        if (clause == null || clause.isBlank()) {
            throw new IllegalArgumentException("WHERE clause must not be null/empty.");
        }
        this.whereClause = clause;
        this.whereParams = params != null ? params : new Object[0];
        return this;
    }

    /**
     * Executes the configured SQL query (INSERT, UPDATE, or DELETE).
     *
     * @return number of rows affected
     * @throws RuntimeException if execution fails or query type is invalid
     */
    @Override
    public int execute() {
        try {
            return switch (type) {
                case "INSERT" -> executeInsert();
                case "UPDATE" -> executeUpdate();
                case "DELETE" -> executeDelete();
                default -> throw new IllegalStateException("Unknown query type: " + type);
            };
        } catch (SQLException e) {
            throw new RuntimeException("Error executing " + type + " query on table '" + table + "': " + e.getMessage(), e);
        }
    }

    /**
     * Executes an INSERT statement.
     *
     * @return number of rows inserted
     * @throws SQLException if the operation fails
     */
    private int executeInsert() throws SQLException {
        if (values.isEmpty()) throw new IllegalStateException("Cannot execute INSERT with no values.");

        StringJoiner cols = new StringJoiner(", ");
        StringJoiner placeholders = new StringJoiner(", ");
        for (String col : values.keySet()) {
            cols.add(col);
            placeholders.add("?");
        }

        String sql = "INSERT INTO " + table + " (" + cols + ") VALUES (" + placeholders + ")";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            setValues(stmt, values.values().toArray());
            return stmt.executeUpdate();
        }
    }

    /**
     * Executes an UPDATE statement.
     *
     * @return number of rows updated
     * @throws SQLException if the operation fails
     */
    private int executeUpdate() throws SQLException {
        if (values.isEmpty()) throw new IllegalStateException("Cannot execute UPDATE with no values.");

        StringJoiner updates = new StringJoiner(", ");
        for (String col : values.keySet()) {
            updates.add(col + " = ?");
        }

        StringBuilder sql = new StringBuilder("UPDATE ").append(table).append(" SET ").append(updates);
        if (whereClause != null) {
            sql.append(" WHERE ").append(whereClause);
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            List<Object> allParams = new ArrayList<>(values.values());
            allParams.addAll(Arrays.asList(whereParams));
            setValues(stmt, allParams.toArray());
            return stmt.executeUpdate();
        }
    }

    /**
     * Executes a DELETE statement.
     *
     * @return number of rows deleted
     * @throws SQLException if the operation fails
     */
    private int executeDelete() throws SQLException {
        StringBuilder sql = new StringBuilder("DELETE FROM ").append(table);
        if (whereClause != null) {
            sql.append(" WHERE ").append(whereClause);
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            setValues(stmt, whereParams);
            return stmt.executeUpdate();
        }
    }

    /**
     * Executes a SELECT query and maps results using the given mapper.
     *
     * @param mapper mapper to convert {@link ResultSet} rows to objects
     * @param <T>    the mapped result type
     * @return list of mapped results
     * @throws RuntimeException if SQL or mapping fails
     */
    @Override
    public <T> List<T> select(ResultMapper<T> mapper) {
        Objects.requireNonNull(mapper, "ResultMapper cannot be null.");
        List<T> results = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM ").append(table);
        if (whereClause != null) {
            sql.append(" WHERE ").append(whereClause);
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            setValues(stmt, whereParams);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapper.map(rs));
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error executing SELECT on table '" + table + "': " + e.getMessage(), e);
        }

        return results;
    }

    /**
     * Utility method to bind values to a {@link PreparedStatement}.
     *
     * @param stmt   the prepared statement
     * @param values the values to bind
     * @throws SQLException if binding fails
     */
    private void setValues(PreparedStatement stmt, Object[] values) throws SQLException {
        for (int i = 0; i < values.length; i++) {
            stmt.setObject(i + 1, values[i]);
        }
    }
}
