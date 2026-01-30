package com.github.darksoulq.abyssallib.common.database.sql;

import com.github.darksoulq.abyssallib.common.database.AbstractBatchQuery;
import java.sql.Connection;
import java.util.concurrent.ExecutorService;

/**
 * A SQLite-specific implementation of {@link AbstractBatchQuery}.
 * This class provides the necessary SQL verbs for batch operations compatible with SQLite syntax.
 */
public class BatchQuery extends AbstractBatchQuery<BatchQuery> {
    /**
     * Constructs a new BatchQuery for SQLite.
     *
     * @param connection The JDBC {@link Connection} to the SQLite database.
     * @param table      The name of the target table.
     * @param asyncPool  The {@link ExecutorService} used for asynchronous execution.
     * @param columns    The specific column names to be included in the batch operation.
     */
    public BatchQuery(Connection connection, String table, ExecutorService asyncPool, String... columns) {
        super(connection, table, asyncPool, columns);
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

    /**
     * Returns the SQLite-specific verb for ignoring duplicates.
     * @return {@code "INSERT OR IGNORE INTO "}
     */
    @Override protected String getInsertIgnoreVerb() { return "INSERT OR IGNORE INTO "; }
}