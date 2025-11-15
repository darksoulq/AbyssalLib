package com.github.darksoulq.abyssallib.extension

import com.github.darksoulq.abyssallib.world.entity.data.Attribute
import com.github.darksoulq.abyssallib.world.entity.data.EntityAttributes
import org.bukkit.entity.Entity

fun <T : Number> Entity.getAttribute(attr: Attribute<T>) : Number {
    return EntityAttributes.of(this).get<T>(attr)
}
fun <T : Number> Entity.getAttributeBase(attr: Attribute<T>) : Number {
    return EntityAttributes.of(this).getBaseValue<T>(attr)
}
fun <T : Number> Entity.setAttribute(attr: Attribute<T>, value: T) {
    EntityAttributes.of(this).set<T>(attr, value)
}
fun Entity.getAttributes() : EntityAttributes {
    return EntityAttributes.of(this)
}