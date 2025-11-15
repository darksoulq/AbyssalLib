package com.github.darksoulq.abyssallib.world.entity

import com.github.darksoulq.abyssallib.common.util.Identifier
import org.bukkit.damage.DamageEffect
import org.bukkit.damage.DamageScaling
import org.bukkit.damage.DeathMessageType

fun damageType(id: Identifier, block: DamageTypeBuilder.() -> Unit = {}): DamageType {
    val builder = DamageTypeBuilder(id)
    builder.block()
    return builder.build()
}

class DamageTypeBuilder(private val id: Identifier) {
    var effect: DamageEffect = DamageEffect.HURT
    var scaling: DamageScaling = DamageScaling.NEVER
    var deathMessageType: DeathMessageType = DeathMessageType.DEFAULT
    var exhaustion: Float = 0f

    fun build(): DamageType {
        return DamageType.builder(id)
            .damageEffect(effect)
            .damageScaling(scaling)
            .deathMessageType(deathMessageType)
            .exhaustion(exhaustion)
            .build()
    }
}
