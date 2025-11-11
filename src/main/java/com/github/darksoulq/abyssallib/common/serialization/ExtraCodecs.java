package com.github.darksoulq.abyssallib.common.serialization;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import io.papermc.paper.block.BlockPredicate;
import io.papermc.paper.datacomponent.DataComponentType;
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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.kyori.adventure.util.TriState;
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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.map.MapCursor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
public class ExtraCodecs {
    public static final Codec<DamageType> DAMAGE_TYPE = Codecs.KEY.xmap(
            RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE)::getOrThrow,
            RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE)::getKeyOrThrow
    );
    public static final Codec<RegistryKeySet<@NotNull DamageType>> DAMAGE_TYPE_KEYS = DAMAGE_TYPE.list().xmap(
            l -> RegistrySet.keySetFromValues(RegistryKey.DAMAGE_TYPE, l),
            k -> k.resolve(RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE)).stream().toList()
    );
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

    public static final Codec<ConsumeEffect.TeleportRandomly> CONSUME_TELEPORT_RANDOMLY = Codecs.FLOAT.xmap(
            ConsumeEffect::teleportRandomlyEffect,
            ConsumeEffect.TeleportRandomly::diameter
    );
    public static Codec<ConsumeEffect.PlaySound> CONSUME_PLAY_SOUND = Codecs.KEY.xmap(
            ConsumeEffect::playSoundConsumeEffect,
            ConsumeEffect.PlaySound::sound
    );
    public static Codec<PotionEffectType> POTION_EFFECT_TYPE = Codecs.KEY.xmap(
            RegistryAccess.registryAccess().getRegistry(RegistryKey.MOB_EFFECT)::getOrThrow,
            RegistryAccess.registryAccess().getRegistry(RegistryKey.MOB_EFFECT)::getKeyOrThrow
    );
    public static Codec<RegistryKeySet<@NotNull PotionEffectType>> POTION_EFFECT_TYPES = POTION_EFFECT_TYPE.list().xmap(
            k -> RegistrySet.keySetFromValues(RegistryKey.MOB_EFFECT, k),
            s -> s.resolve(Registry.POTION_EFFECT_TYPE).stream().toList()
    );
    public static Codec<ConsumeEffect.RemoveStatusEffects> CONSUME_REMOVE_STATUS_EFFECTS = POTION_EFFECT_TYPES.xmap(
            ConsumeEffect::removeEffects,
            ConsumeEffect.RemoveStatusEffects::removeEffects
    );
    public static Codec<PotionEffect> POTION_EFFECT = RecordCodecBuilder.create(
            Codecs.INT.fieldOf("amplifier", PotionEffect::getAmplifier),
            Codecs.INT.fieldOf("duration", PotionEffect::getDuration),
            POTION_EFFECT_TYPE.fieldOf("type", PotionEffect::getType),
            Codecs.BOOLEAN.fieldOf("ambient", PotionEffect::isAmbient),
            Codecs.BOOLEAN.fieldOf("particles", PotionEffect::hasParticles),
            Codecs.BOOLEAN.fieldOf("icon", PotionEffect::hasIcon),
            (amplifier, duration, type, ambient, particles, icon) ->
                    new PotionEffect(type, duration, amplifier, ambient, particles, icon)
    );
    public static Codec<ConsumeEffect.ApplyStatusEffects> CONSUME_APPLY_STATUS_EFFECTS = RecordCodecBuilder.create(
            POTION_EFFECT.list().fieldOf("effects", ConsumeEffect.ApplyStatusEffects::effects),
            Codecs.FLOAT.fieldOf("probability", ConsumeEffect.ApplyStatusEffects::probability),
            ConsumeEffect::applyStatusEffects
    );
    public static Codec<ConsumeEffect.ClearAllStatusEffects> CONSUME_CLEAR_ALL_EFFECTS = Codec.of(o -> ConsumeEffect.clearAllStatusEffects(), null);
    public static Codec<ConsumeEffect> CONSUME_EFFECT = Codec.either(
            CONSUME_APPLY_STATUS_EFFECTS,
            Codec.either(CONSUME_PLAY_SOUND, Codec.either(CONSUME_TELEPORT_RANDOMLY,
                    Codec.either(CONSUME_REMOVE_STATUS_EFFECTS, CONSUME_CLEAR_ALL_EFFECTS)))
    );

    public static Codec<SuspiciousEffectEntry> SUSPICIOUS_EFFECT_ENTRY = RecordCodecBuilder.create(
            POTION_EFFECT_TYPE.fieldOf("effect_type", SuspiciousEffectEntry::effect),
            Codecs.INT.fieldOf("duration", SuspiciousEffectEntry::duration),
            SuspiciousEffectEntry::create
    );

    public static Codec<ProfileProperty> PROFILE_PROPERTY = RecordCodecBuilder.create(
            Codecs.STRING.fieldOf("name", ProfileProperty::getName),
            Codecs.STRING.fieldOf("value", ProfileProperty::getValue),
            Codecs.STRING.nullable().fieldOf("signature", ProfileProperty::getSignature),
            ProfileProperty::new
    );
    public static Codec<ResolvableProfile> RESOLVABLE_PROFILE = RecordCodecBuilder.create(
            Codecs.STRING.nullable().fieldOf("name", ResolvableProfile::name),
            Codecs.UUID.nullable().fieldOf("uuid", ResolvableProfile::uuid),
            PROFILE_PROPERTY.list().fieldOf("properties", p -> p.properties().stream().toList()),
            (name, uuid, properties) -> ResolvableProfile.resolvableProfile()
                    .name(name)
                    .uuid(uuid)
                    .addProperties(properties)
                    .build()
    );

    public static Codec<Tool.Rule> TOOL_RULE = RecordCodecBuilder.create(
            BLOCK_TYPE_KEYS.fieldOf("blocks", Tool.Rule::blocks),
            Codecs.FLOAT.fieldOf("speed", Tool.Rule::speed),
            Codec.enumCodec(TriState.class).fieldOf("correct_for_drops", Tool.Rule::correctForDrops),
            Tool::rule
    );
    public static Codec<Tool> TOOL = RecordCodecBuilder.create(
            Codecs.FLOAT.fieldOf("default_mining_speed", Tool::defaultMiningSpeed),
            Codecs.INT.fieldOf("damage_per_block", Tool::damagePerBlock),
            TOOL_RULE.list().fieldOf("rules", Tool::rules),
            Codecs.BOOLEAN.fieldOf("can_destroy_blocks_in_creative", Tool::canDestroyBlocksInCreative),
            (defaultMiningSpeed, damagePerBlock, rules, canDestroyBlocksInCreative) -> Tool.tool()
                    .defaultMiningSpeed(defaultMiningSpeed)
                    .damagePerBlock(damagePerBlock)
                    .addRules(rules)
                    .canDestroyBlocksInCreative(canDestroyBlocksInCreative)
                    .build()
    );

    public static Codec<Consumable> CONSUMABLE = RecordCodecBuilder.create(
            Codecs.FLOAT.fieldOf("consume_seconds", Consumable::consumeSeconds),
            Codec.enumCodec(ItemUseAnimation.class).fieldOf("animation", Consumable::animation),
            Codecs.KEY.fieldOf("sound", Consumable::sound),
            Codecs.BOOLEAN.fieldOf("has_consume_particles", Consumable::hasConsumeParticles),
            CONSUME_EFFECT.list().fieldOf("consume_effects", Consumable::consumeEffects),
            (consumeSeconds, animation, sound, hasConsumeParticles, consumeEffects) -> Consumable.consumable()
                    .consumeSeconds(consumeSeconds)
                    .animation(animation)
                    .sound(sound)
                    .hasConsumeParticles(hasConsumeParticles)
                    .addEffects(consumeEffects)
                    .build()
    );

    public static Codec<ItemDamageFunction> ITEM_DAMAGE_FUNCTION = RecordCodecBuilder.create(
            Codecs.FLOAT.fieldOf("threshold", ItemDamageFunction::threshold),
            Codecs.FLOAT.fieldOf("base", ItemDamageFunction::base),
            Codecs.FLOAT.fieldOf("factor", ItemDamageFunction::factor),
            (threshold, base, factor) -> ItemDamageFunction.itemDamageFunction()
                    .threshold(threshold)
                    .base(base)
                    .factor(factor)
                    .build()
    );
    public static Codec<DamageReduction> DAMAGE_REDUCTION = RecordCodecBuilder.create(
            DAMAGE_TYPE_KEYS.fieldOf("type", DamageReduction::type),
            Codecs.FLOAT.fieldOf("horizontal_blocking_angle", DamageReduction::horizontalBlockingAngle),
            Codecs.FLOAT.fieldOf("base", DamageReduction::base),
            Codecs.FLOAT.fieldOf("factor", DamageReduction::factor),
            (type, horizontalBlockingAngle, base, factor) -> DamageReduction.damageReduction()
                    .type(type)
                    .horizontalBlockingAngle(horizontalBlockingAngle)
                    .base(base)
                    .factor(factor)
                    .build()
    );
    public static Codec<BlocksAttacks> BLOCKS_ATTACKS = RecordCodecBuilder.create(
            Codecs.FLOAT.fieldOf("block_delay_seconds", BlocksAttacks::blockDelaySeconds),
            Codecs.FLOAT.fieldOf("disable_cooldown_scale", BlocksAttacks::disableCooldownScale),
            DAMAGE_REDUCTION.list().fieldOf("damage_reductions", BlocksAttacks::damageReductions),
            ITEM_DAMAGE_FUNCTION.fieldOf("item_damage", BlocksAttacks::itemDamage),
            DAMAGE_TYPE.nullable().fieldOf("bypassed_by", b -> {
                TagKey<DamageType> bypassedBy = b.bypassedBy();
                if (bypassedBy == null) return null;
                else return RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE).getOrThrow(bypassedBy.key());
            }),
            Codecs.KEY.nullable().fieldOf("block_sound", BlocksAttacks::blockSound),
            Codecs.KEY.nullable().fieldOf("disable_sound", BlocksAttacks::disableSound),
            (blockDelaySeconds, disableCooldownScale, damageReductions, itemDamage, bypassedBy,
             blockSound, disableSound) -> {
                BlocksAttacks.Builder builder = BlocksAttacks.blocksAttacks()
                        .blockDelaySeconds(blockDelaySeconds)
                        .disableCooldownScale(disableCooldownScale)
                        .damageReductions(damageReductions)
                        .itemDamage(itemDamage)
                        .blockSound(blockSound)
                        .disableSound(disableSound);
                if (bypassedBy != null) builder.bypassedBy(TagKey.create(RegistryKey.DAMAGE_TYPE, bypassedBy.key()));
                return builder.build();
            }
    );

    public static Codec<EquipmentSlotGroup> EQUIPMENT_SLOT_GROUP = Codecs.STRING.xmap(
            EquipmentSlotGroup::getByName,
            EquipmentSlotGroup::toString
    );
    public static Codec<Attribute> ATTRIBUTE = Codecs.KEY.xmap(
            RegistryAccess.registryAccess().getRegistry(RegistryKey.ATTRIBUTE)::getOrThrow,
            RegistryAccess.registryAccess().getRegistry(RegistryKey.ATTRIBUTE)::getKeyOrThrow
    );
    public static Codec<AttributeModifier> ATTRIBUTE_MODIFIER = RecordCodecBuilder.create(
            Codecs.NAMESPACED_KEY.fieldOf("key", AttributeModifier::getKey),
            Codecs.DOUBLE.fieldOf("amount", AttributeModifier::getAmount),
            Codec.enumCodec(AttributeModifier.Operation.class).fieldOf("operation", AttributeModifier::getOperation),
            EQUIPMENT_SLOT_GROUP.fieldOf("slot", AttributeModifier::getSlotGroup),
            AttributeModifier::new
    );
    public static final Codec<ItemAttributeModifiers> ITEM_ATTRIBUTE_MODIFIERS = Codec.map(ATTRIBUTE, Codec.map(ATTRIBUTE_MODIFIER, EQUIPMENT_SLOT_GROUP)).xmap(
            map -> {
                ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.itemAttributes();
                map.forEach((attribute, inner) ->
                        inner.forEach((modifier, group) ->
                                builder.addModifier(attribute, modifier, group)
                        )
                );
                return builder.build();
            },
            modifiers -> {
                Map<Attribute, Map<AttributeModifier, EquipmentSlotGroup>> map = new LinkedHashMap<>();
                for (ItemAttributeModifiers.Entry e : modifiers.modifiers()) {
                    map.computeIfAbsent(e.attribute(), k -> new LinkedHashMap<>())
                            .put(e.modifier(), e.getGroup());
                }
                return map;
            }
    );
}
