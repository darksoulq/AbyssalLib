package com.github.darksoulq.abyssallib.common.database


import com.github.darksoulq.abyssallib.common.database.mysql.TableQuery as MySQLTableQuery
import com.github.darksoulq.abyssallib.common.database.sql.TableQuery as SQLiteTableQuery
import kotlinx.coroutines.future.await
import java.sql.ResultSet
import java.util.concurrent.CompletableFuture

class KTableQuery(val handle: AbstractTableQuery<*>) {

    fun insert(block: KValues.() -> Unit): Int {
        handle.insert()
        applyValues(block)
        return handle.execute()
    }

    suspend fun insertAsync(block: KValues.() -> Unit): Int {
        handle.insert()
        applyValues(block)
        return handle.executeAsync().await()
    }

    fun replace(block: KValues.() -> Unit): Int {
        handle.replace()
        applyValues(block)
        return handle.execute()
    }

    suspend fun replaceAsync(block: KValues.() -> Unit): Int {
        handle.replace()
        applyValues(block)
        return handle.executeAsync().await()
    }

    fun update(where: String? = null, vararg params: Any, block: KValues.() -> Unit): Int {
        handle.update()
        if (where != null) handle.where(where, *params)
        applyValues(block)
        return handle.execute()
    }

    suspend fun updateAsync(where: String? = null, vararg params: Any, block: KValues.() -> Unit): Int {
        handle.update()
        if (where != null) handle.where(where, *params)
        applyValues(block)
        return handle.executeAsync().await()
    }

    fun delete(where: String, vararg params: Any): Int {
        handle.delete().where(where, *params)
        return handle.execute()
    }

    suspend fun deleteAsync(where: String, vararg params: Any): Int {
        handle.delete().where(where, *params)
        return handle.executeAsync().await()
    }

    fun count(where: String? = null, vararg params: Any): Long {
        if (where != null) handle.where(where, *params)
        return handle.count()
    }

    fun exists(where: String? = null, vararg params: Any): Boolean {
        if (where != null) handle.where(where, *params)
        return handle.exists()
    }

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
        return handle.select(ResultMapper { mapper(it) })
    }

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
        return handle.selectAsync(ResultMapper { mapper(it) }).await()
    }

    fun <T> first(
        columns: List<String> = emptyList(),
        where: String? = null,
        params: Array<out Any> = emptyArray(),
        mapper: (ResultSet) -> T
    ): T? {
        configureSelect(columns, where, params, null, false, 1, null)
        return handle.first { mapper(it) }
    }

    fun batch(vararg columns: String, init: KBatchQuery.() -> Unit): Int {
        val batchHandle = when (handle) {
            is SQLiteTableQuery -> handle.batch(*columns)
            is MySQLTableQuery -> handle.batch(*columns)
            else -> throw IllegalStateException("Unknown TableQuery type")
        }
        val wrapper = KBatchQuery(batchHandle)
        wrapper.init()
        return wrapper.execute()
    }

    suspend fun batchAsync(vararg columns: String, init: KBatchQuery.() -> Unit): Int {
        val batchHandle = when (handle) {
            is SQLiteTableQuery -> handle.batch(*columns)
            is MySQLTableQuery -> handle.batch(*columns)
            else -> throw IllegalStateException("Unknown TableQuery type")
        }
        val wrapper = KBatchQuery(batchHandle)
        wrapper.init()
        return wrapper.executeAsync().await()
    }

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

    private fun applyValues(block: KValues.() -> Unit) {
        val kv = KValues()
        kv.block()
        kv.map.forEach { (k, v) -> handle.value(k, v) }
    }
}

class KValues {
    internal val map = mutableMapOf<String, Any?>()
    
    infix fun String.to(value: Any?) {
        map[this] = value
    }
    
    operator fun set(column: String, value: Any?) {
        map[column] = value
    }
}

class KBatchQuery(val handle: AbstractBatchQuery<*>) {
    fun insert() { handle.insert() }
    fun replace() { handle.replace() }
    fun insertIgnore() { handle.insertIgnore() }

    fun add(vararg values: Any?) {
        handle.add(*values)
    }

    fun execute() = handle.execute()
    fun executeAsync(): CompletableFuture<Int> = handle.executeAsync()
}