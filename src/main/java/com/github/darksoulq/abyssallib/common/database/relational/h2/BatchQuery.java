package com.github.darksoulq.abyssallib.common.database.relational.h2;

import com.github.darksoulq.abyssallib.common.database.relational.AbstractBatchQuery;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * H2-specific batch operation handler.
 */
public class BatchQuery extends AbstractBatchQuery<BatchQuery> {

    /**
     * Constructs a BatchQuery for H2.
     *
     * @param database The database instance.
     * @param table    The table name.
     * @param columns  The columns.
     */
    public BatchQuery(Database database, String table, String... columns) {
        super(wrapConn(database), table, database.getAsyncPool(), columns);
    }

    private static Connection wrapConn(Database db) {
        try { return db.getConnection(); } catch(SQLException e) { throw new RuntimeException(e); }
    }

    /** @return {@code "INSERT INTO "} */
    @Override protected String getInsertVerb() { return "INSERT INTO "; }
    /** @return {@code "REPLACE INTO "} */
    @Override protected String getReplaceVerb() { return "REPLACE INTO "; }
    /** @return {@code "INSERT IGNORE INTO "} */
    @Override protected String getInsertIgnoreVerb() { return "INSERT IGNORE INTO "; }
}