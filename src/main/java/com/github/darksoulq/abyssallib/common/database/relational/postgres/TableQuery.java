package com.github.darksoulq.abyssallib.common.database.relational.postgres;

import com.github.darksoulq.abyssallib.common.database.relational.AbstractTableQuery;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A PostgreSQL-specific implementation of {@link AbstractTableQuery}.
 * <p>
 * This class provides fluent API access for standard SQL queries on a specific table.
 * Note that PostgreSQL does not support the {@code REPLACE INTO} syntax.
 */
public class TableQuery extends AbstractTableQuery<TableQuery> {

    /** The parent database instance providing connection and pool access. */
    private final Database database;

    /**
     * Constructs a new TableQuery for PostgreSQL.
     *
     * @param database The PostgreSQL {@link Database} instance.
     * @param table    The name of the table to query.
     */
    public TableQuery(Database database, String table) {
        super(wrapConn(database), table, database.getAsyncPool());
        this.database = database;
    }

    /**
     * Safely retrieves a connection from the database instance.
     *
     * @param db The {@link Database} instance.
     * @return A valid {@link Connection}.
     * @throws RuntimeException If a database access error occurs.
     */
    private static Connection wrapConn(Database db) {
        try { return db.getConnection(); } catch(SQLException e) { throw new RuntimeException(e); }
    }

    /**
     * Transitions this query into a batch operation.
     *
     * @param columns The names of the columns to include in the batch.
     * @return A new {@link BatchQuery} instance.
     */
    public BatchQuery batch(String... columns) {
        return new BatchQuery(database, table, columns);
    }

    /**
     * Returns the PostgreSQL verb for standard insertions.
     * * @return {@code "INSERT INTO "}
     */
    @Override
    protected String getInsertVerb() { return "INSERT INTO "; }

    /**
     * PostgreSQL does not support {@code REPLACE INTO}.
     *
     * @throws UnsupportedOperationException Always, as PostgreSQL uses {@code ON CONFLICT} logic.
     */
    @Override
    protected String getReplaceVerb() {
        throw new UnsupportedOperationException("PostgreSQL uses ON CONFLICT DO UPDATE instead of REPLACE INTO. Use a custom query for this.");
    }
}