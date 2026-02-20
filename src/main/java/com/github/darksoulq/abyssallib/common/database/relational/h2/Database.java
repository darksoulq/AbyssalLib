package com.github.darksoulq.abyssallib.common.database.relational.h2;

import com.github.darksoulq.abyssallib.common.database.relational.AbstractDatabase;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

public class Database extends AbstractDatabase {
    private final File file;
    private Connection connection;

    public Database(File file) {
        super(Executors.newCachedThreadPool());
        this.file = file;
    }

    public void connect() throws Exception {
        file.getParentFile().mkdirs();
        String path = file.getAbsolutePath();
        if (path.endsWith(".mv.db")) {
            path = path.substring(0, path.length() - 6);
        }
        connection = DriverManager.getConnection("jdbc:h2:" + path + ";MODE=MySQL;AUTO_SERVER=TRUE");
    }

    public void disconnect() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        asyncPool.shutdown();
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connect();
            } catch (Exception e) {
                throw new SQLException("Failed to reconnect to H2 database", e);
            }
        }
        return connection;
    }

    public QueryExecutor executor() {
        return new QueryExecutor(this);
    }

    public void transaction(Consumer<QueryExecutor> action) {
        super.executeTransaction(conn -> action.accept(new QueryExecutor(this)));
    }

    public <T> T transactionResult(Function<QueryExecutor, T> action) {
        return super.executeTransactionResult(conn -> action.apply(new QueryExecutor(this)));
    }
}