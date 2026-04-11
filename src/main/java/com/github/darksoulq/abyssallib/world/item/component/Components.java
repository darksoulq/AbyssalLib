package com.github.darksoulq.abyssallib.world.item.component;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.item.component.builtin.*;

public class Components {
    public static final DeferredRegistry<DataComponentType<?>> DATA_COMPONENTS = DeferredRegistry.create(Registries.DATA_COMPONENT_TYPES, AbyssalLib.PLUGIN_ID);
    public static final DeferredRegistry<DataComponentType<?>> DATA_COMPONENTS_VANILLA = DeferredRegistry.create(Registries.DATA_COMPONENT_TYPES, AbyssalLib.PLUGIN_ID);

    // Custom
    public static DataComponentType<?> ITEM_MARKER = registerCustom("marker", CustomMarker.TYPE);
    public static DataComponentType<?> BLOCK_ITEM = registerCustom("block_item", BlockItem.TYPE);

    // Vanilla
    public static DataComponentType<?> ATTRIBUTE_MODIFIERS = register("attribute_modifiers", ItemAttributeModifier.TYPE);
    public static DataComponentType<?> BANNER_PATTERNS = register("banner_patterns", BannerPatterns.TYPE);
    public static DataComponentType<?> BASE_COLOR = register("base_color", BaseColor.TYPE);
    public static DataComponentType<?> BLOCKS_ATTACKS = register("blocks_attacks", BlockAttacks.TYPE);
    public static DataComponentType<?> BLOCK_DATA = register("block_data", BlockData.TYPE);
    public static DataComponentType<?> BREAK_SOUND = register("break_sound", BreakSound.TYPE);
    public static DataComponentType<?> BUNDLE_CONTENTS = register("bundle_contents", BundleContent.TYPE);
    public static DataComponentType<?> CAN_BREAK = register("can_break", CanBreak.TYPE);
    public static DataComponentType<?> CAN_PLACE_ON = register("can_place_on", CanPlaceOn.TYPE);
    public static DataComponentType<?> CHARGED_PROJECTILES = register("charged_projectiles", ChargedProjectile.TYPE);
    public static DataComponentType<?> CONSUMABLE = register("consumable", Consume.TYPE);
    public static DataComponentType<?> CONTAINER_LOOT = register("container_loot", ContainerLoot.TYPE);
    public static DataComponentType<?> CUSTOM_DATA = register("custom_data", CustomData.TYPE);
    public static DataComponentType<?> USE_COOLDOWN = register("use_cooldown", CooldownUse.TYPE);
    public static DataComponentType<?> CUSTOM_NAME = register("custom_name", CustomName.TYPE);
    public static DataComponentType<?> DEATH_PROTECTION = register("death_protection", DeathProtect.TYPE);
    public static DataComponentType<?> TOOLTIP_DISPLAY = register("tooltip_display", DisplayTooltip.TYPE);
    public static DataComponentType<?> DAMAGE = register("damage", Durability.TYPE);
    public static DataComponentType<?> DYED_COLOR = register("dyed_color", DyedColor.TYPE);
    public static DataComponentType<?> ENCHANTABLE = register("enchantable", EnchantableComponent.TYPE);
    public static DataComponentType<?> ENCHANTMENT_GLINT_OVERRIDE = register("enchantment_glint_override", EnchantmentGlintOverride.TYPE);
    public static DataComponentType<?> ENCHANTMENTS = register("enchantments", Enchantments.TYPE);
    public static DataComponentType<?> EQUIPPABLE = register("equippable", EquippableComponent.TYPE);
    public static DataComponentType<?> FIREWORKS = register("fireworks", Firework.TYPE);
    public static DataComponentType<?> FIREWORK_EXPLOSION = register("firework_explosion", FireworkExplosion.TYPE);
    public static DataComponentType<?> FOOD = register("food", Food.TYPE);
    public static DataComponentType<?> GLIDER = register("glider", Glider.TYPE);
    public static DataComponentType<?> INSTRUMENT = register("instrument", Instrument.TYPE);
    public static DataComponentType<?> INTANGIBLE_PROJECTILE = register("intangible_projectile", IntangibleProjectile.TYPE);
    public static DataComponentType<?> ITEM_MODEL = register("item_model", ItemModel.TYPE);
    public static DataComponentType<?> ITEM_NAME = register("item_name", ItemName.TYPE);
    public static DataComponentType<?> LORE = register("lore", Lore.TYPE);
    public static DataComponentType<?> MAP_COLOR = register("map_color", MapColor.TYPE);
    public static DataComponentType<?> MAP_DECORATIONS = register("map_decorations", MapDecorates.TYPE);
    public static DataComponentType<?> MAP_ID = register("map_id", MapID.TYPE);
    public static DataComponentType<?> MAP_POST_PROCESSING = register("map_post_processing", MapPostProcess.TYPE);
    public static DataComponentType<?> MAX_DAMAGE = register("max_damage", MaxDurability.TYPE);
    public static DataComponentType<?> MAX_STACK_SIZE = register("max_stack_size", MaxStackSize.TYPE);
    public static DataComponentType<?> CUSTOM_MODEL_DATA = register("custom_model_data", ModelData.TYPE);
    public static DataComponentType<?> NOTE_BLOCK_SOUND = register("note_block_sound", NoteBlockSound.TYPE);
    public static DataComponentType<?> OMINOUS_BOTTLE_AMPLIFIER = register("ominous_bottle_amplifier", OminousAmplifier.TYPE);
    public static DataComponentType<?> JUKEBOX_PLAYABLE = register("jukebox_playable", PlayableJukebox.TYPE);
    public static DataComponentType<?> POT_DECORATIONS = register("pot_decorations", PotDecorates.TYPE);
    public static DataComponentType<?> POTION_CONTENTS = register("potion_contents", PotionContent.TYPE);
    public static DataComponentType<?> POTION_DURATION_SCALE = register("potion_duration_scale", PotionDurationScale.TYPE);
    public static DataComponentType<?> PROFILE = register("profile", ResolvingProfile.TYPE);
    public static DataComponentType<?> RECIPES = register("recipes", Recipes.TYPE);
    public static DataComponentType<?> USE_REMAINDER = register("use_remainder", RemainderUse.TYPE);
    public static DataComponentType<?> REPAIRABLE = register("repairable", RepairableComponent.TYPE);
    public static DataComponentType<?> REPAIR_COST = register("repair_cost", RepairCost.TYPE);
    public static DataComponentType<?> DAMAGE_RESISTAMT = register("damage_resistant", ResistantDamage.TYPE);
    public static DataComponentType<?> SHULKER_COLOR = register("shulker_color", ShulkerColor.TYPE);
    public static DataComponentType<?> STORED_ENCHANTMENTS = register("stored_enchantments", StoredEnchantments.TYPE);
    public static DataComponentType<?> SUSPICIOUS_STEW_EFFECTS = register("suspicious_stew_effects", SuspiciousStewEffect.TYPE);
    public static DataComponentType<?> TOOL = register("tool", ToolComponent.TYPE);
    public static DataComponentType<?> TOOLTIP_STYLE = register("tooltip_style", TooltipStyle.TYPE);
    public static DataComponentType<?> LODESTONE_TRACKER = register("lodestone_tracker", TrackerLodestone.TYPE);
    public static DataComponentType<?> TRIM = register("trim", Trim.TYPE);
    public static DataComponentType<?> UNBREAKABLE = register("unbreakable", Unbreakable.TYPE);
    public static DataComponentType<?> WEAPON = register("weapon", WeaponComponent.TYPE);
    public static DataComponentType<?> WRITABLE_BOOK_CONTENT = register("writable_book_content", WritableBookContents.TYPE);
    public static DataComponentType<?> WRITTEN_BOOK_CONTENT = register("written_book_content", WrittenBookContents.TYPE);


    private static DataComponentType<?> registerCustom(String name, DataComponentType<?> type) {
        return DATA_COMPONENTS.register(name, (id) -> type);
    }
    private static DataComponentType<?> register(String name, DataComponentType<?> type) {
        return DATA_COMPONENTS_VANILLA.register(name, (id) -> type);
    }

















}
