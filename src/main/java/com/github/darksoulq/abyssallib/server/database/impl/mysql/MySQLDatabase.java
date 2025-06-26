package com.github.darksoulq.abyssallib.server.database.impl.mysql;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.database.Database;
import com.github.darksoulq.abyssallib.server.database.QueryExecutor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Implementation of the {@link Database} interface for MySQL.
 * Manages a JDBC connection, ensures it stays alive, and provides query access.
 * Includes automatic reconnection and periodic keep-alive pings.
 */
public class MySQLDatabase implements Database {
    /**
     * The MySQL host (including port if necessary, e.g., "localhost:3306").
     */
    private final String host;

    /**
     * The name of the database to connect to.
     */
    private final String database;

    /**
     * The username used to authenticate with the MySQL server.
     */
    private final String username;

    /**
     * The password used to authenticate with the MySQL server.
     */
    private final String password;

    /**
     * Reference to the plugin (for scheduling tasks).
     */
    private final Plugin plugin;

    /**
     * The current JDBC connection to the MySQL database.
     */
    private Connection connection;

    /**
     * ID of the keep-alive scheduler task.
     */
    private int taskId = -1;

    /**
     * Constructs a new {@link MySQLDatabase} instance.
     *
     * @param plugin   the plugin context (for task scheduling)
     * @param host     the MySQL host (e.g., "localhost" or "localhost:3306")
     * @param database the name of the database
     * @param username the username
     * @param password the password
     */
    public MySQLDatabase(Plugin plugin, String host, String database, String username, String password) {
        this.plugin = plugin;
        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    /**
     * Establishes a connection to the MySQL database using JDBC.
     * Starts the internal keep-alive scheduler.
     *
     * @throws Exception if a connection cannot be established
     */
    @Override
    public synchronized void connect() throws Exception {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        String url = "jdbc:mysql://" + host + "/" + database +
                "?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true";
        connection = DriverManager.getConnection(url, username, password);

        startKeepAliveTask();
    }

    /**
     * Closes the connection to the MySQL database and stops keep-alive task.
     *
     * @throws Exception if an error occurs while closing the connection
     */
    @Override
    public synchronized void disconnect() throws Exception {
        stopKeepAliveTask();

        if (connection != null && !connection.isClosed()) {
            connection.close();
        }

        connection = null;
    }

    /**
     * Returns a {@link QueryExecutor} instance bound to this database connection.
     * Reconnects if the connection is stale.
     *
     * @return a new {@link MySQLQueryExecutor}
     */
    @Override
    public synchronized QueryExecutor executor() {
        ensureConnectionValid();
        return new MySQLQueryExecutor(connection);
    }

    /**
     * Ensures the connection is open and valid. Reconnects if necessary.
     */
    private synchronized void ensureConnectionValid() {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                AbyssalLib.getInstance()
                        .getLogger().warning("[AbyssalLib] MySQL connection lost. Reconnecting...");
                connect();
            }
        } catch (Exception ex) {
            throw new RuntimeException("Failed to reconnect to MySQL", ex);
        }
    }

    /**
     * Starts a periodic scheduler task to keep the connection alive.
     * Pings the server every 5 minutes.
     */
    private void startKeepAliveTask() {
        if (taskId != -1) return;

        taskId = new BukkitRunnable() {
            @Override
            public void run() {
                synchronized (this) {
                    try (Statement stmt = connection.createStatement()) {
                        stmt.execute("SELECT 1");
                    } catch (SQLException e) {
                        AbyssalLib.getInstance()
                                .getLogger().warning("[AbyssalLib] MySQL keep-alive failed: " + e.getMessage());
                        ensureConnectionValid();
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin,  20L * 60L * 5L, 20L * 60L * 5L).getTaskId();
    }

    /**
     * Stops the keep-alive task if it is running.
     */
    private void stopKeepAliveTask() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }
}
