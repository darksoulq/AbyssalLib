package com.github.darksoulq.abyssallib.common.database.relational.h2;

import com.github.darksoulq.abyssallib.common.database.relational.AbstractTableBuilder;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * H2-specific implementation for building table schemas.
 */
public class TableBuilder extends AbstractTableBuilder<TableBuilder> {

    /**
     * Constructs a TableBuilder for H2.
     *
     * @param database The database instance.
     * @param table    The table name.
     */
    public TableBuilder(Database database, String table) {
        super(wrapConn(database), table);
    }

    /**
     * Helper to wrap connection retrieval for the super constructor.
     *
     * @param db The database.
     * @return The connection.
     * @throws RuntimeException if connection fails.
     */
    private static Connection wrapConn(Database db) {
        try { return db.getConnection(); } catch(SQLException e) { throw new RuntimeException(e); }
    }

    /** @return The {@code AUTO_INCREMENT} keyword. */
    @Override protected String getAutoIncrementKeyword() { return "AUTO_INCREMENT"; }
    /** @return An empty string, as H2 doesn't require extra engine options. */
    @Override protected String getTableOptionsSuffix() { return ""; }
}