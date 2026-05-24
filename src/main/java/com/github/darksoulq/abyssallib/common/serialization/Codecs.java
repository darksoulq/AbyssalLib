package com.github.darksoulq.abyssallib.common.serialization;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.util.TextUtil;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.github.darksoulq.abyssallib.world.item.component.ComponentMap;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.builtin.CustomData;
import com.github.darksoulq.abyssallib.world.recipe.type.*;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.potion.PotionMix;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.*;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.*;
import org.bukkit.inventory.recipe.CookingBookCategory;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

public class Codecs {
    public static final Codec<Object> PASSTHROUGH = new Codec<>() {
        @Override
        public <D> Object decode(DynamicOps<D> ops, D input) {
            return input;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <D> D encode(DynamicOps<D> ops, Object value) {
            return (D) value;
        }
    };

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
    public static final Codec<Byte> BYTE = INT.xmap(Integer::byteValue, Byte::intValue);
    public static final Codec<Color> COLOR = INT.xmap(Color::fromARGB, Color::asARGB);

    public static final Codec<Vector3f> VECTOR3F = new Codec<>() {
        @Override
        public <D> Vector3f decode(DynamicOps<D> ops, D input) throws CodecException {
            List<D> list = ops.getList(input).orElseThrow(() -> new CodecException("Expected List for Vector3f"));
            return new Vector3f(
                ops.getFloatValue(list.get(0)).orElseThrow(),
                ops.getFloatValue(list.get(1)).orElseThrow(),
                ops.getFloatValue(list.get(2)).orElseThrow()
            );
        }
        @Override
        public <D> D encode(DynamicOps<D> ops, Vector3f value) throws CodecException {
            return ops.createList(List.of(
                ops.createFloat(value.x()), ops.createFloat(value.y()), ops.createFloat(value.z())
            ));
        }
    };

    public static final Codec<Quaternionf> QUATERNIONF = new Codec<>() {
        @Override
        public <D> Quaternionf decode(DynamicOps<D> ops, D input) throws CodecException {
            List<D> list = ops.getList(input).orElseThrow(() -> new CodecException("Expected List for Quaternionf"));
            return new Quaternionf(
                ops.getFloatValue(list.get(0)).orElseThrow(),
                ops.getFloatValue(list.get(1)).orElseThrow(),
                ops.getFloatValue(list.get(2)).orElseThrow(),
                ops.getFloatValue(list.get(3)).orElseThrow()
            );
        }
        @Override
        public <D> D encode(DynamicOps<D> ops, Quaternionf value) throws CodecException {
            return ops.createList(List.of(
                ops.createFloat(value.x()), ops.createFloat(value.y()),
                ops.createFloat(value.z()), ops.createFloat(value.w())
            ));
        }
    };

    public static final Codec<Matrix4f> MATRIX4F = new Codec<>() {
        @Override
        public <D> Matrix4f decode(DynamicOps<D> ops, D input) throws CodecException {
            List<D> list = ops.getList(input).orElseThrow(() -> new CodecException("Expected List for Matrix4f"));
            return new Matrix4f(
                ops.getFloatValue(list.get(0)).orElseThrow(), ops.getFloatValue(list.get(1)).orElseThrow(), ops.getFloatValue(list.get(2)).orElseThrow(), ops.getFloatValue(list.get(3)).orElseThrow(),
                ops.getFloatValue(list.get(4)).orElseThrow(), ops.getFloatValue(list.get(5)).orElseThrow(), ops.getFloatValue(list.get(6)).orElseThrow(), ops.getFloatValue(list.get(7)).orElseThrow(),
                ops.getFloatValue(list.get(8)).orElseThrow(), ops.getFloatValue(list.get(9)).orElseThrow(), ops.getFloatValue(list.get(10)).orElseThrow(), ops.getFloatValue(list.get(11)).orElseThrow(),
                ops.getFloatValue(list.get(12)).orElseThrow(), ops.getFloatValue(list.get(13)).orElseThrow(), ops.getFloatValue(list.get(14)).orElseThrow(), ops.getFloatValue(list.get(15)).orElseThrow()
            );
        }
        @Override
        public <D> D encode(DynamicOps<D> ops, Matrix4f value) throws CodecException {
            return ops.createList(List.of(
                ops.createFloat(value.m00()), ops.createFloat(value.m01()), ops.createFloat(value.m02()), ops.createFloat(value.m03()),
                ops.createFloat(value.m10()), ops.createFloat(value.m11()), ops.createFloat(value.m12()), ops.createFloat(value.m13()),
                ops.createFloat(value.m20()), ops.createFloat(value.m21()), ops.createFloat(value.m22()), ops.createFloat(value.m23()),
                ops.createFloat(value.m30()), ops.createFloat(value.m31()), ops.createFloat(value.m32()), ops.createFloat(value.m33())
            ));
        }
    };

    public static final Codec<Transformation> TRANSFORMATION = RecordCodecBuilder.create(
        VECTOR3F.fieldOf("translation", Transformation::getTranslation),
        QUATERNIONF.fieldOf("left_rotation", Transformation::getLeftRotation),
        VECTOR3F.fieldOf("scale", Transformation::getScale),
        QUATERNIONF.fieldOf("right_rotation", Transformation::getRightRotation),
        Transformation::new
    );

    public static final Codec<Display.Brightness> DISPLAY_BRIGHTNESS = RecordCodecBuilder.create(
        INT.fieldOf("block_light", Display.Brightness::getBlockLight),
        INT.fieldOf("sky_light", Display.Brightness::getSkyLight),
        Display.Brightness::new
    );

    public static final Codec<Display.Billboard> BILLBOARD = Codec.enumCodec(Display.Billboard.class);
    public static final Codec<ItemDisplay.ItemDisplayTransform> ITEM_DISPLAY_TRANSFORM = Codec.enumCodec(ItemDisplay.ItemDisplayTransform.class);
    public static final Codec<TextDisplay.TextAlignment> TEXT_ALIGNMENT = Codec.enumCodec(TextDisplay.TextAlignment.class);

    public static final Codec<Vector> VECTOR_I = new Codec<>() {
        @Override
        public <D> Vector decode(DynamicOps<D> ops, D input) throws CodecException {
            List<D> vector = ops.getList(input).orElseThrow();
            if (vector.size() != 3) throw new CodecException("Vector list size should be 3");
            int x = ops.getIntValue(vector.getFirst()).orElseThrow();
            int y = ops.getIntValue(vector.get(1)).orElseThrow();
            int z = ops.getIntValue(vector.getLast()).orElseThrow();
            return new Vector(x, y, z);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, Vector value) throws CodecException {
            List<D> vector = new LinkedList<>();
            vector.add(ops.createFloat(value.getBlockX()));
            vector.add(ops.createInt(value.getBlockY()));
            vector.add(ops.createInt(value.getBlockZ()));
            return ops.createList(vector);
        }
    };
    public static final Codec<Vector> VECTOR_F = new Codec<>() {
        @Override
        public <D> Vector decode(DynamicOps<D> ops, D input) throws CodecException {
            List<D> vector = ops.getList(input).orElseThrow();
            if (vector.size() != 3) throw new CodecException("Vector list size should be 3");
            double x = ops.getDoubleValue(vector.getFirst()).orElseThrow();
            double y = ops.getDoubleValue(vector.get(1)).orElseThrow();
            double z = ops.getDoubleValue(vector.getLast()).orElseThrow();
            return new Vector(x, y, z);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, Vector value) throws CodecException {
            List<D> vector = new LinkedList<>();
            vector.add(ops.createDouble(value.getX()));
            vector.add(ops.createDouble(value.getY()));
            vector.add(ops.createDouble(value.getZ()));
            return ops.createList(vector);
        }
    };
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
        key -> RegistryAccess.registryAccess().getRegistry(RegistryKey.DATA_COMPONENT_TYPE).getOrThrow(key),
        type -> RegistryAccess.registryAccess().getRegistry(RegistryKey.DATA_COMPONENT_TYPE).getKeyOrThrow(type)
    );
    public static final Codec<List<DataComponent<?>>> DATA_COMPONENT_MAP = new Codec<>() {
        @Override
        public <D> List<DataComponent<?>> decode(DynamicOps<D> ops, D input) throws CodecException {
            if (input == null) throw new CodecException("Expected Map");
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected Map"));
            List<DataComponent<?>> components = new ArrayList<>();
            for (Map.Entry<D, D> entry : map.entrySet()) {
                String componentID = STRING.decode(ops, entry.getKey());
                com.github.darksoulq.abyssallib.world.item.component.DataComponentType<?> type = Registries.DATA_COMPONENT_TYPES.get(componentID);
                if (type == null) {
                    AbyssalLib.getInstance().getLogger().warning("Failed to load component with ID: " + componentID + ", The component does not exist");
                    continue;
                }
                DataComponent<?> component = type.codec().decode(ops, entry.getValue());
                components.add(component);
            }
            return components;
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, List<DataComponent<?>> value) throws CodecException {
            if (value == null) throw new CodecException("Expected Map");
            Map<D, D> map = new HashMap<>();
            for (DataComponent<?> component : value) {
                String id = Registries.DATA_COMPONENT_TYPES.getId(component.getType());
                if (id == null) {
                    AbyssalLib.getInstance().getLogger().warning("Failed to serialize DataComponent as its ID is null");
                    continue;
                }

                if (component instanceof CustomData customData) {
                    CompoundTag tag = customData.getValue().copy();
                    tag.remove("CustomData");
                    if (tag.isEmpty()) continue;
                }

                map.put(STRING.encode(ops, id), ComponentMap.encodeComponent(component, ops));
            }
            return ops.createMap(map);
        }
    };
    public static final Codec<ItemStack> ITEM_STACK = Codec.fallback(
        new Codec<>() {
            @Override
            public <D> ItemStack decode(DynamicOps<D> ops, D input) throws CodecException {
                Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected Map"));
                Key itemID = KEY.decode(ops, map.get(ops.createString("id")));
                Integer amount = INT.nullable().decode(ops, map.get(ops.createString("amount")));
                List<DataComponent<?>> components = DATA_COMPONENT_MAP.nullable().decode(ops, map.get(ops.createString("data")));
                Item item;
                if ("minecraft".equals(itemID.namespace()))
                    item = new Item(new ItemStack(Material.valueOf(itemID.value().toUpperCase())));
                else {
                    Item base = Registries.ITEMS.get(itemID.asString());
                    if (base == null)
                        throw new CodecException("Failed to load item, item ID: " + itemID.asString() + ", does not exist");
                    item = base.clone();
                }
                if (amount != null) item.getStack().setAmount(amount);
                if (components != null && !components.isEmpty()) {
                    components.forEach(item::setData);
                }
                return item.getStack();
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, ItemStack value) throws CodecException {
                Map<D, D> map = new HashMap<>();
                Item item = new Item(value);
                int amount = value.getAmount();
                List<DataComponent<?>> components = item.getComponentMap().getAllComponents();
                map.put(ops.createString("id"), KEY.encode(ops, item.getId()));
                if (amount > 1) map.put(ops.createString("amount"), ops.createInt(amount));
                if (components != null && !components.isEmpty()) {
                    boolean hasValidComponents = false;
                    for (DataComponent<?> c : components) {
                        if (c instanceof CustomData cd) {
                            CompoundTag tag = cd.getValue().copy();
                            tag.remove("CustomData");
                            if (!tag.isEmpty()) hasValidComponents = true;
                        } else {
                            hasValidComponents = true;
                        }
                    }
                    if (hasValidComponents) {
                        map.put(ops.createString("data"), DATA_COMPONENT_MAP.encode(ops, components));
                    }
                }
                return ops.createMap(map);
            }
        },
        KEY.xmap(
            key -> {
                if (!NamespacedKey.MINECRAFT.equals(key.namespace())) return Registries.ITEMS.get(key.asString()).clone().getStack();
                else return new ItemStack(Material.valueOf(key.value().toUpperCase()));
            },
            stack -> {
                Item item = new Item(stack);
                return item.getId();
            }
        )
    );
    public static final Codec<RecipeChoice.ExactChoice> EXACT_CHOICE = ITEM_STACK.list()
        .xmap(RecipeChoice.ExactChoice::new, RecipeChoice.ExactChoice::getChoices);
    public static final Codec<RecipeChoice.MaterialChoice> MATERIAL_CHOICE = ITEM_STACK.list().xmap(
        (list) -> new RecipeChoice.MaterialChoice(list.stream().map(ItemStack::getType).toList()),
        (mats) -> mats.getChoices().stream().map(ItemStack::of).toList());
    public static final Codec<RecipeChoice> RECIPE_CHOICE = Codec.fallback(EXACT_CHOICE, MATERIAL_CHOICE);

    public static final Codec<CustomShapedRecipe> SHAPED_RECIPE = RecordCodecBuilder.create(
        NAMESPACED_KEY.fieldOf("id", CustomShapedRecipe::getKey),
        STRING.list().fieldOf("shape", CustomShapedRecipe::getShape),
        Codec.map(CHARACTER, RECIPE_CHOICE).fieldOf("ingredients", CustomShapedRecipe::getIngredients),
        ITEM_STACK.fieldOf("result", CustomShapedRecipe::getResult),
        STRING.optional().fieldOf("group", CustomShapedRecipe::getGroup),
        Codec.enumCodec(CraftingBookCategory.class).optional().fieldOf("category", CustomShapedRecipe::getCategory),
        BOOLEAN.optional().fieldOf("replace", r -> Optional.of(r.replace())),
        (id, shape, ing, result, group, cat, replace) -> new CustomShapedRecipe(id, shape, ing, result, group, cat, replace.orElse(false))
    );

    public static final Codec<CustomShapelessRecipe> SHAPELESS_RECIPE = RecordCodecBuilder.create(
        NAMESPACED_KEY.fieldOf("id", CustomShapelessRecipe::getKey),
        RECIPE_CHOICE.list().fieldOf("ingredients", CustomShapelessRecipe::getIngredients),
        ITEM_STACK.fieldOf("result", CustomShapelessRecipe::getResult),
        STRING.optional().fieldOf("group", CustomShapelessRecipe::getGroup),
        Codec.enumCodec(CraftingBookCategory.class).optional().fieldOf("category", CustomShapelessRecipe::getCategory),
        BOOLEAN.optional().fieldOf("replace", r -> Optional.of(r.replace())),
        (id, ing, result, group, cat, replace) -> new CustomShapelessRecipe(id, ing, result, group, cat, replace.orElse(false))
    );

    public static final Codec<CustomTransmuteRecipe> TRANSMUTE_RECIPE = RecordCodecBuilder.create(
        NAMESPACED_KEY.fieldOf("id", CustomTransmuteRecipe::getKey),
        RECIPE_CHOICE.fieldOf("input", CustomTransmuteRecipe::getInput),
        RECIPE_CHOICE.fieldOf("material", CustomTransmuteRecipe::getMaterial),
        ITEM_STACK.fieldOf("result", CustomTransmuteRecipe::getResult),
        STRING.optional().fieldOf("group", CustomTransmuteRecipe::getGroup),
        Codec.enumCodec(CraftingBookCategory.class).optional().fieldOf("category", CustomTransmuteRecipe::getCategory),
        BOOLEAN.optional().fieldOf("replace", r -> Optional.of(r.replace())),
        (id, input, mat, result, group, cat, replace) -> new CustomTransmuteRecipe(id, input, mat, result, group, cat, replace.orElse(false))
    );

    public static final Codec<CustomFurnaceRecipe> FURNACE_RECIPE = RecordCodecBuilder.create(
        NAMESPACED_KEY.fieldOf("id", CustomFurnaceRecipe::getKey),
        RECIPE_CHOICE.fieldOf("input", CustomFurnaceRecipe::getInput),
        ITEM_STACK.fieldOf("result", CustomFurnaceRecipe::getResult),
        INT.fieldOf("cooking_time", CustomFurnaceRecipe::getCookingTime),
        FLOAT.fieldOf("exp", CustomFurnaceRecipe::getExp),
        STRING.optional().fieldOf("group", CustomFurnaceRecipe::getGroup),
        Codec.enumCodec(CookingBookCategory.class).optional().fieldOf("category", CustomFurnaceRecipe::getCategory),
        BOOLEAN.optional().fieldOf("replace", r -> Optional.of(r.replace())),
        (id, input, result, time, exp, group, cat, replace) -> new CustomFurnaceRecipe(id, input, result, time, exp, group, cat, replace.orElse(false))
    );

    public static final Codec<CustomSmokingRecipe> SMOKING_RECIPE = RecordCodecBuilder.create(
        NAMESPACED_KEY.fieldOf("id", CustomSmokingRecipe::getKey),
        RECIPE_CHOICE.fieldOf("input", CustomSmokingRecipe::getInput),
        ITEM_STACK.fieldOf("result", CustomSmokingRecipe::getResult),
        INT.fieldOf("cooking_time", CustomSmokingRecipe::getCookingTime),
        FLOAT.fieldOf("exp", CustomSmokingRecipe::getExp),
        STRING.optional().fieldOf("group", CustomSmokingRecipe::getGroup),
        Codec.enumCodec(CookingBookCategory.class).optional().fieldOf("category", CustomSmokingRecipe::getCategory),
        BOOLEAN.optional().fieldOf("replace", r -> Optional.of(r.replace())),
        (id, input, result, time, exp, group, cat, replace) -> new CustomSmokingRecipe(id, input, result, time, exp, group, cat, replace.orElse(false))
    );

    public static final Codec<CustomBlastingRecipe> BLASTING_RECIPE = RecordCodecBuilder.create(
        NAMESPACED_KEY.fieldOf("id", CustomBlastingRecipe::getKey),
        RECIPE_CHOICE.fieldOf("input", CustomBlastingRecipe::getInput),
        ITEM_STACK.fieldOf("result", CustomBlastingRecipe::getResult),
        INT.fieldOf("cooking_time", CustomBlastingRecipe::getCookingTime),
        FLOAT.fieldOf("exp", CustomBlastingRecipe::getExp),
        STRING.optional().fieldOf("group", CustomBlastingRecipe::getGroup),
        Codec.enumCodec(CookingBookCategory.class).optional().fieldOf("category", CustomBlastingRecipe::getCategory),
        BOOLEAN.optional().fieldOf("replace", r -> Optional.of(r.replace())),
        (id, input, result, time, exp, group, cat, replace) -> new CustomBlastingRecipe(id, input, result, time, exp, group, cat, replace.orElse(false))
    );

    public static final Codec<CustomCampfireRecipe> CAMPFIRE_RECIPE = RecordCodecBuilder.create(
        NAMESPACED_KEY.fieldOf("id", CustomCampfireRecipe::getKey),
        RECIPE_CHOICE.fieldOf("input", CustomCampfireRecipe::getInput),
        ITEM_STACK.fieldOf("result", CustomCampfireRecipe::getResult),
        INT.fieldOf("cooking_time", CustomCampfireRecipe::getCookingTime),
        FLOAT.fieldOf("exp", CustomCampfireRecipe::getExp),
        STRING.optional().fieldOf("group", CustomCampfireRecipe::getGroup),
        Codec.enumCodec(CookingBookCategory.class).optional().fieldOf("category", CustomCampfireRecipe::getCategory),
        BOOLEAN.optional().fieldOf("replace", r -> Optional.of(r.replace())),
        (id, input, result, time, exp, group, cat, replace) -> new CustomCampfireRecipe(id, input, result, time, exp, group, cat, replace.orElse(false))
    );

    public static final Codec<CustomStonecuttingRecipe> STONECUTTING_RECIPE = RecordCodecBuilder.create(
        NAMESPACED_KEY.fieldOf("id", CustomStonecuttingRecipe::getKey),
        RECIPE_CHOICE.fieldOf("input", CustomStonecuttingRecipe::getInput),
        ITEM_STACK.fieldOf("result", CustomStonecuttingRecipe::getResult),
        STRING.optional().fieldOf("group", CustomStonecuttingRecipe::getGroup),
        BOOLEAN.optional().fieldOf("replace", r -> Optional.of(r.replace())),
        (id, input, result, group, replace) -> new CustomStonecuttingRecipe(id, input, result, group, replace.orElse(false))
    );

    public static final Codec<CustomSmithingTransformRecipe> SMITHING_TRANSFORM_RECIPE = RecordCodecBuilder.create(
        NAMESPACED_KEY.fieldOf("id", CustomSmithingTransformRecipe::getKey),
        RECIPE_CHOICE.fieldOf("base", CustomSmithingTransformRecipe::getBase),
        RECIPE_CHOICE.fieldOf("template", CustomSmithingTransformRecipe::getTemplate),
        RECIPE_CHOICE.fieldOf("addition", CustomSmithingTransformRecipe::getAddition),
        ITEM_STACK.fieldOf("result", CustomSmithingTransformRecipe::getResult),
        BOOLEAN.optional().fieldOf("copy_components", CustomSmithingTransformRecipe::getCopyComponents),
        BOOLEAN.optional().fieldOf("replace", r -> Optional.of(r.replace())),
        (id, base, temp, add, res, copy, replace) -> new CustomSmithingTransformRecipe(id, base, temp, add, res, copy, replace.orElse(false))
    );

    public static final Codec<CustomPotionMix> POTION_MIX = RecordCodecBuilder.create(
        NAMESPACED_KEY.fieldOf("id", CustomPotionMix::getKey),
        RECIPE_CHOICE.fieldOf("input", CustomPotionMix::getInput),
        RECIPE_CHOICE.fieldOf("ingredient", CustomPotionMix::getIngredient),
        ITEM_STACK.fieldOf("result", CustomPotionMix::getResult),
        BOOLEAN.optional().fieldOf("replace", r -> Optional.of(r.replace())),
        (id, input, ing, result, replace) -> new CustomPotionMix(id, input, ing, result, replace.orElse(false))
    );
}