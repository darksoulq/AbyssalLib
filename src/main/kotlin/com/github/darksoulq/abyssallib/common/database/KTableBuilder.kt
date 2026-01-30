package com.github.darksoulq.abyssallib.common.database

class KTableBuilder(val handle: AbstractTableBuilder<*>) {
    
    fun column(name: String, type: String, default: String? = null) {
        handle.column(name, type)
        if (default != null) handle.defaultValue(name, default)
    }

    fun primaryKey(vararg keys: String) {
        handle.primaryKey(*keys)
    }

    fun autoIncrement(column: String) {
        handle.autoIncrement(column)
    }

    fun foreignKey(column: String, table: String, refColumn: String) {
        handle.foreignKey(column, table, refColumn)
    }

    fun unique(vararg columns: String) {
        handle.unique(*columns)
    }

    fun check(expression: String) {
        handle.check(expression)
    }

    fun ifNotExists() {
        handle.ifNotExists()
    }

    fun dropIfExists() {
        handle.dropIfExists()
    }

    fun build() = handle.execute()
    
    infix fun String.type(type: String) = column(this, type)
}