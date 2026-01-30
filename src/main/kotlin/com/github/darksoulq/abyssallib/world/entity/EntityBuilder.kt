package com.github.darksoulq.abyssallib.world.entity

import com.github.darksoulq.abyssallib.common.serialization.Codec
import com.github.darksoulq.abyssallib.world.block.property.Property

@DslMarker
annotation class EntityDSL

@EntityDSL
class EntityBuilder<T, E : AbstractPropertyEntity<T>> {
    val properties = mutableMapOf<String, Property<*>>()
    
    var serverTickHandler: ((E) -> Unit)? = null
    var randomTickHandler: ((E) -> Unit)? = null
    var onLoadHandler: ((E) -> Unit)? = null
    var onSaveHandler: ((E) -> Unit)? = null

    fun <V> property(name: String, codec: Codec<V>, default: V): Property<V> {
        val prop = Property(codec, default)
        properties[name] = prop
        return prop
    }

    fun onTick(handler: (E) -> Unit) { serverTickHandler = handler }
    fun onRandomTick(handler: (E) -> Unit) { randomTickHandler = handler }
    fun onLoad(handler: (E) -> Unit) { onLoadHandler = handler }
    fun onSave(handler: (E) -> Unit) { onSaveHandler = handler }
}