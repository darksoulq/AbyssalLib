package com.github.darksoulq.abyssallib.world.entity;

import com.github.darksoulq.abyssallib.common.reflection.Reflect;
import com.github.darksoulq.abyssallib.common.reflection.ReflectClass;
import com.github.darksoulq.abyssallib.common.reflection.ReflectField;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.block.property.Property;

import java.lang.reflect.Field;
import java.util.*;

/**
 * A base class for entities that utilize the property-based serialization system.
 * <p>
 * This class uses reflection to automatically discover {@link Property} fields
 * and handle their persistence. Subclasses can define properties as fields,
 * and they will be automatically saved to and loaded from the world data.
 *
 * @param <T> the type of the parent/owner of this entity (e.g., CustomBlock)
 */
public abstract class AbstractPropertyEntity<T> {

    /**
     * The type or owner associated with this entity.
     */
    private final T type;

    /**
     * Constructs a new AbstractPropertyEntity.
     *
     * @param type the associated type or owner
     */
    public AbstractPropertyEntity(T type) {
        this.type = type;
    }

    /**
     * Gets the associated type or owner of this entity.
     *
     * @return the owner instance
     */
    public T getType() {
        return type;
    }

    /**
     * Called when the entity is loaded into the world.
     */
    public void onLoad() {}

    /**
     * Called before the entity is saved to the world data.
     */
    public void onSave() {}

    /**
     * Serializes all discovered properties into a dynamic map.
     * <p>
     * This method iterates through all declared fields in the class and its
     * superclasses (up to {@code AbstractPropertyEntity}), identifying
     * {@link Property} instances and encoding them.
     *
     * @param ops the dynamic operations logic
     * @param <D> the data format type
     * @return a DataResult containing the serialized map of property names to values
     */
    public <D> DataResult<D> serialize(DynamicOps<D> ops) {
        Map<D, D> map = new LinkedHashMap<>();
        List<DataError> warnings = new ArrayList<>();
        ReflectClass<?> rc = Reflect.of(getClass());

        while (rc != null && rc.getUnderlyingClass() != AbstractPropertyEntity.class && rc.getUnderlyingClass() != Object.class) {
            for (Field rawField : rc.getUnderlyingClass().getDeclaredFields()) {
                ReflectField<Object> field = rc.field(rawField.getName()).getOrNull();

                if (field == null || field.isStatic()) continue;

                Object obj = field.get(this).getOrNull();

                if (obj instanceof Property<?> prop) {
                    DataResult<D> encodedRes = prop.encode(ops);
                    if (encodedRes.isSuccess()) {
                        map.put(ops.createString(field.getName()), encodedRes.getOrThrow());
                        if (encodedRes.isPartial()) {
                            warnings.addAll(encodedRes.warnings());
                        }
                    } else {
                        warnings.add(encodedRes.dataError().orElseGet(() -> DataError.custom(encodedRes.error().get())));
                    }
                }
            }
            rc = rc.getSuperclass().getOrNull();
        }

        D resultMap = ops.createMap(map);
        return warnings.isEmpty() ? DataResult.success(resultMap) : DataResult.partial(resultMap, warnings);
    }

    /**
     * Deserializes properties from a dynamic map into this instance.
     * <p>
     * Matches keys in the serialized map to field names in the class hierarchy
     * that are instances of {@link Property}.
     *
     * @param ops   the dynamic operations logic
     * @param input the serialized map data
     * @param <D>   the data format type
     * @return a DataResult representing the success or partial failure of the decoding process
     */
    public <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input) {
        Optional<Map<D, D>> mapOpt = ops.getMap(input);
        if (mapOpt.isEmpty()) {
            return DataResult.error(DataError.custom("Input is not a valid map format."));
        }

        Map<D, D> map = mapOpt.get();
        List<DataError> warnings = new ArrayList<>();
        ReflectClass<?> rc = Reflect.of(getClass());

        while (rc != null && rc.getUnderlyingClass() != AbstractPropertyEntity.class && rc.getUnderlyingClass() != Object.class) {
            for (Field rawField : rc.getUnderlyingClass().getDeclaredFields()) {
                ReflectField<Object> field = rc.field(rawField.getName()).getOrNull();

                if (field == null || field.isStatic()) continue;

                Object obj = field.get(this).getOrNull();

                if (!(obj instanceof Property<?> prop)) continue;

                D encoded = map.get(ops.createString(field.getName()));
                if (encoded != null) {
                    DataResult<Void> decodedRes = prop.decode(ops, encoded);
                    if (decodedRes.isError()) {
                        warnings.add(decodedRes.dataError().orElseGet(() -> DataError.custom(decodedRes.error().get())));
                    } else if (decodedRes.isPartial()) {
                        warnings.addAll(decodedRes.warnings());
                    }
                }
            }
            rc = rc.getSuperclass().getOrNull();
        }

        return warnings.isEmpty() ? DataResult.success(null) : DataResult.partial(null, warnings);
    }
}