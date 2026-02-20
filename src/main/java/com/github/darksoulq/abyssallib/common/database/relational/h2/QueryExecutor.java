package com.github.darksoulq.abyssallib.common.database.relational.h2;

import java.sql.Statement;

public class QueryExecutor {
    private final Database database;

    public QueryExecutor(Database database) {
        this.database = database;
    }

    public TableQuery table(String name) {
        return new TableQuery(database, name);
    }

    public TableBuilder create(String name) {
        return new TableBuilder(database, name);
    }

    public void executeRaw(String sql) throws Exception {
        try (Statement stmt = database.getConnection().createStatement()) {
            stmt.execute(sql);
        }
    }
}