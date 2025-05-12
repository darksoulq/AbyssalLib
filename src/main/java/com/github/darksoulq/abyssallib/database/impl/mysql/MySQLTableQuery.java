package com.github.darksoulq.abyssallib.database.impl.mysql;

import com.github.darksoulq.abyssallib.database.ResultMapper;
import com.github.darksoulq.abyssallib.database.TableBuilder;
import com.github.darksoulq.abyssallib.database.TableQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * MySQL implementation of {@link TableQuery}. Supports basic SQL operations:
 * INSERT, UPDATE, DELETE, SELECT, and CREATE TABLE.
 */
public class MySQLTableQuery implements TableQuery {
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
     * Constructs a new MySQLTableQuery for the given table and connection.
     *
     * @param conn  JDBC connection
     * @param table table name
     */
    public MySQLTableQuery(Connection conn, String table) {
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
    @Override public TableBuilder create() { return new MySQLTableBuilder(conn, table); }

    /**
     * Adds a column-value pair to the current query.
     *
     * @param column the column name
     * @param value  the value to set
     * @return this query
     */
    @Override
    public TableQuery value(String column, Object value) {
        values.put(column, value);
        return this;
    }

    /**
     * Adds a WHERE clause to the current query.
     *
     * @param clause SQL WHERE clause (e.g., "id = ?")
     * @param params parameters to bind to the clause
     * @return this query
     */
    @Override
    public TableQuery where(String clause, Object... params) {
        this.whereClause = clause;
        this.whereParams = params;
        return this;
    }

    /**
     * Executes the INSERT, UPDATE, or DELETE query.
     *
     * @return number of rows affected
     * @throws RuntimeException on SQL failure
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
     * Executes an INSERT query based on accumulated values.
     */
    private int executeInsert() throws SQLException {
        StringBuilder sql = new StringBuilder("INSERT INTO " + table + " (");
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
     * Executes an UPDATE query with optional WHERE clause.
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
     * Executes a DELETE query with optional WHERE clause.
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
     * Executes a SELECT query and maps results using the given mapper.
     *
     * @param mapper mapper to convert {@link ResultSet} rows to objects
     * @return list of mapped results
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
     * Utility method to bind values to a {@link PreparedStatement}.
     *
     * @param stmt   the statement
     * @param values the values to bind
     * @throws SQLException if a binding fails
     */
    private void setValues(PreparedStatement stmt, Object[] values) throws SQLException {
        for (int i = 0; i < values.length; i++) {
            stmt.setObject(i + 1, values[i]);
        }
    }
}
