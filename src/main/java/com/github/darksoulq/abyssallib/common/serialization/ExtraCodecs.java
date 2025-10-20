package com.github.darksoulq.abyssallib.common.serialization;

import io.papermc.paper.block.BlockPredicate;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.item.*;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.text.Filtered;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.BlockType;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.map.MapCursor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class ExtraCodecs {
    public static final Codec<BlockType> BLOCK_TYPE = Codecs.KEY.xmap(Registry.BLOCK::getOrThrow, BlockType::getKey);
    public static final Codec<RegistryKeySet<@NotNull BlockType>> BLOCK_TYPE_KEYS = BLOCK_TYPE.list().xmap(
            l -> RegistrySet.keySetFromValues(RegistryKey.BLOCK, l),
            k -> k.resolve(Registry.BLOCK).stream().toList()
    );
    public static final Codec<BlockPredicate> BLOCK_PREDICATE = BLOCK_TYPE_KEYS.xmap(
            k -> BlockPredicate.predicate().blocks(k).build(),
            BlockPredicate::blocks
    );
    public static final Codec<ItemAdventurePredicate> ITEM_ADV_PREDICATE = BLOCK_PREDICATE.list().xmap(
            ItemAdventurePredicate::itemAdventurePredicate,
            ItemAdventurePredicate::predicates
    );
    public static final Codec<ItemType> ITEM_TYPE = Codecs.KEY.xmap(Registry.ITEM::getOrThrow, ItemType::getKey);
    public static final Codec<EntityType> ENTITY_TYPE = Codecs.KEY.xmap(Registry.ENTITY_TYPE::getOrThrow, EntityType::getKey);
    // Banner
    public static final Codec<PatternType> BANNER_PATTERN_TYPE = Codecs.KEY.xmap(
            RegistryAccess.registryAccess().getRegistry(RegistryKey.BANNER_PATTERN)::get,
            RegistryAccess.registryAccess().getRegistry(RegistryKey.BANNER_PATTERN)::getKey
    );
    public static final Codec<Pattern> BANNER_PATTERN = RecordCodecBuilder.create(
            Codec.enumCodec(DyeColor.class).fieldOf("color", Pattern::getColor),
            BANNER_PATTERN_TYPE.fieldOf("pattern", Pattern::getPattern),
            Pattern::new
    );
    public static final Codec<BannerPatternLayers> BANNER_PATTERN_LAYERS = BANNER_PATTERN.list().xmap(
            BannerPatternLayers::bannerPatternLayers,
            BannerPatternLayers::patterns
    );

    public static final Codec<Color> COLOR = RecordCodecBuilder.create(
            Codecs.INT.fieldOf("alpha", Color::getAlpha),
            Codecs.INT.fieldOf("red", Color::getRed),
            Codecs.INT.fieldOf("green", Color::getGreen),
            Codecs.INT.fieldOf("blue", Color::getBlue),
            Color::fromARGB
    );

    public static final Codec<UseCooldown> USE_COOLDOWN = RecordCodecBuilder.create(
            Codecs.FLOAT.fieldOf("cooldown", UseCooldown::seconds),
            Codecs.KEY.optional().fieldOf("group", c -> Optional.ofNullable(c.cooldownGroup())),
            (cd, group) ->
                    group.map(key -> UseCooldown.useCooldown(cd).cooldownGroup(key).build()).orElseGet(() -> UseCooldown.useCooldown(cd).build())
    );

    // Components
    public static final Codec<MapCursor.Type> MAP_CURSOR_TYPE = Codecs.KEY.xmap(
            Registry.MAP_DECORATION_TYPE::get,
            MapCursor.Type::getKey
    );

    public static final Codec<MapDecorations.DecorationEntry> MAP_DECO_ENTRY = RecordCodecBuilder.create(
            MAP_CURSOR_TYPE.fieldOf("type", MapDecorations.DecorationEntry::type),
            Codecs.DOUBLE.fieldOf("x", MapDecorations.DecorationEntry::x),
            Codecs.DOUBLE.fieldOf("z", MapDecorations.DecorationEntry::z),
            Codecs.FLOAT.fieldOf("rotation", MapDecorations.DecorationEntry::rotation),
            MapDecorations::decorationEntry
    );

    public static final Codec<CustomModelData> CUSTOM_MODEL_DATA = RecordCodecBuilder.create(
            Codecs.FLOAT.list().fieldOf("floats", CustomModelData::floats),
            Codecs.BOOLEAN.list().fieldOf("flags", CustomModelData::flags),
            Codecs.STRING.list().fieldOf("strings", CustomModelData::strings),
            ExtraCodecs.COLOR.list().fieldOf("colors", CustomModelData::colors),
            (floats, flags, strings, colors) ->
                    CustomModelData.customModelData().addFloats(floats).addFlags(flags).addStrings(strings).addColors(colors).build()
    );

    public static final Codec<Repairable> REPAIRABLE_COMPONENT = ITEM_TYPE.list().xmap(
            l -> Repairable.repairable(RegistrySet.keySetFromValues(RegistryKey.ITEM, l)),
            r -> r.types().resolve(Registry.ITEM).stream().toList()
    );

    public static final Codec<LodestoneTracker> LODESTONE_TRACKER = RecordCodecBuilder.create(
            Codecs.LOCATION.fieldOf("location", LodestoneTracker::location),
            Codecs.BOOLEAN.fieldOf("tracked", LodestoneTracker::tracked),
            LodestoneTracker::lodestoneTracker
    );

    public static final Codec<Weapon> WEAPON = RecordCodecBuilder.create(
            Codecs.INT.fieldOf("item_damage_per_attack", Weapon::itemDamagePerAttack),
            Codecs.FLOAT.fieldOf("disable_blocking_for_seconds", Weapon::disableBlockingForSeconds),
            (itemDamagePerAttack, disableBlockingForSeconds) -> Weapon.weapon()
                    .itemDamagePerAttack(itemDamagePerAttack)
                    .disableBlockingForSeconds(disableBlockingForSeconds).build()
    );

    // Firework
    public static final Codec<FireworkEffect> FIREWORK_EFFECT = RecordCodecBuilder.create(
            Codecs.BOOLEAN.fieldOf("flicker", FireworkEffect::hasFlicker),
            Codecs.BOOLEAN.fieldOf("trail", FireworkEffect::hasTrail),
            COLOR.list().fieldOf("colors", FireworkEffect::getColors),
            COLOR.list().fieldOf("fade_colors", FireworkEffect::getFadeColors),
            Codec.enumCodec(FireworkEffect.Type.class).fieldOf("type", FireworkEffect::getType),
            (flicker, trail, colors, fadeColors, type) -> FireworkEffect.builder()
                    .flicker(flicker).trail(trail).with(type).withColor(colors).withFade(colors).build()
    );

    public static final Codec<Fireworks> FIREWORKS = RecordCodecBuilder.create(
            FIREWORK_EFFECT.list().fieldOf("effects", Fireworks::effects),
            Codecs.INT.fieldOf("flight_duration", Fireworks::flightDuration),
            Fireworks::fireworks
    );

    public static final Codec<SeededContainerLoot> SEEDED_CONTAINER_LOOT = RecordCodecBuilder.create(
            Codecs.KEY.fieldOf("loot_table", SeededContainerLoot::lootTable),
            Codecs.LONG.fieldOf("seed", SeededContainerLoot::seed),
            SeededContainerLoot::seededContainerLoot
    );

    public static final Codec<PotDecorations> POT_DECORATIONS = RecordCodecBuilder.create(
            ITEM_TYPE.fieldOf("front", PotDecorations::front),
            ITEM_TYPE.fieldOf("back", PotDecorations::back),
            ITEM_TYPE.fieldOf("left", PotDecorations::left),
            ITEM_TYPE.fieldOf("right", PotDecorations::right),
            PotDecorations::potDecorations
    );

    public static final Codec<FoodProperties> FOOD_PROPERTIES = RecordCodecBuilder.create(
            Codecs.INT.fieldOf("nutrition", FoodProperties::nutrition),
            Codecs.FLOAT.fieldOf("saturation", FoodProperties::saturation),
            Codecs.BOOLEAN.fieldOf("can_always_eat", FoodProperties::canAlwaysEat),
            (nutrition, saturation, canAlwaysEat) -> FoodProperties.food().nutrition(nutrition).saturation(saturation)
                    .canAlwaysEat(canAlwaysEat).build()
    );

    @SuppressWarnings("unchecked")
    public static final Codec<Equippable> EQUIPPABLE = RecordCodecBuilder.create(
            Codec.enumCodec(EquipmentSlot.class).fieldOf("slot", Equippable::slot),
            Codecs.KEY.fieldOf("equip_sound", Equippable::equipSound),
            Codecs.KEY.nullable().fieldOf("asset_id", Equippable::assetId),
            Codecs.KEY.nullable().fieldOf("camera_overlay", Equippable::cameraOverlay),
            ENTITY_TYPE.list().nullable().fieldOf("allowed_entities", e -> {
                if (e.allowedEntities() == null) return null;
                return e.allowedEntities().resolve(Registry.ENTITY_TYPE).stream().toList();
            }),
            Codecs.BOOLEAN.fieldOf("dispensable", Equippable::dispensable),
            Codecs.BOOLEAN.fieldOf("swappable", Equippable::swappable),
            Codecs.BOOLEAN.fieldOf("damage_on_hurt", Equippable::damageOnHurt),
            Codecs.BOOLEAN.fieldOf("can_be_sheared", Equippable::canBeSheared),
            Codecs.KEY.nullable().fieldOf("shear_sound", Equippable::shearSound),
            (slot, equipSound, assetId, cameraOverlay, allowedEntities, dispensable,
             swappable, damageOnHurt, canBeSheared, shearSound) -> {
                Equippable.Builder builder = Equippable.equippable(slot).equipSound(equipSound).assetId((Key) assetId).cameraOverlay((Key) cameraOverlay);
                if (allowedEntities != null) builder.allowedEntities(RegistrySet.keySetFromValues(RegistryKey.ENTITY_TYPE, (List<? extends EntityType>) allowedEntities));
                return builder.dispensable(dispensable).swappable(swappable).damageOnHurt(damageOnHurt).canBeSheared(canBeSheared).shearSound((Key) shearSound)
                        .build();
            }
    );

    // Enchantments
    public static final Codec<Enchantment> ENCHANTMENT = Codecs.KEY.xmap(
            RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)::getOrThrow,
            Enchantment::getKey
    );
    public static final Codec<ItemEnchantments> ITEM_ENCHANTMENTS = Codec.map(ENCHANTMENT, Codecs.INT).xmap(
            ItemEnchantments::itemEnchantments,
            ItemEnchantments::enchantments
    );

    public static final Codec<TooltipDisplay> TOOLTIP_DISPLAY = RecordCodecBuilder.create(
            Codecs.BOOLEAN.fieldOf("hide_tooltips", TooltipDisplay::hideTooltip),
            Codecs.DATA_COMPONENT_TYPE.list().fieldOf("hidden_components", t -> t.hiddenComponents().stream().toList()),
            (hide, hiddenComponents) -> TooltipDisplay.tooltipDisplay().hideTooltip(hide)
                    .hiddenComponents(new HashSet<>(hiddenComponents)).build()
    );

    //Trims
    public static final Codec<TrimMaterial> TRIM_MATERIAL = Codecs.KEY.xmap(
            RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL)::getOrThrow,
            RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL)::getKeyOrThrow
    );
    public static final Codec<TrimPattern> TRIM_PATTERN = Codecs.KEY.xmap(
            RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_PATTERN)::getOrThrow,
            RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_PATTERN)::getKeyOrThrow
    );
    public static final Codec<ArmorTrim> ARMOR_TRIM = RecordCodecBuilder.create(
            TRIM_MATERIAL.fieldOf("material", ArmorTrim::getMaterial),
            TRIM_PATTERN.fieldOf("pattern", ArmorTrim::getPattern),
            ArmorTrim::new
    );

    // Book
    public static final Codec<Filtered<String>> FILTERED_STRING = Codecs.STRING.xmap(
            s -> Filtered.of(s, null),
            Filtered::raw
    );
    public static final Codec<Filtered<ComponentLike>> FILTERED_COMPONENT = Codecs.TEXT_COMPONENT.xmap(
            c -> Filtered.of(c, null),
            f -> f.raw().asComponent()
    );
    public static final Codec<WrittenBookContent> WRITTEN_BOOK_CONTENT = RecordCodecBuilder.create(
            FILTERED_STRING.fieldOf("title", WrittenBookContent::title),
            Codecs.STRING.fieldOf("author", WrittenBookContent::author),
            Codecs.INT.fieldOf("generation", WrittenBookContent::generation),
            FILTERED_COMPONENT.list().fieldOf("pages", w -> {
                List<Filtered<ComponentLike>> comps = new ArrayList<>();
                w.pages().forEach(fc -> comps.add(Filtered.of(fc.raw(), null)));
                return comps;
            }),
            Codecs.BOOLEAN.fieldOf("resolved", WrittenBookContent::resolved),
            (title, author, gen, pages, resolved) -> {
                WrittenBookContent.Builder builder = WrittenBookContent.writtenBookContent(title, author).resolved(resolved);
                pages.forEach(p -> builder.addPage(p.raw()));
                return builder.build();
            }
    );
}
