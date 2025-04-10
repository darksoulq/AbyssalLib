package me.darksoul.abyssalLib.item;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.keys.InstrumentKeys;
import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.TagKey;
import me.darksoul.abyssalLib.util.ResourceLocation;
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

public class ItemSettings {
    private Item owner;

    public ItemSettings(Item owner) {
        this.owner = owner;
    }

    // Setters
    public ItemSettings attribute(List<Attrib> attribs) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.itemAttributes();
        for (Attrib attrib : attribs) {
            builder.addModifier(attrib.attribute, attrib.modifier);
        }
        owner.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder.build());
        return this;
    }

    public ItemSettings attributeBySlot(List<AttribWithSlot> attribs) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.itemAttributes();
        for (AttribWithSlot attrib : attribs) {
            builder.addModifier(attrib.attribute, attrib.modifier, attrib.slotGroup);
        }
        owner.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder.build());
        return this;
    }

    public ItemSettings food(FoodProperties foodProperties) {
        owner.setData(DataComponentTypes.FOOD, foodProperties);
        return this;
    }
    public ItemSettings food(FoodProperties foodProperties, Consumable consumable) {
        owner.setData(DataComponentTypes.CONSUMABLE, consumable);
        owner.setData(DataComponentTypes.FOOD, foodProperties);
        return this;
    }

    public ItemSettings usingConvertsTo(ItemStack item) {
        owner.setData(DataComponentTypes.USE_REMAINDER, UseRemainder.useRemainder(item));
        return this;
    }

    public ItemSettings useCooldown(float useCooldown) {
        owner.setData(DataComponentTypes.USE_COOLDOWN, UseCooldown.useCooldown(useCooldown).build());
        return this;
    }

    public ItemSettings stackSize(int size) {
        owner.setData(DataComponentTypes.MAX_STACK_SIZE, size);
        return this;
    }

    public ItemSettings durability(int durability) {
        owner.setData(DataComponentTypes.MAX_DAMAGE, durability);
        owner.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        owner.setData(DataComponentTypes.DAMAGE, 0);
        return this;
    }

    public ItemSettings rarity(ItemRarity rarity) {
        owner.setData(DataComponentTypes.RARITY, rarity);
        return this;
    }

    public ItemSettings fireResistant() {
        owner.setData(DataComponentTypes.DAMAGE_RESISTANT, DamageResistant.damageResistant(DamageTypeTagKeys.IS_FIRE));
        return this;
    }

    public ItemSettings jukeboxPlayable(JukeboxSong song) {
        owner.setData(DataComponentTypes.JUKEBOX_PLAYABLE, JukeboxPlayable.jukeboxPlayable(song).build());
        return this;
    }

    public ItemSettings enchantable(int enchantmentLevel) {
        owner.setData(DataComponentTypes.ENCHANTABLE, Enchantable.enchantable(enchantmentLevel));
        return this;
    }

    public ItemSettings repairable(List<ItemType> itemTypes) {
        owner.setData(DataComponentTypes.REPAIRABLE, Repairable.repairable(RegistrySet.keySetFromValues(RegistryKey.ITEM, itemTypes)));
        return this;
    }

    public ItemSettings equippable(EquipmentSlot slot) {
        owner.setData(DataComponentTypes.EQUIPPABLE, Equippable.equippable(slot).assetId(owner.getId().toNamespace()).build());
        return this;
    }

    public ItemSettings equippableUnswappable(EquipmentSlot slot) {
        owner.setData(DataComponentTypes.EQUIPPABLE, Equippable.equippable(slot).swappable(false).assetId(owner.getId().toNamespace()).build());
        return this;
    }

    public ItemSettings tool(List<Tool.Rule> toolRules, int defaultMiningSpeed, int damagePerBlock) {
        owner.setData(DataComponentTypes.TOOL, Tool.tool().addRules(toolRules).defaultMiningSpeed(defaultMiningSpeed).damagePerBlock(damagePerBlock).build());
        return this;
    }

    public ItemSettings lore(ItemLore lore) {
        owner.setData(DataComponentTypes.LORE, lore);
        return this;
    }

    public ItemSettings unbreakable() {
        owner.setData(DataComponentTypes.UNBREAKABLE);
        return this;
    }

    public ItemSettings weapon(int damage, int disableBlockingForSeconds) {
        owner.setData(DataComponentTypes.WEAPON, Weapon.weapon()
                .itemDamagePerAttack(damage)
                .disableBlockingForSeconds(disableBlockingForSeconds)
                .build());
        return this;
    }

    public ItemSettings noteBlockSound(ResourceLocation soundId) {
        owner.setData(DataComponentTypes.NOTE_BLOCK_SOUND, InstrumentKeys.create(soundId.toNamespace()));
        return this;
    }

    public ItemSettings tooltip(boolean hideTooltip, Set<DataComponentType> hiddenComponents) {
        owner.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().hideTooltip(hideTooltip).hiddenComponents(hiddenComponents).build());
        return this;
    }

    public ItemSettings showEnchantGlint(boolean v) {
        owner.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, v);
        return this;
    }

    public ItemSettings intangibleProjectile() {
        owner.setData(DataComponentTypes.INTANGIBLE_PROJECTILE);
        return this;
    }

    public ItemSettings damageResistant(TagKey<DamageType> types) {
        owner.setData(DataComponentTypes.DAMAGE_RESISTANT, DamageResistant.damageResistant(types));
        return this;
    }

    public ItemSettings glider() {
        owner.setData(DataComponentTypes.GLIDER);
        return this;
    }

    public ItemSettings storedEnchants(Map<Enchantment, Integer> enchants) {
        owner.setData(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.itemEnchantments().addAll(enchants).build());
        return this;
    }

    public ItemSettings mapColor(Color color) {
        owner.setData(DataComponentTypes.MAP_COLOR, MapItemColor.mapItemColor().color(color).build());
        return this;
    }

    public ItemSettings dyedColor(Color color) {
        owner.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor().color(color).build());
        return this;
    }

    public ItemSettings chargedProjectiles() {
        owner.setData(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectiles.chargedProjectiles().build());
        return this;
    }

    public ItemSettings chargedProjectiles(List<ItemStack> items) {
        owner.setData(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectiles.chargedProjectiles().addAll(items).build());
        return this;
    }

    public ItemSettings bundleContents() {
        owner.setData(DataComponentTypes.BUNDLE_CONTENTS, BundleContents.bundleContents().build());
        return this;
    }

    public ItemSettings bundleContents(List<ItemStack> items) {
        owner.setData(DataComponentTypes.BUNDLE_CONTENTS, BundleContents.bundleContents().addAll(items).build());
        return this;
    }

    public ItemSettings potionContents(Color color, List<PotionEffect> effects) {
        owner.setData(DataComponentTypes.POTION_CONTENTS, PotionContents.potionContents().customColor(color).addCustomEffects(effects).build());
        return this;
    }

    public ItemSettings potionContents(Color color, PotionType type) {
        owner.setData(DataComponentTypes.POTION_CONTENTS, PotionContents.potionContents().potion(type).customColor(color).build());
        return this;
    }

    public ItemSettings potionContents(List<PotionEffect> effects) {
        owner.setData(DataComponentTypes.POTION_CONTENTS, PotionContents.potionContents().addCustomEffects(effects).build());
        return this;
    }

    public ItemSettings potionContents(PotionType type) {
        owner.setData(DataComponentTypes.POTION_CONTENTS, PotionContents.potionContents().potion(type).build());
        return this;
    }

    public record Attrib(Attribute attribute, AttributeModifier modifier) {}
    public record AttribWithSlot(Attribute attribute, AttributeModifier modifier, EquipmentSlotGroup slotGroup) {}
}
