package io.github.darksoulq.abyssalLib.item;

import io.github.darksoulq.abyssalLib.util.ResourceLocation;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.keys.InstrumentKeys;
import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.Color;
import org.bukkit.JukeboxSong;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class provides an API to set various customizable properties and attributes for an {@link Item}.
 * It allows the setting of multiple data components that affect the behavior and characteristics of an {@link Item}.
 */
public class ItemSettings {
    private final Item owner;

    /**
     * Constructs an {@link ItemSettings} instance for the specified {@link Item}.
     *
     * @param owner the {@link Item} that this settings object is associated with
     */
    public ItemSettings(Item owner) {
        this.owner = owner;
    }

    // Setters
    /**
     * Sets the item attributes using the provided list of attribute modifiers.
     *
     * @param attribs the list of attribute modifiers to apply to the item
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings attribute(List<Attrib> attribs) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.itemAttributes();
        for (Attrib attrib : attribs) {
            builder.addModifier(attrib.attribute, attrib.modifier);
        }
        owner.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder.build());
        return this;
    }

    /**
     * Sets the item attributes by slot using the provided list of attribute modifiers.
     *
     * @param attribs the list of attribute modifiers, each associated with a specific slot
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings attributeBySlot(List<AttribWithSlot> attribs) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.itemAttributes();
        for (AttribWithSlot attrib : attribs) {
            builder.addModifier(attrib.attribute, attrib.modifier, attrib.slotGroup);
        }
        owner.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder.build());
        return this;
    }

    /**
     * Sets the food properties for the item, including the time it takes to consume.
     *
     * @param foodProperties the properties of the food item
     * @param consumeSeconds the time in seconds to consume the food
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings food(FoodProperties foodProperties, float consumeSeconds) {
        owner.setData(DataComponentTypes.FOOD, foodProperties);
        owner.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable().consumeSeconds(consumeSeconds).build());
        return this;
    }

    /**
     * Sets the food properties for the item, using a {@link Consumable} object for additional settings.
     *
     * @param foodProperties the properties of the food item
     * @param consumable the {@link Consumable} object containing additional properties
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings food(FoodProperties foodProperties, Consumable consumable) {
        owner.setData(DataComponentTypes.CONSUMABLE, consumable);
        owner.setData(DataComponentTypes.FOOD, foodProperties);
        return this;
    }

    /**
     * Sets the item to convert into the specified {@link ItemStack} upon use.
     *
     * @param item the {@link ItemStack} to convert to after use
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings usingConvertsTo(ItemStack item) {
        owner.setData(DataComponentTypes.USE_REMAINDER, UseRemainder.useRemainder(item));
        return this;
    }

    /**
     * Sets the cooldown time for using the item.
     *
     * @param useCooldown the cooldown time in seconds
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings useCooldown(float useCooldown) {
        owner.setData(DataComponentTypes.USE_COOLDOWN, UseCooldown.useCooldown(useCooldown).build());
        return this;
    }

    /**
     * Sets the maximum stack size for the item.
     *
     * @param size the maximum stack size
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings stackSize(int size) {
        owner.setData(DataComponentTypes.MAX_STACK_SIZE, size);
        return this;
    }

    /**
     * Sets the durability of the item.
     *
     * @param durability the maximum durability of the item
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings durability(int durability) {
        owner.setData(DataComponentTypes.MAX_DAMAGE, durability);
        owner.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        owner.setData(DataComponentTypes.DAMAGE, 0);
        return this;
    }

    /**
     * Sets the rarity of the item.
     *
     * @param rarity the rarity of the item
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings rarity(ItemRarity rarity) {
        owner.setData(DataComponentTypes.RARITY, rarity);
        return this;
    }

    /**
     * Marks the item as resistant to fire damage.
     *
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings fireResistant() {
        owner.setData(DataComponentTypes.DAMAGE_RESISTANT, DamageResistant.damageResistant(DamageTypeTagKeys.IS_FIRE));
        return this;
    }

    /**
     * Makes the item playable in a jukebox with the given song.
     *
     * @param song the {@link JukeboxSong} to associate with the item
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings jukeboxPlayable(JukeboxSong song) {
        owner.setData(DataComponentTypes.JUKEBOX_PLAYABLE, JukeboxPlayable.jukeboxPlayable(song).build());
        return this;
    }

    /**
     * Makes the item enchantable with a specific enchantment level.
     *
     * @param enchantmentLevel the level of enchantment that can be applied
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings enchantable(int enchantmentLevel) {
        owner.setData(DataComponentTypes.ENCHANTABLE, Enchantable.enchantable(enchantmentLevel));
        return this;
    }

    /**
     * Makes the item repairable with specific item types.
     *
     * @param itemTypes the list of {@link ItemType} that can be used for repairs
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings repairable(List<ItemType> itemTypes) {
        owner.setData(DataComponentTypes.REPAIRABLE, Repairable.repairable(RegistrySet.keySetFromValues(RegistryKey.ITEM, itemTypes)));
        return this;
    }

    /**
     * Makes the item equippable in the specified {@link EquipmentSlot}.
     *
     * @param slot the {@link EquipmentSlot} where the item can be equipped
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings equippable(EquipmentSlot slot) {
        owner.setData(DataComponentTypes.EQUIPPABLE, Equippable.equippable(slot).assetId(owner.getId().toNamespace()).build());
        return this;
    }

    /**
     * Makes the item equippable but unswappable in the specified {@link EquipmentSlot}.
     *
     * @param slot the {@link EquipmentSlot} where the item can be equipped
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings equippableUnswappable(EquipmentSlot slot) {
        owner.setData(DataComponentTypes.EQUIPPABLE, Equippable.equippable(slot).swappable(false).assetId(owner.getId().toNamespace()).build());
        return this;
    }

    /**
     * Configures the item as a tool with specific rules, mining speed, and block damage.
     *
     * @param toolRules the list of tool rules for the item
     * @param defaultMiningSpeed the default mining speed
     * @param damagePerBlock the damage dealt per block mined
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings tool(List<Tool.Rule> toolRules, int defaultMiningSpeed, int damagePerBlock) {
        owner.setData(DataComponentTypes.TOOL, Tool.tool().addRules(toolRules).defaultMiningSpeed(defaultMiningSpeed).damagePerBlock(damagePerBlock).build());
        return this;
    }

    /**
     * Sets the item lore.
     *
     * @param lore the lore text associated with the item
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings lore(ItemLore lore) {
        owner.setData(DataComponentTypes.LORE, lore);
        return this;
    }

    /**
     * Marks the item as unbreakable.
     *
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings unbreakable() {
        owner.setData(DataComponentTypes.UNBREAKABLE);
        return this;
    }

    /**
     * Makes the item a weapon with specified damage and blocking disable time.
     *
     * @param damage the damage dealt per attack
     * @param disableBlockingForSeconds the time during which blocking is disabled
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings weapon(int damage, int disableBlockingForSeconds) {
        owner.setData(DataComponentTypes.WEAPON, Weapon.weapon()
                .itemDamagePerAttack(damage)
                .disableBlockingForSeconds(disableBlockingForSeconds)
                .build());
        return this;
    }

    /**
     * Sets the item to produce a specific sound when placed in a note block.
     *
     * @param soundId the {@link ResourceLocation} representing the sound to play
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings noteBlockSound(ResourceLocation soundId) {
        owner.setData(DataComponentTypes.NOTE_BLOCK_SOUND, InstrumentKeys.create(soundId.toNamespace()));
        return this;
    }

    /**
     * Sets the tooltip display properties, including whether the tooltip should be hidden and which components should be hidden.
     *
     * @param hideTooltip whether to hide the tooltip
     * @param hiddenComponents the set of data components to hide from the tooltip
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings tooltip(boolean hideTooltip, Set<DataComponentType> hiddenComponents) {
        owner.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().hideTooltip(hideTooltip).hiddenComponents(hiddenComponents).build());
        return this;
    }

    /**
     * Specifies whether the item should display enchantment glint when enchanted.
     *
     * @param v whether to show the enchantment glint
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings showEnchantGlint(boolean v) {
        owner.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, v);
        return this;
    }

    /**
     * Marks the item as an intangible projectile.
     *
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings intangibleProjectile() {
        owner.setData(DataComponentTypes.INTANGIBLE_PROJECTILE);
        return this;
    }

    /**
     * Sets the item to be resistant to specific damage types.
     *
     * @param types the {@link TagKey} representing the damage types to resist
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings damageResistant(TagKey<DamageType> types) {
        owner.setData(DataComponentTypes.DAMAGE_RESISTANT, DamageResistant.damageResistant(types));
        return this;
    }

    /**
     * Marks the item as a glider.
     *
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings glider() {
        owner.setData(DataComponentTypes.GLIDER);
        return this;
    }

    /**
     * Sets the stored enchantments for the item.
     *
     * @param enchants a map of {@link Enchantment} to their respective levels
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings storedEnchants(Map<Enchantment, Integer> enchants) {
        owner.setData(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.itemEnchantments().addAll(enchants).build());
        return this;
    }

    /**
     * Sets the Tint of the decoration of a FilledMap item
     *
     * @param color the {@link Color} to use
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings mapColor(Color color) {
        owner.setData(DataComponentTypes.MAP_COLOR, MapItemColor.mapItemColor().color(color).build());
        return this;
    }

    /**
     * Sets the color of the item when dyed.
     *
     * @param color the {@link Color} to dye the item
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings dyedColor(Color color) {
        owner.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor().color(color).build());
        return this;
    }

    /**
     * Sets the loaded projectiles of a crossbow with a list of {@link ItemStack}s.
     *
     * @param items the list of {@link ItemStack}s that will be used as charged projectiles
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings chargedProjectiles(List<ItemStack> items) {
        owner.setData(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectiles.chargedProjectiles().addAll(items).build());
        return this;
    }

    /**
     * Sets the contents of the item as a bundle.
     *
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings bundleContents() {
        owner.setData(DataComponentTypes.BUNDLE_CONTENTS, BundleContents.bundleContents().build());
        return this;
    }

    /**
     * Sets the contents of the item as a bundle with a list of {@link ItemStack}s.
     *
     * @param items the list of {@link ItemStack}s to be added to the bundle
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings bundleContents(List<ItemStack> items) {
        owner.setData(DataComponentTypes.BUNDLE_CONTENTS, BundleContents.bundleContents().addAll(items).build());
        return this;
    }

    /**
     * Sets the potion contents of the item with a custom color and a list of effects.
     *
     * @param color the custom color of the potion
     * @param effects the list of {@link PotionEffect} to apply
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings potionContents(Color color, List<PotionEffect> effects) {
        owner.setData(DataComponentTypes.POTION_CONTENTS, PotionContents.potionContents().customColor(color).addCustomEffects(effects).build());
        return this;
    }

    /**
     * Sets the potion contents of the item with a custom color and a predefined potion type.
     *
     * @param color the custom color of the potion
     * @param type the {@link PotionType} to apply
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings potionContents(Color color, PotionType type) {
        owner.setData(DataComponentTypes.POTION_CONTENTS, PotionContents.potionContents().potion(type).customColor(color).build());
        return this;
    }

    /**
     * Sets the potion contents of the item with custom effects, using the default color.
     *
     * @param effects the list of {@link PotionEffect} to apply
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings potionContents(List<PotionEffect> effects) {
        owner.setData(DataComponentTypes.POTION_CONTENTS, PotionContents.potionContents().addCustomEffects(effects).build());
        return this;
    }

    /**
     * Sets the potion contents of the item with a predefined potion type and no custom color.
     *
     * @param type the {@link PotionType} to apply
     * @return the updated {@link ItemSettings} instance
     */
    public ItemSettings potionContents(PotionType type) {
        owner.setData(DataComponentTypes.POTION_CONTENTS, PotionContents.potionContents().potion(type).build());
        return this;
    }

    /**
     * A record for defining an attribute modifier to be applied to the item.
     *
     * @param attribute the {@link Attribute} to modify
     * @param modifier the corresponding {@link AttributeModifier}
     */
    public record Attrib(Attribute attribute, AttributeModifier modifier) {}
    /**
     * A record for defining an attribute modifier with an equipment slot group.
     *
     * @param attribute the {@link Attribute} to modify
     * @param modifier the modifier to apply
     * @param slotGroup the slot group the modifier applies to
     */
    public record AttribWithSlot(Attribute attribute, AttributeModifier modifier, EquipmentSlotGroup slotGroup) {}
}
