package com.github.darksoulq.abyssallib.common.database.relational.postgres;

import com.github.darksoulq.abyssallib.common.database.relational.AbstractBatchQuery;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A PostgreSQL-specific implementation of {@link AbstractBatchQuery}.
 * <p>
 * Optimized for executing multiple insertion operations in a single database round-trip.
 * Due to PostgreSQL's strict SQL compliance, "Replace" operations are explicitly disabled
 * in favor of conflict resolution patterns.
 */
public class BatchQuery extends AbstractBatchQuery<BatchQuery> {

    /**
     * Constructs a new BatchQuery for PostgreSQL.
     *
     * @param database The PostgreSQL {@link Database} instance.
     * @param table    The target table name.
     * @param columns  The specific columns involved in the batch operation.
     */
    public BatchQuery(Database database, String table, String... columns) {
        super(wrapConn(database), table, database.getAsyncPool(), columns);
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
     * Returns the PostgreSQL verb for batch insertions.
     * * @return {@code "INSERT INTO "}
     */
    @Override
    protected String getInsertVerb() { return "INSERT INTO "; }

    /**
     * PostgreSQL does not support {@code REPLACE INTO}.
     *
     * @throws UnsupportedOperationException Always.
     */
    @Override
    protected String getReplaceVerb() {
        throw new UnsupportedOperationException("PostgreSQL uses ON CONFLICT DO UPDATE instead of REPLACE INTO. Use a custom query for this.");
    }

    /**
     * Returns the standard insertion verb. Note that ignoring duplicates
     * in PostgreSQL typically requires an {@code ON CONFLICT DO NOTHING} suffix,
     * which is handled outside this verb prefix.
     * * @return {@code "INSERT INTO "}
     */
    @Override
    protected String getInsertIgnoreVerb() { return "INSERT INTO "; }
}