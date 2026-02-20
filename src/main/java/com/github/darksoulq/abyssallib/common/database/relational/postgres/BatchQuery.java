package com.github.darksoulq.abyssallib.common.database.relational.postgres;

import com.github.darksoulq.abyssallib.common.database.relational.AbstractBatchQuery;

import java.sql.Connection;
import java.sql.SQLException;

public class BatchQuery extends AbstractBatchQuery<BatchQuery> {

    public BatchQuery(Database database, String table, String... columns) {
        super(wrapConn(database), table, database.getAsyncPool(), columns);
    }

    private static Connection wrapConn(Database db) {
        try { return db.getConnection(); } catch(SQLException e) { throw new RuntimeException(e); }
    }

    @Override protected String getInsertVerb() { return "INSERT INTO "; }
    @Override protected String getReplaceVerb() { throw new UnsupportedOperationException("PostgreSQL uses ON CONFLICT DO UPDATE instead of REPLACE INTO. Use a custom query for this."); }
    @Override protected String getInsertIgnoreVerb() { return "INSERT INTO "; }
}