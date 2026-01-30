package com.github.darksoulq.abyssallib.common.database.sql;

import com.github.darksoulq.abyssallib.common.database.AbstractTableQuery;
import java.sql.Connection;
import java.util.concurrent.ExecutorService;

/**
 * A SQLite-specific implementation of {@link AbstractTableQuery}.
 * Provides support for SQLite-specific verbs and allows transition into batch query operations.
 */
public class TableQuery extends AbstractTableQuery<TableQuery> {
    /**
     * Constructs a new TableQuery for SQLite.
     *
     * @param conn      The JDBC {@link Connection}.
     * @param table     The name of the table to query.
     * @param asyncPool The {@link ExecutorService} for asynchronous operations.
     */
    public TableQuery(Connection conn, String table, ExecutorService asyncPool) {
        super(conn, table, asyncPool);
    }

    /**
     * Transitions this query into a batch operation for the current table.
     *
     * @param columns The column names to be included in the batch.
     * @return A new {@link BatchQuery} instance for the current table.
     */
    public BatchQuery batch(String... columns) {
        return new BatchQuery(connection, table, asyncPool, columns);
    }

    /**
     * Returns the SQLite-specific verb for insertion.
     * @return {@code "INSERT INTO "}
     */
    @Override protected String getInsertVerb() { return "INSERT INTO "; }

    /**
     * Returns the SQLite-specific verb for replacement.
     * @return {@code "INSERT OR REPLACE INTO "}
     */
    @Override protected String getReplaceVerb() { return "INSERT OR REPLACE INTO "; }
}