package com.github.darksoulq.abyssallib.world.item

import com.github.darksoulq.abyssallib.server.event.ActionResult
import com.github.darksoulq.abyssallib.server.event.ClickType
import com.github.darksoulq.abyssallib.server.event.InventoryClickType
import com.github.darksoulq.abyssallib.server.event.context.item.AnvilContext
import com.github.darksoulq.abyssallib.server.event.context.item.UseContext
import com.github.darksoulq.abyssallib.world.block.CustomBlock
import com.github.darksoulq.abyssallib.world.data.tag.impl.ItemTag
import com.github.darksoulq.abyssallib.world.item.component.DataComponent
import com.github.darksoulq.abyssallib.world.item.component.builtin.BlockItem
import net.kyori.adventure.key.Key
import io.papermc.paper.datacomponent.DataComponentType as PaperComponentType
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.PlayerInventory

@DslMarker
annotation class ItemDSL

@ItemDSL
class ItemBuilder(val id: Key, val material: Material) {
    private val dataComponents = mutableListOf<DataComponent<*>>()
    private val tags = mutableListOf<ItemTag>()
    private var tooltipConfig: (Item.Tooltip.() -> Unit)? = null
    private var playerTooltipConfig: (Item.Tooltip.(Player?) -> Unit)? = null

    private var onMineHandler: ((LivingEntity, Block) -> ActionResult)? = null
    private var onHitHandler: ((LivingEntity, Entity) -> ActionResult)? = null
    private var useOnHandler: ((UseContext) -> ActionResult)? = null
    private var useHandler: ((LivingEntity, EquipmentSlot, ClickType) -> ActionResult)? = null
    private var onInventoryTickHandler: ((Player) -> Unit)? = null
    private var onSlotChangeHandler: ((Player, Int?) -> Unit)? = null
    private var onInventoryClickHandler: ((Player, Int, PlayerInventory, InventoryClickType) -> ActionResult)? = null
    private var onDropHandler: ((Player) -> ActionResult)? = null
    private var onPickupHandler: ((Player) -> ActionResult)? = null
    private var onSwapHandHandler: ((Player, EquipmentSlot) -> ActionResult)? = null
    private var onAnvilPrepareHandler: ((AnvilContext) -> ActionResult)? = null
    private var onCraftHandler: ((Player) -> Unit)? = null

    fun tooltip(init: TooltipBuilder.() -> Unit) {
        this.tooltipConfig = {
            val builder = TooltipBuilder(this)
            builder.init()
        }
    }
    fun tooltip(init: TooltipBuilder.(Player?) -> Unit) {
        this.playerTooltipConfig = { player ->
            val builder = TooltipBuilder(this)
            builder.init(player)
        }
    }

    fun <T> component(component: DataComponent<T>) {
        dataComponents.add(component)
    }

    fun block(block: CustomBlock) {
        component(BlockItem(block.id))
    }

    fun tag(tag: ItemTag) {
        tags.add(tag)
    }

    fun onMine(handler: (LivingEntity, Block) -> ActionResult) { onMineHandler = handler }
    fun onHit(handler: (LivingEntity, Entity) -> ActionResult) { onHitHandler = handler }
    fun onUseOn(handler: (UseContext) -> ActionResult) { useOnHandler = handler }
    fun onUse(handler: (LivingEntity, EquipmentSlot, ClickType) -> ActionResult) { useHandler = handler }
    fun onInventoryTick(handler: (Player) -> Unit) { onInventoryTickHandler = handler }
    fun onSlotChange(handler: (Player, Int?) -> Unit) { onSlotChangeHandler = handler }
    fun onClick(handler: (Player, Int, PlayerInventory, InventoryClickType) -> ActionResult) { onInventoryClickHandler = handler }
    fun onDrop(handler: (Player) -> ActionResult) { onDropHandler = handler }
    fun onPickup(handler: (Player) -> ActionResult) { onPickupHandler = handler }
    fun onSwapHand(handler: (Player, EquipmentSlot) -> ActionResult) { onSwapHandHandler = handler }
    fun onAnvil(handler: (AnvilContext) -> ActionResult) { onAnvilPrepareHandler = handler }
    fun onCraft(handler: (Player) -> Unit) { onCraftHandler = handler }

    fun build(): Item {
        return object : Item(id, material) {
            init {
                this@ItemBuilder.dataComponents.forEach { setData(it) }
                this@ItemBuilder.tags.forEach { setTag(it) }
            }

            override fun createTooltip(tooltip: Tooltip) {
                this@ItemBuilder.tooltipConfig?.invoke(tooltip)
            }

            override fun createTooltip(tooltip: Tooltip, player: Player?) {
                this@ItemBuilder.playerTooltipConfig?.invoke(tooltip, player)
            }

            override fun onMine(source: LivingEntity, target: Block): ActionResult =
                onMineHandler?.invoke(source, target) ?: super.onMine(source, target)

            override fun onHit(source: LivingEntity, target: Entity): ActionResult =
                onHitHandler?.invoke(source, target) ?: super.onHit(source, target)

            override fun onUseOn(ctx: UseContext): ActionResult =
                useOnHandler?.invoke(ctx) ?: super.onUseOn(ctx)

            override fun onUse(source: LivingEntity, hand: EquipmentSlot, type: ClickType): ActionResult =
                useHandler?.invoke(source, hand, type) ?: super.onUse(source, hand, type)

            override fun onInventoryTick(player: Player) {
                onInventoryTickHandler?.invoke(player) ?: super.onInventoryTick(player)
            }

            override fun onSlotChange(player: Player, newSlot: Int?) {
                onSlotChangeHandler?.invoke(player, newSlot) ?: super.onSlotChange(player, newSlot)
            }

            override fun onClick(player: Player, slot: Int, inventory: PlayerInventory, type: InventoryClickType): ActionResult =
                onInventoryClickHandler?.invoke(player, slot, inventory, type) ?: super.onClick(player, slot, inventory, type)

            override fun onDrop(player: Player): ActionResult =
                onDropHandler?.invoke(player) ?: super.onDrop(player)

            override fun onPickup(player: Player): ActionResult =
                onPickupHandler?.invoke(player) ?: super.onPickup(player)

            override fun onSwapHand(player: Player, current: EquipmentSlot): ActionResult =
                onSwapHandHandler?.invoke(player, current) ?: super.onSwapHand(player, current)

            override fun onAnvil(ctx: AnvilContext): ActionResult =
                onAnvilPrepareHandler?.invoke(ctx) ?: super.onAnvil(ctx)

            override fun onCraft(player: Player) {
                onCraftHandler?.invoke(player) ?: super.onCraft(player)
            }
        }
    }
}

@ItemDSL
class TooltipBuilder(private val tooltip: Item.Tooltip) {
    var isVisible: Boolean
        get() = tooltip.isVisible
        set(value) {
            tooltip.isVisible = value
        }

    var style: Key?
        get() = tooltip.style
        set(value) = tooltip.withStyle(value)

    fun line(component: Component) {
        tooltip.addLine(component)
    }

    fun hide(type: PaperComponentType) {
        tooltip.withHidden(type)
    }
}