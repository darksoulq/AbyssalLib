# Advanced Context & Parsing
<link-summary>Simplifying map traversal using DecodeContext and EncodeContext</link-summary>

When manually writing codecs for map structures, checking `Optional` returns and appending `DataResult` error paths can become verbose. AbyssalLib provides `DecodeContext` and `EncodeContext` to streamline this process by wrapping map operations in a fluent API that automatically collects errors and warnings.

### Decoding Maps
`DecodeContext` wraps a deserialized map node. It allows you to sequentially read keys, apply their respective codecs, and pass the resulting values into a callback function.

If a required key is missing, or if a codec fails, the context logs a `DataError` internally. Calling `.result()` returns a single `DataResult` containing all collected issues, or `DataResult.success(null)` if parsing succeeded.

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
        new Codec.Decoder<CustomData>() {
            @Override
            public <D> DataResult<CustomData> decode(DynamicOps<D> ops, D input) {
                var mapOpt = ops.getMap(input);
                if (mapOpt.isEmpty()) return DataResult.error("Expected a map object");
                
                // Prepare variables to hold the parsed data
                String[] name = new String[1];
                int[] count = new int[1];
                
                // Parse the map sequentially
                DataResult<Void> validation = DecodeContext.of(ops, mapOpt.get())
                    // Reads a required key
                    .read("name", Codecs.STRING, val -> name[0] = val)
                    
                    // Reads an optional key, providing a default if missing
                    .readOrElse("count", Codecs.INT, 1, val -> count[0] = val)
                    
                    // Finish and get the combined result
                    .result();
                    
                // If validation failed, return the error
                if (validation.isError()) {
                    return DataResult.error(validation.dataError().get());
                }
                
                return DataResult.success(new CustomData(name[0], count[0]));
            }
        },
        // ... Encoder logic below ...
```

---

### Encoding Maps
Similarly, `EncodeContext` simplifies building a new serialized map by chaining write operations. If a `null` value is passed or a codec fails to encode, it automatically logs the error to the context.

```Java
        // ... Decoder logic above ...
        new Codec.Encoder<CustomData>() {
            @Override
            public <D> DataResult<D> encode(DynamicOps<D> ops, CustomData value) {
                return EncodeContext.of(ops)
                    .write("name", Codecs.STRING, value.getName())
                    // Only writes the count if it is not the default value of 1
                    .writeOrElse("count", Codecs.INT, value.getCount(), 1)
                    .result();
            }
        }
    );
}
```