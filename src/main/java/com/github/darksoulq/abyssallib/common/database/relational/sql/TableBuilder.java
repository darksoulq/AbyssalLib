package com.github.darksoulq.abyssallib.common.database.relational.sql;

import com.github.darksoulq.abyssallib.common.database.relational.AbstractTableBuilder;

import java.sql.Connection;

/**
 * A SQLite-specific implementation of {@link AbstractTableBuilder}.
 * Handles the specific auto-increment syntax used by SQLite.
 */
public class TableBuilder extends AbstractTableBuilder<TableBuilder> {
    /**
     * Constructs a new TableBuilder for SQLite.
     *
     * @param conn  The JDBC {@link Connection}.
     * @param table The name of the table to build.
     */
    public TableBuilder(Connection conn, String table) {
        super(conn, table);
    }

    /**
     * Returns the SQLite auto-increment keyword definition.
     * In SQLite, this typically involves defining the column as an INTEGER PRIMARY KEY.
     *
     * @return {@code "INTEGER PRIMARY KEY AUTOINCREMENT"}
     */
    @Override protected String getAutoIncrementKeyword() { return "INTEGER PRIMARY KEY AUTOINCREMENT"; }

    /**
     * Returns the table options suffix for SQLite.
     * SQLite generally does not require engine specifications like MySQL.
     *
     * @return An empty string.
     */
    @Override protected String getTableOptionsSuffix() { return ""; }
}