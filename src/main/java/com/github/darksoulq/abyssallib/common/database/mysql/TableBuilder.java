package com.github.darksoulq.abyssallib.common.database.mysql;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableBuilder {
    private final Database database;
    private final String table;
    private boolean ifNotExists = false;
    private final List<String> columns = new ArrayList<>();
    private final List<String> primaryKeys = new ArrayList<>();
    private final List<String> foreignKeys = new ArrayList<>();
    private final List<String> uniqueColumns = new ArrayList<>();
    private final List<String> checkConstraints = new ArrayList<>();
    private final Map<String, String> defaultValues = new HashMap<>();

    public TableBuilder(Database database, String table) {
        this.database = database;
        this.table = table;
    }

    public TableBuilder ifNotExists() { this.ifNotExists = true; return this; }

    public TableBuilder column(String name, String type) {
        StringBuilder columnDef = new StringBuilder(name + " " + type);
        if (defaultValues.containsKey(name)) {
            columnDef.append(" DEFAULT ").append(defaultValues.get(name));
        }
        columns.add(columnDef.toString());
        return this;
    }

    public TableBuilder primaryKey(String... keys) {
        primaryKeys.addAll(List.of(keys));
        return this;
    }

    public TableBuilder autoIncrement(String column) {
        columns.removeIf(s -> s.startsWith(column + " "));
        columns.add(column + " INT PRIMARY KEY AUTO_INCREMENT");
        return this;
    }

    public TableBuilder foreignKey(String column, String referencesTable, String referencesColumn) {
        foreignKeys.add("FOREIGN KEY (" + column + ") REFERENCES " + referencesTable + " (" + referencesColumn + ")");
        return this;
    }

    public TableBuilder unique(String... columns) {
        uniqueColumns.add("UNIQUE (" + String.join(", ", columns) + ")");
        return this;
    }

    public TableBuilder check(String expression) {
        checkConstraints.add("CHECK (" + expression + ")");
        return this;
    }

    public TableBuilder defaultValue(String column, String defaultValue) {
        defaultValues.put(column, defaultValue);
        return this;
    }

    public void dropIfExists() {
        try (Statement stmt = database.getConnection().createStatement()) {
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

        sql.append(") CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");

        try (Statement stmt = database.getConnection().createStatement()) {
            stmt.execute(sql.toString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to create table: " + table, e);
        }
    }
}