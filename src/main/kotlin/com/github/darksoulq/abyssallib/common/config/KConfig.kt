package com.github.darksoulq.abyssallib.common.config

import com.github.darksoulq.abyssallib.common.serialization.Codec
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A Kotlin-idiomatic wrapper for the Java-based [Config] system.
 *
 * This class allows for the use of Kotlin property delegates (`by`), transforming
 * configuration keys into first-class Kotlin properties.
 *
 * @property handle The underlying Java [Config] instance that performs the actual I/O.
 */
open class KConfig(val handle: Config) {

    /**
     * Constructs a [KConfig] by initializing a new [Config] with a subfolder.
     *
     * @param pluginId The unique ID of the plugin owning this configuration.
     * @param name The filename (excluding extension).
     * @param subfolder The directory within the plugin folder.
     */
    constructor(pluginId: String, name: String, subfolder: String) : this(Config(pluginId, name, subfolder))

    /**
     * Constructs a [KConfig] by initializing a new [Config] in the root plugin folder.
     *
     * @param pluginId The unique ID of the plugin owning this configuration.
     * @param name The filename (excluding extension).
     */
    constructor(pluginId: String, name: String) : this(Config(pluginId, name))

    /**
     * Persists all current in-memory values to the physical configuration file.
     */
    fun save() {
        handle.save()
    }

    /**
     * Synchronizes the in-memory values with the current state of the physical file.
     */
    fun reload() {
        handle.reload()
    }

    /**
     * Creates a configuration value handle that supports Kotlin delegation.
     *
     * @param T The type of the value being retrieved/stored.
     * @param path The YAML path to the value.
     * @param default The value used if the path does not exist.
     * @param codec An optional [Codec] for serializing/deserializing complex types.
     * @return A [KConfigValue] instance ready for delegation.
     */
    fun <T> value(path: String, default: T, codec: Codec<T>? = null): KConfigValue<T> {
        val valueHandle = if (codec != null) {
            handle.value(path, default, codec)
        } else {
            handle.value(path, default)
        }
        return KConfigValue(valueHandle)
    }

    /**
     * Creates a nested configuration section with a shared path prefix.
     *
     * @param path The prefix to be applied to all values inside this section.
     * @return A [Section] instance.
     */
    fun section(path: String): Section {
        return Section(path)
    }

    /**
     * Represents a logical grouping within the configuration file.
     *
     * @property prefix The string prepended to all relative paths within this section.
     */
    inner class Section(private val prefix: String) {

        /**
         * Creates a configuration value relative to this section's prefix.
         *
         * @param T The data type.
         * @param path The relative path from the prefix.
         * @param default The default value.
         * @param codec Optional custom serialization logic.
         * @return A [KConfigValue] delegate.
         */
        fun <T> value(path: String, default: T, codec: Codec<T>? = null): KConfigValue<T> {
            val fullPath = if (prefix.isEmpty()) path else "$prefix.$path"
            return this@KConfig.value(fullPath, default, codec)
        }

        /**
         * Creates a sub-section nested within this section.
         *
         * @param path The relative path to append to the current prefix.
         * @return A new nested [Section].
         */
        fun section(path: String): Section {
            val fullPath = if (prefix.isEmpty()) path else "$prefix.$path"
            return Section(fullPath)
        }
    }

    /**
     * A property delegate that bridges Kotlin properties to the AbyssalLib Config system.
     *
     * @param T The type of the value held.
     * @property handle The underlying Java-based value handle.
     */
    class KConfigValue<T>(private val handle: Config.Value<T>) : ReadWriteProperty<Any?, T> {

        /**
         * Retrieves the value from the configuration.
         * * @param thisRef The object the property belongs to.
         * @param property The metadata for the property.
         * @return The current value of type [T].
         */
        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return handle.get()
        }

        /**
         * Updates the value in the configuration.
         *
         * @param thisRef The object the property belongs to.
         * @param property The metadata for the property.
         * @param value The new value to store.
         */
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            handle.set(value)
        }

        /**
         * Adds a header comment above this value in the YAML file.
         *
         * @param lines The lines of text to include in the comment.
         * @return This [KConfigValue] instance for method chaining.
         */
        fun comment(vararg lines: String): KConfigValue<T> {
            handle.withComment(*lines)
            return this
        }

        /**
         * Functional getter for the value.
         * @return The value.
         */
        fun get(): T = handle.get()

        /**
         * Functional setter for the value.
         * @param value The value to set.
         */
        fun set(value: T) = handle.set(value)

        /**
         * Provides access to the underlying Java API.
         * @return The [Config.Value] instance.
         */
        fun asJava(): Config.Value<T> = handle
    }
}