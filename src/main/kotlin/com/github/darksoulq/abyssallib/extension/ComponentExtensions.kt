package com.github.darksoulq.abyssallib.extension

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer

val String.mm: Component
    get() = MiniMessage.miniMessage().deserialize(this)

val Component.mmString: String
    get() = MiniMessage.miniMessage().serialize(this)

val String.text: TextComponent
    get() = Component.text(this)

val Component.plain: String
    get() = PlainTextComponentSerializer.plainText().serialize(this)

fun String.mm(vararg resolvers: TagResolver): Component =
    MiniMessage.miniMessage().deserialize(this, *resolvers)

fun String.mm(vararg placeholders: Pair<String, Any?>): Component =
    mm(placeholders.toMap())

fun String.mm(placeholders: Map<String, Any?>): Component {
    val resolvers = placeholders.mapNotNull { (k, v) ->
        when (v) {
            null -> null
            is Component -> Placeholder.component(k, v)
            is TagResolver -> v
            else -> Placeholder.parsed(k, v.toString())
        }
    }
    return MiniMessage.miniMessage().deserialize(this, TagResolver.resolver(resolvers))
}

operator fun Component.plus(other: Component): Component = this.append(other)
operator fun Component.plus(text: String): Component = this.append(text.text)
operator fun String.plus(other: Component): Component = this.text.append(other)

infix fun Component.color(color: TextColor): Component = this.color(color)
infix fun Component.color(hex: String): Component = this.color(TextColor.fromHexString(if (hex.startsWith("#")) hex else "#$hex") ?: NamedTextColor.WHITE)

infix fun String.color(color: TextColor): Component = this.text.color(color)
infix fun String.color(hex: String): Component = this.text.color(TextColor.fromHexString(if (hex.startsWith("#")) hex else "#$hex") ?: NamedTextColor.WHITE)

infix fun Component.hover(text: String): Component = this.hoverEvent(HoverEvent.showText(text.mm))
infix fun Component.hover(component: Component): Component = this.hoverEvent(HoverEvent.showText(component))
infix fun String.hover(text: String): Component = this.text.hoverEvent(HoverEvent.showText(text.mm))
infix fun String.hover(component: Component): Component = this.text.hoverEvent(HoverEvent.showText(component))

infix fun Component.clickRun(command: String): Component = this.clickEvent(ClickEvent.runCommand(command))
infix fun Component.clickSuggest(command: String): Component = this.clickEvent(ClickEvent.suggestCommand(command))
infix fun Component.clickUrl(url: String): Component = this.clickEvent(ClickEvent.openUrl(url))
infix fun Component.clickCopy(text: String): Component = this.clickEvent(ClickEvent.copyToClipboard(text))

infix fun String.clickRun(command: String): Component = this.text.clickEvent(ClickEvent.runCommand(command))
infix fun String.clickSuggest(command: String): Component = this.text.clickEvent(ClickEvent.suggestCommand(command))
infix fun String.clickUrl(url: String): Component = this.text.clickEvent(ClickEvent.openUrl(url))
infix fun String.clickCopy(text: String): Component = this.text.clickEvent(ClickEvent.copyToClipboard(text))

infix fun Component.insert(text: String): Component = this.insertion(text)
infix fun String.insert(text: String): Component = this.text.insertion(text)

val Component.bold: Component get() = this.decorate(TextDecoration.BOLD)
val Component.italic: Component get() = this.decorate(TextDecoration.ITALIC)
val Component.underlined: Component get() = this.decorate(TextDecoration.UNDERLINED)
val Component.strikethrough: Component get() = this.decorate(TextDecoration.STRIKETHROUGH)
val Component.obfuscated: Component get() = this.decorate(TextDecoration.OBFUSCATED)

val Component.notBold: Component get() = this.decoration(TextDecoration.BOLD, false)
val Component.notItalic: Component get() = this.decoration(TextDecoration.ITALIC, false)
val Component.notUnderlined: Component get() = this.decoration(TextDecoration.UNDERLINED, false)
val Component.notStrikethrough: Component get() = this.decoration(TextDecoration.STRIKETHROUGH, false)
val Component.notObfuscated: Component get() = this.decoration(TextDecoration.OBFUSCATED, false)

val String.bold: Component get() = this.text.decorate(TextDecoration.BOLD)
val String.italic: Component get() = this.text.decorate(TextDecoration.ITALIC)
val String.underlined: Component get() = this.text.decorate(TextDecoration.UNDERLINED)
val String.strikethrough: Component get() = this.text.decorate(TextDecoration.STRIKETHROUGH)
val String.obfuscated: Component get() = this.text.decorate(TextDecoration.OBFUSCATED)

val Component.black: Component get() = this.color(NamedTextColor.BLACK)
val Component.darkBlue: Component get() = this.color(NamedTextColor.DARK_BLUE)
val Component.darkGreen: Component get() = this.color(NamedTextColor.DARK_GREEN)
val Component.darkAqua: Component get() = this.color(NamedTextColor.DARK_AQUA)
val Component.darkRed: Component get() = this.color(NamedTextColor.DARK_RED)
val Component.darkPurple: Component get() = this.color(NamedTextColor.DARK_PURPLE)
val Component.gold: Component get() = this.color(NamedTextColor.GOLD)
val Component.gray: Component get() = this.color(NamedTextColor.GRAY)
val Component.darkGray: Component get() = this.color(NamedTextColor.DARK_GRAY)
val Component.blue: Component get() = this.color(NamedTextColor.BLUE)
val Component.green: Component get() = this.color(NamedTextColor.GREEN)
val Component.aqua: Component get() = this.color(NamedTextColor.AQUA)
val Component.red: Component get() = this.color(NamedTextColor.RED)
val Component.lightPurple: Component get() = this.color(NamedTextColor.LIGHT_PURPLE)
val Component.yellow: Component get() = this.color(NamedTextColor.YELLOW)
val Component.white: Component get() = this.color(NamedTextColor.WHITE)

val String.black: Component get() = this.text.color(NamedTextColor.BLACK)
val String.darkBlue: Component get() = this.text.color(NamedTextColor.DARK_BLUE)
val String.darkGreen: Component get() = this.text.color(NamedTextColor.DARK_GREEN)
val String.darkAqua: Component get() = this.text.color(NamedTextColor.DARK_AQUA)
val String.darkRed: Component get() = this.text.color(NamedTextColor.DARK_RED)
val String.darkPurple: Component get() = this.text.color(NamedTextColor.DARK_PURPLE)
val String.gold: Component get() = this.text.color(NamedTextColor.GOLD)
val String.gray: Component get() = this.text.color(NamedTextColor.GRAY)
val String.darkGray: Component get() = this.text.color(NamedTextColor.DARK_GRAY)
val String.blue: Component get() = this.text.color(NamedTextColor.BLUE)
val String.green: Component get() = this.text.color(NamedTextColor.GREEN)
val String.aqua: Component get() = this.text.color(NamedTextColor.AQUA)
val String.red: Component get() = this.text.color(NamedTextColor.RED)
val String.lightPurple: Component get() = this.text.color(NamedTextColor.LIGHT_PURPLE)
val String.yellow: Component get() = this.text.color(NamedTextColor.YELLOW)
val String.white: Component get() = this.text.color(NamedTextColor.WHITE)

val emptyComponent: Component get() = Component.empty()
val spaceComponent: Component get() = Component.space()
val newlineComponent: Component get() = Component.newline()

fun Iterable<Component>.joinToComponent(separator: Component = Component.empty()): Component =
    Component.join(JoinConfiguration.separator(separator), this)

fun Iterable<Component>.joinToComponent(separator: String): Component =
    Component.join(JoinConfiguration.separator(separator.text), this)

fun Array<out Component>.joinToComponent(separator: Component = Component.empty()): Component =
    Component.join(JoinConfiguration.separator(separator), *this)

fun Array<out Component>.joinToComponent(separator: String): Component =
    Component.join(JoinConfiguration.separator(separator.text), *this)

inline fun buildComponent(builder: TextComponent.Builder.() -> Unit): TextComponent =
    Component.text().apply(builder).build()

inline fun Component.edit(builder: TextComponent.Builder.() -> Unit): Component =
    if (this is TextComponent) this.toBuilder().apply(builder).build()
    else Component.text().append(this).apply(builder).build()

fun Component.style(builder: Style.Builder.() -> Unit): Component =
    this.style(Style.style().apply(builder).build())