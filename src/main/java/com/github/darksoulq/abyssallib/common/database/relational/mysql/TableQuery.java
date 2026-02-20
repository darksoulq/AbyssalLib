package com.github.darksoulq.abyssallib.common.database.relational.mysql;

import com.github.darksoulq.abyssallib.common.database.relational.AbstractTableQuery;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A MySQL-specific implementation of {@link AbstractTableQuery}.
 * Provides MySQL verbs and easy access to batch query operations.
 */
public class TableQuery extends AbstractTableQuery<TableQuery> {
    /** The MySQL database instance. */
    private final Database database;

    /**
     * Constructs a new TableQuery for MySQL.
     *
     * @param database The database manager.
     * @param table    The table name.
     */
    public TableQuery(Database database, String table) {
        super(wrapConn(database), table, database.getAsyncPool());
        this.database = database;
    }

    /**
     * Internal helper to extract a connection.
     *
     * @param db The database manager.
     * @return A valid connection.
     */
    private static Connection wrapConn(Database db) {
        try { return db.getConnection(); } catch(SQLException e) { throw new RuntimeException(e); }
    }

    /**
     * Creates a {@link BatchQuery} for the current table.
     *
     * @param columns The columns to include in the batch.
     * @return A new batch query instance.
     */
    public BatchQuery batch(String... columns) {
        return new BatchQuery(database, table, columns);
    }

    /**
     * Returns the MySQL insert verb.
     * @return {@code "INSERT INTO "}
     */
    @Override protected String getInsertVerb() { return "INSERT INTO "; }

    /**
     * Returns the MySQL replace verb.
     * @return {@code "REPLACE INTO "}
     */
    @Override protected String getReplaceVerb() { return "REPLACE INTO "; }
}