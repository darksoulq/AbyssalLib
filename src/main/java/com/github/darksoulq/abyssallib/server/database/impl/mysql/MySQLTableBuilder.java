package com.github.darksoulq.abyssallib.server.database.impl.mysql;

import com.github.darksoulq.abyssallib.server.database.TableBuilder;

import java.sql.Connection;
import java.sql.Statement;
import java.util.*;

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
     * List of column definitions.
     */
    private final List<String> columns = new ArrayList<>();

    /**
     * List of primary key column names.
     */
    private final List<String> primaryKeys = new ArrayList<>();

    /**
     * List of foreign key constraints.
     */
    private final List<String> foreignKeys = new ArrayList<>();

    /**
     * List of unique constraints.
     */
    private final List<String> uniqueConstraints = new ArrayList<>();

    /**
     * List of check constraints.
     */
    private final List<String> checkConstraints = new ArrayList<>();

    /**
     * Map of column names to their default values.
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
     * Adds IF NOT EXISTS to the CREATE TABLE clause.
     *
     * @return this builder instance
     */
    @Override
    public TableBuilder ifNotExists() {
        this.ifNotExists = true;
        return this;
    }

    /**
     * Adds a column to the table.
     *
     * @param name the column name
     * @param type the SQL data type
     * @return this builder instance
     */
    @Override
    public TableBuilder column(String name, String type) {
        if (name == null || name.isBlank() || type == null || type.isBlank()) {
            throw new IllegalArgumentException("Column name and type must be non-empty.");
        }

        StringBuilder column = new StringBuilder(name).append(" ").append(type);
        if (defaultValues.containsKey(name)) {
            column.append(" DEFAULT ").append(defaultValues.get(name));
        }

        columns.add(column.toString());
        return this;
    }

    /**
     * Defines one or more primary key columns.
     *
     * @param keys the primary key column names
     * @return this builder instance
     */
    @Override
    public TableBuilder primaryKey(String... keys) {
        Collections.addAll(primaryKeys, keys);
        return this;
    }

    /**
     * Adds a foreign key constraint.
     *
     * @param column           the referencing column
     * @param referencesTable  the referenced table
     * @param referencesColumn the referenced column
     * @return this builder instance
     */
    @Override
    public TableBuilder foreignKey(String column, String referencesTable, String referencesColumn) {
        if (column == null || referencesTable == null || referencesColumn == null) {
            throw new IllegalArgumentException("Foreign key parameters must not be null.");
        }

        foreignKeys.add("FOREIGN KEY (" + column + ") REFERENCES " + referencesTable + " (" + referencesColumn + ")");
        return this;
    }

    /**
     * Adds a unique constraint to the given columns.
     *
     * @param columns the column names
     * @return this builder instance
     */
    @Override
    public TableBuilder unique(String... columns) {
        if (columns.length > 0) {
            uniqueConstraints.add("UNIQUE (" + String.join(", ", columns) + ")");
        }
        return this;
    }

    /**
     * Adds a check constraint to the table.
     *
     * @param expression the SQL condition
     * @return this builder instance
     */
    @Override
    public TableBuilder check(String expression) {
        if (expression != null && !expression.isBlank()) {
            checkConstraints.add("CHECK (" + expression + ")");
        }
        return this;
    }

    /**
     * Specifies a default value for the given column.
     *
     * @param column       the column name
     * @param defaultValue the SQL literal default value
     * @return this builder instance
     */
    @Override
    public TableBuilder defaultValue(String column, String defaultValue) {
        if (column == null || defaultValue == null) {
            throw new IllegalArgumentException("Default value parameters must not be null.");
        }

        defaultValues.put(column, defaultValue);
        return this;
    }

    /**
     * Builds and executes the CREATE TABLE SQL statement.
     *
     * @throws RuntimeException if SQL execution fails
     */
    @Override
    public void execute() {
        if (columns.isEmpty()) {
            throw new IllegalStateException("Cannot create table with no columns.");
        }

        StringBuilder sql = new StringBuilder("CREATE TABLE ");
        if (ifNotExists) sql.append("IF NOT EXISTS ");
        sql.append(table).append(" (");

        List<String> allDefinitions = new ArrayList<>(columns);

        if (!primaryKeys.isEmpty()) {
            allDefinitions.add("PRIMARY KEY (" + String.join(", ", primaryKeys) + ")");
        }
        if (!foreignKeys.isEmpty()) {
            allDefinitions.addAll(foreignKeys);
        }
        if (!uniqueConstraints.isEmpty()) {
            allDefinitions.addAll(uniqueConstraints);
        }
        if (!checkConstraints.isEmpty()) {
            allDefinitions.addAll(checkConstraints);
        }

        sql.append(String.join(", ", allDefinitions)).append(")");

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql.toString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to create table '" + table + "': " + e.getMessage(), e);
        }
    }
}
