package com.github.darksoulq.abyssallib.common.database.relational.mysql;

import com.github.darksoulq.abyssallib.common.database.relational.AbstractTableBuilder;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A MySQL-specific implementation of {@link AbstractTableBuilder}.
 * Configured with MySQL auto-increment syntax and UTF-8mb4 character sets.
 */
public class TableBuilder extends AbstractTableBuilder<TableBuilder> {

    /**
     * Constructs a new TableBuilder for MySQL.
     *
     * @param database The {@link Database} instance.
     * @param table    The table name.
     */
    public TableBuilder(Database database, String table) {
        super(wrapConn(database), table);
    }

    /**
     * Internal helper to extract a connection.
     *
     * @param db The database manager.
     * @return A valid connection.
     * @throws RuntimeException if a database error occurs.
     */
    private static Connection wrapConn(Database db) {
        try { return db.getConnection(); } catch(SQLException e) { throw new RuntimeException(e); }
    }

    /**
     * Returns the MySQL auto-increment definition.
     * @return {@code "INT PRIMARY KEY AUTO_INCREMENT"}
     */
    @Override protected String getAutoIncrementKeyword() { return "INT PRIMARY KEY AUTO_INCREMENT"; }

    /**
     * Returns the default MySQL table options, enforcing UTF-8mb4 for full emoji support.
     * @return The collation and character set SQL string.
     */
    @Override protected String getTableOptionsSuffix() { return " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci"; }
}