package com.github.darksoulq.abyssallib.world.item

import com.github.darksoulq.abyssallib.common.util.Identifier
import com.github.darksoulq.abyssallib.server.event.ActionResult
import com.github.darksoulq.abyssallib.server.event.ClickType
import com.github.darksoulq.abyssallib.server.event.InventoryClickType
import com.github.darksoulq.abyssallib.server.event.context.item.AnvilContext
import com.github.darksoulq.abyssallib.server.event.context.item.UseContext
import com.github.darksoulq.abyssallib.world.block.CustomBlock
import com.github.darksoulq.abyssallib.world.data.tag.impl.ItemTag
import com.github.darksoulq.abyssallib.world.item.component.DataComponent
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType
import com.github.darksoulq.abyssallib.world.item.component.builtin.BlockItem
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
class ItemBuilder(val id: Identifier, val material: Material) {
    private val dataComponents = mutableListOf<DataComponent<*>>()
    private val tags = mutableListOf<ItemTag>()
    private var tooltipConfig: (Item.Tooltip.() -> Unit)? = null

    private var postMineHandler: ((LivingEntity, Block) -> ActionResult)? = null
    private var postHitHandler: ((LivingEntity, Entity) -> ActionResult)? = null
    private var useOnHandler: ((UseContext) -> ActionResult)? = null
    private var useHandler: ((LivingEntity, EquipmentSlot, ClickType) -> ActionResult)? = null
    private var inventoryTickHandler: ((Player) -> Unit)? = null
    private var slotChangeHandler: ((Player, Int?) -> Unit)? = null
    private var inventoryClickHandler: ((Player, Int, PlayerInventory, InventoryClickType) -> ActionResult)? = null
    private var dropHandler: ((Player) -> ActionResult)? = null
    private var pickupHandler: ((Player) -> ActionResult)? = null
    private var swapHandHandler: ((Player, EquipmentSlot) -> ActionResult)? = null
    private var anvilPrepareHandler: ((AnvilContext) -> ActionResult)? = null
    private var craftedByHandler: ((Player) -> Unit)? = null

    fun tooltip(init: TooltipBuilder.() -> Unit) {
        this.tooltipConfig = {
            val builder = TooltipBuilder(this)
            builder.init()
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

    fun onMine(handler: (LivingEntity, Block) -> ActionResult) { postMineHandler = handler }
    fun onHit(handler: (LivingEntity, Entity) -> ActionResult) { postHitHandler = handler }
    fun onUseOn(handler: (UseContext) -> ActionResult) { useOnHandler = handler }
    fun onUse(handler: (LivingEntity, EquipmentSlot, ClickType) -> ActionResult) { useHandler = handler }
    fun onInventoryTick(handler: (Player) -> Unit) { inventoryTickHandler = handler }
    fun onSlotChange(handler: (Player, Int?) -> Unit) { slotChangeHandler = handler }
    fun onClick(handler: (Player, Int, PlayerInventory, InventoryClickType) -> ActionResult) { inventoryClickHandler = handler }
    fun onDrop(handler: (Player) -> ActionResult) { dropHandler = handler }
    fun onPickup(handler: (Player) -> ActionResult) { pickupHandler = handler }
    fun onSwapHand(handler: (Player, EquipmentSlot) -> ActionResult) { swapHandHandler = handler }
    fun onAnvil(handler: (AnvilContext) -> ActionResult) { anvilPrepareHandler = handler }
    fun onCraft(handler: (Player) -> Unit) { craftedByHandler = handler }

    fun build(): Item {
        return object : Item(id, material) {
            init {
                this@ItemBuilder.dataComponents.forEach { setData(it) }
                this@ItemBuilder.tags.forEach { setTag(it) }
            }

            override fun createTooltip(tooltip: Tooltip) {
                this@ItemBuilder.tooltipConfig?.invoke(tooltip)
            }

            override fun postMine(source: LivingEntity, target: Block): ActionResult =
                postMineHandler?.invoke(source, target) ?: super.postMine(source, target)

            override fun postHit(source: LivingEntity, target: Entity): ActionResult =
                postHitHandler?.invoke(source, target) ?: super.postHit(source, target)

            override fun onUseOn(ctx: UseContext): ActionResult =
                useOnHandler?.invoke(ctx) ?: super.onUseOn(ctx)

            override fun onUse(source: LivingEntity, hand: EquipmentSlot, type: ClickType): ActionResult =
                useHandler?.invoke(source, hand, type) ?: super.onUse(source, hand, type)

            override fun onInventoryTick(player: Player) {
                inventoryTickHandler?.invoke(player) ?: super.onInventoryTick(player)
            }

            override fun onSlotChange(player: Player, newSlot: Int?) {
                slotChangeHandler?.invoke(player, newSlot) ?: super.onSlotChange(player, newSlot)
            }

            override fun onClickInInventory(player: Player, slot: Int, inventory: PlayerInventory, type: InventoryClickType): ActionResult =
                inventoryClickHandler?.invoke(player, slot, inventory, type) ?: super.onClickInInventory(player, slot, inventory, type)

            override fun onDrop(player: Player): ActionResult =
                dropHandler?.invoke(player) ?: super.onDrop(player)

            override fun onPickup(player: Player): ActionResult =
                pickupHandler?.invoke(player) ?: super.onPickup(player)

            override fun onSwapHand(player: Player, current: EquipmentSlot): ActionResult =
                swapHandHandler?.invoke(player, current) ?: super.onSwapHand(player, current)

            override fun onAnvilPrepare(ctx: AnvilContext): ActionResult =
                anvilPrepareHandler?.invoke(ctx) ?: super.onAnvilPrepare(ctx)

            override fun onCraftedBy(player: Player) {
                craftedByHandler?.invoke(player) ?: super.onCraftedBy(player)
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

    var style: Identifier?
        get() = tooltip.style
        set(value) = tooltip.withStyle(value)

    fun line(component: Component) {
        tooltip.addLine(component)
    }

    fun hide(type: PaperComponentType) {
        tooltip.withHidden(type)
    }
}