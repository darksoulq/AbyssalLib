package com.github.darksoulq.abyssallib.server.database;

/**
 * Represents a fluent interface for building SQL table creation statements.
 *
 * <p>This builder allows the creation of tables with constraints such as primary keys,
 * foreign keys, unique constraints, default values, and check conditions.</p>
 */
public interface TableBuilder {

    /**
     * Specifies that the table should only be created if it does not already exist.
     *
     * @return this builder instance for chaining
     */
    TableBuilder ifNotExists();

    /**
     * Adds a column to the table with the given name and SQL type.
     *
     * @param name the name of the column
     * @param type the SQL data type of the column (e.g. "TEXT", "INTEGER", "VARCHAR(255)")
     * @return this builder instance for chaining
     */
    TableBuilder column(String name, String type);

    /**
     * Sets one or more columns as the primary key for the table.
     *
     * @param columns the column names to include in the primary key
     * @return this builder instance for chaining
     */
    TableBuilder primaryKey(String... columns);

    /**
     * Adds a foreign key constraint to the table.
     *
     * @param column the column in this table that references another table
     * @param referencesTable the referenced table name
     * @param referencesColumn the referenced column name
     * @return this builder instance for chaining
     */
    TableBuilder foreignKey(String column, String referencesTable, String referencesColumn);

    /**
     * Adds a unique constraint for the specified columns.
     *
     * @param columns the column names to enforce uniqueness on
     * @return this builder instance for chaining
     */
    TableBuilder unique(String... columns);

    /**
     * Adds a check constraint to the table.
     *
     * @param expression a SQL expression that must evaluate to true for rows to be valid
     * @return this builder instance for chaining
     */
    TableBuilder check(String expression);

    /**
     * Sets a default value for a column.
     *
     * @param column the column name
     * @param defaultValue the default value expressed as a SQL literal (e.g. "0", "'default text'")
     * @return this builder instance for chaining
     */
    TableBuilder defaultValue(String column, String defaultValue);

    /**
     * Executes the table creation using the current configuration.
     *
     * <p>This will construct and run the SQL <code>CREATE TABLE</code> statement.</p>
     *
     * @throws RuntimeException if table creation fails
     */
    void execute();
}
