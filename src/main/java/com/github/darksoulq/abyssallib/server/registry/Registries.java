package com.github.darksoulq.abyssallib.server.registry;

import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.data.loot.LootTable;
import com.github.darksoulq.abyssallib.world.data.statistic.Statistic;
import com.github.darksoulq.abyssallib.world.data.tag.Tag;
import com.github.darksoulq.abyssallib.world.entity.DamageType;
import com.github.darksoulq.abyssallib.world.entity.Entity;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.multiblock.Multiblock;
import io.papermc.paper.potion.PotionMix;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.*;

public class Registries {
    public static final Registry<Class<? extends DataComponent<?>>> DATA_COMPONENTS = new Registry<>();
    public static final Registry<Statistic> STATISTICS = new Registry<>();

    public static final Registry<Item> ITEMS = new Registry<>();
    public static final Registry<CustomBlock> BLOCKS = new Registry<>();
    public static final Registry<Multiblock> MULTIBLOCKS = new Registry<>();
    public static final Registry<Entity<? extends LivingEntity>> ENTITIES = new Registry<>();

    public static final Registry<LootTable> LOOT_TABLES = new Registry<>();
    public static final Registry<DamageType> DAMAGE_TYPES = new Registry<>();
    public static final Registry<Tag<?>> TAGS = new Registry<>();

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
