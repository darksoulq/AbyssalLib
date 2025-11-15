package com.github.darksoulq.abyssallib.world.gui

import net.kyori.adventure.text.Component
import org.bukkit.inventory.MenuType

fun gui(menuType: MenuType, title: Component, block: GuiBuilder.() -> Unit): Gui {
    val builder = GuiBuilder(menuType, title)
    builder.block()
    return builder.build()
}

class GuiBuilder(private val menuType: MenuType, private val title: Component) {
    private val elements = mutableMapOf<SlotPosition, GuiElement>()
    private val layers = mutableListOf<GuiLayer>()
    private val tickers = mutableListOf<(GuiView) -> Unit>()
    private val flags = mutableSetOf<GuiFlag>()
    private var onOpen: ((GuiView) -> Unit)? = null
    private var onClose: ((GuiView) -> Unit)? = null

    fun set(pos: SlotPosition, element: GuiElement) = apply {
        elements[pos] = element
    }

    fun layer(layer: GuiLayer) = apply {
        layers.add(layer)
    }

    fun tick(block: (GuiView) -> Unit) = apply {
        tickers.add(block)
    }

    fun onOpen(block: (GuiView) -> Unit) = apply { onOpen = block }
    fun onClose(block: (GuiView) -> Unit) = apply { onClose = block }

    fun flag(flag: GuiFlag) = apply { flags.add(flag) }
    fun flags(vararg fs: GuiFlag) = apply { flags.addAll(fs) }

    fun build(): Gui {
        val javaBuilder = Gui.Builder(menuType, title)
        elements.forEach { (pos, elem) -> javaBuilder.set(pos, elem) }
        layers.forEach { javaBuilder.addLayer(it) }
        tickers.forEach { javaBuilder.onTick(it) }
        onOpen?.let { javaBuilder.onOpen(it) }
        onClose?.let { javaBuilder.onClose(it) }
        flags.forEach { javaBuilder.addFlag(it) }
        return javaBuilder.build()
    }
}
