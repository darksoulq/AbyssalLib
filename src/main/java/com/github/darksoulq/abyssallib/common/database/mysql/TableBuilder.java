package com.github.darksoulq.abyssallib.common.database.mysql;

import com.github.darksoulq.abyssallib.common.database.AbstractTableBuilder;
import java.sql.Connection;
import java.sql.SQLException;

public class TableBuilder extends AbstractTableBuilder<TableBuilder> {
    public TableBuilder(Database database, String table) {
        super(wrapConn(database), table);
    }
    private static Connection wrapConn(Database db) {
        try { return db.getConnection(); } catch(SQLException e) { throw new RuntimeException(e); }
    }

    @Override protected String getAutoIncrementKeyword() { return "INT PRIMARY KEY AUTO_INCREMENT"; }
    @Override protected String getTableOptionsSuffix() { return " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci"; }
}