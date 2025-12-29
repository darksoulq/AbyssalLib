package com.github.darksoulq.abyssallib.common.database.sql;

import com.github.darksoulq.abyssallib.common.database.AbstractDatabase;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
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
        connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath() + "?foreign_keys=on");
    }
    public void disconnect() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        asyncPool.shutdown();
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    public QueryExecutor executor() {
        return new QueryExecutor(connection, asyncPool);
    }

    public void transaction(Consumer<QueryExecutor> action) {
        super.executeTransaction(conn -> action.accept(new QueryExecutor(conn, asyncPool)));
    }

    public <T> T transactionResult(Function<QueryExecutor, T> action) {
        return super.executeTransactionResult(conn -> action.apply(new QueryExecutor(conn, asyncPool)));
    }
}