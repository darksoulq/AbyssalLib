package com.github.darksoulq.abyssallib.common.config

import com.github.darksoulq.abyssallib.common.serialization.Codec
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

open class KConfig(val handle: Config) {

    constructor(pluginId: String, name: String, subfolder: String) : this(Config(pluginId, name, subfolder))
    constructor(pluginId: String, name: String) : this(Config(pluginId, name))

    fun save() {
        handle.save()
    }

    fun reload() {
        handle.reload()
    }

    fun <T> value(path: String, default: T, codec: Codec<T>? = null): KConfigValue<T> {
        val valueHandle = if (codec != null) {
            handle.value(path, default, codec)
        } else {
            handle.value(path, default)
        }
        return KConfigValue(valueHandle)
    }

    fun section(path: String): Section {
        return Section(path)
    }

    inner class Section(private val prefix: String) {
        fun <T> value(path: String, default: T, codec: Codec<T>? = null): KConfigValue<T> {
            val fullPath = if (prefix.isEmpty()) path else "$prefix.$path"
            return this@KConfig.value(fullPath, default, codec)
        }

        fun section(path: String): Section {
            val fullPath = if (prefix.isEmpty()) path else "$prefix.$path"
            return Section(fullPath)
        }
    }

    class KConfigValue<T>(private val handle: Config.Value<T>) : ReadWriteProperty<Any?, T> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return handle.get()
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            handle.set(value)
        }

        fun comment(vararg lines: String): KConfigValue<T> {
            handle.withComment(*lines)
            return this
        }

        fun get(): T = handle.get()
        fun set(value: T) = handle.set(value)
        fun asJava(): Config.Value<T> = handle
    }
}