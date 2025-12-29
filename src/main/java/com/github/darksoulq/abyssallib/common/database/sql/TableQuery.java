package com.github.darksoulq.abyssallib.common.database.sql;

import com.github.darksoulq.abyssallib.common.database.AbstractTableQuery;
import java.sql.Connection;
import java.util.concurrent.ExecutorService;

public class TableQuery extends AbstractTableQuery<TableQuery> {
    public TableQuery(Connection conn, String table, ExecutorService asyncPool) {
        super(conn, table, asyncPool);
    }

    public BatchQuery batch(String... columns) {
        return new BatchQuery(connection, table, asyncPool, columns);
    }

    @Override protected String getInsertVerb() { return "INSERT INTO "; }
    @Override protected String getReplaceVerb() { return "INSERT OR REPLACE INTO "; }
}