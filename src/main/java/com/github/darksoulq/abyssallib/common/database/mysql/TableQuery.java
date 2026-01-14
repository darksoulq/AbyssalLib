package com.github.darksoulq.abyssallib.common.database.mysql;

import com.github.darksoulq.abyssallib.common.database.AbstractTableQuery;

import java.sql.Connection;
import java.sql.SQLException;

public class TableQuery extends AbstractTableQuery<TableQuery> {
    private final Database database;

    public TableQuery(Database database, String table) {
        super(wrapConn(database), table, database.getAsyncPool());
        this.database = database;
    }
    private static Connection wrapConn(Database db) {
        try { return db.getConnection(); } catch(SQLException e) { throw new RuntimeException(e); }
    }

    public BatchQuery batch(String... columns) {
        return new BatchQuery(database, table, columns);
    }

    @Override protected String getInsertVerb() { return "INSERT INTO "; }
    @Override protected String getReplaceVerb() { return "REPLACE INTO "; }
}