package com.github.darksoulq.abyssallib.common.database.sql;

import java.sql.Connection;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;

public class QueryExecutor {
    private final Connection connection;
    private final ExecutorService asyncPool;

    public QueryExecutor(Connection connection, ExecutorService asyncPool) {
        this.connection = connection;
        this.asyncPool = asyncPool;
    }

    public TableQuery table(String name) {
        return new TableQuery(connection, name, asyncPool);
    }

    public void executeRaw(String sql) throws Exception {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }
}