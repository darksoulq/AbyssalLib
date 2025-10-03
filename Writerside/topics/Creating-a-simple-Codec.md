# Codecs

> Codecs are objects for easy serialization/deserialization of an instance of a specific class.

### Creating a Codec for NamespacedKey
```Java
Codec<NamespacedKey> CODEC = Codecs.STRING.xmap(NamespacedKey::fromString, NamespacedKey::toString)
```

Then you would simply call `CODEC.serialize(DynamicOps, key)` for serialization and `CODEC.deserialize(DynamicOps, data)` for deserialization (data must be correct type, e.g for YamlOps it is Map<String, Object>)

### Setting up Codec for a record with 3 vars

```Java
public record Example(String name, int age, float money) {
    public static final Codec<Exmaple> CODEC = RecordCodecBuilder.create(
            Codecs.STRING.fieldOf("name", Example::name),
            Codecs.INT.fieldOf("age", Example::age),
            Codecs.FLOAT.fieldOf("money", Example::money),
            Example::new
    );

}
```

Then simply call as was shown for NamespacedKey to serialize/deserialize.

> Provided Codecs:
> - `Codecs.STRING`
> - `Codecs.CHARACTER`
> - `Codecs.INT`
> - `Codecs.FLOAT`
> - `Codecs.DOUBLE`
> - `Codecs.LONG`
> - `Codecs.BOOLEAN`
> - `Codecs.NAMESPACED_KEY`
> - `Codecs.ITEM_STACK`
> - `Codecs.EXACT_CHOICE`
> - `Codecs.MATERIAL_CHOICE`
> - `Codecs.RECIPE_CHOICE`
> - `Codecs.SHAPED_RECIPE`
> - `Codecs.SHAPELESS_RECIPE`
> - `Codecs.TRANSMUTE_RECIPE`
> - `Codecs.FURNACE_RECIPE`
> - `Codecs.SMOKING_RECIPE`
> - `Codecs.BLASTING_RECIPE`
> - `Codecs.CAMPFIRE_RECIPE`
> - `Codecs.STONECUTTING_RECIPE`
> - `Codecs.SMITHING_TRANSFORM_RECIPE`
> - `Codecs.POTION_MIX`