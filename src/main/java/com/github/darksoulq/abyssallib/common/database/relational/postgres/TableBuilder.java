package com.github.darksoulq.abyssallib.common.database.relational.postgres;

import com.github.darksoulq.abyssallib.common.database.relational.AbstractTableBuilder;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A PostgreSQL-specific implementation of {@link AbstractTableBuilder}.
 * <p>
 * This class handles the construction of SQL {@code CREATE TABLE} statements,
 * utilizing the {@code SERIAL} keyword for primary key auto-increment functionality
 * common in PostgreSQL schemas.
 */
public class TableBuilder extends AbstractTableBuilder<TableBuilder> {

    /**
     * Constructs a new TableBuilder for a PostgreSQL database.
     *
     * @param database The PostgreSQL {@link Database} instance.
     * @param table    The name of the table to be created.
     */
    public TableBuilder(Database database, String table) {
        super(wrapConn(database), table);
    }

    /**
     * Safely retrieves a connection from the database instance for the super constructor.
     *
     * @param db The {@link Database} instance.
     * @return A valid {@link Connection}.
     * @throws RuntimeException If a database access error occurs.
     */
    private static Connection wrapConn(Database db) {
        try { return db.getConnection(); } catch(SQLException e) { throw new RuntimeException(e); }
    }

    /**
     * Returns the PostgreSQL keyword for auto-incrementing primary keys.
     * * @return {@code "SERIAL PRIMARY KEY"}
     */
    @Override
    protected String getAutoIncrementKeyword() { return "SERIAL PRIMARY KEY"; }

    /**
     * Returns the table options suffix. PostgreSQL does not typically require
     * engine specifications in the create statement like MySQL.
     *
     * @return An empty string.
     */
    @Override
    protected String getTableOptionsSuffix() { return ""; }
}