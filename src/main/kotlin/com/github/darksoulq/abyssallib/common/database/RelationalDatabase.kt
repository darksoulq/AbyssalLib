package com.github.darksoulq.abyssallib.common.database

import com.github.darksoulq.abyssallib.common.database.relational.AbstractBatchQuery
import com.github.darksoulq.abyssallib.common.database.relational.AbstractDatabase
import com.github.darksoulq.abyssallib.common.database.relational.AbstractTableBuilder
import com.github.darksoulq.abyssallib.common.database.relational.AbstractTableQuery
import com.github.darksoulq.abyssallib.common.database.relational.ResultMapper
import com.github.darksoulq.abyssallib.common.database.relational.h2.Database as H2Database
import com.github.darksoulq.abyssallib.common.database.relational.h2.TableQuery as H2TableQuery
import com.github.darksoulq.abyssallib.common.database.relational.mariadb.Database as MariaDBDatabase
import com.github.darksoulq.abyssallib.common.database.relational.mariadb.TableQuery as MariaDBTableQuery
import com.github.darksoulq.abyssallib.common.database.relational.mysql.Database as MySQLDatabase
import com.github.darksoulq.abyssallib.common.database.relational.mysql.TableQuery as MySQLTableQuery
import com.github.darksoulq.abyssallib.common.database.relational.postgres.Database as PostgresDatabase
import com.github.darksoulq.abyssallib.common.database.relational.postgres.TableQuery as PostgresTableQuery
import com.github.darksoulq.abyssallib.common.database.relational.sql.Database as SQLiteDatabase
import com.github.darksoulq.abyssallib.common.database.relational.sql.TableQuery as SQLiteTableQuery
import kotlinx.coroutines.future.await
import java.io.File
import java.sql.Connection
import java.sql.ResultSet

/**
 * Creates and initializes a SQLite database connection context.
 *
 * @param file The local file representing the SQLite database.
 * @param init A configuration block executed on the [KDatabase] instance.
 * @return A fully initialized [KDatabase] wrapper.
 */
fun sqlite(file: File, init: KDatabase.() -> Unit = {}): KDatabase {
    val db = SQLiteDatabase(file)
    val adapter = object : ExecutorAdapter {
        override fun table(name: String) = db.executor().table(name)
        override fun create(name: String) = db.executor().create(name)
        override fun raw(sql: String) = db.executor().executeRaw(sql)
    }
    return KDatabase(db, adapter).apply(init)
}

/**
 * Creates and initializes an H2 database connection context.
 *
 * @param file The local file where the H2 database is stored.
 * @param init A configuration block executed on the [KDatabase] instance.
 * @return A fully initialized [KDatabase] wrapper.
 */
fun h2(file: File, init: KDatabase.() -> Unit = {}): KDatabase {
    val db = H2Database(file)
    val adapter = object : ExecutorAdapter {
        override fun table(name: String) = db.executor().table(name)
        override fun create(name: String) = db.executor().create(name)
        override fun raw(sql: String) = db.executor().executeRaw(sql)
    }
    return KDatabase(db, adapter).apply(init)
}

/**
 * Creates and initializes a MySQL database connection context.
 *
 * @param host The server hostname or IP.
 * @param port The server port (usually 3306).
 * @param dbName The name of the database.
 * @param user The database username.
 * @param pass The database password.
 * @param init A configuration block executed on the [KDatabase] instance.
 * @return A fully initialized [KDatabase] wrapper.
 */
fun mysql(host: String, port: Int, dbName: String, user: String, pass: String, init: KDatabase.() -> Unit = {}): KDatabase {
    val db = MySQLDatabase(host, port, dbName, user, pass)
    val adapter = object : ExecutorAdapter {
        override fun table(name: String) = db.executor().table(name)
        override fun create(name: String) = db.executor().create(name)
        override fun raw(sql: String) = db.executor().executeRaw(sql)
    }
    return KDatabase(db, adapter).apply(init)
}

/**
 * Creates and initializes a MariaDB database connection context.
 *
 * @param host The server hostname or IP.
 * @param port The server port (usually 3306).
 * @param dbName The name of the database.
 * @param user The database username.
 * @param pass The database password.
 * @param init A configuration block executed on the [KDatabase] instance.
 * @return A fully initialized [KDatabase] wrapper.
 */
fun mariadb(host: String, port: Int, dbName: String, user: String, pass: String, init: KDatabase.() -> Unit = {}): KDatabase {
    val db = MariaDBDatabase(host, port, dbName, user, pass)
    val adapter = object : ExecutorAdapter {
        override fun table(name: String) = db.executor().table(name)
        override fun create(name: String) = db.executor().create(name)
        override fun raw(sql: String) = db.executor().executeRaw(sql)
    }
    return KDatabase(db, adapter).apply(init)
}

/**
 * Creates and initializes a PostgreSQL database connection context.
 *
 * @param host The server hostname or IP.
 * @param port The server port (usually 5432).
 * @param dbName The name of the database.
 * @param user The database username.
 * @param pass The database password.
 * @param init A configuration block executed on the [KDatabase] instance.
 * @return A fully initialized [KDatabase] wrapper.
 */
fun postgres(host: String, port: Int, dbName: String, user: String, pass: String, init: KDatabase.() -> Unit = {}): KDatabase {
    val db = PostgresDatabase(host, port, dbName, user, pass)
    val adapter = object : ExecutorAdapter {
        override fun table(name: String) = db.executor().table(name)
        override fun create(name: String) = db.executor().create(name)
        override fun raw(sql: String) = db.executor().executeRaw(sql)
    }
    return KDatabase(db, adapter).apply(init)
}

/**
 * An internal abstraction layer to handle operations across different SQL engine executors.
 */
interface ExecutorAdapter {
    /**
     * Resolves a table query builder.
     *
     * @param name Name of the SQL table.
     * @return An abstract table query implementation.
     */
    fun table(name: String): AbstractTableQuery<*>

    /**
     * Resolves a table schema builder.
     *
     * @param name Name of the SQL table to create.
     * @return An abstract table builder implementation.
     */
    fun create(name: String): AbstractTableBuilder<*>

    /**
     * Executes a raw SQL string.
     *
     * @param sql The SQL command to execute.
     */
    fun raw(sql: String)
}

/**
 * The primary Kotlin DSL wrapper for Relational Databases.
 *
 * @property handle The underlying [AbstractDatabase] implementation (SQLite, MySQL, etc).
 * @property adapter The [ExecutorAdapter] providing engine-specific execution logic.
 */
class KDatabase(val handle: AbstractDatabase, private val adapter: ExecutorAdapter) {

    /**
     * Connects to the database server or file and retrieves the active connection.
     *
     * @return The active [Connection] object.
     */
    fun connect(): Connection {
        when (handle) {
            is SQLiteDatabase -> handle.connect()
            is H2Database -> handle.connect()
            is MySQLDatabase -> handle.connect()
            is MariaDBDatabase -> handle.connect()
            is PostgresDatabase -> handle.connect()
        }
        return handle.getConnection()
    }

    /**
     * Gracefully closes the database connection and shuts down internal connection pools.
     */
    fun disconnect() {
        when (handle) {
            is SQLiteDatabase -> handle.disconnect()
            is H2Database -> handle.disconnect()
            is MySQLDatabase -> handle.disconnect()
            is MariaDBDatabase -> handle.disconnect()
            is PostgresDatabase -> handle.disconnect()
        }
    }

    /**
     * Wraps multiple operations in a single database transaction.
     *
     * @param action Lambda with [KTransaction] receiver to define transaction logic.
     */
    fun transaction(action: KTransaction.() -> Unit) {
        handle.executeTransaction { _ ->
            action(KTransaction(adapter))
        }
    }

    /**
     * Wraps operations in a transaction and returns a calculated result.
     *
     * @param T The return type of the transaction.
     * @param action Lambda with [KTransaction] receiver returning [T].
     * @return The result produced by the [action] block.
     */
    fun <T> transactionResult(action: KTransaction.() -> T): T {
        return handle.executeTransactionResult { _ ->
            action(KTransaction(adapter))
        }
    }

    /**
     * Retrieves a query builder for a specific table.
     *
     * @param name The table name.
     * @return A [KTableQuery] instance.
     */
    fun table(name: String): KTableQuery {
        return KTableQuery(adapter.table(name))
    }

    /**
     * Creates a new table using a schema builder DSL.
     *
     * @param name The table name to create.
     * @param init A configuration block for [KTableBuilder].
     */
    fun create(name: String, init: KTableBuilder.() -> Unit) {
        val builder = KTableBuilder(adapter.create(name))
        builder.init()
        builder.build()
    }

    /**
     * Executes a raw SQL command without mapping.
     *
     * @param sql The SQL string.
     */
    fun raw(sql: String) = adapter.raw(sql)
}

/**
 * Contextual wrapper for performing operations within a SQL transaction.
 *
 * @property adapter The [ExecutorAdapter] used for command routing.
 */
class KTransaction(private val adapter: ExecutorAdapter) {

    /**
     * Resolves a table query within the transaction.
     *
     * @param name Table name.
     * @return A [KTableQuery] instance.
     */
    fun table(name: String) = KTableQuery(adapter.table(name))

    /**
     * Creates a table within the transaction context.
     *
     * @param name Table name.
     * @param init Builder block.
     */
    fun create(name: String, init: KTableBuilder.() -> Unit) {
        val builder = KTableBuilder(adapter.create(name))
        builder.init()
        builder.build()
    }

    /**
     * Executes raw SQL within the transaction context.
     *
     * @param sql The SQL string.
     */
    fun raw(sql: String) = adapter.raw(sql)
}

/**
 * DSL builder for defining SQL table schemas (DDL).
 *
 * @property handle The underlying [AbstractTableBuilder] for the specific SQL engine.
 */
class KTableBuilder(val handle: AbstractTableBuilder<*>) {

    /**
     * Defines a new column in the table.
     *
     * @param name The column name.
     * @param type The SQL data type (e.g., "VARCHAR(255)", "INT").
     * @param default Optional SQL default value string.
     */
    fun column(name: String, type: String, default: String? = null) {
        handle.column(name, type)
        if (default != null) handle.defaultValue(name, default)
    }

    /**
     * Sets the primary key(s) for the table.
     *
     * @param keys Column names to be included in the key.
     */
    fun primaryKey(vararg keys: String) {
        handle.primaryKey(*keys)
    }

    /**
     * Enables auto-increment for a specific column.
     *
     * @param column Column name.
     */
    fun autoIncrement(column: String) {
        handle.autoIncrement(column)
    }

    /**
     * Defines a foreign key constraint.
     *
     * @param column Source column.
     * @param table Reference table.
     * @param refColumn Reference column in the external table.
     */
    fun foreignKey(column: String, table: String, refColumn: String) {
        handle.foreignKey(column, table, refColumn)
    }

    /**
     * Sets a UNIQUE constraint on columns.
     *
     * @param columns Column names to be unique.
     */
    fun unique(vararg columns: String) {
        handle.unique(*columns)
    }

    /**
     * Adds a CHECK constraint.
     *
     * @param expression SQL check expression.
     */
    fun check(expression: String) {
        handle.check(expression)
    }

    /**
     * Ensures the table is only created if it doesn't already exist.
     */
    fun ifNotExists() {
        handle.ifNotExists()
    }

    /**
     * Drops the table before creation if it currently exists.
     */
    fun dropIfExists() {
        handle.dropIfExists()
    }

    /**
     * Finalizes building and executes the CREATE TABLE statement.
     */
    fun build() = handle.execute()

    /**
     * Infix notation to define a column type.
     * Usage: `"id" type "INT"`
     *
     * @param type The SQL data type.
     */
    infix fun String.type(type: String) = column(this, type)
}

/**
 * DSL builder for performing DML (Data Manipulation Language) queries on a table.
 *
 * @property handle The underlying [AbstractTableQuery] instance.
 */
class KTableQuery(val handle: AbstractTableQuery<*>) {

    /**
     * Executes a SQL INSERT statement synchronously.
     *
     * @param block Lambda to define column values via [KValues].
     * @return Number of rows affected.
     */
    fun insert(block: KValues.() -> Unit): Int {
        handle.insert()
        applyValues(block)
        return handle.execute()
    }

    /**
     * Executes a SQL INSERT statement asynchronously.
     *
     * @param block Lambda to define column values via [KValues].
     * @return Number of rows affected.
     */
    suspend fun insertAsync(block: KValues.() -> Unit): Int {
        handle.insert()
        applyValues(block)
        return handle.executeAsync().await()
    }

    /**
     * Executes a SQL REPLACE statement synchronously.
     *
     * @param block Lambda to define column values.
     * @return Number of rows affected.
     */
    fun replace(block: KValues.() -> Unit): Int {
        handle.replace()
        applyValues(block)
        return handle.execute()
    }

    /**
     * Executes a SQL REPLACE statement asynchronously.
     *
     * @param block Lambda to define column values.
     * @return Number of rows affected.
     */
    suspend fun replaceAsync(block: KValues.() -> Unit): Int {
        handle.replace()
        applyValues(block)
        return handle.executeAsync().await()
    }

    /**
     * Executes a SQL UPDATE statement synchronously.
     *
     * @param where Optional SQL WHERE clause.
     * @param params Parameter arguments for the WHERE clause.
     * @param block Lambda to define the new values for columns.
     * @return Number of rows updated.
     */
    fun update(where: String? = null, vararg params: Any, block: KValues.() -> Unit): Int {
        handle.update()
        if (where != null) handle.where(where, *params)
        applyValues(block)
        return handle.execute()
    }

    /**
     * Executes a SQL UPDATE statement asynchronously.
     *
     * @param where Optional SQL WHERE clause.
     * @param params Parameter arguments for the WHERE clause.
     * @param block Lambda to define the new values for columns.
     * @return Number of rows updated.
     */
    suspend fun updateAsync(where: String? = null, vararg params: Any, block: KValues.() -> Unit): Int {
        handle.update()
        if (where != null) handle.where(where, *params)
        applyValues(block)
        return handle.executeAsync().await()
    }

    /**
     * Executes a SQL DELETE statement synchronously.
     *
     * @param where The SQL WHERE clause.
     * @param params Parameter arguments for the WHERE clause.
     * @return Number of rows deleted.
     */
    fun delete(where: String, vararg params: Any): Int {
        handle.delete().where(where, *params)
        return handle.execute()
    }

    /**
     * Executes a SQL DELETE statement asynchronously.
     *
     * @param where The SQL WHERE clause.
     * @param params Parameter arguments for the WHERE clause.
     * @return Number of rows deleted.
     */
    suspend fun deleteAsync(where: String, vararg params: Any): Int {
        handle.delete().where(where, *params)
        return handle.executeAsync().await()
    }

    /**
     * Counts the rows in the table matching the criteria.
     *
     * @param where Optional WHERE clause.
     * @param params Arguments for the WHERE clause.
     * @return Total count of rows.
     */
    fun count(where: String? = null, vararg params: Any): Long {
        if (where != null) handle.where(where, *params)
        return handle.count()
    }

    /**
     * Checks if any row exists matching the criteria.
     *
     * @param where Optional WHERE clause.
     * @param params Arguments for the WHERE clause.
     * @return True if at least one row is found.
     */
    fun exists(where: String? = null, vararg params: Any): Boolean {
        if (where != null) handle.where(where, *params)
        return handle.exists()
    }

    /**
     * Executes a SELECT query synchronously and maps results.
     *
     * @param T Result type.
     * @param columns List of column names to select.
     * @param where SQL WHERE clause.
     * @param params Query parameters.
     * @param orderBy Column to sort by.
     * @param desc True for DESC, false for ASC.
     * @param limit Maximum rows.
     * @param offset Row offset.
     * @param mapper Mapping lambda for the [ResultSet].
     * @return List of mapped results.
     */
    fun <T> select(
        columns: List<String> = emptyList(),
        where: String? = null,
        params: Array<out Any> = emptyArray(),
        orderBy: String? = null,
        desc: Boolean = false,
        limit: Int? = null,
        offset: Int? = null,
        mapper: (ResultSet) -> T
    ): List<T> {
        configureSelect(columns, where, params, orderBy, desc, limit, offset)
        return handle.select(ResultMapper<T> { mapper(it) })
    }

    /**
     * Executes a SELECT query asynchronously and maps results.
     *
     * @param T Result type.
     * @param columns List of column names to select.
     * @param where SQL WHERE clause.
     * @param params Query parameters.
     * @param orderBy Column to sort by.
     * @param desc True for DESC, false for ASC.
     * @param limit Maximum rows.
     * @param offset Row offset.
     * @param mapper Mapping lambda for the [ResultSet].
     * @return List of mapped results.
     */
    suspend fun <T> selectAsync(
        columns: List<String> = emptyList(),
        where: String? = null,
        params: Array<out Any> = emptyArray(),
        orderBy: String? = null,
        desc: Boolean = false,
        limit: Int? = null,
        offset: Int? = null,
        mapper: (ResultSet) -> T
    ): List<T> {
        configureSelect(columns, where, params, orderBy, desc, limit, offset)
        return handle.selectAsync(ResultMapper<T> { mapper(it) }).await()
    }

    /**
     * Fetches the first matching row and maps it.
     *
     * @param T Result type.
     * @param columns Columns to select.
     * @param where WHERE clause.
     * @param params Query parameters.
     * @param mapper Mapping lambda.
     * @return The mapped result or null if empty.
     */
    fun <T> first(
        columns: List<String> = emptyList(),
        where: String? = null,
        params: Array<out Any> = emptyArray(),
        mapper: (ResultSet) -> T
    ): T? {
        configureSelect(columns, where, params, null, false, 1, null)
        return handle.first(ResultMapper<T> { mapper(it) })
    }

    /**
     * Begins a batch insertion/replacement operation.
     *
     * @param columns The names of the columns included in the batch.
     * @param init Configuration block for [KBatchQuery].
     * @return Total affected rows across the batch.
     */
    fun batch(vararg columns: String, init: KBatchQuery.() -> Unit): Int {
        val batchHandle = when (handle) {
            is SQLiteTableQuery -> handle.batch(*columns)
            is MySQLTableQuery -> handle.batch(*columns)
            is MariaDBTableQuery -> handle.batch(*columns)
            is PostgresTableQuery -> handle.batch(*columns)
            is H2TableQuery -> handle.batch(*columns)
            else -> throw IllegalStateException("Unknown TableQuery type")
        }
        val wrapper = KBatchQuery(batchHandle)
        wrapper.init()
        return wrapper.execute()
    }

    /**
     * Begins a batch operation asynchronously.
     *
     * @param columns The names of the columns.
     * @param init Configuration block for [KBatchQuery].
     * @return Total affected rows.
     */
    suspend fun batchAsync(vararg columns: String, init: KBatchQuery.() -> Unit): Int {
        val batchHandle = when (handle) {
            is SQLiteTableQuery -> handle.batch(*columns)
            is MySQLTableQuery -> handle.batch(*columns)
            is MariaDBTableQuery -> handle.batch(*columns)
            is PostgresTableQuery -> handle.batch(*columns)
            is H2TableQuery -> handle.batch(*columns)
            else -> throw IllegalStateException("Unknown TableQuery type")
        }
        val wrapper = KBatchQuery(batchHandle)
        wrapper.init()
        return wrapper.executeAsync()
    }

    /**
     * Internal helper to apply query constraints to the table query handle.
     *
     * @param columns Columns to include.
     * @param where WHERE condition.
     * @param params Condition arguments.
     * @param orderBy Column to sort.
     * @param desc Descending flag.
     * @param limit Row limit.
     * @param offset Row offset.
     */
    private fun configureSelect(
        columns: List<String>,
        where: String?,
        params: Array<out Any>,
        orderBy: String?,
        desc: Boolean,
        limit: Int?,
        offset: Int?
    ) {
        if (where != null) handle.where(where, *params)
        if (orderBy != null) handle.orderBy(orderBy, !desc)
        if (limit != null) handle.limit(limit)
        if (offset != null) handle.offset(offset)
    }

    /**
     * Internal helper to extract values from a DSL block and apply them to the query handle.
     *
     * @param block The [KValues] lambda.
     */
    private fun applyValues(block: KValues.() -> Unit) {
        val kv = KValues()
        kv.block()
        kv.map.forEach { (k, v) -> handle.value(k, v) }
    }
}

/**
 * Utility container for column-to-value mappings.
 */
class KValues {
    /** Internal storage for column keys and their associated values. */
    internal val map = mutableMapOf<String, Any?>()

    /**
     * Infix mapping tool.
     * Usage: `"name" to "value"`
     *
     * @param value The value to map to the key.
     */
    infix fun String.to(value: Any?) {
        map[this] = value
    }

    /**
     * Bracket mapping tool.
     * Usage: `values["name"] = "value"`
     *
     * @param column The column key.
     * @param value The value.
     */
    operator fun set(column: String, value: Any?) {
        map[column] = value
    }
}

/**
 * DSL for executing SQL batch operations (INSERT/REPLACE).
 *
 * @property handle The engine-specific [AbstractBatchQuery] implementation.
 */
class KBatchQuery(val handle: AbstractBatchQuery<*>) {
    /**
     * Configures the batch to perform INSERT operations.
     */
    fun insert() { handle.insert() }

    /**
     * Configures the batch to perform REPLACE operations.
     */
    fun replace() { handle.replace() }

    /**
     * Configures the batch to perform INSERT IGNORE operations (where supported).
     */
    fun insertIgnore() { handle.insertIgnore() }

    /**
     * Adds a single row's worth of values to the batch buffer.
     *
     * @param values Values in column order.
     */
    fun add(vararg values: Any?) {
        handle.add(*values)
    }

    /**
     * Executes all buffered batch operations synchronously.
     *
     * @return Total rows affected.
     */
    fun execute(): Int = handle.execute()

    /**
     * Executes all buffered batch operations asynchronously.
     *
     * @return Total rows affected.
     */
    suspend fun executeAsync(): Int = handle.executeAsync().await()
}