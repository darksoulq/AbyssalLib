package com.github.darksoulq.abyssallib.common.database.sql;

import com.github.darksoulq.abyssallib.common.database.AbstractTableBuilder;
import java.sql.Connection;

public class TableBuilder extends AbstractTableBuilder<TableBuilder> {
    public TableBuilder(Connection conn, String table) {
        super(conn, table);
    }

    @Override protected String getAutoIncrementKeyword() { return "INTEGER PRIMARY KEY AUTOINCREMENT"; }
    @Override protected String getTableOptionsSuffix() { return ""; }
}