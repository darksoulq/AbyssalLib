package com.github.darksoulq.abyssallib.world.multiblock

import com.github.darksoulq.abyssallib.common.util.Identifier
import com.github.darksoulq.abyssallib.server.event.ActionResult
import com.github.darksoulq.abyssallib.world.block.property.Property
import com.github.darksoulq.abyssallib.world.entity.EntityBuilder
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.inventory.ItemStack

@DslMarker
annotation class MultiblockDSL

@MultiblockDSL
class MultiblockBuilder(val id: Identifier) {
    private val patternMap = mutableMapOf<Char, MultiblockChoice>()
    private var shape: Array<String> = emptyArray()
    private var triggerChar: Char = '0'
    private var entityFactory: ((Multiblock) -> MultiblockEntity)? = null

    private var onLoadHandler: (() -> Unit)? = null
    private var onUnLoadHandler: (() -> Unit)? = null
    private var constructHandler: ((Player, Multiblock, ItemStack) -> ActionResult)? = null
    private var breakHandler: ((Player, Multiblock, ItemStack) -> ActionResult)? = null
    private var explosionHandler: ((Entity?, Block?) -> ActionResult)? = null
    private var redstoneHandler: ((Int, Int) -> Int)? = null
    private var projectileHandler: ((Projectile) -> ActionResult)? = null

    fun entity(init: EntityBuilder<Multiblock, MultiblockEntity>.() -> Unit) {
        val builder = EntityBuilder<Multiblock, MultiblockEntity>()
        builder.init()
        applyEntityBuilder(builder)
    }

    fun entity(builder: EntityBuilder<Multiblock, MultiblockEntity>) {
        applyEntityBuilder(builder)
    }

    private fun applyEntityBuilder(builder: EntityBuilder<Multiblock, MultiblockEntity>) {
        entityFactory = { multiblock ->
            object : MultiblockEntity(multiblock) {
                private val dynamicProperties = builder.properties

                override fun serverTick() = builder.serverTickHandler?.invoke(this) ?: super.serverTick()
                override fun randomTick() = builder.randomTickHandler?.invoke(this) ?: super.randomTick()
                override fun onLoad() = builder.onLoadHandler?.invoke(this) ?: super.onLoad()
                override fun onSave() = builder.onSaveHandler?.invoke(this) ?: super.onSave()

                @Suppress("UNCHECKED_CAST")
                override fun <D> serialize(ops: com.github.darksoulq.abyssallib.common.serialization.DynamicOps<D>): D {
                    val map = mutableMapOf<D, D>()
                    for ((name, prop) in dynamicProperties) {
                        map[ops.createString(name)] = (prop as Property<Any>).encode(ops)
                    }
                    return ops.createMap(map)
                }

                @Suppress("UNCHECKED_CAST")
                override fun <D> deserialize(ops: com.github.darksoulq.abyssallib.common.serialization.DynamicOps<D>, input: D) {
                    val map = ops.getMap(input).orElse(emptyMap())
                    for ((name, prop) in dynamicProperties) {
                        val encoded = map[ops.createString(name)]
                        if (encoded != null) {
                            (prop as Property<Any>).decode(ops, encoded)
                        }
                    }
                }
            }
        }
    }

    fun pattern(vararg rows: String) {
        shape = rows as Array<String>
    }

    fun where(char: Char, choice: MultiblockChoice) {
        patternMap[char] = choice
    }

    fun where(char: Char, material: Material) {
        patternMap[char] = object : MultiblockChoice() {
            override fun matches(block: Block): Boolean = block.type == material
        }
    }

    fun trigger(char: Char) {
        triggerChar = char
    }

    fun onLoad(handler: () -> Unit) { onLoadHandler = handler }
    fun onUnLoad(handler: () -> Unit) { onUnLoadHandler = handler }
    fun onConstruct(handler: (Player, Multiblock, ItemStack) -> ActionResult) { constructHandler = handler }
    fun onBreak(handler: (Player, Multiblock, ItemStack) -> ActionResult) { breakHandler = handler }
    fun onExplosion(handler: (Entity?, Block?) -> ActionResult) { explosionHandler = handler }
    fun onRedstone(handler: (Int, Int) -> Int) { redstoneHandler = handler }
    fun onProjectile(handler: (Projectile) -> ActionResult) { projectileHandler = handler }

    fun build(): Multiblock {
        val mb = object : Multiblock(id) {
            override fun getTriggerChoice(): MultiblockChoice = patternMap[triggerChar] ?: MultiblockChoice.empty()

            override fun createMultiblockEntity(origin: Location): MultiblockEntity? {
                return entityFactory?.invoke(this)
            }

            override fun onLoad() = onLoadHandler?.invoke() ?: super.onLoad()
            override fun onUnLoad() = onUnLoadHandler?.invoke() ?: super.onUnLoad()
            override fun onConstruct(player: Player, mb: Multiblock, held: ItemStack) = constructHandler?.invoke(player, mb, held) ?: super.onConstruct(player, mb, held)
            override fun onBreak(player: Player, mb: Multiblock, tool: ItemStack) = breakHandler?.invoke(player, mb, tool) ?: super.onBreak(player, mb, tool)
            override fun onDestroyedByExplosion(eCause: Entity?, bCause: Block?) = explosionHandler?.invoke(eCause, bCause) ?: super.onDestroyedByExplosion(eCause, bCause)
            override fun onRedstone(old: Int, now: Int) = redstoneHandler?.invoke(old, now) ?: super.onRedstone(old, now)
            override fun onProjectileHit(projectile: Projectile) = projectileHandler?.invoke(projectile) ?: super.onProjectileHit(projectile)
        }

        buildPattern(mb)
        return mb
    }

    private fun buildPattern(mb: Multiblock) {
        if (shape.isEmpty()) return

        var triggerPos: RelativeBlockPos? = null

        for (z in shape.indices) {
            val row = shape[z]
            for (x in row.indices) {
                if (row[x] == triggerChar) {
                    triggerPos = RelativeBlockPos(x, 0, z)
                    break
                }
            }
        }

        if (triggerPos == null) triggerPos = RelativeBlockPos(0, 0, 0)

        for (z in shape.indices) {
            val row = shape[z]
            for (x in row.indices) {
                val char = row[x]
                if (char == ' ') continue
                val choice = patternMap[char] ?: continue

                val relX = x - triggerPos.x()
                val relY = 0
                val relZ = z - triggerPos.z()

                mb.pattern[RelativeBlockPos(relX, relY, relZ)] = choice
            }
        }
    }
}