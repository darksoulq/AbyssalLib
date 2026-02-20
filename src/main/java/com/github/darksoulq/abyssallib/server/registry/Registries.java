package com.github.darksoulq.abyssallib.server.registry;

import com.github.darksoulq.abyssallib.common.energy.EnergyNodeType;
import com.github.darksoulq.abyssallib.common.energy.EnergyUnit;
import com.github.darksoulq.abyssallib.server.permission.PermissionGroup;
import com.github.darksoulq.abyssallib.server.permission.PermissionNode;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.data.loot.LootConditionType;
import com.github.darksoulq.abyssallib.world.data.loot.LootFunctionType;
import com.github.darksoulq.abyssallib.world.data.loot.LootTable;
import com.github.darksoulq.abyssallib.world.data.statistic.Statistic;
import com.github.darksoulq.abyssallib.world.data.tag.Tag;
import com.github.darksoulq.abyssallib.world.entity.CustomEntity;
import com.github.darksoulq.abyssallib.world.entity.DamageType;
import com.github.darksoulq.abyssallib.world.gen.feature.Feature;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifierType;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.github.darksoulq.abyssallib.world.item.ItemCategory;
import com.github.darksoulq.abyssallib.world.item.ItemPredicate;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;
import com.github.darksoulq.abyssallib.world.multiblock.Multiblock;
import com.github.darksoulq.abyssallib.world.structure.Structure;
import com.github.darksoulq.abyssallib.world.structure.processor.StructureProcessorType;
import io.papermc.paper.potion.PotionMix;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.*;

public class Registries {
    public static final Registry<EnergyUnit> ENERGY_UNITS = new Registry<>();
    public static final Registry<EnergyNodeType<?>> ENERGY_NODE_TYPES = new Registry<>();

    public static final Registry<DataComponentType<?>> DATA_COMPONENT_TYPES = new Registry<>();
    public static final Registry<Statistic> STATISTICS = new Registry<>();

    public static final Registry<Item> ITEMS = new Registry<>();
    public static final Registry<ItemCategory> ITEM_CATEGORIES = new Registry<>();
    public static final Registry<CustomBlock> BLOCKS = new Registry<>();
    public static final Registry<Multiblock> MULTIBLOCKS = new Registry<>();
    public static final Registry<CustomEntity<? extends LivingEntity>> ENTITIES = new Registry<>();

    public static final Registry<Structure> STRUCTURES = new Registry<>();
    public static final Registry<StructureProcessorType<?>> PROCESSOR_TYPES = new Registry<>();
    public static final Registry<Feature<?>> FEATURES = new Registry<>();
    public static final Registry<PlacementModifierType<?>> PLACEMENT_MODIFIERS = new Registry<>();

    public static final Registry<LootTable> LOOT_TABLES = new Registry<>();
    public static final Registry<LootFunctionType<?>> LOOT_FUNCTIONS = new Registry<>();
    public static final Registry<LootConditionType<?>> LOOT_CONDITIONS = new Registry<>();

    public static final Registry<DamageType> DAMAGE_TYPES = new Registry<>();
    public static final Registry<Tag<?, ?>> TAGS = new Registry<>();
    public static final Registry<ItemPredicate> PREDICATES = new Registry<>();

    public static final Registry<PermissionNode> PERMISSIONS = new Registry<>();
    public static final Registry<PermissionGroup> PERMISSION_GROUPS = new Registry<>();

    public static final Registry<ShapedRecipe> SHAPED_RECIPES = new Registry<>();
    public static final Registry<ShapelessRecipe> SHAPELESS_RECIPES = new Registry<>();
    public static final Registry<TransmuteRecipe> TRANSMUTE_RECIPES = new Registry<>();
    public static final Registry<FurnaceRecipe> FURNACE_RECIPES = new Registry<>();
    public static final Registry<BlastingRecipe> BLASTING_RECIPES = new Registry<>();
    public static final Registry<SmokingRecipe> SMOKING_RECIPES = new Registry<>();
    public static final Registry<CampfireRecipe> CAMPFIRE_RECIPES = new Registry<>();
    public static final Registry<StonecuttingRecipe> STONECUTTING_RECIPES = new Registry<>();
    public static final Registry<SmithingTransformRecipe> SMITHING_TRANSFORM_RECIPES = new Registry<>();
    public static final Registry<PotionMix> POTION_MIXES = new Registry<>();
}