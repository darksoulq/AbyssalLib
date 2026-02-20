package com.github.darksoulq.abyssallib.common.database

import com.github.darksoulq.abyssallib.common.database.relational.AbstractDatabase
import com.github.darksoulq.abyssallib.common.database.relational.AbstractTableBuilder
import com.github.darksoulq.abyssallib.common.database.relational.AbstractTableQuery
import com.github.darksoulq.abyssallib.common.database.relational.mysql.Database as MySQLDatabase
import com.github.darksoulq.abyssallib.common.database.relational.sql.Database as SQLiteDatabase
import java.io.File
import java.sql.Connection

fun sqlite(file: File, init: KDatabase.() -> Unit = {}): KDatabase {
    val db = SQLiteDatabase(file)
    val adapter = object : ExecutorAdapter {
        override fun table(name: String) = db.executor().table(name)
        override fun create(name: String) = db.executor().create(name)
        override fun raw(sql: String) = db.executor().executeRaw(sql)
    }
    return KDatabase(db, adapter).apply(init)
}

fun mysql(host: String, port: Int, dbName: String, user: String, pass: String, init: KDatabase.() -> Unit = {}): KDatabase {
    val db = MySQLDatabase(host, port, dbName, user, pass)
    val adapter = object : ExecutorAdapter {
        override fun table(name: String) = db.executor().table(name)
        override fun create(name: String) = db.executor().create(name)
        override fun raw(sql: String) = db.executor().executeRaw(sql)
    }
    return KDatabase(db, adapter).apply(init)
}

interface ExecutorAdapter {
    fun table(name: String): AbstractTableQuery<*>
    fun create(name: String): AbstractTableBuilder<*>
    fun raw(sql: String)
}

class KDatabase(val handle: AbstractDatabase, private val adapter: ExecutorAdapter) {
    fun connect(): Connection = handle.getConnection()
    
    fun transaction(action: KTransaction.() -> Unit) {
        handle.executeTransaction { _ ->
            action(KTransaction(adapter))
        }
    }

    fun <T> transactionResult(action: KTransaction.() -> T): T {
        return handle.executeTransactionResult { _ ->
            action(KTransaction(adapter))
        }
    }

    fun table(name: String): KTableQuery {
        return KTableQuery(adapter.table(name))
    }

    fun create(name: String, init: KTableBuilder.() -> Unit) {
        val builder = KTableBuilder(adapter.create(name))
        builder.init()
        builder.build()
    }

    fun raw(sql: String) = adapter.raw(sql)
}

class KTransaction(private val adapter: ExecutorAdapter) {
    fun table(name: String) = KTableQuery(adapter.table(name))
    
    fun create(name: String, init: KTableBuilder.() -> Unit) {
        val builder = KTableBuilder(adapter.create(name))
        builder.init()
        builder.build()
    }
    
    fun raw(sql: String) = adapter.raw(sql)
}