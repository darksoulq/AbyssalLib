package com.github.darksoulq.abyssallib.common.database.nosql.mongodb;

public class QueryExecutor {
    private final Database database;

    public QueryExecutor(Database database) {
        this.database = database;
    }

    public CollectionQuery collection(String name) {
        return new CollectionQuery(database, name);
    }
}