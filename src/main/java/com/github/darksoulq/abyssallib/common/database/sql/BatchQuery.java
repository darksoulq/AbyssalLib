package com.github.darksoulq.abyssallib.common.database.sql;

import com.github.darksoulq.abyssallib.common.database.AbstractBatchQuery;
import java.sql.Connection;
import java.util.concurrent.ExecutorService;

public class BatchQuery extends AbstractBatchQuery<BatchQuery> {
    public BatchQuery(Connection connection, String table, ExecutorService asyncPool, String... columns) {
        super(connection, table, asyncPool, columns);
    }

    @Override protected String getInsertVerb() { return "INSERT INTO "; }
    @Override protected String getReplaceVerb() { return "INSERT OR REPLACE INTO "; }
    @Override protected String getInsertIgnoreVerb() { return "INSERT OR IGNORE INTO "; }
}