package com.github.darksoulq.abyssallib.common.database;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A functional interface used to map a {@link ResultSet} row into a Java object.
 *
 * <p>This is typically used in query operations to convert SQL result rows into application-level objects.</p>
 *
 * @param <T> the type of object to map each row into
 */
@FunctionalInterface
public interface ResultMapper<T> {

    /**
     * Maps the current row of the given {@link ResultSet} into an instance of type {@code T}.
     *
     * @param rs the result set positioned at the current row
     * @return the mapped object of type {@code T}
     * @throws SQLException if a database access error occurs or if reading from the result set fails
     */
    T map(ResultSet rs) throws SQLException, ClassNotFoundException, CloneNotSupportedException;
}
