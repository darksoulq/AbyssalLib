package com.github.darksoulq.abyssallib.world.entity

import net.kyori.adventure.key.Key
import org.bukkit.damage.DamageEffect
import org.bukkit.damage.DamageScaling
import org.bukkit.damage.DeathMessageType

fun damageType(id: Key, block: DamageTypeBuilder.() -> Unit = {}): DamageType {
    val builder = DamageTypeBuilder(id)
    builder.block()
    return builder.build()
}

@DslMarker
annotation class DamageTypeDsl

@DamageTypeDsl
class DamageTypeBuilder(private val id: Key) {
    var effect: DamageEffect = DamageEffect.HURT
    var scaling: DamageScaling = DamageScaling.NEVER
    var deathMessageType: DeathMessageType = DeathMessageType.DEFAULT
    var messageId: String = ";"
    var exhaustion: Float = 0f

    fun build(): DamageType {
        return DamageType.builder(id)
            .damageEffect(effect)
            .damageScaling(scaling)
            .deathMessageType(deathMessageType)
            .messageId(messageId)
            .exhaustion(exhaustion)
            .build()
    }
}
