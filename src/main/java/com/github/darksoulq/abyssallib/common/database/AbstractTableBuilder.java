package com.github.darksoulq.abyssallib.common.database;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractTableBuilder<T extends AbstractTableBuilder<T>> {
    protected final Connection connection;
    protected final String table;
    protected boolean ifNotExists = false;
    protected final List<String> columns = new ArrayList<>();
    protected final List<String> primaryKeys = new ArrayList<>();
    protected final List<String> foreignKeys = new ArrayList<>();
    protected final List<String> uniqueColumns = new ArrayList<>();
    protected final List<String> checkConstraints = new ArrayList<>();
    protected final Map<String, String> defaultValues = new HashMap<>();

    public AbstractTableBuilder(Connection connection, String table) {
        this.connection = connection;
        this.table = table;
    }

    @SuppressWarnings("unchecked")
    public T ifNotExists() { this.ifNotExists = true; return (T) this; }

    @SuppressWarnings("unchecked")
    public T column(String name, String type) {
        StringBuilder columnDef = new StringBuilder(name + " " + type);
        if (defaultValues.containsKey(name)) {
            columnDef.append(" DEFAULT ").append(defaultValues.get(name));
        }
        columns.add(columnDef.toString());
        return (T) this;
    }
    
    @SuppressWarnings("unchecked")
    public T primaryKey(String... keys) {
        primaryKeys.addAll(List.of(keys));
        return (T) this;
    }

    protected abstract String getAutoIncrementKeyword(); 
    protected abstract String getTableOptionsSuffix(); 

    @SuppressWarnings("unchecked")
    public T autoIncrement(String column) {
        columns.removeIf(s -> s.startsWith(column + " "));
        columns.add(column + " " + getAutoIncrementKeyword());
        return (T) this;
    }
    
    @SuppressWarnings("unchecked")
    public T foreignKey(String column, String referencesTable, String referencesColumn) {
        foreignKeys.add("FOREIGN KEY (" + column + ") REFERENCES " + referencesTable + " (" + referencesColumn + ")");
        return (T) this;
    }
    @SuppressWarnings("unchecked")
    public T unique(String... columns) {
        uniqueColumns.add("UNIQUE (" + String.join(", ", columns) + ")");
        return (T) this;
    }
    @SuppressWarnings("unchecked")
    public T check(String expression) {
        checkConstraints.add("CHECK (" + expression + ")");
        return (T) this;
    }
    @SuppressWarnings("unchecked")
    public T defaultValue(String column, String defaultValue) {
        defaultValues.put(column, defaultValue);
        return (T) this;
    }

    public void dropIfExists() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS " + table);
        } catch (Exception e) {
            throw new RuntimeException("Failed to drop table: " + table, e);
        }
    }

    public void execute() {
        StringBuilder sql = new StringBuilder("CREATE TABLE ");
        if (ifNotExists) sql.append("IF NOT EXISTS ");
        sql.append(table).append(" (");
        sql.append(String.join(", ", columns));
        if (!primaryKeys.isEmpty()) {
            sql.append(", PRIMARY KEY(").append(String.join(", ", primaryKeys)).append(")");
        }
        if (!foreignKeys.isEmpty()) sql.append(", ").append(String.join(", ", foreignKeys));
        if (!uniqueColumns.isEmpty()) sql.append(", ").append(String.join(", ", uniqueColumns));
        if (!checkConstraints.isEmpty()) sql.append(", ").append(String.join(", ", checkConstraints));
        sql.append(")").append(getTableOptionsSuffix());

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql.toString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to create table: " + table, e);
        }
    }
}