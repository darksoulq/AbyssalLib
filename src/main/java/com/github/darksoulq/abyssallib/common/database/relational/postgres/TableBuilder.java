package com.github.darksoulq.abyssallib.common.database.relational.postgres;

import com.github.darksoulq.abyssallib.common.database.relational.AbstractTableBuilder;

import java.sql.Connection;
import java.sql.SQLException;

public class TableBuilder extends AbstractTableBuilder<TableBuilder> {

    public TableBuilder(Database database, String table) {
        super(wrapConn(database), table);
    }

    private static Connection wrapConn(Database db) {
        try { return db.getConnection(); } catch(SQLException e) { throw new RuntimeException(e); }
    }

    @Override protected String getAutoIncrementKeyword() { return "SERIAL PRIMARY KEY"; }
    @Override protected String getTableOptionsSuffix() { return ""; }
}