package com.github.darksoulq.abyssallib.common.database.relational.mariadb;

import com.github.darksoulq.abyssallib.common.database.relational.AbstractTableQuery;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A MariaDB-specific query handler for individual table operations.
 */
public class TableQuery extends AbstractTableQuery<TableQuery> {
    /** The source database. */
    private final Database database;

    /**
     * Constructs a TableQuery for MariaDB.
     *
     * @param database The database instance.
     * @param table    The table name.
     */
    public TableQuery(Database database, String table) {
        super(wrapConn(database), table, database.getAsyncPool());
        this.database = database;
    }

    /**
     * Helper to wrap connection retrieval.
     */
    private static Connection wrapConn(Database db) {
        try { return db.getConnection(); } catch(SQLException e) { throw new RuntimeException(e); }
    }

    /**
     * Transitions into a batch operation for this table.
     *
     * @param columns The columns for the batch.
     * @return A {@link BatchQuery} instance.
     */
    public BatchQuery batch(String... columns) {
        return new BatchQuery(database, table, columns);
    }

    /** @return Standard {@code "INSERT INTO "} verb. */
    @Override protected String getInsertVerb() { return "INSERT INTO "; }
    /** @return MariaDB-specific {@code "REPLACE INTO "} verb. */
    @Override protected String getReplaceVerb() { return "REPLACE INTO "; }
}