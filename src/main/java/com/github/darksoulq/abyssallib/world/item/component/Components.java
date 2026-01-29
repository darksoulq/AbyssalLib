package com.github.darksoulq.abyssallib.world.item.component;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.registry.object.Holder;
import com.github.darksoulq.abyssallib.world.item.component.builtin.*;

public class Components {
    public static final DeferredRegistry<DataComponentType<?>> DATA_COMPONENTS = DeferredRegistry.create(Registries.DATA_COMPONENT_TYPES, AbyssalLib.PLUGIN_ID);
    public static final DeferredRegistry<DataComponentType<?>> DATA_COMPONENTS_VANILLA = DeferredRegistry.create(Registries.DATA_COMPONENT_TYPES, AbyssalLib.PLUGIN_ID);

    // Custom
    public static Holder<DataComponentType<?>> ITEM_MARKER = registerCustom("marker", CustomMarker.TYPE);
    public static Holder<DataComponentType<?>> BLOCK_ITEM = registerCustom("block_item", BlockItem.TYPE);

    // Vanilla
    public static Holder<DataComponentType<?>> ATTRIBUTE_MODIFIERS = register("attribute_modifiers", ItemAttributeModifier.TYPE);
    public static Holder<DataComponentType<?>> BANNER_PATTERNS = register("banner_patterns", BannerPatterns.TYPE);
    public static Holder<DataComponentType<?>> BASE_COLOR = register("base_color", BaseColor.TYPE);
    public static Holder<DataComponentType<?>> BLOCKS_ATTACKS = register("blocks_attacks", BlockAttacks.TYPE);
    public static Holder<DataComponentType<?>> BLOCK_DATA = register("block_data", BlockData.TYPE);
    public static Holder<DataComponentType<?>> BREAK_SOUND = register("break_sound", BreakSound.TYPE);
    public static Holder<DataComponentType<?>> BUNDLE_CONTENTS = register("bundle_contents", BundleContent.TYPE);
    public static Holder<DataComponentType<?>> CAN_BREAK = register("can_break", CanBreak.TYPE);
    public static Holder<DataComponentType<?>> CAN_PLACE_ON = register("can_place_on", CanPlaceOn.TYPE);
    public static Holder<DataComponentType<?>> CHARGED_PROJECTILES = register("charged_projectiles", ChargedProjectile.TYPE);
    public static Holder<DataComponentType<?>> CONSUMABLE = register("consumable", Consume.TYPE);
    public static Holder<DataComponentType<?>> CONTAINER_LOOT = register("container_loot", ContainerLoot.TYPE);
    public static Holder<DataComponentType<?>> USE_COOLDOWN = register("use_cooldown", CooldownUse.TYPE);
    public static Holder<DataComponentType<?>> CUSTOM_NAME = register("custom_name", CustomName.TYPE);
    public static Holder<DataComponentType<?>> DEATH_PROTECTION = register("death_protection", DeathProtect.TYPE);
    public static Holder<DataComponentType<?>> TOOLTIP_DISPLAY = register("tooltip_display", DisplayTooltip.TYPE);
    public static Holder<DataComponentType<?>> DAMAGE = register("damage", Durability.TYPE);
    public static Holder<DataComponentType<?>> DYED_COLOR = register("dyed_color", DyedColor.TYPE);
    public static Holder<DataComponentType<?>> ENCHANTABLE = register("enchantable", EnchantableComponent.TYPE);
    public static Holder<DataComponentType<?>> ENCHANTMENT_GLINT_OVERRIDE = register("enchantment_glint_override", EnchantmentGlintOverride.TYPE);
    public static Holder<DataComponentType<?>> ENCHANTMENTS = register("enchantments", Enchantments.TYPE);
    public static Holder<DataComponentType<?>> EQUIPPABLE = register("equippable", EquippableComponent.TYPE);
    public static Holder<DataComponentType<?>> FIREWORKS = register("fireworks", Firework.TYPE);
    public static Holder<DataComponentType<?>> FIREWORK_EXPLOSION = register("firework_explosion", FireworkExplosion.TYPE);
    public static Holder<DataComponentType<?>> FOOD = register("food", Food.TYPE);
    public static Holder<DataComponentType<?>> GLIDER = register("glider", Glider.TYPE);
    public static Holder<DataComponentType<?>> INSTRUMENT = register("instrument", Instrument.TYPE);
    public static Holder<DataComponentType<?>> INTANGIBLE_PROJECTILE = register("intangible_projectile", IntangibleProjectile.TYPE);
    public static Holder<DataComponentType<?>> ITEM_MODEL = register("item_model", ItemModel.TYPE);
    public static Holder<DataComponentType<?>> ITEM_NAME = register("item_name", ItemName.TYPE);
    public static Holder<DataComponentType<?>> LORE = register("lore", Lore.TYPE);
    public static Holder<DataComponentType<?>> MAP_COLOR = register("map_color", MapColor.TYPE);
    public static Holder<DataComponentType<?>> MAP_DECORATIONS = register("map_decorations", MapDecorates.TYPE);
    public static Holder<DataComponentType<?>> MAP_ID = register("map_id", MapID.TYPE);
    public static Holder<DataComponentType<?>> MAP_POST_PROCESSING = register("map_post_processing", MapPostProcess.TYPE);
    public static Holder<DataComponentType<?>> MAX_DAMAGE = register("max_damage", MaxDurability.TYPE);
    public static Holder<DataComponentType<?>> MAX_STACK_SIZE = register("max_stack_size", MaxStackSize.TYPE);
    public static Holder<DataComponentType<?>> CUSTOM_MODEL_DATA = register("custom_model_data", ModelData.TYPE);
    public static Holder<DataComponentType<?>> NOTE_BLOCK_SOUND = register("note_block_sound", NoteBlockSound.TYPE);
    public static Holder<DataComponentType<?>> OMINOUS_BOTTLE_AMPLIFIER = register("ominous_bottle_amplifier", OminousAmplifier.TYPE);
    public static Holder<DataComponentType<?>> JUKEBOX_PLAYABLE = register("jukebox_playable", PlayableJukebox.TYPE);
    public static Holder<DataComponentType<?>> POT_DECORATIONS = register("pot_decorations", PotDecorates.TYPE);
    public static Holder<DataComponentType<?>> POTION_CONTENTS = register("potion_contents", PotionContent.TYPE);
    public static Holder<DataComponentType<?>> POTION_DURATION_SCALE = register("potion_duration_scale", PotionDurationScale.TYPE);
    public static Holder<DataComponentType<?>> PROFILE = register("profile", ResolvingProfile.TYPE);
    public static Holder<DataComponentType<?>> RECIPES = register("recipes", Recipes.TYPE);
    public static Holder<DataComponentType<?>> USE_REMAINDER = register("use_remainder", RemainderUse.TYPE);
    public static Holder<DataComponentType<?>> REPAIRABLE = register("repairable", RepairableComponent.TYPE);
    public static Holder<DataComponentType<?>> REPAIR_COST = register("repair_cost", RepairCost.TYPE);
    public static Holder<DataComponentType<?>> DAMAGE_RESISTAMT = register("damage_resistant", ResistantDamage.TYPE);
    public static Holder<DataComponentType<?>> SHULKER_COLOR = register("shulker_color", ShulkerColor.TYPE);
    public static Holder<DataComponentType<?>> STORED_ENCHANTMENTS = register("stored_enchantments", StoredEnchantments.TYPE);
    public static Holder<DataComponentType<?>> SUSPICIOUS_STEW_EFFECTS = register("suspicious_stew_effects", SuspiciousStewEffect.TYPE);
    public static Holder<DataComponentType<?>> TOOL = register("tool", ToolComponent.TYPE);
    public static Holder<DataComponentType<?>> TOOLTIP_STYLE = register("tooltip_style", TooltipStyle.TYPE);
    public static Holder<DataComponentType<?>> LODESTONE_TRACKER = register("lodestone_tracker", TrackerLodestone.TYPE);
    public static Holder<DataComponentType<?>> TRIM = register("trim", Trim.TYPE);
    public static Holder<DataComponentType<?>> UNBREAKABLE = register("unbreakable", Unbreakable.TYPE);
    public static Holder<DataComponentType<?>> WEAPON = register("weapon", WeaponComponent.TYPE);
    public static Holder<DataComponentType<?>> WRITABLE_BOOK_CONTENT = register("writable_book_content", WritableBookContents.TYPE);
    public static Holder<DataComponentType<?>> WRITTEN_BOOK_CONTENT = register("written_book_content", WrittenBookContents.TYPE);


    private static Holder<DataComponentType<?>> registerCustom(String name, DataComponentType<?> type) {
        return DATA_COMPONENTS.register(name, (id) -> type);
    }
    private static Holder<DataComponentType<?>> register(String name, DataComponentType<?> type) {
        return DATA_COMPONENTS_VANILLA.register(name, (id) -> type);
    }

















}
