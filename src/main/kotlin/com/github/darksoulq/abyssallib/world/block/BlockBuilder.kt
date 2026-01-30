package com.github.darksoulq.abyssallib.world.block

import com.github.darksoulq.abyssallib.common.util.Identifier
import com.github.darksoulq.abyssallib.server.event.ActionResult
import com.github.darksoulq.abyssallib.server.event.custom.block.BlockInteractionEvent
import com.github.darksoulq.abyssallib.world.block.property.Property
import com.github.darksoulq.abyssallib.world.entity.EntityBuilder
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.BlockState
import org.bukkit.block.sign.Side
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.block.BlockIgniteEvent
import org.bukkit.inventory.ItemStack

@DslMarker
annotation class BlockDSL

@BlockDSL
class BlockBuilder(val id: Identifier, val material: Material) {
    private var propertiesConfig: (BlockProperties.Builder.() -> Unit)? = null
    private var entityFactory: ((CustomBlock) -> BlockEntity)? = null
    private var generateItem: Boolean = true

    private var onLoadHandler: (() -> Unit)? = null
    private var onUnLoadHandler: (() -> Unit)? = null
    private var interactHandler: ((BlockInteractionEvent) -> ActionResult)? = null
    private var placeHandler: ((Player, Location, ItemStack) -> ActionResult)? = null
    private var breakHandler: ((Player, Location, ItemStack) -> ActionResult)? = null
    private var explosionHandler: ((Entity?, Block?) -> ActionResult)? = null
    private var landHandler: ((Entity) -> Unit)? = null
    private var stepHandler: ((LivingEntity) -> Unit)? = null
    private var redstoneHandler: ((Int, Int) -> Int)? = null
    private var projectileHandler: ((Projectile) -> ActionResult)? = null
    private var neighborUpdateHandler: ((Block) -> ActionResult)? = null
    private var pistonMoveHandler: ((BlockFace) -> ActionResult)? = null
    private var boneMealHandler: ((Player) -> ActionResult)? = null
    private var fadeHandler: ((Block, BlockState) -> ActionResult)? = null
    private var formHandler: ((Block, BlockState) -> ActionResult)? = null
    private var growHandler: ((Block, BlockState) -> ActionResult)? = null
    private var igniteHandler: ((BlockIgniteEvent.IgniteCause, Entity, Block) -> ActionResult)? = null
    private var spreadHandler: ((Block, Block, BlockState) -> ActionResult)? = null
    private var leavesDecayHandler: (() -> ActionResult)? = null
    private var spongeAbsorbHandler: ((List<BlockState>) -> ActionResult)? = null
    private var signChangeHandler: ((Player, Side) -> ActionResult)? = null

    fun properties(init: BlockProperties.Builder.() -> Unit) {
        propertiesConfig = init
    }

    fun entity(init: EntityBuilder<CustomBlock, BlockEntity>.() -> Unit) {
        val builder = EntityBuilder<CustomBlock, BlockEntity>()
        builder.init()
        applyEntityBuilder(builder)
    }

    fun entity(builder: EntityBuilder<CustomBlock, BlockEntity>) {
        applyEntityBuilder(builder)
    }

    private fun applyEntityBuilder(builder: EntityBuilder<CustomBlock, BlockEntity>) {
        entityFactory = { block ->
            object : BlockEntity(block) {
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

    fun disableItem() { generateItem = false }

    fun onLoad(handler: () -> Unit) { onLoadHandler = handler }
    fun onUnLoad(handler: () -> Unit) { onUnLoadHandler = handler }
    fun onInteract(handler: (BlockInteractionEvent) -> ActionResult) { interactHandler = handler }
    fun onPlace(handler: (Player, Location, ItemStack) -> ActionResult) { placeHandler = handler }
    fun onBreak(handler: (Player, Location, ItemStack) -> ActionResult) { breakHandler = handler }
    fun onExplosion(handler: (Entity?, Block?) -> ActionResult) { explosionHandler = handler }
    fun onLand(handler: (Entity) -> Unit) { landHandler = handler }
    fun onStep(handler: (LivingEntity) -> Unit) { stepHandler = handler }
    fun onRedstone(handler: (Int, Int) -> Int) { redstoneHandler = handler }
    fun onProjectile(handler: (Projectile) -> ActionResult) { projectileHandler = handler }
    fun onNeighbor(handler: (Block) -> ActionResult) { neighborUpdateHandler = handler }
    fun onPiston(handler: (BlockFace) -> ActionResult) { pistonMoveHandler = handler }
    fun onBoneMeal(handler: (Player) -> ActionResult) { boneMealHandler = handler }
    fun onFade(handler: (Block, BlockState) -> ActionResult) { fadeHandler = handler }
    fun onForm(handler: (Block, BlockState) -> ActionResult) { formHandler = handler }
    fun onGrow(handler: (Block, BlockState) -> ActionResult) { growHandler = handler }
    fun onIgnite(handler: (BlockIgniteEvent.IgniteCause, Entity, Block) -> ActionResult) { igniteHandler = handler }
    fun onSpread(handler: (Block, Block, BlockState) -> ActionResult) { spreadHandler = handler }
    fun onLeavesDecay(handler: () -> ActionResult) { leavesDecayHandler = handler }
    fun onSponge(handler: (List<BlockState>) -> ActionResult) { spongeAbsorbHandler = handler }
    fun onSign(handler: (Player, Side) -> ActionResult) { signChangeHandler = handler }

    fun build(): CustomBlock {
        val block = object : CustomBlock(id, material) {
            override fun generateItem(): Boolean = generateItem

            override fun createBlockEntity(loc: Location): BlockEntity? {
                return entityFactory?.invoke(this)
            }

            override fun onLoad() = onLoadHandler?.invoke() ?: super.onLoad()
            override fun onUnLoad() = onUnLoadHandler?.invoke() ?: super.onUnLoad()
            override fun onInteract(event: BlockInteractionEvent) = interactHandler?.invoke(event) ?: super.onInteract(event)
            override fun onPlaced(player: Player, loc: Location, stack: ItemStack) = placeHandler?.invoke(player, loc, stack) ?: super.onPlaced(player, loc, stack)
            override fun onBreak(player: Player, loc: Location, tool: ItemStack) = breakHandler?.invoke(player, loc, tool) ?: super.onBreak(player, loc, tool)
            override fun onDestroyedByExplosion(eCause: Entity?, bCause: Block?) = explosionHandler?.invoke(eCause, bCause) ?: super.onDestroyedByExplosion(eCause, bCause)
            override fun onLanded(entity: Entity) { landHandler?.invoke(entity) ?: super.onLanded(entity) }
            override fun onSteppedOn(entity: LivingEntity) { stepHandler?.invoke(entity) ?: super.onSteppedOn(entity) }
            override fun onRedstone(old: Int, new: Int) = redstoneHandler?.invoke(old, new) ?: super.onRedstone(old, new)
            override fun onProjectileHit(p: Projectile) = projectileHandler?.invoke(p) ?: super.onProjectileHit(p)
            override fun onNeighborUpdate(b: Block) = neighborUpdateHandler?.invoke(b) ?: super.onNeighborUpdate(b)
            override fun onPistonMove(f: BlockFace) = pistonMoveHandler?.invoke(f) ?: super.onPistonMove(f)
            override fun onBoneMeal(p: Player) = boneMealHandler?.invoke(p) ?: super.onBoneMeal(p)
            override fun onFade(b: Block, s: BlockState) = fadeHandler?.invoke(b, s) ?: super.onFade(b, s)
            override fun onForm(b: Block, s: BlockState) = formHandler?.invoke(b, s) ?: super.onForm(b, s)
            override fun onGrow(b: Block, s: BlockState) = growHandler?.invoke(b, s) ?: super.onGrow(b, s)
            override fun onIgnite(c: BlockIgniteEvent.IgniteCause, e: Entity, b: Block) = igniteHandler?.invoke(c, e, b) ?: super.onIgnite(c, e, b)
            override fun onSpread(b: Block, s: Block, ns: BlockState) = spreadHandler?.invoke(b, s, ns) ?: super.onSpread(b, s, ns)
            override fun onLeavesDecay() = leavesDecayHandler?.invoke() ?: super.onLeavesDecay()
            override fun onSpongeAbsorb(l: List<BlockState>) = spongeAbsorbHandler?.invoke(l) ?: super.onSpongeAbsorb(l)
            override fun onSignChange(p: Player, s: Side) = signChangeHandler?.invoke(p, s) ?: super.onSignChange(p, s)
        }

        if (propertiesConfig != null) {
            val propBuilder = BlockProperties.of()
            propertiesConfig!!.invoke(propBuilder)
            block.properties = propBuilder.build()
        }

        return block
    }
}