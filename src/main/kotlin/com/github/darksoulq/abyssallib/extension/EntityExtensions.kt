package com.github.darksoulq.abyssallib.extension

import com.github.darksoulq.abyssallib.world.data.attribute.Attribute
import com.github.darksoulq.abyssallib.world.data.attribute.EntityAttributes
import org.bukkit.entity.Entity

fun Entity.getAttribute(attr: Attribute): Double {
    return EntityAttributes.of(this).getValue(attr)
}

fun Entity.getAttributeBase(attr: Attribute): Double {
    return EntityAttributes.of(this).getBaseValue(attr)
}

fun Entity.setAttributeBase(attr: Attribute, value: Double) {
    EntityAttributes.of(this).setBaseValue(attr, value)
}

fun Entity.getAttributes(): EntityAttributes {
    return EntityAttributes.of(this)
}