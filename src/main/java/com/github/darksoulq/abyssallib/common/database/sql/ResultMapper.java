package com.github.darksoulq.abyssallib.common.database.sql;

import java.sql.ResultSet;

@FunctionalInterface
public interface ResultMapper<T> {
    T map(ResultSet rs) throws Exception;
}