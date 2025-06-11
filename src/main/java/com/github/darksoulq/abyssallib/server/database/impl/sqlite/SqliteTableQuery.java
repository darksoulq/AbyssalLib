package com.github.darksoulq.abyssallib.server.database.impl.sqlite;

import com.github.darksoulq.abyssallib.server.database.ResultMapper;
import com.github.darksoulq.abyssallib.server.database.TableBuilder;
import com.github.darksoulq.abyssallib.server.database.TableQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * SQLite implementation of {@link TableQuery} that supports basic
 * CRUD operations on a single table.
 */
public class SqliteTableQuery implements TableQuery {
    /**
     * The database connection.
     */
    private final Connection conn;
    /**
     * The name of the table being queried.
     */
    private final String table;
    /**
     * The type of operation to execute ("INSERT", "UPDATE", "DELETE").
     */
    private String type = "INSERT";
    /**
     * The values to insert or update, mapped by column name.
     */
    private final Map<String, Object> values = new LinkedHashMap<>();
    /**
     * Optional WHERE clause used in UPDATE, DELETE, or SELECT operations.
     */
    private String whereClause = null;
    /**
     * Parameters to bind in the WHERE clause.
     */
    private Object[] whereParams = new Object[0];

    /**
     * Constructs a new query for the given table using the provided connection.
     *
     * @param conn  the SQLite connection
     * @param table the table name
     */
    public SqliteTableQuery(Connection conn, String table) {
        this.conn = conn;
        this.table = table;
    }

    /** {@inheritDoc} */
    @Override public TableQuery insert() { this.type = "INSERT"; return this; }
    /** {@inheritDoc} */
    @Override public TableQuery update() { this.type = "UPDATE"; return this; }
    /** {@inheritDoc} */
    @Override public TableQuery delete() { this.type = "DELETE"; return this; }
    /** {@inheritDoc} */
    @Override public TableBuilder create() { return new SqliteTableBuilder(conn, table); }

    /**
     * Adds a column-value pair for INSERT or UPDATE operations.
     *
     * @param column the column name
     * @param value  the value to insert or update
     */
    @Override
    public TableQuery value(String column, Object value) {
        values.put(column, value);
        return this;
    }

    /**
     * Adds a WHERE clause with optional parameter bindings.
     *
     * @param clause the SQL WHERE clause
     * @param params the values to bind to the placeholders in the clause
     */
    @Override
    public TableQuery where(String clause, Object... params) {
        this.whereClause = clause;
        this.whereParams = params;
        return this;
    }

    /**
     * Executes the INSERT, UPDATE, or DELETE operation based on the current query type.
     *
     * @return the number of affected rows
     */
    @Override
    public int execute() {
        try {
            return switch (type) {
                case "INSERT" -> executeInsert();
                case "UPDATE" -> executeUpdate();
                case "DELETE" -> executeDelete();
                default -> 0;
            };
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Executes an INSERT OR REPLACE operation.
     */
    private int executeInsert() throws SQLException {
        StringBuilder sql = new StringBuilder("INSERT OR REPLACE INTO " + table + " (");
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

    /**
     * Executes an UPDATE operation.
     */
    private int executeUpdate() throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE " + table + " SET ");
        StringJoiner updates = new StringJoiner(", ");
        for (String col : values.keySet()) {
            updates.add(col + " = ?");
        }
        sql.append(updates);
        if (whereClause != null) sql.append(" WHERE ").append(whereClause);

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            List<Object> all = new ArrayList<>(values.values());
            all.addAll(List.of(whereParams));
            setValues(stmt, all.toArray());
            return stmt.executeUpdate();
        }
    }

    /**
     * Executes a DELETE operation.
     */
    private int executeDelete() throws SQLException {
        StringBuilder sql = new StringBuilder("DELETE FROM " + table);
        if (whereClause != null) sql.append(" WHERE ").append(whereClause);

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            setValues(stmt, whereParams);
            return stmt.executeUpdate();
        }
    }

    /**
     * Executes a SELECT query and maps each row using the provided mapper.
     *
     * @param mapper the result set mapper
     * @param <T>    the type to map to
     * @return a list of mapped results
     */
    @Override
    public <T> List<T> select(ResultMapper<T> mapper) {
        List<T> results = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM " + table);
        if (whereClause != null) sql.append(" WHERE ").append(whereClause);

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            setValues(stmt, whereParams);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) results.add(mapper.map(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return results;
    }

    /**
     * Binds the provided values to a {@link PreparedStatement}.
     *
     * @param stmt   the statement to bind values to
     * @param values the values to bind
     */
    private void setValues(PreparedStatement stmt, Object[] values) throws SQLException {
        for (int i = 0; i < values.length; i++) {
            stmt.setObject(i + 1, values[i]);
        }
    }
}
