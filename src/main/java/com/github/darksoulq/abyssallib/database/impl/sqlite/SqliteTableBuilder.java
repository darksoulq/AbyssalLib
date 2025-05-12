package com.github.darksoulq.abyssallib.database.impl.sqlite;

import com.github.darksoulq.abyssallib.database.TableBuilder;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SQLite implementation of {@link TableBuilder} for dynamically building and creating tables.
 */
public class SqliteTableBuilder implements TableBuilder {
    /**
     * The SQLite database connection.
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
     * Constructs a new {@code SqliteTableBuilder} for the specified table.
     *
     * @param conn  the SQLite connection
     * @param table the table name
     */
    public SqliteTableBuilder(Connection conn, String table) {
        this.conn = conn;
        this.table = table;
    }

    /**
     * Adds the IF NOT EXISTS clause to the CREATE TABLE statement.
     *
     * @return this builder instance
     */
    @Override
    public TableBuilder ifNotExists() {
        this.ifNotExists = true;
        return this;
    }

    /**
     * Adds a column definition to the table.
     *
     * @param name the column name
     * @param type the column SQL type
     * @return this builder instance
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
     * Sets the primary key columns for the table.
     *
     * @param columns the names of the primary key columns
     * @return this builder instance
     */
    @Override
    public TableBuilder primaryKey(String... keys) {
        primaryKeys.addAll(List.of(keys));
        return this;
    }

    /**
     * Adds a foreign key constraint to the table.
     *
     * @param column           the column name in this table
     * @param referencesTable  the referenced table name
     * @param referencesColumn the referenced column name
     * @return this builder instance
     */
    @Override
    public TableBuilder foreignKey(String column, String referencesTable, String referencesColumn) {
        foreignKeys.add("FOREIGN KEY (" + column + ") REFERENCES " + referencesTable + " (" + referencesColumn + ")");
        return this;
    }

    /**
     * Adds a unique constraint to the specified columns.
     *
     * @param columns the column names to enforce uniqueness on
     * @return this builder instance
     */
    @Override
    public TableBuilder unique(String... columns) {
        uniqueColumns.add("UNIQUE (" + String.join(", ", columns) + ")");
        return this;
    }

    /**
     * Adds a check constraint to the table.
     *
     * @param expression the SQL check expression
     * @return this builder instance
     */
    @Override
    public TableBuilder check(String expression) {
        checkConstraints.add("CHECK (" + expression + ")");
        return this;
    }

    /**
     * Sets a default value for a column.
     *
     * @param column       the column name
     * @param defaultValue the default value (as SQL literal)
     * @return this builder instance
     */
    @Override
    public TableBuilder defaultValue(String column, String defaultValue) {
        defaultValues.put(column, defaultValue);
        return this;
    }

    /**
     * Executes the constructed CREATE TABLE SQL statement against the database.
     * Throws a {@link RuntimeException} if execution fails.
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
