package com.github.darksoulq.abyssallib.database.impl.mysql;

import com.github.darksoulq.abyssallib.database.TableBuilder;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the {@link TableBuilder} interface for MySQL.
 * Used to construct and execute SQL queries to create a table with various constraints.
 */
public class MySQLTableBuilder implements TableBuilder {
    /**
     * The MySQL database connection.
     */
    private final Connection conn;
    /**
     * The name of the table to create.
     */
    private final String table;
    /**
     * Whether to include IF NOT EXISTS clause in the CREATE TABLE statement.
     */
    private boolean ifNotExists = false;
    /**
     * The list of column definitions.
     */
    private final List<String> columns = new ArrayList<>();
    /**
     * The list of primary key column names.
     */
    private final List<String> primaryKeys = new ArrayList<>();
    /**
     * The list of foreign key constraints.
     */
    private final List<String> foreignKeys = new ArrayList<>();
    /**
     * The list of unique constraints.
     */
    private final List<String> uniqueColumns = new ArrayList<>();
    /**
     * The list of check constraints.
     */
    private final List<String> checkConstraints = new ArrayList<>();
    /**
     * A map of column names to default values.
     */
    private final Map<String, String> defaultValues = new HashMap<>();

    /**
     * Constructs a new {@link MySQLTableBuilder} instance.
     *
     * @param conn  the JDBC connection to the MySQL database
     * @param table the name of the table to build
     */
    public MySQLTableBuilder(Connection conn, String table) {
        this.conn = conn;
        this.table = table;
    }

    /**
     * Specifies that the table should be created only if it doesn't already exist.
     *
     * @return the current {@link TableBuilder} instance
     */
    @Override
    public TableBuilder ifNotExists() {
        this.ifNotExists = true;
        return this;
    }

    /**
     * Adds a column to the table definition.
     *
     * @param name the name of the column
     * @param type the data type of the column
     * @return the current {@link TableBuilder} instance
     */
    @Override
    public TableBuilder column(String name, String type) {
        StringBuilder columnDef = new StringBuilder(name + " " + type);

        // Apply default value if exists
        if (defaultValues.containsKey(name)) {
            columnDef.append(" DEFAULT ").append(defaultValues.get(name));
        }

        columns.add(columnDef.toString());
        return this;
    }

    /**
     * Adds primary keys to the table definition.
     *
     * @param keys the columns to be used as primary keys
     * @return the current {@link TableBuilder} instance
     */
    @Override
    public TableBuilder primaryKey(String... keys) {
        primaryKeys.addAll(List.of(keys));
        return this;
    }

    /**
     * Adds a foreign key constraint to the table.
     *
     * @param column         the column that references another table
     * @param referencesTable the table being referenced
     * @param referencesColumn the column being referenced in the other table
     * @return the current {@link TableBuilder} instance
     */
    @Override
    public TableBuilder foreignKey(String column, String referencesTable, String referencesColumn) {
        foreignKeys.add("FOREIGN KEY (" + column + ") REFERENCES " + referencesTable + " (" + referencesColumn + ")");
        return this;
    }

    /**
     * Adds unique constraints to the table.
     *
     * @param columns the columns that should have a unique constraint
     * @return the current {@link TableBuilder} instance
     */
    @Override
    public TableBuilder unique(String... columns) {
        uniqueColumns.add("UNIQUE (" + String.join(", ", columns) + ")");
        return this;
    }

    /**
     * Adds a check constraint to the table.
     *
     * @param expression the condition for the check constraint
     * @return the current {@link TableBuilder} instance
     */
    @Override
    public TableBuilder check(String expression) {
        checkConstraints.add("CHECK (" + expression + ")");
        return this;
    }

    /**
     * Specifies a default value for a column.
     *
     * @param column      the column to set the default value for
     * @param defaultValue the default value to set
     * @return the current {@link TableBuilder} instance
     */
    @Override
    public TableBuilder defaultValue(String column, String defaultValue) {
        defaultValues.put(column, defaultValue);
        return this;
    }

    /**
     * Executes the SQL statement to create the table with the defined constraints and options.
     *
     * @throws RuntimeException if the table creation fails
     */
    @Override
    public void execute() {
        StringBuilder sql = new StringBuilder("CREATE TABLE ");
        if (ifNotExists) sql.append("IF NOT EXISTS ");
        sql.append(table).append(" (");

        sql.append(String.join(", ", columns));

        if (!primaryKeys.isEmpty()) {
            sql.append(", PRIMARY KEY(").append(String.join(", ", primaryKeys)).append(")");
        }

        if (!foreignKeys.isEmpty()) {
            sql.append(", ").append(String.join(", ", foreignKeys));
        }

        if (!uniqueColumns.isEmpty()) {
            sql.append(", ").append(String.join(", ", uniqueColumns));
        }

        if (!checkConstraints.isEmpty()) {
            sql.append(", ").append(String.join(", ", checkConstraints));
        }

        sql.append(")");

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql.toString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to create table: " + table, e);
        }
    }
}
