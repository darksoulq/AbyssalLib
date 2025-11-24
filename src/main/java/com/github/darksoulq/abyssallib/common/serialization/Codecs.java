package com.github.darksoulq.abyssallib.common.serialization;

import com.github.darksoulq.abyssallib.common.util.TextUtil;
import com.github.darksoulq.abyssallib.server.bridge.ItemBridge;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.potion.PotionMix;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.inventory.*;
import org.bukkit.inventory.recipe.CookingBookCategory;
import org.bukkit.inventory.recipe.CraftingBookCategory;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Codecs {
    public static final Codec<String> STRING = new Codec<>() {
        @Override
        public <D> String decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
            return ops.getStringValue(input).orElseThrow(() -> new Codec.CodecException("Expected string"));
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, String value) {
            return ops.createString(value);
        }
    };
    public static final Codec<Character> CHARACTER = STRING.xmap((str) -> str.charAt(0), String::valueOf);
    public static final Codec<Integer> INT = new Codec<>() {
        @Override
        public <D> Integer decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
            return ops.getIntValue(input).orElseThrow(() -> new Codec.CodecException("Expected integer"));
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, Integer value) {
            return ops.createInt(value);
        }
    };
    public static final Codec<Long> LONG = new Codec<>() {
        @Override
        public <D> Long decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
            return ops.getLongValue(input).orElseThrow(() -> new Codec.CodecException("Expected long"));
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, Long value) {
            return ops.createLong(value);
        }
    };
    public static final Codec<Double> DOUBLE = new Codec<>() {
        @Override
        public <D> Double decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
            return ops.getDoubleValue(input).orElseThrow(() -> new Codec.CodecException("Expected double"));
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, Double value) {
            return ops.createDouble(value);
        }
    };
    public static final Codec<Float> FLOAT = new Codec<>() {
        @Override
        public <D> Float decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
            return ops.getFloatValue(input).orElseThrow(() -> new Codec.CodecException("Expected float"));
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, Float value) {
            return ops.createFloat(value);
        }
    };
    public static final Codec<Boolean> BOOLEAN = new Codec<>() {
        @Override
        public <D> Boolean decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
            return ops.getBooleanValue(input).orElseThrow(() -> new Codec.CodecException("Expected boolean"));
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, Boolean value) {
            return ops.createBoolean(value);
        }
    };

    // Extra
    public static final Codec<UUID> UUID = Codecs.STRING.xmap(
            java.util.UUID::fromString,
            java.util.UUID::toString
    );
    public static final Codec<Component> TEXT_COMPONENT = Codecs.STRING.xmap(
            MiniMessage.miniMessage()::deserialize,
            MiniMessage.miniMessage()::serialize
    );
    public static final Codec<Key> KEY = STRING.xmap(Key::key, Key::asString);
    public static final Codec<NamespacedKey> NAMESPACED_KEY = STRING.xmap(NamespacedKey::fromString, NamespacedKey::toString);
    public static final Codec<World> WORLD = KEY.xmap(Bukkit::getWorld, World::getKey);
    public static final Codec<Location> LOCATION = RecordCodecBuilder.create(
            WORLD.fieldOf("world", Location::getWorld),
            DOUBLE.fieldOf("x", Location::getX),
            DOUBLE.fieldOf("y", Location::getY),
            DOUBLE.fieldOf("z", Location::getZ),
            FLOAT.fieldOf("yaw", Location::getYaw),
            FLOAT.fieldOf("pitch", Location::getPitch),
            Location::new
    );
    public static final Codec<DataComponentType> DATA_COMPONENT_TYPE = KEY.xmap(
            RegistryAccess.registryAccess().getRegistry(RegistryKey.DATA_COMPONENT_TYPE)::getOrThrow,
            RegistryAccess.registryAccess().getRegistry(RegistryKey.DATA_COMPONENT_TYPE)::getKeyOrThrow
    );
    public static final Codec<ItemStack> ITEM_STACK = Codec.either(
            STRING.xmap(
                    (s) -> {
                        if (ItemBridge.hasProvider(s)) {
                            throw new Codec.CodecException("Provider exists, Not Exprcted");
                        }
                        return ItemBridge.get(s);
                    },
                    (i) -> {
                        if (ItemBridge.hasProvider(i))
                            throw new Codec.CodecException("Provider exists, Not Expected.");
                        return ItemBridge.asString(i);
                    }
            ),
            Codec.map(STRING, INT).xmap(
                    (m) -> {
                        if (m.size() != 1)
                            throw new Codec.CodecException("Expected single-entry map for item, got: " + m);

                        Map.Entry<String, Integer> entry = m.entrySet().iterator().next();
                        String key = entry.getKey();
                        int amount = entry.getValue();

                        if (!ItemBridge.hasProvider(key))
                            if (!key.contains(":")) {
                                throw new Codec.CodecException("Not a valid namespaced key: " + key);
                            } else {
                                throw new Codec.CodecException("No Provider was found for: " + key);
                            }

                        ItemStack item = ItemBridge.get(key);
                        if (item == null) throw new Codec.CodecException("ItemBridge failed to get ItemStack for key: " + key);
                        item.setAmount(amount);
                        return item;
                    },
                    ItemBridge::asAmountMap
            )
    );
    public static final Codec<RecipeChoice.ExactChoice> EXACT_CHOICE = ITEM_STACK.list()
            .xmap(RecipeChoice.ExactChoice::new, RecipeChoice.ExactChoice::getChoices);
    public static final Codec<RecipeChoice.MaterialChoice> MATERIAL_CHOICE = ITEM_STACK.list().xmap(
                        (list) -> new RecipeChoice.MaterialChoice(list.stream().map(ItemStack::getType).toList()),
                        (mats) -> mats.getChoices().stream().map(ItemStack::of).toList());
    public static final Codec<RecipeChoice> RECIPE_CHOICE = Codec.either(EXACT_CHOICE, MATERIAL_CHOICE)
                .xmap((o) -> o, (r) -> r);

    public static final Codec<ShapedRecipe> SHAPED_RECIPE = RecordCodecBuilder.create(
            NAMESPACED_KEY.fieldOf("id", ShapedRecipe::getKey),
            STRING.list().fieldOf("shape", o -> TextUtil.convertToList(o.getShape())),
            Codec.map(CHARACTER, RECIPE_CHOICE).fieldOf("ingredients", ShapedRecipe::getChoiceMap),
            ITEM_STACK.fieldOf("result", ShapedRecipe::getResult),
            STRING.optional().fieldOf("group", (s) -> Optional.of(s.getGroup())),
            Codec.enumCodec(CraftingBookCategory.class).optional().fieldOf("category", (s) ->
                    Optional.of(s.getCategory())),
            (id, shape, ing, result,
             group, category) -> {
                ShapedRecipe recipe = new ShapedRecipe(id, result);
                recipe.shape(TextUtil.convertToArray(shape));
                ing.forEach(recipe::setIngredient);
                group.ifPresent(recipe::setGroup);
                category.ifPresent(recipe::setCategory);
                return recipe;
            }
    );
    public static final Codec<ShapelessRecipe> SHAPELESS_RECIPE = RecordCodecBuilder.create(
            NAMESPACED_KEY.fieldOf("id", ShapelessRecipe::getKey),
            RECIPE_CHOICE.list().fieldOf("ingredients", ShapelessRecipe::getChoiceList),
            ITEM_STACK.fieldOf("result", ShapelessRecipe::getResult),
            STRING.optional().fieldOf("group", (s) -> Optional.of(s.getGroup())),
            Codec.enumCodec(CraftingBookCategory.class).optional().fieldOf("category", (s) ->
                    Optional.of(s.getCategory())),
            (id, choices, result, group, cat) -> {
                ShapelessRecipe recipe = new ShapelessRecipe(id, result);
                choices.forEach(recipe::addIngredient);
                group.ifPresent(recipe::setGroup);
                cat.ifPresent(recipe::setCategory);
                return recipe;
            }
    );
    public static final Codec<TransmuteRecipe> TRANSMUTE_RECIPE = RecordCodecBuilder.create(
            NAMESPACED_KEY.fieldOf("id", TransmuteRecipe::getKey),
            RECIPE_CHOICE.fieldOf("input", TransmuteRecipe::getInput),
            RECIPE_CHOICE.fieldOf("material", TransmuteRecipe::getMaterial),
            ITEM_STACK.fieldOf("result", TransmuteRecipe::getResult),
            STRING.optional().fieldOf("group", (s) -> Optional.of(s.getGroup())),
            Codec.enumCodec(CraftingBookCategory.class).optional().fieldOf("category", (s) ->
                    Optional.of(s.getCategory())),
            (id, input, material, result, group, cat) -> {
                TransmuteRecipe recipe = new TransmuteRecipe(id, result.getType(), input, material);
                group.ifPresent(recipe::setGroup);
                cat.ifPresent(recipe::setCategory);
                return recipe;
            }
    );
    public static final Codec<FurnaceRecipe> FURNACE_RECIPE = RecordCodecBuilder.create(
            NAMESPACED_KEY.fieldOf("id", FurnaceRecipe::getKey),
            RECIPE_CHOICE.fieldOf("input", FurnaceRecipe::getInputChoice),
            ITEM_STACK.fieldOf("result", FurnaceRecipe::getResult),
            INT.fieldOf("cooking_time", FurnaceRecipe::getCookingTime),
            FLOAT.fieldOf("exp", FurnaceRecipe::getExperience),
            STRING.optional().fieldOf("group", (f) -> Optional.of(f.getGroup())),
            Codec.enumCodec(CookingBookCategory.class).optional().fieldOf("category", (f) ->
                    Optional.of(f.getCategory())),
            (id, input, result, time, exp,
             group, cat) -> {
                FurnaceRecipe recipe = new FurnaceRecipe(id, result, input, exp, time);
                cat.ifPresent(recipe::setCategory);
                return recipe;
            }
    );
    public static final Codec<SmokingRecipe> SMOKING_RECIPE = RecordCodecBuilder.create(
            NAMESPACED_KEY.fieldOf("id", SmokingRecipe::getKey),
            RECIPE_CHOICE.fieldOf("input", SmokingRecipe::getInputChoice),
            ITEM_STACK.fieldOf("result", SmokingRecipe::getResult),
            INT.fieldOf("cooking_time", SmokingRecipe::getCookingTime),
            FLOAT.fieldOf("exp", SmokingRecipe::getExperience),
            STRING.optional().fieldOf("group", (f) -> Optional.of(f.getGroup())),
            Codec.enumCodec(CookingBookCategory.class).optional().fieldOf("category", (f) ->
                    Optional.of(f.getCategory())),
            (id, input, result, time, exp,
             group, cat) -> {
                SmokingRecipe recipe = new SmokingRecipe(id, result, input, exp, time);
                cat.ifPresent(recipe::setCategory);
                return recipe;
            }
    );
    public static final Codec<BlastingRecipe> BLASTING_RECIPE = RecordCodecBuilder.create(
            NAMESPACED_KEY.fieldOf("id", BlastingRecipe::getKey),
            RECIPE_CHOICE.fieldOf("input", BlastingRecipe::getInputChoice),
            ITEM_STACK.fieldOf("result", BlastingRecipe::getResult),
            INT.fieldOf("cooking_time", BlastingRecipe::getCookingTime),
            FLOAT.fieldOf("exp", BlastingRecipe::getExperience),
            STRING.optional().fieldOf("group", (f) -> Optional.of(f.getGroup())),
            Codec.enumCodec(CookingBookCategory.class).optional().fieldOf("category", (f) ->
                    Optional.of(f.getCategory())),
            (id, input, result, time, exp,
             group, cat) -> {
                BlastingRecipe recipe = new BlastingRecipe(id, result, input, exp, time);
                cat.ifPresent(recipe::setCategory);
                return recipe;
            }
    );
    public static final Codec<CampfireRecipe> CAMPFIRE_RECIPE = RecordCodecBuilder.create(
            NAMESPACED_KEY.fieldOf("id", CampfireRecipe::getKey),
            RECIPE_CHOICE.fieldOf("input", CampfireRecipe::getInputChoice),
            ITEM_STACK.fieldOf("result", CampfireRecipe::getResult),
            INT.fieldOf("cooking_time", CampfireRecipe::getCookingTime),
            FLOAT.fieldOf("exp", CampfireRecipe::getExperience),
            STRING.optional().fieldOf("group", (f) -> Optional.of(f.getGroup())),
            Codec.enumCodec(CookingBookCategory.class).optional().fieldOf("category", (f) ->
                    Optional.of(f.getCategory())),
            (id, input, result, time, exp,
             group, cat) -> {
                CampfireRecipe recipe = new CampfireRecipe(id, result, input, exp, time);
                cat.ifPresent(recipe::setCategory);
                return recipe;
            }
    );
    public static final Codec<StonecuttingRecipe> STONECUTTING_RECIPE = RecordCodecBuilder.create(
            NAMESPACED_KEY.fieldOf("id", StonecuttingRecipe::getKey),
            RECIPE_CHOICE.fieldOf("input", StonecuttingRecipe::getInputChoice),
            ITEM_STACK.fieldOf("result", StonecuttingRecipe::getResult),
            STRING.optional().fieldOf("group", (f) -> Optional.of(f.getGroup())),
            (id, input, result, group) -> {
                StonecuttingRecipe recipe = new StonecuttingRecipe(id, result, input);
                group.ifPresent(recipe::setGroup);
                return recipe;
            }
    );
    public static final Codec<SmithingTransformRecipe> SMITHING_TRANSFORM_RECIPE = RecordCodecBuilder.create(
            NAMESPACED_KEY.fieldOf("id", SmithingTransformRecipe::getKey),
            RECIPE_CHOICE.fieldOf("base", SmithingTransformRecipe::getBase),
            RECIPE_CHOICE.fieldOf("template", SmithingTransformRecipe::getTemplate),
            RECIPE_CHOICE.fieldOf("addition", SmithingTransformRecipe::getAddition),
            ITEM_STACK.fieldOf("result", SmithingTransformRecipe::getResult),
            BOOLEAN.optional().fieldOf("copy_components", r ->
                    Optional.of(r.willCopyDataComponents())),
            (id, base, template, addition,
             result, copy) -> copy.map(aBoolean ->
                    new SmithingTransformRecipe(id, result, template, base, addition, aBoolean)).orElseGet(() -> new SmithingTransformRecipe(id, result, template, base, addition))
    );
    public static final Codec<PotionMix> POTION_MIX = RecordCodecBuilder.create(
            NAMESPACED_KEY.fieldOf("id", PotionMix::getKey),
            RECIPE_CHOICE.fieldOf("input", PotionMix::getInput),
            RECIPE_CHOICE.fieldOf("ingredient", PotionMix::getIngredient),
            ITEM_STACK.fieldOf("result", PotionMix::getResult),
            (id, input, ingredient, result) -> {
                return new PotionMix(id, result, input, ingredient);
            }
    );
}
