package com.github.darksoulq.abyssallib.common.serialization;

import com.destroystokyo.paper.profile.ProfileProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.darksoulq.abyssallib.common.serialization.ops.JsonOps;
import com.github.darksoulq.abyssallib.common.util.Either;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.entity.CustomEntity;
import io.papermc.paper.block.BlockPredicate;
import io.papermc.paper.datacomponent.item.*;
import io.papermc.paper.datacomponent.item.blocksattacks.DamageReduction;
import io.papermc.paper.datacomponent.item.blocksattacks.ItemDamageFunction;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import io.papermc.paper.potion.SuspiciousEffectEntry;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.TagKey;
import io.papermc.paper.text.Filtered;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.util.TriState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockType;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.block.data.BlockData;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.map.MapCursor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class ExtraCodecs {

    public static final Codec<DamageType> DAMAGE_TYPE = Codecs.KEY.flatXmap(
        key -> {
            DamageType t = RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE).get(key);
            return t != null ? DataResult.success(t) : DataResult.error(DataError.custom("Unknown DamageType registry key: " + key));
        },
        type -> {
            Key k = RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE).getKey(type);
            return k != null ? DataResult.success(k) : DataResult.error(DataError.custom("Unregistered DamageType execution"));
        }
    ).describe("DamageType");

    public static final Codec<RegistryKeySet<@NotNull DamageType>> DAMAGE_TYPE_KEYS = DAMAGE_TYPE.list().xmap(
        l -> RegistrySet.keySetFromValues(RegistryKey.DAMAGE_TYPE, l),
        k -> k.resolve(RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE)).stream().toList()
    ).describe("DamageTypeKeySet");

    public static final Codec<BlockType> BLOCK_TYPE = Codecs.KEY.flatXmap(
        key -> {
            BlockType b = Registry.BLOCK.get(key);
            return b != null ? DataResult.success(b) : DataResult.error(DataError.custom("Unknown BlockType registry key: " + key));
        },
        type -> {
            Key k = type.getKey();
            return DataResult.success(k);
        }
    ).describe("BlockType");

    public static final Codec<RegistryKeySet<@NotNull BlockType>> BLOCK_TYPE_KEYS = BLOCK_TYPE.list().xmap(
        l -> RegistrySet.keySetFromValues(RegistryKey.BLOCK, l),
        k -> k.resolve(Registry.BLOCK).stream().toList()
    ).describe("BlockTypeKeySet");

    public static final Codec<BlockPredicate> BLOCK_PREDICATE = BLOCK_TYPE_KEYS.xmap(
        k -> BlockPredicate.predicate().blocks(k).build(),
        BlockPredicate::blocks
    ).describe("BlockPredicate");

    public static final Codec<ItemAdventurePredicate> ITEM_ADV_PREDICATE = BLOCK_PREDICATE.list().xmap(
        ItemAdventurePredicate::itemAdventurePredicate,
        ItemAdventurePredicate::predicates
    ).describe("ItemAdventurePredicate");

    public static final Codec<ItemType> ITEM_TYPE = Codecs.KEY.flatXmap(
        key -> {
            ItemType t = Registry.ITEM.get(key);
            return t != null ? DataResult.success(t) : DataResult.error(DataError.custom("Unknown ItemType registry key: " + key));
        },
        type -> {
            Key k = type.getKey();
            return k != null ? DataResult.success(k) : DataResult.error(DataError.custom("Unregistered ItemType execution"));
        }
    ).describe("ItemType");

    public static final Codec<EntityType> ENTITY_TYPE = Codecs.KEY.flatXmap(
        key -> {
            EntityType t = Registry.ENTITY_TYPE.get(key);
            return t != null ? DataResult.success(t) : DataResult.error(DataError.custom("Unknown EntityType registry key: " + key));
        },
        type -> {
            Key k = type.getKey();
            return k != null ? DataResult.success(k) : DataResult.error(DataError.custom("Unregistered EntityType execution"));
        }
    ).describe("EntityType");

    public static final Codec<net.minecraft.world.entity.EntityType<?>> NMS_ENTITY_TYPE = Codecs.STRING.<net.minecraft.world.entity.EntityType<?>>flatXmap(
        s -> {
            try {
                return DataResult.success(BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.parse(s)));
            } catch (Exception e) {
                return DataResult.error(DataError.invalidFormat(s, "NMS EntityType Identifier"));
            }
        },
        e -> {
            try {
                return DataResult.success(BuiltInRegistries.ENTITY_TYPE.getKey(e).toString());
            } catch (Exception ex) {
                return DataResult.error(DataError.custom("Failed to map NMS EntityType identifier"));
            }
        }
    ).describe("NMSEntityType");

    public static final Codec<PatternType> BANNER_PATTERN_TYPE = Codecs.KEY.flatXmap(
        key -> {
            PatternType t = RegistryAccess.registryAccess().getRegistry(RegistryKey.BANNER_PATTERN).get(key);
            return t != null ? DataResult.success(t) : DataResult.error(DataError.custom("Unknown PatternType registry key: " + key));
        },
        type -> {
            Key k = RegistryAccess.registryAccess().getRegistry(RegistryKey.BANNER_PATTERN).getKey(type);
            return k != null ? DataResult.success(k) : DataResult.error(DataError.custom("Unregistered PatternType execution"));
        }
    ).describe("PatternType");

    public static final Codec<Pattern> BANNER_PATTERN = RecordBuilder.create(instance -> instance.group(
        Codec.enumCodec(DyeColor.class).fieldOf("color").forGetter(Pattern::getColor),
        BANNER_PATTERN_TYPE.fieldOf("pattern").forGetter(Pattern::getPattern)
    ).apply(instance, Pattern::new)).describe("BannerPattern");

    public static final Codec<BannerPatternLayers> BANNER_PATTERN_LAYERS = BANNER_PATTERN.list().xmap(
        BannerPatternLayers::bannerPatternLayers,
        BannerPatternLayers::patterns
    ).describe("BannerPatternLayers");

    public static final Codec<Color> COLOR = RecordBuilder.create(instance -> instance.group(
        Codecs.INT.fieldOf("alpha").forGetter(Color::getAlpha),
        Codecs.INT.fieldOf("red").forGetter(Color::getRed),
        Codecs.INT.fieldOf("green").forGetter(Color::getGreen),
        Codecs.INT.fieldOf("blue").forGetter(Color::getBlue)
    ).apply(instance, Color::fromARGB)).describe("Color");

    public static final Codec<UseCooldown> USE_COOLDOWN = RecordBuilder.create(instance -> instance.group(
        Codecs.FLOAT.fieldOf("cooldown").forGetter(UseCooldown::seconds),
        Codecs.KEY.optionalFieldOf("group", null).forGetter(UseCooldown::cooldownGroup)
    ).apply(instance, (cd, group) ->
        group != null ? UseCooldown.useCooldown(cd).cooldownGroup(group).build() : UseCooldown.useCooldown(cd).build()
    )).describe("UseCooldown");

    public static final Codec<MapCursor.Type> MAP_CURSOR_TYPE = Codecs.KEY.flatXmap(
        key -> {
            MapCursor.Type t = Registry.MAP_DECORATION_TYPE.get(key);
            return t != null ? DataResult.success(t) : DataResult.error(DataError.custom("Unknown MapCursorType: " + key));
        },
        type -> {
            Key k = type.getKey();
            return k != null ? DataResult.success(k) : DataResult.error(DataError.custom("Unregistered MapCursorType execution"));
        }
    ).describe("MapCursorType");

    public static final Codec<MapDecorations.DecorationEntry> MAP_DECO_ENTRY = RecordBuilder.create(instance -> instance.group(
        MAP_CURSOR_TYPE.fieldOf("type").forGetter(MapDecorations.DecorationEntry::type),
        Codecs.DOUBLE.fieldOf("x").forGetter(MapDecorations.DecorationEntry::x),
        Codecs.DOUBLE.fieldOf("z").forGetter(MapDecorations.DecorationEntry::z),
        Codecs.FLOAT.fieldOf("rotation").forGetter(MapDecorations.DecorationEntry::rotation)
    ).apply(instance, MapDecorations::decorationEntry)).describe("MapDecorationEntry");

    public static final Codec<CustomModelData> CUSTOM_MODEL_DATA = RecordBuilder.create(instance -> instance.group(
        Codecs.FLOAT.list().fieldOf("floats").forGetter(CustomModelData::floats),
        Codecs.BOOLEAN.list().fieldOf("flags").forGetter(CustomModelData::flags),
        Codecs.STRING.list().fieldOf("strings").forGetter(CustomModelData::strings),
        ExtraCodecs.COLOR.list().fieldOf("colors").forGetter(CustomModelData::colors)
    ).apply(instance, (floats, flags, strings, colors) ->
        CustomModelData.customModelData().addFloats(floats).addFlags(flags).addStrings(strings).addColors(colors).build()
    )).describe("CustomModelData");

    public static final Codec<Repairable> REPAIRABLE_COMPONENT = ITEM_TYPE.list().xmap(
        l -> Repairable.repairable(RegistrySet.keySetFromValues(RegistryKey.ITEM, l)),
        r -> r.types().resolve(Registry.ITEM).stream().toList()
    ).describe("RepairableComponent");

    public static final Codec<LodestoneTracker> LODESTONE_TRACKER = RecordBuilder.create(instance -> instance.group(
        Codecs.LOCATION.fieldOf("location").forGetter(LodestoneTracker::location),
        Codecs.BOOLEAN.fieldOf("tracked").forGetter(LodestoneTracker::tracked)
    ).apply(instance, LodestoneTracker::lodestoneTracker)).describe("LodestoneTracker");

    public static final Codec<Weapon> WEAPON = RecordBuilder.create(instance -> instance.group(
        Codecs.INT.fieldOf("item_damage_per_attack").forGetter(Weapon::itemDamagePerAttack),
        Codecs.FLOAT.fieldOf("disable_blocking_for_seconds").forGetter(Weapon::disableBlockingForSeconds)
    ).apply(instance, (itemDamagePerAttack, disableBlockingForSeconds) -> Weapon.weapon()
        .itemDamagePerAttack(itemDamagePerAttack)
        .disableBlockingForSeconds(disableBlockingForSeconds).build()
    )).describe("Weapon");

    public static final Codec<FireworkEffect> FIREWORK_EFFECT = RecordBuilder.create(instance -> instance.group(
        Codecs.BOOLEAN.fieldOf("flicker").forGetter(FireworkEffect::hasFlicker),
        Codecs.BOOLEAN.fieldOf("trail").forGetter(FireworkEffect::hasTrail),
        COLOR.list().fieldOf("colors").forGetter(FireworkEffect::getColors),
        COLOR.list().fieldOf("fade_colors").forGetter(FireworkEffect::getFadeColors),
        Codec.enumCodec(FireworkEffect.Type.class).fieldOf("type").forGetter(FireworkEffect::getType)
    ).apply(instance, (flicker, trail, colors, fadeColors, type) -> FireworkEffect.builder()
        .flicker(flicker).trail(trail).with(type).withColor(colors).withFade(fadeColors).build()
    )).describe("FireworkEffect");

    public static final Codec<Fireworks> FIREWORKS = RecordBuilder.create(instance -> instance.group(
        FIREWORK_EFFECT.list().fieldOf("effects").forGetter(Fireworks::effects),
        Codecs.INT.fieldOf("flight_duration").forGetter(Fireworks::flightDuration)
    ).apply(instance, Fireworks::fireworks)).describe("Fireworks");

    public static final Codec<SeededContainerLoot> SEEDED_CONTAINER_LOOT = RecordBuilder.create(instance -> instance.group(
        Codecs.KEY.fieldOf("loot_table").forGetter(SeededContainerLoot::lootTable),
        Codecs.LONG.fieldOf("seed").forGetter(SeededContainerLoot::seed)
    ).apply(instance, SeededContainerLoot::seededContainerLoot)).describe("SeededContainerLoot");

    public static final Codec<PotDecorations> POT_DECORATIONS = RecordBuilder.create(instance -> instance.group(
        ITEM_TYPE.fieldOf("front").forGetter(PotDecorations::front),
        ITEM_TYPE.fieldOf("back").forGetter(PotDecorations::back),
        ITEM_TYPE.fieldOf("left").forGetter(PotDecorations::left),
        ITEM_TYPE.fieldOf("right").forGetter(PotDecorations::right)
    ).apply(instance, PotDecorations::potDecorations)).describe("PotDecorations");

    public static final Codec<FoodProperties> FOOD_PROPERTIES = RecordBuilder.create(instance -> instance.group(
        Codecs.INT.fieldOf("nutrition").forGetter(FoodProperties::nutrition),
        Codecs.FLOAT.fieldOf("saturation").forGetter(FoodProperties::saturation),
        Codecs.BOOLEAN.fieldOf("can_always_eat").forGetter(FoodProperties::canAlwaysEat)
    ).apply(instance, (nutrition, saturation, canAlwaysEat) -> FoodProperties.food()
        .nutrition(nutrition).saturation(saturation).canAlwaysEat(canAlwaysEat).build()
    )).describe("FoodProperties");

    public static final Codec<Equippable> EQUIPPABLE = RecordBuilder.create(instance -> instance.group(
        Codec.enumCodec(EquipmentSlot.class).fieldOf("slot").forGetter(Equippable::slot),
        Codecs.KEY.fieldOf("equip_sound").forGetter(Equippable::equipSound),
        Codecs.KEY.optionalFieldOf("asset_id", null).forGetter(Equippable::assetId),
        Codecs.KEY.optionalFieldOf("camera_overlay", null).forGetter(Equippable::cameraOverlay),
        ENTITY_TYPE.list().optionalFieldOf("allowed_entities", null).forGetter(e -> e.allowedEntities() != null ? e.allowedEntities().resolve(Registry.ENTITY_TYPE).stream().toList() : null),
        Codecs.BOOLEAN.fieldOf("dispensable").forGetter(Equippable::dispensable),
        Codecs.BOOLEAN.fieldOf("swappable").forGetter(Equippable::swappable),
        Codecs.BOOLEAN.fieldOf("damage_on_hurt").forGetter(Equippable::damageOnHurt),
        Codecs.BOOLEAN.fieldOf("can_be_sheared").forGetter(Equippable::canBeSheared),
        Codecs.KEY.optionalFieldOf("shear_sound", null).forGetter(Equippable::shearSound)
    ).apply(instance, (slot, equipSound, assetId, cameraOverlay, allowedEntities, dispensable, swappable, damageOnHurt, canBeSheared, shearSound) -> {
        Equippable.Builder builder = Equippable.equippable(slot).equipSound(equipSound).assetId(assetId).cameraOverlay(cameraOverlay);
        if (allowedEntities != null) builder.allowedEntities(RegistrySet.keySetFromValues(RegistryKey.ENTITY_TYPE, allowedEntities));
        return builder.dispensable(dispensable).swappable(swappable).damageOnHurt(damageOnHurt).canBeSheared(canBeSheared).shearSound(shearSound).build();
    })).describe("Equippable");

    public static final Codec<Enchantment> ENCHANTMENT = Codecs.KEY.flatXmap(
        key -> {
            Enchantment e = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(key);
            return e != null ? DataResult.success(e) : DataResult.error(DataError.custom("Unknown Enchantment registry key: " + key));
        },
        type -> {
            Key k = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).getKey(type);
            return k != null ? DataResult.success(k) : DataResult.error(DataError.custom("Unregistered Enchantment execution"));
        }
    ).describe("Enchantment");

    public static final Codec<ItemEnchantments> ITEM_ENCHANTMENTS = Codec.map(ENCHANTMENT, Codecs.INT).xmap(
        ItemEnchantments::itemEnchantments,
        ItemEnchantments::enchantments
    ).describe("ItemEnchantments");

    public static final Codec<TooltipDisplay> TOOLTIP_DISPLAY = RecordBuilder.create(instance -> instance.group(
        Codecs.BOOLEAN.fieldOf("hide_tooltips").forGetter(TooltipDisplay::hideTooltip),
        Codecs.DATA_COMPONENT_TYPE.list().fieldOf("hidden_components").forGetter(t -> t.hiddenComponents().stream().toList())
    ).apply(instance, (hide, hiddenComponents) -> TooltipDisplay.tooltipDisplay()
        .hideTooltip(hide).hiddenComponents(new HashSet<>(hiddenComponents)).build()
    )).describe("TooltipDisplay");

    public static final Codec<TrimMaterial> TRIM_MATERIAL = Codecs.KEY.flatXmap(
        key -> {
            TrimMaterial t = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL).get(key);
            return t != null ? DataResult.success(t) : DataResult.error(DataError.custom("Unknown TrimMaterial registry key: " + key));
        },
        type -> {
            Key k = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL).getKey(type);
            return k != null ? DataResult.success(k) : DataResult.error(DataError.custom("Unregistered TrimMaterial execution"));
        }
    ).describe("TrimMaterial");

    public static final Codec<TrimPattern> TRIM_PATTERN = Codecs.KEY.flatXmap(
        key -> {
            TrimPattern p = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_PATTERN).get(key);
            return p != null ? DataResult.success(p) : DataResult.error(DataError.custom("Unknown TrimPattern registry key: " + key));
        },
        type -> {
            Key k = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_PATTERN).getKey(type);
            return k != null ? DataResult.success(k) : DataResult.error(DataError.custom("Unregistered TrimPattern execution"));
        }
    ).describe("TrimPattern");

    public static final Codec<ArmorTrim> ARMOR_TRIM = RecordBuilder.create(instance -> instance.group(
        TRIM_MATERIAL.fieldOf("material").forGetter(ArmorTrim::getMaterial),
        TRIM_PATTERN.fieldOf("pattern").forGetter(ArmorTrim::getPattern)
    ).apply(instance, ArmorTrim::new)).describe("ArmorTrim");

    public static final Codec<Filtered<String>> FILTERED_STRING = Codecs.STRING.xmap(
        s -> Filtered.of(s, null),
        Filtered::raw
    ).describe("FilteredString");

    public static final Codec<Filtered<ComponentLike>> FILTERED_COMPONENT = Codecs.TEXT_COMPONENT.xmap(
        c -> Filtered.of((ComponentLike) c, null),
        f -> f.raw().asComponent()
    ).describe("FilteredComponent");

    public static final Codec<WrittenBookContent> WRITTEN_BOOK_CONTENT = RecordBuilder.create(instance -> instance.group(
        FILTERED_STRING.fieldOf("title").forGetter(WrittenBookContent::title),
        Codecs.STRING.fieldOf("author").forGetter(WrittenBookContent::author),
        Codecs.INT.fieldOf("generation").forGetter(WrittenBookContent::generation),
        FILTERED_COMPONENT.list().fieldOf("pages").forGetter(w -> {
            List<Filtered<ComponentLike>> comps = new ArrayList<>();
            w.pages().forEach(fc -> comps.add(Filtered.of(fc.raw(), null)));
            return comps;
        }),
        Codecs.BOOLEAN.fieldOf("resolved").forGetter(WrittenBookContent::resolved)
    ).apply(instance, (title, author, gen, pages, resolved) -> {
        WrittenBookContent.Builder builder = WrittenBookContent.writtenBookContent(title, author).resolved(resolved);
        pages.forEach(p -> builder.addPage(p.raw()));
        return builder.build();
    })).describe("WrittenBookContent");

    public static Codec<PotionEffectType> POTION_EFFECT_TYPE = Codecs.KEY.flatXmap(
        key -> {
            PotionEffectType e = RegistryAccess.registryAccess().getRegistry(RegistryKey.MOB_EFFECT).get(key);
            return e != null ? DataResult.success(e) : DataResult.error(DataError.custom("Unknown PotionEffectType registry key: " + key));
        },
        type -> {
            Key k = RegistryAccess.registryAccess().getRegistry(RegistryKey.MOB_EFFECT).getKey(type);
            return k != null ? DataResult.success(k) : DataResult.error(DataError.custom("Unregistered PotionEffectType execution"));
        }
    ).describe("PotionEffectType");

    public static Codec<RegistryKeySet<@NotNull PotionEffectType>> POTION_EFFECT_TYPES = POTION_EFFECT_TYPE.list().xmap(
        k -> RegistrySet.keySetFromValues(RegistryKey.MOB_EFFECT, k),
        s -> s.resolve(Registry.POTION_EFFECT_TYPE).stream().toList()
    ).describe("PotionEffectTypes");

    public static Codec<PotionEffect> POTION_EFFECT = RecordBuilder.<PotionEffect>create(instance -> instance.group(
        Codecs.INT.fieldOf("amplifier").forGetter(PotionEffect::getAmplifier),
        Codecs.INT.fieldOf("duration").forGetter(PotionEffect::getDuration),
        POTION_EFFECT_TYPE.fieldOf("type").forGetter(PotionEffect::getType),
        Codecs.BOOLEAN.fieldOf("ambient").forGetter(PotionEffect::isAmbient),
        Codecs.BOOLEAN.fieldOf("particles").forGetter(PotionEffect::hasParticles),
        Codecs.BOOLEAN.fieldOf("icon").forGetter(PotionEffect::hasIcon)
    ).apply(instance, (amplifier, duration, type, ambient, particles, icon) ->
        new PotionEffect(type, duration, amplifier, ambient, particles, icon)
    )).describe("PotionEffect");

    public static final Codec<ConsumeEffect.TeleportRandomly> CONSUME_TELEPORT_RANDOMLY = Codecs.DOUBLE.xmap(
        d -> ConsumeEffect.teleportRandomlyEffect(d.floatValue()),
        e -> (double) e.diameter()
    ).describe("ConsumeEffect.TeleportRandomly");

    public static final Codec<ConsumeEffect.PlaySound> CONSUME_PLAY_SOUND = Codecs.KEY.xmap(
        ConsumeEffect::playSoundConsumeEffect,
        ConsumeEffect.PlaySound::sound
    ).describe("ConsumeEffect.PlaySound");

    public static final Codec<ConsumeEffect.RemoveStatusEffects> CONSUME_REMOVE_STATUS_EFFECTS = POTION_EFFECT_TYPES.xmap(
        ConsumeEffect::removeEffects,
        ConsumeEffect.RemoveStatusEffects::removeEffects
    ).describe("ConsumeEffect.RemoveStatusEffects");

    public static final Codec<ConsumeEffect.ApplyStatusEffects> CONSUME_APPLY_STATUS_EFFECTS = RecordBuilder.create(instance -> instance.group(
        POTION_EFFECT.list().fieldOf("effects").forGetter(ConsumeEffect.ApplyStatusEffects::effects),
        Codecs.DOUBLE.<Float>xmap(Double::floatValue, Float::doubleValue).fieldOf("probability").forGetter(ConsumeEffect.ApplyStatusEffects::probability)
    ).apply(instance, ConsumeEffect::applyStatusEffects)).describe("ConsumeEffect.ApplyStatusEffects");

    public static final Codec<ConsumeEffect> CONSUME_EFFECT = new Codec<>() {
        @Override
        public <D> DataResult<ConsumeEffect> decode(DynamicOps<D> ops, D input) {
            return ops.getMap(input)
                .map(DataResult::success)
                .orElseGet(() -> DataResult.error(DataError.typeMismatch("Map", "Unknown")))
                .flatMap(map -> {
                    D typeNode = map.get(ops.createString("type"));
                    if (typeNode == null) return DataResult.error(DataError.missingField("type"));

                    return Codecs.STRING.decode(ops, typeNode).prependPath("type").flatMap(typeStr -> {
                        String type = typeStr.replace("minecraft:", "");
                        return switch (type) {
                            case "apply_effects" ->
                                CONSUME_APPLY_STATUS_EFFECTS.decode(ops, input).map(e -> (ConsumeEffect) e);
                            case "remove_effects" -> {
                                D node = map.get(ops.createString("effects"));
                                if (node == null) yield DataResult.error(DataError.missingField("effects"));
                                yield CONSUME_REMOVE_STATUS_EFFECTS.decode(ops, node).prependPath("effects").map(e -> (ConsumeEffect) e);
                            }
                            case "clear_all_effects" ->
                                DataResult.success((ConsumeEffect) ConsumeEffect.clearAllStatusEffects());
                            case "teleport_randomly" -> {
                                D node = map.get(ops.createString("diameter"));
                                if (node == null) yield DataResult.error(DataError.missingField("diameter"));
                                yield CONSUME_TELEPORT_RANDOMLY.decode(ops, node).prependPath("diameter").map(e -> (ConsumeEffect) e);
                            }
                            case "play_sound" -> {
                                D node = map.get(ops.createString("sound"));
                                if (node == null) yield DataResult.error(DataError.missingField("sound"));
                                yield CONSUME_PLAY_SOUND.decode(ops, node).prependPath("sound").map(e -> (ConsumeEffect) e);
                            }
                            default -> DataResult.error(DataError.custom("Unknown consume effect type: " + type));
                        };
                    });
                });
        }

        @Override
        public <D> DataResult<D> encode(DynamicOps<D> ops, ConsumeEffect value) {
            Map<D, D> map = new HashMap<>();

            if (value instanceof ConsumeEffect.ApplyStatusEffects ase) {
                DataResult<D> res = CONSUME_APPLY_STATUS_EFFECTS.encode(ops, ase);
                if (res.isError()) return res;
                ops.getMap(res.getOrThrow()).ifPresent(map::putAll);
                map.put(ops.createString("type"), ops.createString("minecraft:apply_effects"));
            } else if (value instanceof ConsumeEffect.RemoveStatusEffects rse) {
                DataResult<D> res = CONSUME_REMOVE_STATUS_EFFECTS.encode(ops, rse).prependPath("effects");
                if (res.isError()) return res;
                map.put(ops.createString("effects"), res.getOrThrow());
                map.put(ops.createString("type"), ops.createString("minecraft:remove_effects"));
            } else if (value instanceof ConsumeEffect.ClearAllStatusEffects) {
                map.put(ops.createString("type"), ops.createString("minecraft:clear_all_effects"));
            } else if (value instanceof ConsumeEffect.TeleportRandomly tr) {
                DataResult<D> res = CONSUME_TELEPORT_RANDOMLY.encode(ops, tr).prependPath("diameter");
                if (res.isError()) return res;
                map.put(ops.createString("diameter"), res.getOrThrow());
                map.put(ops.createString("type"), ops.createString("minecraft:teleport_randomly"));
            } else if (value instanceof ConsumeEffect.PlaySound ps) {
                DataResult<D> res = CONSUME_PLAY_SOUND.encode(ops, ps).prependPath("sound");
                if (res.isError()) return res;
                map.put(ops.createString("sound"), res.getOrThrow());
                map.put(ops.createString("type"), ops.createString("minecraft:play_sound"));
            } else {
                return DataResult.error(DataError.custom("Unknown consume effect type: " + value.getClass().getName()));
            }

            return DataResult.success(ops.createMap(map));
        }

        @Override
        public String describe() {
            return "ConsumeEffect";
        }
    };

    public static Codec<SuspiciousEffectEntry> SUSPICIOUS_EFFECT_ENTRY = RecordBuilder.create(instance -> instance.group(
        POTION_EFFECT_TYPE.fieldOf("effect_type").forGetter(SuspiciousEffectEntry::effect),
        Codecs.INT.fieldOf("duration").forGetter(SuspiciousEffectEntry::duration)
    ).apply(instance, SuspiciousEffectEntry::create)).describe("SuspiciousEffectEntry");

    public static Codec<ProfileProperty> PROFILE_PROPERTY = RecordBuilder.create(instance -> instance.group(
        Codecs.STRING.fieldOf("name").forGetter(ProfileProperty::getName),
        Codecs.STRING.fieldOf("value").forGetter(ProfileProperty::getValue),
        Codecs.STRING.optionalFieldOf("signature", null).forGetter(ProfileProperty::getSignature)
    ).apply(instance, ProfileProperty::new)).describe("ProfileProperty");

    public static Codec<ResolvableProfile> RESOLVABLE_PROFILE = RecordBuilder.create(instance -> instance.group(
        Codecs.STRING.optionalFieldOf("name", null).forGetter(ResolvableProfile::name),
        Codecs.UUID.optionalFieldOf("uuid", null).forGetter(ResolvableProfile::uuid),
        PROFILE_PROPERTY.list().fieldOf("properties").forGetter(p -> p.properties().stream().toList())
    ).apply(instance, (name, uuid, properties) -> ResolvableProfile.resolvableProfile()
        .name(name).uuid(uuid).addProperties(properties).build()
    )).describe("ResolvableProfile");

    public static Codec<Tool.Rule> TOOL_RULE = RecordBuilder.create(instance -> instance.group(
        BLOCK_TYPE_KEYS.fieldOf("blocks").forGetter(Tool.Rule::blocks),
        Codecs.FLOAT.fieldOf("speed").forGetter(Tool.Rule::speed),
        Codec.enumCodec(TriState.class).fieldOf("correct_for_drops").forGetter(Tool.Rule::correctForDrops)
    ).apply(instance, Tool::rule)).describe("Tool.Rule");

    public static Codec<Tool> TOOL = RecordBuilder.create(instance -> instance.group(
        Codecs.FLOAT.fieldOf("default_mining_speed").forGetter(Tool::defaultMiningSpeed),
        Codecs.INT.fieldOf("damage_per_block").forGetter(Tool::damagePerBlock),
        TOOL_RULE.list().fieldOf("rules").forGetter(Tool::rules),
        Codecs.BOOLEAN.fieldOf("can_destroy_blocks_in_creative").forGetter(Tool::canDestroyBlocksInCreative)
    ).apply(instance, (defaultMiningSpeed, damagePerBlock, rules, canDestroyBlocksInCreative) -> Tool.tool()
        .defaultMiningSpeed(defaultMiningSpeed).damagePerBlock(damagePerBlock)
        .addRules(rules).canDestroyBlocksInCreative(canDestroyBlocksInCreative).build()
    )).describe("Tool");

    public static Codec<Consumable> CONSUMABLE = RecordBuilder.create(instance -> instance.group(
        Codecs.FLOAT.fieldOf("consume_seconds").forGetter(Consumable::consumeSeconds),
        Codec.enumCodec(ItemUseAnimation.class).fieldOf("animation").forGetter(Consumable::animation),
        Codecs.KEY.fieldOf("sound").forGetter(Consumable::sound),
        Codecs.BOOLEAN.fieldOf("has_consume_particles").forGetter(Consumable::hasConsumeParticles),
        CONSUME_EFFECT.list().fieldOf("consume_effects").forGetter(Consumable::consumeEffects)
    ).apply(instance, (consumeSeconds, animation, sound, hasConsumeParticles, consumeEffects) -> Consumable.consumable()
        .consumeSeconds(consumeSeconds).animation(animation).sound(sound)
        .hasConsumeParticles(hasConsumeParticles).addEffects(consumeEffects).build()
    )).describe("Consumable");

    public static Codec<ItemDamageFunction> ITEM_DAMAGE_FUNCTION = RecordBuilder.create(instance -> instance.group(
        Codecs.FLOAT.fieldOf("threshold").forGetter(ItemDamageFunction::threshold),
        Codecs.FLOAT.fieldOf("base").forGetter(ItemDamageFunction::base),
        Codecs.FLOAT.fieldOf("factor").forGetter(ItemDamageFunction::factor)
    ).apply(instance, (threshold, base, factor) -> ItemDamageFunction.itemDamageFunction()
        .threshold(threshold).base(base).factor(factor).build()
    )).describe("ItemDamageFunction");

    public static Codec<DamageReduction> DAMAGE_REDUCTION = RecordBuilder.create(instance -> instance.group(
        DAMAGE_TYPE_KEYS.fieldOf("type").forGetter(DamageReduction::type),
        Codecs.FLOAT.fieldOf("horizontal_blocking_angle").forGetter(DamageReduction::horizontalBlockingAngle),
        Codecs.FLOAT.fieldOf("base").forGetter(DamageReduction::base),
        Codecs.FLOAT.fieldOf("factor").forGetter(DamageReduction::factor)
    ).apply(instance, (type, horizontalBlockingAngle, base, factor) -> DamageReduction.damageReduction()
        .type(type).horizontalBlockingAngle(horizontalBlockingAngle).base(base).factor(factor).build()
    )).describe("DamageReduction");

    public static Codec<BlocksAttacks> BLOCKS_ATTACKS = RecordBuilder.create(instance -> instance.group(
        Codecs.FLOAT.fieldOf("block_delay_seconds").forGetter(BlocksAttacks::blockDelaySeconds),
        Codecs.FLOAT.fieldOf("disable_cooldown_scale").forGetter(BlocksAttacks::disableCooldownScale),
        DAMAGE_REDUCTION.list().fieldOf("damage_reductions").forGetter(BlocksAttacks::damageReductions),
        ITEM_DAMAGE_FUNCTION.fieldOf("item_damage").forGetter(BlocksAttacks::itemDamage),
        //? if >=26.1.2 {
        DAMAGE_TYPE_KEYS.optionalFieldOf("bypassed_by", null).forGetter(BlocksAttacks::bypassedBy),
        //?} else {
        /*DAMAGE_TYPE.optionalFieldOf("bypassed_by", null).forGetter(BlocksAttacks.class, o -> {
            TagKey<DamageType> bypassedBy = o.bypassedBy();
            if (bypassedBy == null) return null;
            return RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE).get(bypassedBy.key());
        }),
        *///?}
        Codecs.KEY.optionalFieldOf("block_sound", null).forGetter(BlocksAttacks::blockSound),
        Codecs.KEY.optionalFieldOf("disable_sound", null).forGetter(BlocksAttacks::disableSound)
    ).apply(instance, (blockDelaySeconds, disableCooldownScale, damageReductions, itemDamage, bypassedBy, blockSound, disableSound) -> {
        BlocksAttacks.Builder builder = BlocksAttacks.blocksAttacks()
            .blockDelaySeconds(blockDelaySeconds).disableCooldownScale(disableCooldownScale)
            .damageReductions(damageReductions).itemDamage(itemDamage)
            .blockSound(blockSound).disableSound(disableSound);
        if (bypassedBy != null) {
            //? if >=26.1.2 {
            builder.bypassedBy(bypassedBy);
            //?} else {
            /*builder.bypassedBy(TagKey.create(RegistryKey.DAMAGE_TYPE, bypassedBy.key()));
            *///?}
        }
        return builder.build();
    })).describe("BlocksAttacks");

    public static Codec<EquipmentSlotGroup> EQUIPMENT_SLOT_GROUP = Codecs.STRING.flatXmap(
        str -> {
            try {
                EquipmentSlotGroup group = EquipmentSlotGroup.getByName(str);
                return group != null ? DataResult.success(group) : DataResult.error(DataError.custom("Unknown EquipmentSlotGroup target: " + str));
            } catch (Exception e) { return DataResult.error(DataError.custom(e.getMessage())); }
        },
        group -> DataResult.success(group != null ? group.toString() : EquipmentSlotGroup.ANY.toString())
    ).describe("EquipmentSlotGroup");

    public static Codec<Attribute> ATTRIBUTE = Codecs.KEY.flatXmap(
        key -> {
            Attribute a = RegistryAccess.registryAccess().getRegistry(RegistryKey.ATTRIBUTE).get(key);
            return a != null ? DataResult.success(a) : DataResult.error(DataError.custom("Unknown Attribute registry key: " + key));
        },
        type -> {
            Key k = RegistryAccess.registryAccess().getRegistry(RegistryKey.ATTRIBUTE).getKey(type);
            return k != null ? DataResult.success(k) : DataResult.error(DataError.custom("Unregistered Attribute execution"));
        }
    ).describe("Attribute");

    public static Codec<AttributeModifier> ATTRIBUTE_MODIFIER = RecordBuilder.create(instance -> instance.group(
        Codecs.NAMESPACED_KEY.fieldOf("key").forGetter(AttributeModifier::getKey),
        Codecs.DOUBLE.fieldOf("amount").forGetter(AttributeModifier::getAmount),
        Codec.enumCodec(AttributeModifier.Operation.class).fieldOf("operation").forGetter(AttributeModifier::getOperation),
        EQUIPMENT_SLOT_GROUP.fieldOf("slot").forGetter(AttributeModifier::getSlotGroup)
    ).apply(instance, AttributeModifier::new)).describe("AttributeModifier");

    public static final Codec<ItemAttributeModifiers> ITEM_ATTRIBUTE_MODIFIERS = Codec.map(ATTRIBUTE, Codec.map(EQUIPMENT_SLOT_GROUP, ATTRIBUTE_MODIFIER)).xmap(
        map -> {
            ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.itemAttributes();
            map.forEach((attribute, inner) ->
                inner.forEach((group, modifier) -> builder.addModifier(attribute, modifier, group))
            );
            return builder.build();
        },
        modifiers -> {
            Map<Attribute, Map<EquipmentSlotGroup, AttributeModifier>> map = new LinkedHashMap<>();
            for (ItemAttributeModifiers.Entry e : modifiers.modifiers()) {
                map.computeIfAbsent(e.attribute(), k -> new LinkedHashMap<>()).put(e.getGroup(), e.modifier());
            }
            return map;
        }
    ).describe("ItemAttributeModifiers");

    public static final Codec<BlockInfo> BLOCK_INFO = new Codec<>() {
        @Override
        public <D> DataResult<BlockInfo> decode(DynamicOps<D> ops, D input) {
            return ops.getMap(input)
                .map(DataResult::success)
                .orElseGet(() -> DataResult.error(DataError.typeMismatch("Map", "Unknown")))
                .flatMap(map -> {
                    D idNode = map.get(ops.createString("id"));
                    if (idNode == null) return DataResult.error(DataError.missingField("id"));

                    return Codecs.STRING.decode(ops, idNode).prependPath("id").flatMap(id -> {
                        Vector pos = null;
                        D posObj = map.get(ops.createString("pos"));
                        if (posObj != null) {
                            DataResult<Vector> posRes = Codecs.VECTOR_I.decode(ops, posObj).prependPath("pos");
                            if (posRes.isError()) return DataResult.error(posRes.error().get());
                            pos = posRes.getOrThrow();
                        }

                        Object blockObject;
                        if (id.startsWith("minecraft:")) {
                            Material mat = Material.matchMaterial(id.substring(10));
                            if (mat == null || !mat.isBlock())
                                return DataResult.error(DataError.custom("Invalid vanilla material mapping: " + id));
                            blockObject = mat.createBlockData();
                        } else {
                            CustomBlock base = Registries.BLOCKS.get(id);
                            if (base == null) return DataResult.error(DataError.custom("Unknown custom block identity: " + id));
                            blockObject = base.clone();
                        }

                        ObjectNode states = null;
                        ObjectNode properties = null;
                        ObjectNode nbt = null;

                        if (ops instanceof JsonOps && input instanceof JsonNode jsonInput) {
                            if (jsonInput.has("states")) states = (ObjectNode) jsonInput.get("states");
                            if (jsonInput.has("properties")) properties = (ObjectNode) jsonInput.get("properties");
                            if (jsonInput.has("nbt")) nbt = (ObjectNode) jsonInput.get("nbt");
                        }

                        if (states != null) {
                            Map<D, D> statesMap = ops.getMap((D) states).orElse(Collections.emptyMap());
                            if (blockObject instanceof BlockData bd) {
                                MinecraftBlockSerializer.deserialize(bd, statesMap, ops);
                            } else if (blockObject instanceof CustomBlock cb) {
                                BlockData tempData = cb.getMaterial().createBlockData();
                                MinecraftBlockSerializer.deserialize(tempData, statesMap, ops);
                            }
                        }

                        return DataResult.success(new BlockInfo(pos, blockObject, states, properties, nbt));
                    });
                });
        }

        @Override
        @SuppressWarnings("unchecked")
        public <D> DataResult<D> encode(DynamicOps<D> ops, BlockInfo value) {
            Map<D, D> map = new HashMap<>();

            DataResult<D> idRes = Codecs.STRING.encode(ops, value.getAsString()).prependPath("id");
            if (idRes.isError()) return idRes;
            map.put(ops.createString("id"), idRes.getOrThrow());

            if (value.pos() != null) {
                DataResult<D> posRes = Codecs.VECTOR_I.encode(ops, value.pos()).prependPath("pos");
                if (posRes.isError()) return posRes;
                map.put(ops.createString("pos"), posRes.getOrThrow());
            }

            if (value.states() != null && !value.states().isEmpty()) {
                map.put(ops.createString("states"), (D) value.states());
            }

            if (value.properties() != null && !value.properties().isEmpty()) {
                map.put(ops.createString("properties"), (D) value.properties());
            }

            if (value.nbt() != null && !value.nbt().isEmpty()) {
                map.put(ops.createString("nbt"), (D) value.nbt());
            }

            return DataResult.success(ops.createMap(map));
        }

        @Override
        public String describe() {
            return "BlockInfo";
        }
    };

    public static final Codec<CompoundTag> COMPOUND_TAG = new Codec<>() {
        @Override
        public <D> DataResult<CompoundTag> decode(DynamicOps<D> ops, D input) {
            Tag tag = decodeTag(ops, input);
            if (tag instanceof CompoundTag ct) {
                return DataResult.success(ct);
            }
            return DataResult.error(DataError.typeMismatch("CompoundTag", "Unknown"));
        }

        @Override
        public <D> DataResult<D> encode(DynamicOps<D> ops, CompoundTag value) {
            return DataResult.success(encodeTag(ops, value));
        }

        @Override
        public String describe() {
            return "CompoundTag";
        }
    };

    private static <D> Tag decodeTag(DynamicOps<D> ops, D input) {
        if (ops.getMap(input).isPresent()) {
            CompoundTag tag = new CompoundTag();
            ops.getMap(input).get().forEach((k, v) ->
                ops.getStringValue(k).ifPresent(keyStr -> tag.put(keyStr, decodeTag(ops, v)))
            );
            return tag;
        }
        if (ops.getList(input).isPresent()) {
            ListTag list = new ListTag();
            ops.getList(input).get().forEach(e -> list.add(decodeTag(ops, e)));
            return list;
        }
        if (ops.getStringValue(input).isPresent()) {
            return StringTag.valueOf(ops.getStringValue(input).get());
        }
        if (ops.getBooleanValue(input).isPresent()) {
            return ByteTag.valueOf(ops.getBooleanValue(input).get());
        }
        if (ops.getIntValue(input).isPresent()) {
            return IntTag.valueOf(ops.getIntValue(input).get());
        }
        if (ops.getLongValue(input).isPresent()) {
            return LongTag.valueOf(ops.getLongValue(input).get());
        }
        if (ops.getFloatValue(input).isPresent()) {
            return FloatTag.valueOf(ops.getFloatValue(input).get());
        }
        if (ops.getDoubleValue(input).isPresent()) {
            return DoubleTag.valueOf(ops.getDoubleValue(input).get());
        }

        return new CompoundTag();
    }

    private static <D> D encodeTag(DynamicOps<D> ops, Tag tag) {
        if (tag == null) return ops.empty();

        switch (tag.getId()) {
            case Tag.TAG_COMPOUND:
                CompoundTag ct = (CompoundTag) tag;
                Map<D, D> map = new LinkedHashMap<>();
                for (String key : ct.keySet()) {
                    map.put(ops.createString(key), encodeTag(ops, ct.get(key)));
                }
                return ops.createMap(map);
            case Tag.TAG_LIST:
                ListTag lt = (ListTag) tag;
                List<D> list = new ArrayList<>();
                for (Tag e : lt) {
                    list.add(encodeTag(ops, e));
                }
                return ops.createList(list);
            case Tag.TAG_STRING:
                return ops.createString(tag.asString().orElse(""));
            case Tag.TAG_BYTE:
                return ops.createBoolean(tag.asByte().orElse((byte) 0) != 0);
            case Tag.TAG_SHORT:
                return ops.createInt(tag.asShort().orElse((short) 0));
            case Tag.TAG_INT:
                return ops.createInt(tag.asInt().orElse(0));
            case Tag.TAG_LONG:
                return ops.createLong(tag.asLong().orElse(0L));
            case Tag.TAG_FLOAT:
                return ops.createFloat(tag.asFloat().orElse(0F));
            case Tag.TAG_DOUBLE:
                return ops.createDouble(tag.asDouble().orElse(0D));
            case Tag.TAG_BYTE_ARRAY:
                List<D> byteList = new ArrayList<>();
                for (byte b : tag.asByteArray().orElse(new byte[0])) {
                    byteList.add(ops.createInt(b));
                }
                return ops.createList(byteList);
            case Tag.TAG_INT_ARRAY:
                List<D> intList = new ArrayList<>();
                for (int i : tag.asIntArray().orElse(new int[0])) {
                    intList.add(ops.createInt(i));
                }
                return ops.createList(intList);
            case Tag.TAG_LONG_ARRAY:
                List<D> longList = new ArrayList<>();
                for (long l : tag.asLongArray().orElse(new long[0])) {
                    longList.add(ops.createLong(l));
                }
                return ops.createList(longList);
            default:
                return ops.empty();
        }
    }

    public static final Codec<SavedEntity> SAVED_ENTITY = new Codec<>() {
        @Override
        public <D> DataResult<SavedEntity> decode(DynamicOps<D> ops, D input) {
            return ops.getMap(input)
                .map(DataResult::success)
                .orElseGet(() -> DataResult.error(DataError.typeMismatch("Map", "Unknown")))
                .flatMap(map -> {
                    D idNode = map.get(ops.createString("id"));
                    if (idNode == null) return DataResult.error(DataError.missingField("id"));

                    return Codecs.STRING.decode(ops, idNode).prependPath("id").flatMap(idStr -> {
                        Either<EntityType, CustomEntity<? extends LivingEntity>> type;
                        if (idStr.startsWith("minecraft:")) {
                            try {
                                type = Either.left(EntityType.valueOf(idStr.substring(10).toUpperCase()));
                            } catch (IllegalArgumentException e) {
                                return DataResult.error(DataError.custom("Unknown vanilla entity type context: " + idStr));
                            }
                        } else {
                            CustomEntity<?> custom = Registries.ENTITIES.get(idStr);
                            if (custom == null)
                                return DataResult.error(DataError.custom("Unknown custom entity identity string: " + idStr));
                            type = Either.right(custom);
                        }
                        return DataResult.success(new SavedEntity(type, input, ops));
                    });
                });
        }

        @Override
        @SuppressWarnings("unchecked")
        public <D> DataResult<D> encode(DynamicOps<D> ops, SavedEntity value) {
            return DataResult.success((D) value.getRawData());
        }

        @Override
        public String describe() {
            return "SavedEntity";
        }
    };
}