package com.github.darksoulq.abyssallib.common.serialization;

import com.github.darksoulq.abyssallib.common.serialization.schema.SchemaNode;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.item.Item;
import com.github.darksoulq.abyssallib.world.item.component.ComponentMap;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.builtin.CustomData;
import com.github.darksoulq.abyssallib.world.recipe.type.*;
import io.papermc.paper.datacomponent.DataComponentType;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.recipe.CookingBookCategory;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

public class Codecs {

    public static final Codec<Object> PASSTHROUGH = new Codec<Object>() {
        @Override
        public <D> DataResult<Object> decode(DynamicOps<D> ops, D input) {
            return DataResult.success(input);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <D> DataResult<D> encode(DynamicOps<D> ops, Object value) {
            return DataResult.success((D) value);
        }

        @Override
        public String describe() {
            return "Passthrough";
        }
    }.withSchema(new SchemaNode.PrimitiveSchema("Any"));

    public static final Codec<String> STRING = new Codec<String>() {
        @Override
        public <D> DataResult<String> decode(DynamicOps<D> ops, D input) {
            return ops.getStringValue(input)
                .map(DataResult::success)
                .orElseGet(() -> DataResult.error(DataError.typeMismatch("String", "Unknown")));
        }

        @Override
        public <D> DataResult<D> encode(DynamicOps<D> ops, String value) {
            return DataResult.success(ops.createString(value));
        }

        @Override
        public String describe() {
            return "String";
        }
    }.withSchema(new SchemaNode.PrimitiveSchema("String"));

    public static final Codec<Character> CHARACTER = STRING.comapFlatMap(
        str -> str.length() == 1 ? DataResult.success(str.charAt(0)) : DataResult.error(DataError.invalidFormat(str, "Single Character")),
        String::valueOf
    ).describe("Character");

    public static final Codec<Integer> INT = new Codec<Integer>() {
        @Override
        public <D> DataResult<Integer> decode(DynamicOps<D> ops, D input) {
            return ops.getIntValue(input)
                .map(DataResult::success)
                .orElseGet(() -> DataResult.error(DataError.typeMismatch("Integer", "Unknown")));
        }

        @Override
        public <D> DataResult<D> encode(DynamicOps<D> ops, Integer value) {
            return DataResult.success(ops.createInt(value));
        }

        @Override
        public String describe() {
            return "Integer";
        }
    }.withSchema(new SchemaNode.PrimitiveSchema("Integer"));

    public static final Codec<Long> LONG = new Codec<Long>() {
        @Override
        public <D> DataResult<Long> decode(DynamicOps<D> ops, D input) {
            return ops.getLongValue(input)
                .map(DataResult::success)
                .orElseGet(() -> DataResult.error(DataError.typeMismatch("Long", "Unknown")));
        }

        @Override
        public <D> DataResult<D> encode(DynamicOps<D> ops, Long value) {
            return DataResult.success(ops.createLong(value));
        }

        @Override
        public String describe() {
            return "Long";
        }
    }.withSchema(new SchemaNode.PrimitiveSchema("Long"));

    public static final Codec<Double> DOUBLE = new Codec<Double>() {
        @Override
        public <D> DataResult<Double> decode(DynamicOps<D> ops, D input) {
            return ops.getDoubleValue(input)
                .map(DataResult::success)
                .orElseGet(() -> DataResult.error(DataError.typeMismatch("Double", "Unknown")));
        }

        @Override
        public <D> DataResult<D> encode(DynamicOps<D> ops, Double value) {
            return DataResult.success(ops.createDouble(value));
        }

        @Override
        public String describe() {
            return "Double";
        }
    }.withSchema(new SchemaNode.PrimitiveSchema("Double"));

    public static final Codec<Float> FLOAT = new Codec<Float>() {
        @Override
        public <D> DataResult<Float> decode(DynamicOps<D> ops, D input) {
            return ops.getFloatValue(input)
                .map(DataResult::success)
                .orElseGet(() -> DataResult.error(DataError.typeMismatch("Float", "Unknown")));
        }

        @Override
        public <D> DataResult<D> encode(DynamicOps<D> ops, Float value) {
            return DataResult.success(ops.createFloat(value));
        }

        @Override
        public String describe() {
            return "Float";
        }
    }.withSchema(new SchemaNode.PrimitiveSchema("Float"));

    public static final Codec<Boolean> BOOLEAN = new Codec<Boolean>() {
        @Override
        public <D> DataResult<Boolean> decode(DynamicOps<D> ops, D input) {
            return ops.getBooleanValue(input)
                .map(DataResult::success)
                .orElseGet(() -> DataResult.error(DataError.typeMismatch("Boolean", "Unknown")));
        }

        @Override
        public <D> DataResult<D> encode(DynamicOps<D> ops, Boolean value) {
            return DataResult.success(ops.createBoolean(value));
        }

        @Override
        public String describe() {
            return "Boolean";
        }
    }.withSchema(new SchemaNode.PrimitiveSchema("Boolean"));

    public static final Codec<Byte> BYTE = INT.xmap(Integer::byteValue, Byte::intValue).describe("Byte");

    public static final Codec<Color> COLOR = INT.xmap(Color::fromARGB, Color::asARGB).describe("Color");

    public static final Codec<Vector3f> VECTOR3F = new Codec<Vector3f>() {
        @Override
        public <D> DataResult<Vector3f> decode(DynamicOps<D> ops, D input) {
            return ops.getList(input)
                .map(DataResult::success)
                .orElseGet(() -> DataResult.error(DataError.typeMismatch("List", "Unknown")))
                .flatMap(list -> {
                    if (list.size() < 3)
                        return DataResult.error(DataError.custom("Vector3f list requires 3 elements, found " + list.size()));
                    try {
                        return DataResult.success(new Vector3f(
                            ops.getFloatValue(list.get(0)).orElseThrow(),
                            ops.getFloatValue(list.get(1)).orElseThrow(),
                            ops.getFloatValue(list.get(2)).orElseThrow()
                        ));
                    } catch (Exception e) {
                        return DataResult.error(DataError.custom("Malformed float elements in Vector3f list"));
                    }
                });
        }

        @Override
        public <D> DataResult<D> encode(DynamicOps<D> ops, Vector3f value) {
            return DataResult.success(ops.createList(List.of(
                ops.createFloat(value.x()), ops.createFloat(value.y()), ops.createFloat(value.z())
            )));
        }

        @Override
        public String describe() {
            return "Vector3f";
        }
    }.withSchema(new SchemaNode.ListSchema(new SchemaNode.PrimitiveSchema("Float")));

    public static final Codec<Quaternionf> QUATERNIONF = new Codec<Quaternionf>() {
        @Override
        public <D> DataResult<Quaternionf> decode(DynamicOps<D> ops, D input) {
            return ops.getList(input)
                .map(DataResult::success)
                .orElseGet(() -> DataResult.error(DataError.typeMismatch("List", "Unknown")))
                .flatMap(list -> {
                    if (list.size() < 4)
                        return DataResult.error(DataError.custom("Quaternionf list requires 4 elements, found " + list.size()));
                    try {
                        return DataResult.success(new Quaternionf(
                            ops.getFloatValue(list.get(0)).orElseThrow(),
                            ops.getFloatValue(list.get(1)).orElseThrow(),
                            ops.getFloatValue(list.get(2)).orElseThrow(),
                            ops.getFloatValue(list.get(3)).orElseThrow()
                        ));
                    } catch (Exception e) {
                        return DataResult.error(DataError.custom("Malformed float elements in Quaternionf list"));
                    }
                });
        }

        @Override
        public <D> DataResult<D> encode(DynamicOps<D> ops, Quaternionf value) {
            return DataResult.success(ops.createList(List.of(
                ops.createFloat(value.x()), ops.createFloat(value.y()),
                ops.createFloat(value.z()), ops.createFloat(value.w())
            )));
        }

        @Override
        public String describe() {
            return "Quaternionf";
        }
    }.withSchema(new SchemaNode.ListSchema(new SchemaNode.PrimitiveSchema("Float")));

    public static final Codec<Matrix4f> MATRIX4F = new Codec<Matrix4f>() {
        @Override
        public <D> DataResult<Matrix4f> decode(DynamicOps<D> ops, D input) {
            return ops.getList(input)
                .map(DataResult::success)
                .orElseGet(() -> DataResult.error(DataError.typeMismatch("List", "Unknown")))
                .flatMap(list -> {
                    if (list.size() < 16)
                        return DataResult.error(DataError.custom("Matrix4f list requires 16 elements, found " + list.size()));
                    try {
                        return DataResult.success(new Matrix4f(
                            ops.getFloatValue(list.get(0)).orElseThrow(), ops.getFloatValue(list.get(1)).orElseThrow(), ops.getFloatValue(list.get(2)).orElseThrow(), ops.getFloatValue(list.get(3)).orElseThrow(),
                            ops.getFloatValue(list.get(4)).orElseThrow(), ops.getFloatValue(list.get(5)).orElseThrow(), ops.getFloatValue(list.get(6)).orElseThrow(), ops.getFloatValue(list.get(7)).orElseThrow(),
                            ops.getFloatValue(list.get(8)).orElseThrow(), ops.getFloatValue(list.get(9)).orElseThrow(), ops.getFloatValue(list.get(10)).orElseThrow(), ops.getFloatValue(list.get(11)).orElseThrow(),
                            ops.getFloatValue(list.get(12)).orElseThrow(), ops.getFloatValue(list.get(13)).orElseThrow(), ops.getFloatValue(list.get(14)).orElseThrow(), ops.getFloatValue(list.get(15)).orElseThrow()
                        ));
                    } catch (Exception e) {
                        return DataResult.error(DataError.custom("Malformed float elements in Matrix4f list"));
                    }
                });
        }

        @Override
        public <D> DataResult<D> encode(DynamicOps<D> ops, Matrix4f value) {
            return DataResult.success(ops.createList(List.of(
                ops.createFloat(value.m00()), ops.createFloat(value.m01()), ops.createFloat(value.m02()), ops.createFloat(value.m03()),
                ops.createFloat(value.m10()), ops.createFloat(value.m11()), ops.createFloat(value.m12()), ops.createFloat(value.m13()),
                ops.createFloat(value.m20()), ops.createFloat(value.m21()), ops.createFloat(value.m22()), ops.createFloat(value.m23()),
                ops.createFloat(value.m30()), ops.createFloat(value.m31()), ops.createFloat(value.m32()), ops.createFloat(value.m33())
            )));
        }

        @Override
        public String describe() {
            return "Matrix4f";
        }
    }.withSchema(new SchemaNode.ListSchema(new SchemaNode.PrimitiveSchema("Float")));

    public static final Codec<Transformation> TRANSFORMATION = RecordBuilder.create(instance -> instance.group(
        VECTOR3F.fieldOf("translation").forGetter(Transformation::getTranslation),
        QUATERNIONF.fieldOf("left_rotation").forGetter(Transformation::getLeftRotation),
        VECTOR3F.fieldOf("scale").forGetter(Transformation::getScale),
        QUATERNIONF.fieldOf("right_rotation").forGetter(Transformation::getRightRotation)
    ).apply(instance, Transformation::new)).describe("Transformation");

    public static final Codec<Display.Brightness> DISPLAY_BRIGHTNESS = RecordBuilder.create(instance -> instance.group(
        INT.fieldOf("block_light").forGetter(Display.Brightness::getBlockLight),
        INT.fieldOf("sky_light").forGetter(Display.Brightness::getSkyLight)
    ).apply(instance, Display.Brightness::new)).describe("Display.Brightness");

    public static final Codec<Display.Billboard> BILLBOARD = Codec.enumCodec(Display.Billboard.class);
    public static final Codec<ItemDisplay.ItemDisplayTransform> ITEM_DISPLAY_TRANSFORM = Codec.enumCodec(ItemDisplay.ItemDisplayTransform.class);
    public static final Codec<TextDisplay.TextAlignment> TEXT_ALIGNMENT = Codec.enumCodec(TextDisplay.TextAlignment.class);

    public static final Codec<Vector> VECTOR_I = new Codec<Vector>() {
        @Override
        public <D> DataResult<Vector> decode(DynamicOps<D> ops, D input) {
            return ops.getList(input)
                .map(DataResult::success)
                .orElseGet(() -> DataResult.error(DataError.typeMismatch("List", "Unknown")))
                .flatMap(vector -> {
                    if (vector.size() != 3)
                        return DataResult.error(DataError.custom("Integer Vector list requires 3 elements, found " + vector.size()));
                    try {
                        return DataResult.success(new Vector(
                            ops.getIntValue(vector.get(0)).orElseThrow(),
                            ops.getIntValue(vector.get(1)).orElseThrow(),
                            ops.getIntValue(vector.get(2)).orElseThrow()
                        ));
                    } catch (Exception e) {
                        return DataResult.error(DataError.custom("Malformed integer elements in Vector list"));
                    }
                });
        }

        @Override
        public <D> DataResult<D> encode(DynamicOps<D> ops, Vector value) {
            return DataResult.success(ops.createList(List.of(
                ops.createInt(value.getBlockX()),
                ops.createInt(value.getBlockY()),
                ops.createInt(value.getBlockZ())
            )));
        }

        @Override
        public String describe() {
            return "VectorI";
        }
    }.withSchema(new SchemaNode.ListSchema(new SchemaNode.PrimitiveSchema("Integer")));

    public static final Codec<Vector> VECTOR_F = new Codec<Vector>() {
        @Override
        public <D> DataResult<Vector> decode(DynamicOps<D> ops, D input) {
            return ops.getList(input)
                .map(DataResult::success)
                .orElseGet(() -> DataResult.error(DataError.typeMismatch("List", "Unknown")))
                .flatMap(vector -> {
                    if (vector.size() != 3)
                        return DataResult.error(DataError.custom("Float Vector list requires 3 elements, found " + vector.size()));
                    try {
                        return DataResult.success(new Vector(
                            ops.getDoubleValue(vector.get(0)).orElseThrow(),
                            ops.getDoubleValue(vector.get(1)).orElseThrow(),
                            ops.getDoubleValue(vector.get(2)).orElseThrow()
                        ));
                    } catch (Exception e) {
                        return DataResult.error(DataError.custom("Malformed float elements in Vector list"));
                    }
                });
        }

        @Override
        public <D> DataResult<D> encode(DynamicOps<D> ops, Vector value) {
            return DataResult.success(ops.createList(List.of(
                ops.createDouble(value.getX()),
                ops.createDouble(value.getY()),
                ops.createDouble(value.getZ())
            )));
        }

        @Override
        public String describe() {
            return "VectorF";
        }
    }.withSchema(new SchemaNode.ListSchema(new SchemaNode.PrimitiveSchema("Float")));

    public static final Codec<UUID> UUID = STRING.comapFlatMap(
        str -> {
            try {
                return DataResult.success(java.util.UUID.fromString(str));
            } catch (IllegalArgumentException e) {
                return DataResult.error(DataError.invalidFormat(str, "UUID"));
            }
        },
        java.util.UUID::toString
    ).describe("UUID");

    public static final Codec<Component> TEXT_COMPONENT = STRING.flatXmap(
        str -> {
            try {
                return DataResult.success(MiniMessage.miniMessage().deserialize(str));
            } catch (Exception e) {
                return DataResult.error(DataError.custom("Failed to parse MiniMessage content"));
            }
        },
        comp -> {
            try {
                return DataResult.success(MiniMessage.miniMessage().serialize(comp));
            } catch (Exception e) {
                return DataResult.error(DataError.custom("Failed to serialize MiniMessage content"));
            }
        }
    ).describe("TextComponent");

    public static final Codec<Key> KEY = STRING.comapFlatMap(
        str -> {
            try {
                return DataResult.success(Key.key(str));
            } catch (Exception e) {
                return DataResult.error(DataError.invalidFormat(str, "Namespace:Key"));
            }
        },
        Key::asString
    ).describe("Key");

    public static final Codec<NamespacedKey> NAMESPACED_KEY = STRING.comapFlatMap(
        str -> {
            try {
                NamespacedKey key = NamespacedKey.fromString(str);
                return key != null ? DataResult.success(key) : DataResult.error(DataError.invalidFormat(str, "NamespacedKey"));
            } catch (Exception e) {
                return DataResult.error(DataError.custom(e.getMessage()));
            }
        },
        NamespacedKey::toString
    ).describe("NamespacedKey");

    public static final Codec<World> WORLD = KEY.comapFlatMap(
        key -> {
            World w = Bukkit.getWorld(key);
            return w != null ? DataResult.success(w) : DataResult.error(DataError.custom("Unknown world target: " + key.asString()));
        },
        World::getKey
    ).describe("World");

    public static final Codec<Location> LOCATION = RecordBuilder.create(instance -> instance.group(
        WORLD.fieldOf("world").forGetter(Location::getWorld),
        DOUBLE.fieldOf("x").forGetter(Location::getX),
        DOUBLE.fieldOf("y").forGetter(Location::getY),
        DOUBLE.fieldOf("z").forGetter(Location::getZ),
        FLOAT.fieldOf("yaw").forGetter(Location::getYaw),
        FLOAT.fieldOf("pitch").forGetter(Location::getPitch)
    ).apply(instance, Location::new)).describe("Location");

    public static final Codec<DataComponentType> DATA_COMPONENT_TYPE = KEY.flatXmap(
        key -> {
            try {
                DataComponentType type = RegistryAccess.registryAccess().getRegistry(RegistryKey.DATA_COMPONENT_TYPE).get(key);
                return type != null ? DataResult.success(type) : DataResult.error(DataError.custom("Unknown DataComponentType definition: " + key));
            } catch (Exception e) {
                return DataResult.error(DataError.custom(e.getMessage()));
            }
        },
        type -> {
            try {
                Key key = RegistryAccess.registryAccess().getRegistry(RegistryKey.DATA_COMPONENT_TYPE).getKey(type);
                return key != null ? DataResult.success(key) : DataResult.error(DataError.custom("Unregistered DataComponentType execution"));
            } catch (Exception e) {
                return DataResult.error(DataError.custom(e.getMessage()));
            }
        }
    ).describe("DataComponentType");

    public static final Codec<List<DataComponent<?>>> DATA_COMPONENT_MAP = new Codec<List<DataComponent<?>>>() {
        @Override
        public <D> DataResult<List<DataComponent<?>>> decode(DynamicOps<D> ops, D input) {
            return ops.getMap(input)
                .map(DataResult::success)
                .orElseGet(() -> DataResult.error(DataError.typeMismatch("Map", "Unknown")))
                .flatMap(map -> {
                    List<DataError> warnings = new ArrayList<>();
                    List<DataComponent<?>> components = new ArrayList<>();
                    for (Map.Entry<D, D> entry : map.entrySet()) {
                        DataResult<String> idRes = STRING.decode(ops, entry.getKey()).prependPath("[key]");
                        if (idRes.isError()) {
                            warnings.add(idRes.dataError().orElseGet(() -> DataError.custom(idRes.error().get())));
                            continue;
                        }

                        String componentID = idRes.getOrThrow();
                        com.github.darksoulq.abyssallib.world.item.component.DataComponentType<?> type = Registries.DATA_COMPONENT_TYPES.get(componentID);

                        if (type == null) {
                            warnings.add(DataError.custom("Failed to load component with ID: " + componentID + ", The component does not exist"));
                            continue;
                        }

                        DataResult<?> compRes = type.codec().decode(ops, entry.getValue()).prependPath("[" + componentID + "]");
                        if (compRes.isError()) {
                            warnings.add(compRes.dataError().orElseGet(() -> DataError.custom(compRes.error().get())));
                            continue;
                        }

                        if (compRes.isPartial()) {
                            warnings.addAll(compRes.warnings());
                        }

                        components.add((DataComponent<?>) compRes.getOrThrow());
                    }
                    return warnings.isEmpty() ? DataResult.success(components) : DataResult.partial(components, warnings);
                });
        }

        @Override
        public <D> DataResult<D> encode(DynamicOps<D> ops, List<DataComponent<?>> value) {
            if (value == null) return DataResult.error(DataError.nullValue("List<DataComponent<?>>"));
            List<DataError> warnings = new ArrayList<>();
            Map<D, D> map = new HashMap<>();

            for (DataComponent<?> component : value) {
                String id = Registries.DATA_COMPONENT_TYPES.getId(component.getType());
                if (id == null) {
                    warnings.add(DataError.custom("Failed to serialize DataComponent as its ID is null"));
                    continue;
                }

                if (component instanceof CustomData customData) {
                    CompoundTag tag = customData.getValue().copy();
                    tag.remove("CustomData");
                    if (tag.isEmpty()) continue;
                }

                DataResult<D> idRes = STRING.encode(ops, id).prependPath("[key]");
                if (idRes.isError()) {
                    warnings.add(idRes.dataError().orElseGet(() -> DataError.custom(idRes.error().get())));
                    continue;
                }

                try {
                    D encodedData = ComponentMap.encodeComponent(component, ops);
                    if (encodedData != null) {
                        map.put(idRes.getOrThrow(), encodedData);
                    }
                } catch (Exception e) {
                    warnings.add(DataError.custom("Failed to encode DataComponent " + id + ": " + e.getMessage()));
                }
            }
            return warnings.isEmpty() ? DataResult.success(ops.createMap(map)) : DataResult.partial(ops.createMap(map), warnings);
        }

        @Override
        public String describe() {
            return "DataComponentMap";
        }
    }.withSchema(new SchemaNode.MapSchema(new SchemaNode.PrimitiveSchema("String"), new SchemaNode.PrimitiveSchema("Any")));

    private static final Map<String, SchemaNode.FieldSchema> ITEM_STACK_FIELDS = new LinkedHashMap<>();

    static {
        ITEM_STACK_FIELDS.put("id", new SchemaNode.FieldSchema("id", KEY.schema(), false, null, "Item Identifier"));
        ITEM_STACK_FIELDS.put("amount", new SchemaNode.FieldSchema("amount", INT.schema(), true, 1, "Item Amount"));
        ITEM_STACK_FIELDS.put("data", new SchemaNode.FieldSchema("data", DATA_COMPONENT_MAP.schema(), true, null, "Item Components"));
    }

    public static final Codec<ItemStack> ITEM_STACK = Codec.fallback(
        new Codec<ItemStack>() {
            @Override
            public <D> DataResult<ItemStack> decode(DynamicOps<D> ops, D input) {
                return ops.getMap(input)
                    .map(DataResult::success)
                    .orElseGet(() -> DataResult.error(DataError.typeMismatch("Map", "Unknown")))
                    .flatMap(map -> {
                        D idNode = map.get(ops.createString("id"));
                        if (idNode == null) return DataResult.error(DataError.missingField("id"));

                        DataResult<Key> idRes = KEY.decode(ops, idNode).prependPath("id");
                        if (idRes.isError()) return DataResult.error(idRes.error().get());
                        Key itemID = idRes.getOrThrow();

                        D amountNode = map.get(ops.createString("amount"));
                        int amount = amountNode != null ? INT.decode(ops, amountNode).prependPath("amount").orElse(1) : 1;

                        D dataNode = map.get(ops.createString("data"));
                        List<DataComponent<?>> components = dataNode != null ? DATA_COMPONENT_MAP.decode(ops, dataNode).prependPath("data").orElse(null) : null;

                        Item item;
                        if ("minecraft".equals(itemID.namespace())) {
                            Material mat = Material.matchMaterial(itemID.value().toUpperCase());
                            if (mat == null)
                                return DataResult.error(DataError.custom("Unknown vanilla material reference: " + itemID.value()));
                            item = new Item(new ItemStack(mat));
                        } else {
                            Item base = Registries.ITEMS.get(itemID.asString());
                            if (base == null)
                                return DataResult.error(DataError.custom("Failed to load generic item variant, ID: " + itemID.asString() + " does not exist"));
                            item = base.clone();
                        }

                        item.getStack().setAmount(amount);
                        if (components != null && !components.isEmpty()) {
                            components.forEach(item::setData);
                        }

                        return DataResult.success(item.getStack());
                    });
            }

            @Override
            public <D> DataResult<D> encode(DynamicOps<D> ops, ItemStack value) {
                Map<D, D> map = new HashMap<>();
                Item item = new Item(value);
                int amount = value.getAmount();
                List<DataComponent<?>> components = item.getComponentMap().getAllComponents();

                DataResult<D> idRes = KEY.encode(ops, item.getId()).prependPath("id");
                if (idRes.isError()) return idRes;
                map.put(ops.createString("id"), idRes.getOrThrow());

                if (amount > 1) {
                    DataResult<D> amountRes = INT.encode(ops, amount).prependPath("amount");
                    if (amountRes.isError()) return amountRes;
                    map.put(ops.createString("amount"), amountRes.getOrThrow());
                }

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
                        DataResult<D> compRes = DATA_COMPONENT_MAP.encode(ops, components).prependPath("data");
                        if (compRes.isSuccess()) map.put(ops.createString("data"), compRes.getOrThrow());
                    }
                }
                return DataResult.success(ops.createMap(map));
            }

            @Override
            public String describe() {
                return "ItemStack";
            }
        }.withSchema(new SchemaNode.RecordSchema(ITEM_STACK_FIELDS)),
        KEY.comapFlatMap(
            key -> {
                if (!NamespacedKey.MINECRAFT.equals(key.namespace())) {
                    Item item = Registries.ITEMS.get(key.asString());
                    return item != null ? DataResult.success(item.clone().getStack()) : DataResult.error(DataError.custom("Unknown custom item identity"));
                } else {
                    Material mat = Material.matchMaterial(key.value().toUpperCase());
                    return mat != null ? DataResult.success(new ItemStack(mat)) : DataResult.error(DataError.custom("Unknown core material execution"));
                }
            },
            stack -> new Item(stack).getId()
        ).withSchema(new SchemaNode.PrimitiveSchema("String"))
    ).describe("ItemStack");

    public static final Codec<RecipeChoice.ExactChoice> EXACT_CHOICE = ITEM_STACK.list().comapFlatMap(
        list -> list.isEmpty() ? DataResult.error(DataError.custom("ExactChoice definition requires at least one discrete item limit")) : DataResult.success(new RecipeChoice.ExactChoice(list)),
        RecipeChoice.ExactChoice::getChoices
    ).describe("RecipeChoice.ExactChoice");

    public static final Codec<RecipeChoice.MaterialChoice> MATERIAL_CHOICE = ITEM_STACK.list().comapFlatMap(
        list -> list.isEmpty() ? DataResult.error(DataError.custom("MaterialChoice definition requires at least one abstract limit")) : DataResult.success(new RecipeChoice.MaterialChoice(list.stream().map(ItemStack::getType).toList())),
        choice -> choice.getChoices().stream().map(ItemStack::of).toList()
    ).describe("RecipeChoice.MaterialChoice");

    public static final Codec<RecipeChoice> RECIPE_CHOICE = Codec.fallback(EXACT_CHOICE, MATERIAL_CHOICE).describe("RecipeChoice");

    public static final Codec<CustomShapedRecipe> SHAPED_RECIPE = RecordBuilder.create(instance -> instance.group(
        NAMESPACED_KEY.fieldOf("id").forGetter(CustomShapedRecipe::getKey),
        STRING.list().fieldOf("shape").forGetter(CustomShapedRecipe::getShape),
        Codec.map(CHARACTER, RECIPE_CHOICE).fieldOf("ingredients").forGetter(CustomShapedRecipe::getIngredients),
        ITEM_STACK.fieldOf("result").forGetter(CustomShapedRecipe::getResult),
        STRING.optionalFieldOf("group").forGetter(CustomShapedRecipe::getGroup),
        Codec.enumCodec(CraftingBookCategory.class).optionalFieldOf("category").forGetter(CustomShapedRecipe::getCategory),
        BOOLEAN.optionalFieldOf("replace", false).forGetter(CustomShapedRecipe::replace)
    ).apply(instance, CustomShapedRecipe::new)).describe("CustomShapedRecipe");

    public static final Codec<CustomShapelessRecipe> SHAPELESS_RECIPE = RecordBuilder.create(instance -> instance.group(
        NAMESPACED_KEY.fieldOf("id").forGetter(CustomShapelessRecipe::getKey),
        RECIPE_CHOICE.list().fieldOf("ingredients").forGetter(CustomShapelessRecipe::getIngredients),
        ITEM_STACK.fieldOf("result").forGetter(CustomShapelessRecipe::getResult),
        STRING.optionalFieldOf("group").forGetter(CustomShapelessRecipe::getGroup),
        Codec.enumCodec(CraftingBookCategory.class).optionalFieldOf("category").forGetter(CustomShapelessRecipe::getCategory),
        BOOLEAN.optionalFieldOf("replace", false).forGetter(CustomShapelessRecipe::replace)
    ).apply(instance, CustomShapelessRecipe::new)).describe("CustomShapelessRecipe");

    public static final Codec<CustomTransmuteRecipe> TRANSMUTE_RECIPE = RecordBuilder.create(instance -> instance.group(
        NAMESPACED_KEY.fieldOf("id").forGetter(CustomTransmuteRecipe::getKey),
        RECIPE_CHOICE.fieldOf("input").forGetter(CustomTransmuteRecipe::getInput),
        RECIPE_CHOICE.fieldOf("material").forGetter(CustomTransmuteRecipe::getMaterial),
        ITEM_STACK.fieldOf("result").forGetter(CustomTransmuteRecipe::getResult),
        STRING.optionalFieldOf("group").forGetter(CustomTransmuteRecipe::getGroup),
        Codec.enumCodec(CraftingBookCategory.class).optionalFieldOf("category").forGetter(CustomTransmuteRecipe::getCategory),
        BOOLEAN.optionalFieldOf("replace", false).forGetter(CustomTransmuteRecipe::replace)
    ).apply(instance, CustomTransmuteRecipe::new)).describe("CustomTransmuteRecipe");

    public static final Codec<CustomFurnaceRecipe> FURNACE_RECIPE = RecordBuilder.create(instance -> instance.group(
        NAMESPACED_KEY.fieldOf("id").forGetter(CustomFurnaceRecipe::getKey),
        RECIPE_CHOICE.fieldOf("input").forGetter(CustomFurnaceRecipe::getInput),
        ITEM_STACK.fieldOf("result").forGetter(CustomFurnaceRecipe::getResult),
        INT.fieldOf("cooking_time").forGetter(CustomFurnaceRecipe::getCookingTime),
        FLOAT.fieldOf("exp").forGetter(CustomFurnaceRecipe::getExp),
        STRING.optionalFieldOf("group").forGetter(CustomFurnaceRecipe::getGroup),
        Codec.enumCodec(CookingBookCategory.class).optionalFieldOf("category").forGetter(CustomFurnaceRecipe::getCategory),
        BOOLEAN.optionalFieldOf("replace", false).forGetter(CustomFurnaceRecipe::replace)
    ).apply(instance, CustomFurnaceRecipe::new)).describe("CustomFurnaceRecipe");

    public static final Codec<CustomSmokingRecipe> SMOKING_RECIPE = RecordBuilder.create(instance -> instance.group(
        NAMESPACED_KEY.fieldOf("id").forGetter(CustomSmokingRecipe::getKey),
        RECIPE_CHOICE.fieldOf("input").forGetter(CustomSmokingRecipe::getInput),
        ITEM_STACK.fieldOf("result").forGetter(CustomSmokingRecipe::getResult),
        INT.fieldOf("cooking_time").forGetter(CustomSmokingRecipe::getCookingTime),
        FLOAT.fieldOf("exp").forGetter(CustomSmokingRecipe::getExp),
        STRING.optionalFieldOf("group").forGetter(CustomSmokingRecipe::getGroup),
        Codec.enumCodec(CookingBookCategory.class).optionalFieldOf("category").forGetter(CustomSmokingRecipe::getCategory),
        BOOLEAN.optionalFieldOf("replace", false).forGetter(CustomSmokingRecipe::replace)
    ).apply(instance, CustomSmokingRecipe::new)).describe("CustomSmokingRecipe");

    public static final Codec<CustomBlastingRecipe> BLASTING_RECIPE = RecordBuilder.create(instance -> instance.group(
        NAMESPACED_KEY.fieldOf("id").forGetter(CustomBlastingRecipe::getKey),
        RECIPE_CHOICE.fieldOf("input").forGetter(CustomBlastingRecipe::getInput),
        ITEM_STACK.fieldOf("result").forGetter(CustomBlastingRecipe::getResult),
        INT.fieldOf("cooking_time").forGetter(CustomBlastingRecipe::getCookingTime),
        FLOAT.fieldOf("exp").forGetter(CustomBlastingRecipe::getExp),
        STRING.optionalFieldOf("group").forGetter(CustomBlastingRecipe::getGroup),
        Codec.enumCodec(CookingBookCategory.class).optionalFieldOf("category").forGetter(CustomBlastingRecipe::getCategory),
        BOOLEAN.optionalFieldOf("replace", false).forGetter(CustomBlastingRecipe::replace)
    ).apply(instance, CustomBlastingRecipe::new)).describe("CustomBlastingRecipe");

    public static final Codec<CustomCampfireRecipe> CAMPFIRE_RECIPE = RecordBuilder.create(instance -> instance.group(
        NAMESPACED_KEY.fieldOf("id").forGetter(CustomCampfireRecipe::getKey),
        RECIPE_CHOICE.fieldOf("input").forGetter(CustomCampfireRecipe::getInput),
        ITEM_STACK.fieldOf("result").forGetter(CustomCampfireRecipe::getResult),
        INT.fieldOf("cooking_time").forGetter(CustomCampfireRecipe::getCookingTime),
        FLOAT.fieldOf("exp").forGetter(CustomCampfireRecipe::getExp),
        STRING.optionalFieldOf("group").forGetter(CustomCampfireRecipe::getGroup),
        Codec.enumCodec(CookingBookCategory.class).optionalFieldOf("category").forGetter(CustomCampfireRecipe::getCategory),
        BOOLEAN.optionalFieldOf("replace", false).forGetter(CustomCampfireRecipe::replace)
    ).apply(instance, CustomCampfireRecipe::new)).describe("CustomCampfireRecipe");

    public static final Codec<CustomStonecuttingRecipe> STONECUTTING_RECIPE = RecordBuilder.create(instance -> instance.group(
        NAMESPACED_KEY.fieldOf("id").forGetter(CustomStonecuttingRecipe::getKey),
        RECIPE_CHOICE.fieldOf("input").forGetter(CustomStonecuttingRecipe::getInput),
        ITEM_STACK.fieldOf("result").forGetter(CustomStonecuttingRecipe::getResult),
        STRING.optionalFieldOf("group").forGetter(CustomStonecuttingRecipe::getGroup),
        BOOLEAN.optionalFieldOf("replace", false).forGetter(CustomStonecuttingRecipe::replace)
    ).apply(instance, CustomStonecuttingRecipe::new)).describe("CustomStonecuttingRecipe");

    public static final Codec<CustomSmithingTransformRecipe> SMITHING_TRANSFORM_RECIPE = RecordBuilder.create(instance -> instance.group(
        NAMESPACED_KEY.fieldOf("id").forGetter(CustomSmithingTransformRecipe::getKey),
        RECIPE_CHOICE.fieldOf("base").forGetter(CustomSmithingTransformRecipe::getBase),
        RECIPE_CHOICE.fieldOf("template").forGetter(CustomSmithingTransformRecipe::getTemplate),
        RECIPE_CHOICE.fieldOf("addition").forGetter(CustomSmithingTransformRecipe::getAddition),
        ITEM_STACK.fieldOf("result").forGetter(CustomSmithingTransformRecipe::getResult),
        BOOLEAN.optionalFieldOf("copy_components").forGetter(CustomSmithingTransformRecipe::getCopyComponents),
        BOOLEAN.optionalFieldOf("replace", false).forGetter(CustomSmithingTransformRecipe::replace)
    ).apply(instance, CustomSmithingTransformRecipe::new)).describe("CustomSmithingTransformRecipe");

    public static final Codec<CustomPotionMix> POTION_MIX = RecordBuilder.create(instance -> instance.group(
        NAMESPACED_KEY.fieldOf("id").forGetter(CustomPotionMix::getKey),
        RECIPE_CHOICE.fieldOf("input").forGetter(CustomPotionMix::getInput),
        RECIPE_CHOICE.fieldOf("ingredient").forGetter(CustomPotionMix::getIngredient),
        ITEM_STACK.fieldOf("result").forGetter(CustomPotionMix::getResult),
        BOOLEAN.optionalFieldOf("replace", false).forGetter(CustomPotionMix::replace)
    ).apply(instance, CustomPotionMix::new)).describe("CustomPotionMix");
}