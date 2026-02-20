package com.github.darksoulq.abyssallib.common.database.nosql.mongodb;

import org.bson.Document;

@FunctionalInterface
public interface DocumentMapper<T> {
    T map(Document doc) throws Exception;
}