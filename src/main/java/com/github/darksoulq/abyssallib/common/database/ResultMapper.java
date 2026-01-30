package com.github.darksoulq.abyssallib.common.database;

import java.sql.ResultSet;

/**
 * A functional interface used to convert a row from a JDBC {@link ResultSet} into a POJO or other data structure.
 * This interface is designed to be used in conjunction with {@link AbstractTableQuery#select(ResultMapper)}.
 *
 * @param <T> The type of the object that the row is mapped to.
 */
@FunctionalInterface
public interface ResultMapper<T> {
    /**
     * Performs the mapping of the current row in the given ResultSet.
     *
     * @param rs The {@link ResultSet} positioned at the row to be mapped.
     * @return An instance of type {@code T} containing the row data.
     * @throws Exception If an error occurs during mapping or database retrieval.
     */
    T map(ResultSet rs) throws Exception;
}