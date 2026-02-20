package com.github.darksoulq.abyssallib.common.database.relational.h2;

import com.github.darksoulq.abyssallib.common.database.relational.AbstractTableQuery;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * H2-specific query handler for single table operations.
 */
public class TableQuery extends AbstractTableQuery<TableQuery> {
    /** The parent database. */
    private final Database database;

    /**
     * Constructs a TableQuery for H2.
     *
     * @param database The database instance.
     * @param table    The table name.
     */
    public TableQuery(Database database, String table) {
        super(wrapConn(database), table, database.getAsyncPool());
        this.database = database;
    }

    private static Connection wrapConn(Database db) {
        try { return db.getConnection(); } catch(SQLException e) { throw new RuntimeException(e); }
    }

    /**
     * Transitions into a batch operation.
     *
     * @param columns Target columns.
     * @return A {@link BatchQuery} instance.
     */
    public BatchQuery batch(String... columns) {
        return new BatchQuery(database, table, columns);
    }

    /** @return {@code "INSERT INTO "} */
    @Override protected String getInsertVerb() { return "INSERT INTO "; }
    /** @return {@code "REPLACE INTO "} (Supported in H2 MySQL mode). */
    @Override protected String getReplaceVerb() { return "REPLACE INTO "; }
}