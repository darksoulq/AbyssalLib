package com.github.darksoulq.abyssallib.common.database.mysql;

import com.github.darksoulq.abyssallib.common.database.AbstractBatchQuery;
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
    @Override protected String getReplaceVerb() { return "REPLACE INTO "; }
    @Override protected String getInsertIgnoreVerb() { return "INSERT IGNORE INTO "; }
}