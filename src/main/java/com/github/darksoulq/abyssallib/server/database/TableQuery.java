package com.github.darksoulq.abyssallib.server.database;

import java.util.List;

/**
 * Represents a fluent query interface for performing basic SQL operations
 * such as INSERT, UPDATE, DELETE, and SELECT on a specific table.
 */
public interface TableQuery {

    /**
     * Begins an INSERT operation on the target table.
     *
     * @return this query instance for chaining
     */
    TableQuery insert();

    /**
     * Begins an UPDATE operation on the target table.
     *
     * @return this query instance for chaining
     */
    TableQuery update();

    /**
     * Begins a DELETE operation on the target table.
     *
     * @return this query instance for chaining
     */
    TableQuery delete();

    /**
     * Begins a CREATE TABLE operation using a {@link TableBuilder}.
     *
     * @return a {@link TableBuilder} for defining the table schema
     */
    TableBuilder create();

    /**
     * Sets a value for a column in an INSERT or UPDATE operation.
     *
     * @param column the column name
     * @param value the value to assign
     * @return this query instance for chaining
     */
    TableQuery value(String column, Object value);

    /**
     * Adds a WHERE clause to the query.
     *
     * @param clause the SQL condition (e.g. "id = ?")
     * @param params the parameters to bind to the placeholders in the clause
     * @return this query instance for chaining
     */
    TableQuery where(String clause, Object... params);

    /**
     * Executes the INSERT, UPDATE, or DELETE query.
     *
     * @return the number of rows affected
     */
    int execute();

    /**
     * Executes a SELECT query and maps the results using the given mapper.
     *
     * @param mapper the function that maps each {@link java.sql.ResultSet} row to a custom object
     * @param <T> the type of the result object
     * @return a list of mapped results
     */
    <T> List<T> select(ResultMapper<T> mapper);
}
