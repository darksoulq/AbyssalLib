package com.github.darksoulq.abyssallib.common.database.relational.mysql;

import com.github.darksoulq.abyssallib.common.database.relational.AbstractBatchQuery;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A MySQL-specific implementation of {@link AbstractBatchQuery}.
 * Utilizes standard MySQL batch syntax including {@code REPLACE} and {@code INSERT IGNORE}.
 */
public class BatchQuery extends AbstractBatchQuery<BatchQuery> {

    /**
     * Constructs a new MySQL BatchQuery.
     *
     * @param database The {@link Database} instance providing the connection and pool.
     * @param table    The name of the target table.
     * @param columns  The column names to be included in the batch operation.
     */
    public BatchQuery(Database database, String table, String... columns) {
        super(wrapConn(database), table, database.getAsyncPool(), columns);
    }

    /**
     * Internal helper to extract a connection from the database instance for the super constructor.
     *
     * @param db The {@link Database} instance.
     * @return A valid {@link Connection}.
     * @throws RuntimeException if a {@link SQLException} occurs during retrieval.
     */
    private static Connection wrapConn(Database db) {
        try { return db.getConnection(); } catch(SQLException e) { throw new RuntimeException(e); }
    }

    /**
     * Returns the MySQL-specific verb for insertion.
     * @return {@code "INSERT INTO "}
     */
    @Override protected String getInsertVerb() { return "INSERT INTO "; }

    /**
     * Returns the MySQL-specific verb for replacement.
     * @return {@code "REPLACE INTO "}
     */
    @Override protected String getReplaceVerb() { return "REPLACE INTO "; }

    /**
     * Returns the MySQL-specific verb for ignoring duplicates.
     * @return {@code "INSERT IGNORE INTO "}
     */
    @Override protected String getInsertIgnoreVerb() { return "INSERT IGNORE INTO "; }
}