package com.github.darksoulq.abyssallib.common.database;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A fluent-style builder for programmatically creating database tables.
 * This class handles column definitions, keys, constraints, and table options,
 * abstracting away the differences between SQL dialects.
 *
 * @param <T> The implementation type for fluent chaining.
 */
public abstract class AbstractTableBuilder<T extends AbstractTableBuilder<T>> {
    /** The connection used to execute the table creation. */
    protected final Connection connection;
    /** The name of the table to create. */
    protected final String table;
    /** If true, adds "IF NOT EXISTS" to the SQL statement. */
    protected boolean ifNotExists = false;
    /** List of raw column definition strings. */
    protected final List<String> columns = new ArrayList<>();
    /** List of columns designated as primary keys. */
    protected final List<String> primaryKeys = new ArrayList<>();
    /** List of foreign key constraint definitions. */
    protected final List<String> foreignKeys = new ArrayList<>();
    /** List of unique constraint definitions. */
    protected final List<String> uniqueColumns = new ArrayList<>();
    /** List of check constraint expressions. */
    protected final List<String> checkConstraints = new ArrayList<>();
    /** Map of column names to their defined default values. */
    protected final Map<String, String> defaultValues = new HashMap<>();

    /**
     * Constructs a new AbstractTableBuilder.
     *
     * @param connection The JDBC connection to use.
     * @param table      The name of the table to build.
     */
    public AbstractTableBuilder(Connection connection, String table) {
        this.connection = connection;
        this.table = table;
    }

    /**
     * Configures the builder to include the "IF NOT EXISTS" clause.
     * @return The current instance cast to {@code T}.
     */
    @SuppressWarnings("unchecked")
    public T ifNotExists() { this.ifNotExists = true; return (T) this; }

    /**
     * Defines a column in the table.
     *
     * @param name The column name.
     * @param type The SQL data type string (e.g., "INT", "TEXT", "VARCHAR(32)").
     * @return The current instance cast to {@code T}.
     */
    @SuppressWarnings("unchecked")
    public T column(String name, String type) {
        StringBuilder columnDef = new StringBuilder(name + " " + type);
        if (defaultValues.containsKey(name)) {
            columnDef.append(" DEFAULT ").append(defaultValues.get(name));
        }
        columns.add(columnDef.toString());
        return (T) this;
    }

    /**
     * Sets one or more columns as the table's primary key.
     *
     * @param keys The column names to include in the primary key.
     * @return The current instance cast to {@code T}.
     */
    @SuppressWarnings("unchecked")
    public T primaryKey(String... keys) {
        primaryKeys.addAll(List.of(keys));
        return (T) this;
    }

    /**
     * Returns the SQL keyword used for auto-incrementing columns.
     * @return A string like "AUTO_INCREMENT" (MySQL) or "AUTOINCREMENT" (SQLite).
     */
    protected abstract String getAutoIncrementKeyword();

    /**
     * Returns the table-level options suffix.
     * @return A string such as " ENGINE=InnoDB" or an empty string.
     */
    protected abstract String getTableOptionsSuffix();

    /**
     * Redefines a column to include the auto-increment property.
     *
     * @param column The name of the column to modify.
     * @return The current instance cast to {@code T}.
     */
    @SuppressWarnings("unchecked")
    public T autoIncrement(String column) {
        columns.removeIf(s -> s.startsWith(column + " "));
        columns.add(column + " " + getAutoIncrementKeyword());
        return (T) this;
    }

    /**
     * Adds a foreign key constraint to the table.
     *
     * @param column           The local column name.
     * @param referencesTable  The table name being referenced.
     * @param referencesColumn The column name being referenced in the target table.
     * @return The current instance cast to {@code T}.
     */
    @SuppressWarnings("unchecked")
    public T foreignKey(String column, String referencesTable, String referencesColumn) {
        foreignKeys.add("FOREIGN KEY (" + column + ") REFERENCES " + referencesTable + " (" + referencesColumn + ")");
        return (T) this;
    }

    /**
     * Adds a unique constraint on the specified columns.
     *
     * @param columns The columns that must contain unique data.
     * @return The current instance cast to {@code T}.
     */
    @SuppressWarnings("unchecked")
    public T unique(String... columns) {
        uniqueColumns.add("UNIQUE (" + String.join(", ", columns) + ")");
        return (T) this;
    }

    /**
     * Adds a CHECK constraint to ensure valid data.
     *
     * @param expression The SQL boolean expression to check.
     * @return The current instance cast to {@code T}.
     */
    @SuppressWarnings("unchecked")
    public T check(String expression) {
        checkConstraints.add("CHECK (" + expression + ")");
        return (T) this;
    }

    /**
     * Assigns a default value to a column.
     *
     * @param column       The name of the column.
     * @param defaultValue The default value as a string (e.g., "'Unknown'" or "0").
     * @return The current instance cast to {@code T}.
     */
    @SuppressWarnings("unchecked")
    public T defaultValue(String column, String defaultValue) {
        defaultValues.put(column, defaultValue);
        return (T) this;
    }

    /**
     * Drops the table if it exists in the database.
     * @throws RuntimeException If the drop operation fails.
     */
    public void dropIfExists() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS " + table);
        } catch (Exception e) {
            throw new RuntimeException("Failed to drop table: " + table, e);
        }
    }

    /**
     * Compiles and executes the CREATE TABLE SQL statement based on the provided configuration.
     * @throws RuntimeException If the table creation fails.
     */
    public void execute() {
        StringBuilder sql = new StringBuilder("CREATE TABLE ");
        if (ifNotExists) sql.append("IF NOT EXISTS ");
        sql.append(table).append(" (");
        sql.append(String.join(", ", columns));
        if (!primaryKeys.isEmpty()) {
            sql.append(", PRIMARY KEY(").append(String.join(", ", primaryKeys)).append(")");
        }
        if (!foreignKeys.isEmpty()) sql.append(", ").append(String.join(", ", foreignKeys));
        if (!uniqueColumns.isEmpty()) sql.append(", ").append(String.join(", ", uniqueColumns));
        if (!checkConstraints.isEmpty()) sql.append(", ").append(String.join(", ", checkConstraints));
        sql.append(")").append(getTableOptionsSuffix());

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql.toString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to create table: " + table, e);
        }
    }
}