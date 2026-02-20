package com.github.darksoulq.abyssallib.common.database.nosql.mongodb;

import org.bson.Document;

/**
 * A functional interface for mapping a BSON {@link Document} to a Java object.
 *
 * @param <T> The type of the resulting object.
 */
@FunctionalInterface
public interface DocumentMapper<T> {
    /**
     * Maps the provided BSON document to an object of type T.
     *
     * @param doc The source BSON document.
     * @return The mapped object.
     * @throws Exception If mapping logic fails.
     */
    T map(Document doc) throws Exception;
}