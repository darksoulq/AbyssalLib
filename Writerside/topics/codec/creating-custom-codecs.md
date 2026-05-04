# Creating Custom Codecs
<link-summary>Guide to building codecs for primitive wrappers, enums, and complex objects</link-summary>

While AbyssalLib provides codecs for standard data types, you will frequently need to create codecs for your own custom classes, records, and enums.

### Simple Codecs
For simple wrapper classes or single-value objects, you can create a codec by providing a decoding function and an encoding function directly to `Codec.of()`.

```Java
public static final Codec<UUID> UUID_CODEC = Codec.of(
    // Decoder: Converts the raw data (handled as an Object) into a UUID
    raw -> UUID.fromString(raw.toString()),
    // Encoder: Converts the UUID back into a raw string
    uuid -> uuid.toString()
);
```

### Enum Codecs
Serializing enums is incredibly common. The API provides a built-in `Codec.enumCodec(Class)` factory that automatically serializes any enum to and from its exact string name.

```Java
// Creates a codec that writes "SURVIVAL", "CREATIVE", etc.
public static final Codec<GameMode> GAMEMODE_CODEC = Codec.enumCodec(GameMode.class);
```

### Complex Objects (RecordCodecBuilder)
For structured data like custom classes or Java records, use the `RecordCodecBuilder`. This builder allows you to map specific fields to specific serialized keys (like JSON keys).

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
If a field in your object is not strictly required, remember to use the <code>.optional()</code> or <code>.orElse()</code> modifiers on that field's codec before calling <code>.fieldOf()</code> to avoid exceptions when loading older data!
</tip>

### Manual Implementation (Constructor Method)
While `RecordCodecBuilder` is excellent for standard, flat objects, it completely abstracts away the `DynamicOps` provider. Sometimes, you need direct access to the `ops` instance to read dynamic maps, handle deeply nested unknown structures, or process highly specific legacy data formats.

In these cases, you can implement the `Codec<T>` interface directly via an anonymous class (`new Codec<>() {}`). This grants you full control over reading and writing the tree.

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

    public static final Codec<CustomData> CODEC = new Codec<>() {
        @Override
        public <D> CustomData decode(DynamicOps<D> ops, D input) throws CodecException {
            // 1. Direct access to 'ops' allows manual traversal of the serialized tree.
            // We ask ops to treat the input as a Map structure.
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected a map"));
            
            // 2. Extract and decode fields manually
            D nameData = map.get(ops.createString("name"));
            String name = Codecs.STRING.decode(ops, nameData);
            
            D countData = map.get(ops.createString("count"));
            int count = Codecs.INT.decode(ops, countData);
            
            return new CustomData(name, count);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, CustomData value) throws CodecException {
            // Manually construct the serialized map using 'ops' creation methods
            return ops.createMap(Map.of(
                ops.createString("name"), Codecs.STRING.encode(ops, value.getName()),
                ops.createString("count"), Codecs.INT.encode(ops, value.getCount())
            ));
        }
    };
}
```