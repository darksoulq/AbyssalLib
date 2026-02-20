package com.github.darksoulq.abyssallib.common.database.relational.postgres;

import com.github.darksoulq.abyssallib.common.database.relational.AbstractTableQuery;

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
    @Override protected String getReplaceVerb() { throw new UnsupportedOperationException("PostgreSQL uses ON CONFLICT DO UPDATE instead of REPLACE INTO. Use a custom query for this."); }
}