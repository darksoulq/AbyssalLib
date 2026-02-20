package com.github.darksoulq.abyssallib.extension

import com.github.darksoulq.abyssallib.common.util.Identifier
import com.github.darksoulq.abyssallib.server.chat.ChatInputHandler
import com.github.darksoulq.abyssallib.server.permission.PermissionNode
import com.github.darksoulq.abyssallib.server.registry.`object`.Holder
import com.github.darksoulq.abyssallib.world.data.statistic.PlayerStatistics
import com.github.darksoulq.abyssallib.world.data.statistic.Statistic
import com.github.darksoulq.abyssallib.world.gui.Gui
import com.github.darksoulq.abyssallib.world.gui.GuiManager
import com.github.darksoulq.abyssallib.world.item.Item
import io.papermc.paper.datacomponent.DataComponentTypes
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

data class GiveResult(
    val leftovers: List<Item>,
    val drops: List<Item>
)

fun Player.openGui(gui: Gui) {
    GuiManager.close(this)
    GuiManager.open(this, gui);
}
fun Player.closeGui() = GuiManager.close(this)

fun Player.give(item: Item): GiveResult = give(item, true)
fun Player.give(item: Item, shouldDrop: Boolean): GiveResult {
    val stack = item.stack.clone()
    val remaining = this.remainingSpaceFor(listOf(stack))
    val toGive = stack.clone()
    toGive.amount = remaining.coerceAtMost(stack.amount)
    this.give(toGive)

    val leftoverAmount = stack.amount - toGive.amount
    if (leftoverAmount <= 0) return GiveResult(emptyList(), emptyList())

    val leftoverStack = stack.clone()
    leftoverStack.amount = leftoverAmount
    val leftoverItem = Item.resolve(leftoverStack)

    val drops = if (shouldDrop) {
        this.world.dropItemNaturally(this.location, leftoverStack)
        listOf(leftoverItem)
    } else listOf()

    val leftovers = if (shouldDrop) emptyList() else listOf(leftoverItem)

    return GiveResult(leftovers, drops)
}
fun Player.give(items: List<Item>): GiveResult = give(items, true)
fun Player.give(items: List<Item>, shouldDrop: Boolean): GiveResult {
    val totalLeftovers = mutableListOf<Item>()
    val totalDrops = mutableListOf<Item>()

    for (item in items) {
        val stack = item.stack.clone()
        while (stack.amount > 0) {
            val remaining = this.remainingSpaceFor(listOf(stack))
            if (remaining <= 0) {
                if (shouldDrop) {
                    this.world.dropItemNaturally(this.location, stack.clone())
                    totalDrops.add(Item.resolve(stack.clone()))
                } else {
                    totalLeftovers.add(Item.resolve(stack.clone()))
                }
                break
            }

            val toGive = stack.clone()
            toGive.amount = remaining.coerceAtMost(stack.amount)
            this.give(toGive)

            stack.amount -= toGive.amount
        }
    }

    return GiveResult(totalLeftovers, totalDrops)
}

fun Player.remainingSpaceFor(items: List<ItemStack>): Int {
    var totalSpace = 0
    val inv = this.inventory
    val contents = inv.storageContents

    for (item in items) {
        val max = item.getData(DataComponentTypes.MAX_STACK_SIZE) ?: 1
        var amountLeft = max

        for (slot in contents) {
            if (slot == null) {
                amountLeft = 0
                break
            }
            if (!slot.isSimilar(item)) continue

            val remaining = max - slot.amount
            if (remaining > 0) {
                amountLeft -= remaining
                if (amountLeft <= 0) break
            }
        }

        if (amountLeft > 0) {
            val emptySlots = contents.count { it == null }
            val spaceFromEmpty = emptySlots * max
            totalSpace += max - amountLeft + spaceFromEmpty
        } else {
            totalSpace += max
        }
    }

    return totalSpace
}

fun Player.chatInput(inputHandler: Consumer<String>) = ChatInputHandler.await(this, inputHandler)
fun Player.chatInput(inputHandler: Consumer<String>, prompt: Component) = ChatInputHandler.await(this, inputHandler, prompt)
fun Player.chatInput(inputHandler: Consumer<String>, timeoutTicks: Long) = ChatInputHandler.await(this, inputHandler, timeoutTicks)
fun Player.chatInput(inputHandler: Consumer<String>, prompt: Component, timeoutTicks: Long) = ChatInputHandler.await(this, inputHandler, prompt, timeoutTicks)
fun Player.cancelChatInput() = ChatInputHandler.cancel(this)

fun Player.getStat(id: Identifier) : Statistic? = PlayerStatistics.of(this).get(id)
fun Player.setStat(stat: Statistic) = PlayerStatistics.of(this).set(stat)

fun Player.hasPerm(perm: PermissionNode) : Boolean = perm.has(this)
fun Player.hasPerm(perm: Holder<PermissionNode>) : Boolean = perm.get().has(this)