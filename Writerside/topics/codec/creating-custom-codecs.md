# Creating Custom Codecs
<link-summary>Guide to building codecs for primitive wrappers, enums, and complex objects</link-summary>

While AbyssalLib provides codecs for standard data types, you will frequently need to create codecs for your own custom classes, records, and enums.

### Simple Codecs
For simple wrapper classes or single-value objects, you can create a codec by providing a decoding and encoding function directly to `Codec.of()`.

```Java
public static final Codec<UUID> UUID_CODEC = Codec.of(
    // Decoder: Converts the raw data object into a UUID
    raw -> UUID.fromString(raw.toString()),
    // Encoder: Converts the UUID back into a raw string
    UUID::toString
);
```

---

### Enum Codecs
Serializing enums is a highly common requirement. The API provides a built-in `Codec.enumCodec(Class)` factory that automatically serializes any enum to and from its exact string name.

```Java
// Creates a codec that writes "SURVIVAL", "CREATIVE", etc.
public static final Codec<GameMode> GAMEMODE_CODEC = Codec.enumCodec(GameMode.class);
```

---

### Complex Objects
For structured data like Java records or custom POJOs, use the `RecordCodecBuilder`. This builder allows you to map specific object fields to specific serialized keys (like JSON properties).

You define fields by taking an existing codec, calling `.fieldOf("key_name", getterMethod)`, and combining them into a constructor.

```Java
public record PlayerStats(int kills, int deaths) {

    public static final Codec<PlayerStats> CODEC = RecordCodecBuilder.create(
        // Map the "kills" key to the kills() getter using the INT codec
        Codecs.INT.fieldOf("kills", PlayerStats::kills),

        // Map the "deaths" key
        Codecs.INT.fieldOf("deaths", PlayerStats::deaths),

        // The constructor used to build the object during deserialization
        PlayerStats::new
    );
}
```

<tip>
If a field in your object is not strictly required, append the <code>.optional()</code> or <code>.orElse()</code> modifiers to that field's codec before calling <code>.fieldOf()</code>. This prevents deserialization failures when loading older or incomplete data!
</tip>

---

### DynamicOps-Aware Codecs
While `RecordCodecBuilder` is excellent for standard, flat objects, it completely abstracts away the `DynamicOps` provider. Sometimes, you need direct access to the `ops` instance to read dynamic maps, handle deeply nested unknown structures, or process highly specific legacy data formats.

In these cases, you can use the DynamicOps-aware overload of `Codec.of(Decoder, Encoder)`. Because `Decoder` and `Encoder` are `@FunctionalInterface`s, this can be written cleanly using lambdas. This grants you full control over reading and writing the serialized tree without needing to manually implement the entire `Codec` interface.

```Java
public class CustomData {
    private final String name;
    private final int count;

    public CustomData(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public String getName() { return name; }
    public int getCount() { return count; }

    public static final Codec<CustomData> CODEC = Codec.of(
        (ops, input) -> {
            // 1. Direct access to 'ops' allows manual traversal of the serialized tree.
            var map = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected a map"));

            // 2. Extract and decode fields manually
            String name = Codecs.STRING.decode(ops, map.get(ops.createString("name")));
            int count = Codecs.INT.decode(ops, map.get(ops.createString("count")));

            return new CustomData(name, count);
        },
        (ops, value) -> {
            // Manually construct the serialized map using 'ops' creation methods
            return ops.createMap(Map.of(
                ops.createString("name"), Codecs.STRING.encode(ops, value.getName()),
                ops.createString("count"), Codecs.INT.encode(ops, value.getCount())
            ));
        }
    );
}
```