package com.github.darksoulq.abyssallib.common.database.relational.mariadb;

import com.github.darksoulq.abyssallib.common.database.relational.AbstractTableBuilder;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A MariaDB-specific table schema builder.
 */
public class TableBuilder extends AbstractTableBuilder<TableBuilder> {

    /**
     * Constructs a TableBuilder for MariaDB.
     *
     * @param database The source database.
     * @param table    The target table name.
     */
    public TableBuilder(Database database, String table) {
        super(wrapConn(database), table);
    }

    /**
     * Internal wrapper to provide a connection to the abstract superclass.
     *
     * @param db The database.
     * @return The connection.
     * @throws RuntimeException If connection retrieval fails.
     */
    private static Connection wrapConn(Database db) {
        try { return db.getConnection(); } catch(SQLException e) { throw new RuntimeException(e); }
    }

    /**
     * Returns the MariaDB auto-increment keyword.
     * @return {@code "AUTO_INCREMENT"}
     */
    @Override
    protected String getAutoIncrementKeyword() { return "AUTO_INCREMENT"; }

    /**
     * Returns the InnoDB engine specification used by MariaDB for ACID compliance.
     * @return {@code " ENGINE=InnoDB"}
     */
    @Override
    protected String getTableOptionsSuffix() { return " ENGINE=InnoDB"; }
}