package com.github.darksoulq.abyssallib.common.database.relational.mariadb;

import com.github.darksoulq.abyssallib.common.database.relational.AbstractBatchQuery;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Optimized batch operation handler for MariaDB.
 */
public class BatchQuery extends AbstractBatchQuery<BatchQuery> {

    /**
     * Constructs a BatchQuery for MariaDB.
     *
     * @param database The database instance.
     * @param table    The table name.
     * @param columns  The columns to include in the batch.
     */
    public BatchQuery(Database database, String table, String... columns) {
        super(wrapConn(database), table, database.getAsyncPool(), columns);
    }

    /**
     * Helper to wrap connection retrieval.
     */
    private static Connection wrapConn(Database db) {
        try { return db.getConnection(); } catch(SQLException e) { throw new RuntimeException(e); }
    }

    /** @return Standard {@code "INSERT INTO "} verb. */
    @Override protected String getInsertVerb() { return "INSERT INTO "; }
    /** @return MariaDB {@code "REPLACE INTO "} verb. */
    @Override protected String getReplaceVerb() { return "REPLACE INTO "; }
    /** @return MariaDB {@code "INSERT IGNORE INTO "} verb. */
    @Override protected String getInsertIgnoreVerb() { return "INSERT IGNORE INTO "; }
}