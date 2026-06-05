# Creating Custom Codecs
<link-summary>Guide to building codecs for complex records, custom classes, enums, and tuples</link-summary>

While AbyssalLib provides codecs for standard primitive types, you will frequently need to create codecs for your own custom classes, records, and data structures. The API provides several tools to cleanly map your objects without dealing with manual parsing logic.

### Complex Objects (RecordBuilder)
For structured data like Java records or custom POJOs, use the `RecordBuilder`. This builder maps your object's fields to serialized keys (like JSON properties) and binds them directly to your constructor.

To define fields, call `.fieldOf("key")` on a base codec, followed by `.forGetter(getterMethod)`. You group these fields together using `instance.group(...)` and apply them to the constructor.

```Java
public record PlayerStats(String username, int kills, int deaths) {

    public static final Codec<PlayerStats> CODEC = RecordBuilder.create(instance -> 
        instance.group(
            // A required field
            Codecs.STRING.fieldOf("username").forGetter(PlayerStats::username),
            
            // A required field
            Codecs.INT.fieldOf("kills").forGetter(PlayerStats::kills),
            
            // An optional field with a default fallback of 0
            Codecs.INT.optionalFieldOf("deaths", 0).forGetter(PlayerStats::deaths)
        ).apply(instance, PlayerStats::new)
    );
}
```

<note>
The <code>RecordBuilder</code> supports grouping up to 20 fields natively. If your object requires more than 20 parameters, consider nesting your data structures into sub-objects!
</note>

---

### Simple Codecs
For simple wrapper classes or single-value objects, you can create a codec by providing decoding and encoding lambdas directly to `Codec.of()`.

Because codecs are safe, these lambdas must return a `DataResult` rather than throwing exceptions or returning raw objects.

```Java
public static final Codec<UUID> UUID_CODEC = Codec.of(
    (ops, input) -> {
        // 1. Decode the input as a string first
        return Codecs.STRING.decode(ops, input).flatMap(str -> {
            // 2. Safely parse the UUID
            try {
                return DataResult.success(UUID.fromString(str));
            } catch (IllegalArgumentException e) {
                return DataResult.error("Invalid UUID format: " + str);
            }
        });
    },
    (ops, value) -> {
        // Encode the UUID back to a string
        return Codecs.STRING.encode(ops, value.toString());
    }
);
```

---

### DynamicOps-Aware Codecs
Sometimes you need direct access to the `DynamicOps` instance to read dynamic maps or highly irregular legacy data formats that `RecordBuilder` cannot handle.

Using `Codec.of(Decoder, Encoder)`, you can manually traverse the tree. Use `DataResult.success()` and `DataResult.error()` to handle the serialization state gracefully.

```Java
public class CustomData {
    private final String name;
    
    public CustomData(String name) { this.name = name; }
    public String getName() { return name; }

    public static final Codec<CustomData> CODEC = Codec.of(
        new Codec.Decoder<CustomData>() {
            @Override
            public <D> DataResult<CustomData> decode(DynamicOps<D> ops, D input) {
                // Ask ops to treat the input as a Map
                var mapOpt = ops.getMap(input);
                if (mapOpt.isEmpty()) return DataResult.error("Expected a map object");
                
                var map = mapOpt.get();
                D nameData = map.get(ops.createString("name"));
                
                if (nameData == null) return DataResult.error("Missing 'name' key");
                
                return Codecs.STRING.decode(ops, nameData)
                    .map(nameString -> new CustomData(nameString));
            }
        },
        new Codec.Encoder<CustomData>() {
            @Override
            public <D> DataResult<D> encode(DynamicOps<D> ops, CustomData value) {
                // Manually construct the serialized map
                DataResult<D> encodedString = Codecs.STRING.encode(ops, value.getName());
                if (encodedString.isError()) return encodedString;
                
                return DataResult.success(ops.createMap(java.util.Map.of(
                    ops.createString("name"), encodedString.getOrThrow()
                )));
            }
        }
    );
}
```

<tip>
For highly complex manual map parsing, check out <a href="codec-context.md"/> to learn how to use <code>DecodeContext</code> and <code>EncodeContext</code> to remove the boilerplate associated with manual <code>DynamicOps</code> navigation!
</tip>

---

### Enums and Sequences
The API includes built-in factories for common programmatic patterns like Enums and Tuples.

<tabs>
<tab title="Enum Codecs">

Serializing enums is a highly common requirement. The `Codec.enumCodec(Class)` factory automatically serializes any enum to and from its exact string name. If the serialized string does not match an enum constant, it safely yields a `DataError.UnknownEnum`.

```Java
// Serializes to/from "SURVIVAL", "CREATIVE", etc.
public static final Codec<GameMode> GAMEMODE_CODEC = Codec.enumCodec(GameMode.class);
```

</tab>
<tab title="Pairs and Tuples">

If you need to serialize a strictly ordered array of mixed types (e.g., `[15, "hello", true]`), you can construct a Tuple codec. The API supports `pair()` (2 elements) and `tuple()` (up to 4 elements).

```Java
// Creates a codec that writes data as: [ 50, "hello" ]
Codec<Codec.Pair<Integer, String>> PAIR_CODEC = Codec.pair(Codecs.INT, Codecs.STRING);

// Decoding yields the immutable Pair wrapper
DataResult<Codec.Pair<Integer, String>> result = PAIR_CODEC.decode(JsonOps.INSTANCE, jsonNode);
if (result.isSuccess()) {
    int number = result.getOrThrow().first();
    String text = result.getOrThrow().second();
}
```

</tab>
</tabs>