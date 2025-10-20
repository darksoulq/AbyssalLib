package com.github.darksoulq.abyssallib.world.item.component;

import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.server.registry.object.Holder;
import com.github.darksoulq.abyssallib.world.item.component.builtin.*;

public class Components {
    public static final DeferredRegistry<Class<? extends DataComponent<?>>> DATA_COMPONENTS = DeferredRegistry.create(
            Registries.DATA_COMPONENTS, "abyssallib");
    public static final DeferredRegistry<Class<? extends DataComponent<?>>> DATA_COMPONENTS_VANILLA = DeferredRegistry.create(
            Registries.DATA_COMPONENTS, "minecraft");

    // Custom
    public static Holder<Class<? extends DataComponent<?>>> ITEM_MARKER = registerCustom("marker", CustomMarker.class);
    public static Holder<Class<? extends DataComponent<?>>> BLOCK_ITEM = registerCustom("block_item", BlockItem.class);

    // Vanilla
    public static Holder<Class<? extends DataComponent<?>>> ATTRIBUTE_MODIFIERS =
            register("attribute_modifiers", AttributeModifier.class);
    public static Holder<Class<? extends DataComponent<?>>> BANNER_PATTERNS =
            register("banner_patterns", BannerPatterns.class);
    public static Holder<Class<? extends DataComponent<?>>> BASE_COLOR =
            register("base_color", BaseColor.class);
    public static Holder<Class<? extends DataComponent<?>>> BLOCKS_ATTACKS =
            register("blocks_attacks", BlockAttacks.class);
    public static Holder<Class<? extends DataComponent<?>>> BLOCK_DATA =
            register("block_data", BlockData.class);
    public static Holder<Class<? extends DataComponent<?>>> BREAK_SOUND =
            register("break_sound", BreakSound.class);
    public static Holder<Class<? extends DataComponent<?>>> BUNDLE_CONTENTS =
            register("bundle_contents", BundleContent.class);
    public static Holder<Class<? extends DataComponent<?>>> CAN_BREAK =
            register("can_break", CanBreak.class);
    public static Holder<Class<? extends DataComponent<?>>> CAN_PLACE_ON =
            register("can_place_on", CanPlaceOn.class);
    public static Holder<Class<? extends DataComponent<?>>> CHARGED_PROJECTILES =
            register("charged_projectiles", ChargedProjectile.class);
    public static Holder<Class<? extends DataComponent<?>>> CONSUMABLE =
            register("consumable", Consume.class);
    public static Holder<Class<? extends DataComponent<?>>> CONTAINER_LOOT =
            register("container_loot", ContainerLoot.class);
    public static Holder<Class<? extends DataComponent<?>>> USE_COOLDOWN =
            register("use_cooldown", CooldownUse.class);
    public static Holder<Class<? extends DataComponent<?>>> CUSTOM_NAME =
            register("custom_name", CustomName.class);
    public static Holder<Class<? extends DataComponent<?>>> DEATH_PROTECTION =
            register("death_protection", DeathProtect.class);
    public static Holder<Class<? extends DataComponent<?>>> TOOLTIP_DISPLAY =
            register("tooltip_display", DisplayTooltip.class);
    public static Holder<Class<? extends DataComponent<?>>> DAMAGE =
            register("damage", Durability.class);
    public static Holder<Class<? extends DataComponent<?>>> DYED_COLOR =
            register("dyed_color", DyedColor.class);
    public static Holder<Class<? extends DataComponent<?>>> ENCHANTABLE =
            register("enchantable", EnchantableComponent.class);
    public static Holder<Class<? extends DataComponent<?>>> ENCHANTMENT_GLINT_OVERRIDE =
            register("enchantment_glint_override", EnchantmentGlintOverride.class);
    public static Holder<Class<? extends DataComponent<?>>> ENCHANTMENTS =
            register("enchantments", Enchantments.class);
    public static Holder<Class<? extends DataComponent<?>>> EQUIPPABLE =
            register("equippable", EquippableComponent.class);
    public static Holder<Class<? extends DataComponent<?>>> FIREWORKS =
            register("fireworks", Firework.class);
    public static Holder<Class<? extends DataComponent<?>>> FIREWORK_EXPLOSION =
            register("firework_explosion", FireworkExplosion.class);
    public static Holder<Class<? extends DataComponent<?>>> FOOD =
            register("food", Food.class);
    public static Holder<Class<? extends DataComponent<?>>> GLIDER =
            register("glider", Glider.class);
    public static Holder<Class<? extends DataComponent<?>>> INSTRUMENT =
            register("instrument", Instrument.class);
    public static Holder<Class<? extends DataComponent<?>>> INTANGIBLE_PROJECTILE =
            register("intangible_projectile", IntangibleProjectile.class);
    public static Holder<Class<? extends DataComponent<?>>> ITEM_MODEL =
            register("item_model", ItemModel.class);
    public static Holder<Class<? extends DataComponent<?>>> ITEM_NAME =
            register("item_name", ItemName.class);
    public static Holder<Class<? extends DataComponent<?>>> LORE =
            register("lore", Lore.class);
    public static Holder<Class<? extends DataComponent<?>>> MAP_COLOR =
            register("map_color", MapColor.class);
    public static Holder<Class<? extends DataComponent<?>>> MAP_DECORATIONS =
            register("map_decorations", MapDecorates.class);
    public static Holder<Class<? extends DataComponent<?>>> MAP_ID =
            register("map_id", MapID.class);
    public static Holder<Class<? extends DataComponent<?>>> MAP_POST_PROCESSING =
            register("map_post_processing", MapPostProcess.class);
    public static Holder<Class<? extends DataComponent<?>>> MAX_DAMAGE =
            register("max_damage", MaxDurability.class);
    public static Holder<Class<? extends DataComponent<?>>> MAX_STACK_SIZE =
            register("max_stack_size", MaxStackSize.class);
    public static Holder<Class<? extends DataComponent<?>>> CUSTOM_MODEL_DATA =
            register("custom_model_data", ModelData.class);
    public static Holder<Class<? extends DataComponent<?>>> NOTE_BLOCK_SOUND =
            register("note_block_sound", NoteBlockSound.class);
    public static Holder<Class<? extends DataComponent<?>>> OMINOUS_BOTTLE_AMPLIFIER =
            register("ominous_bottle_amplifier", OminousAmplifier.class);
    public static Holder<Class<? extends DataComponent<?>>> JUKEBOX_PLAYABLE =
            register("jukebox_playable", PlayableJukebox.class);
    public static Holder<Class<? extends DataComponent<?>>> POT_DECORATIONS =
            register("pot_decorations", PotDecorates.class);
    public static Holder<Class<? extends DataComponent<?>>> POTION_CONTENTS =
            register("potion_contents", PotionContent.class);
    public static Holder<Class<? extends DataComponent<?>>> POTION_DURATION_SCALE =
            register("potion_duration_scale", PotionDurationScale.class);
    public static Holder<Class<? extends DataComponent<?>>> PROFILE =
            register("profile", ResolvingProfile.class);
    public static Holder<Class<? extends DataComponent<?>>> RECIPES =
            register("recipes", Recipes.class);
    public static Holder<Class<? extends DataComponent<?>>> USE_REMAINDER =
            register("use_remainder", RemainderUse.class);
    public static Holder<Class<? extends DataComponent<?>>> REPAIRABLE =
            register("repairable", RepairableComponent.class);
    public static Holder<Class<? extends DataComponent<?>>> REPAIR_COST =
            register("repair_cost", RepairCost.class);
    public static Holder<Class<? extends DataComponent<?>>> DAMAGE_RESISTAMT =
            register("damage_resistant", ResistantDamage.class);
    public static Holder<Class<? extends DataComponent<?>>> SHULKER_COLOR =
            register("shulker_color", ShulkerColor.class);
    public static Holder<Class<? extends DataComponent<?>>> STORED_ENCHANTMENTS =
            register("stored_enchantments", StoredEnchantments.class);
    public static Holder<Class<? extends DataComponent<?>>> SUSPICIOUS_STEW_EFFECTS =
            register("suspicious_stew_effects", SuspiciousStewEffect.class);
    public static Holder<Class<? extends DataComponent<?>>> TOOL =
            register("tool", ToolComponent.class);
    public static Holder<Class<? extends DataComponent<?>>> TOOLTIP_STYLE =
            register("tooltip_style", TooltipStyle.class);
    public static Holder<Class<? extends DataComponent<?>>> LODESTONE_TRACKER =
            register("lodestone_tracker", TrackerLodestone.class);
    public static Holder<Class<? extends DataComponent<?>>> TRIM =  
            register("trim", Trim.class);
    public static Holder<Class<? extends DataComponent<?>>> UNBREAKABLE =
            register("unbreakable", Unbreakable.class);
    public static Holder<Class<? extends DataComponent<?>>> WEAPON =
            register("weapon", WeaponComponent.class);
    public static Holder<Class<? extends DataComponent<?>>> WRITABLE_BOOK_CONTENT =
            register("writable_book_content", WritableBookContents.class);
    public static Holder<Class<? extends DataComponent<?>>> WRITTEN_BOOK_CONTENT =
            register("written_book_content", WrittenBookContents.class);


    private static Holder<Class<? extends DataComponent<?>>> registerCustom(String name,
                                                            Class<? extends DataComponent<?>> clazz) {
        return DATA_COMPONENTS.register(name, (id) -> clazz);
    }
    private static Holder<Class<? extends DataComponent<?>>> register(String name,
                                                      Class<? extends DataComponent<?>> clazz) {
        return DATA_COMPONENTS_VANILLA.register(name, (id) -> clazz);
    }

















}
