package com.github.darksoulq.abyssallib.world.particle

import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Display
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.function.BooleanSupplier

fun particles(block: ParticlesBuilder.() -> Unit): Particles {
    val builder = ParticlesBuilder()
    builder.block()
    return builder.build()
}

class ParticlesBuilder {
    var type: Particle? = null
    var displayItem: ItemStack? = null
    var origin: Location? = null
    var emitter: ParticleEmitter? = null
    var count: Int = 1
    var offsetX: Double = 0.0
    var offsetY: Double = 0.0
    var offsetZ: Double = 0.0
    var speed: Double = 0.0
    var shape: Shape? = null
    var scaleX: Float = 1f
    var scaleY: Float = 1f
    var scaleZ: Float = 1f
    var billboard: Display.Billboard = Display.Billboard.HORIZONTAL
    var xRotDeg: Float = 0f
    var yRotDeg: Float = 0f
    var zRotDeg: Float = 0f
    var data: Any? = null
    var interval: Long = 1
    var duration: Long = -1
    var cancelIf: BooleanSupplier? = null
    var viewers: List<Player>? = null
    var asyncShape: Boolean = false

    fun scale(value: Float) {
        scaleX = value
        scaleY = value
        scaleZ = value
    }

    fun scale(x: Float, y: Float, z: Float) {
        scaleX = x
        scaleY = y
        scaleZ = z
    }

    fun viewers(vararg players: Player) {
        viewers = players.toList()
    }

    fun build(): Particles {
        val builder = Particles.Builder()
        type?.let { builder.particle(it) }
        displayItem?.let { builder.display(it) }
        origin?.let { builder.spawnAt(it) }
        emitter?.let { builder.spawnAt(it) }
        builder.count(count)
            .offset(offsetX, offsetY, offsetZ)
            .speed(speed)
            .shape(shape)
            .scale(scaleX, scaleY, scaleZ)
            .billboard(billboard)
            .rotation(xRotDeg, yRotDeg, zRotDeg)
            .data(data)
            .interval(interval)
            .duration(duration)
            .asyncShape(asyncShape)
        cancelIf?.let { builder.cancelIf(it) }
        viewers?.let { builder.viewers(it) }
        return builder.build()
    }
}
