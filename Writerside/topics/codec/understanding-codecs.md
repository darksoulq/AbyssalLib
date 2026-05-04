# Understanding Codecs
<link-summary>Introduction to bidirectional serialization, DynamicOps implementations, and sub-methods</link-summary>

The Codec API provides a standardized, format-agnostic way to serialize and deserialize Java objects. Instead of writing separate logic to read from JSON, write to JSON, read from YAML, and write to YAML, you define the structure of your object *once* using a `Codec<T>`.

A `Codec<T>` represents the exact rules for converting a Java object of type `T` into raw, serialized data, and vice versa.

### How Codecs Work
Codecs operate bidirectionally. Every codec guarantees two primary operations:

1. **Decoding (`decode`):** Translates raw data into a Java object. Throws a `CodecException` if the data is malformed or missing required fields.
2. **Encoding (`encode`):** Translates a Java object back into raw data.

### Executing Codecs (DynamicOps)
A `Codec` only defines the *rules* for translation. To actually serialize or deserialize data, you must execute the codec using a `DynamicOps` provider.

The `DynamicOps<T>` interface acts as the bridge. It defines exactly how to create and read strings, integers, lists, and maps in a specific target format. AbyssalLib provides five highly versatile operation implementations:

<table>
<tr>
<th>Implementation</th>
<th>Target Format</th>
<th>Use Case</th>
</tr>
<tr>
<td><code>JsonOps.INSTANCE</code></td>
<td>Jackson <code>JsonNode</code></td>
<td>Used for web requests, datapacks, advancements, and strict structured data storage.</td>
</tr>
<tr>
<td><code>YamlOps.INSTANCE</code></td>
<td>SnakeYAML / Bukkit YAML</td>
<td>Used natively by the Config API for player-facing configuration files.</td>
</tr>
<tr>
<td><code>NbtOps.INSTANCE</code></td>
<td>Minecraft <code>Tag</code></td>
<td>Direct serialization to/from native NBT tags (CompoundTag, ListTag, etc.) without intermediate JSON conversion. Perfect for custom item data or chunk storage.</td>
</tr>
<tr>
<td><code>ByteOps.INSTANCE</code></td>
<td><code>byte[]</code> (Binary)</td>
<td>Serializes data into raw byte arrays using a custom length-prefixed binary protocol. Ideal for network packets or highly compressed data.</td>
</tr>
<tr>
<td><code>StringOps.INSTANCE</code></td>
<td><code>String</code></td>
<td>Serializes data into a custom string-based format with literal suffixes (e.g., <code>100L</code>, <code>2.0d</code>) and bracketed collections (<code>[a,b]</code>, <code>{k:v}</code>). Useful for inline commands or SQL database cells.</td>
</tr>
</table>

#### Example: JSON Execution
To read or write data, pass the `DynamicOps` instance directly to your codec's `encode` or `decode` methods.

```Java
// 1. Create our Java object
PlayerStats stats = new PlayerStats(150, 12);

// ENCODING: Java Object -> Jackson JsonNode
JsonNode json = PlayerStats.CODEC.encode(JsonOps.INSTANCE, stats);

// DECODING: Jackson JsonNode -> Java Object
try {
    String rawJson = "{\"kills\": 5, \"deaths\": 0}";
    JsonNode parsedNode = JsonOps.INSTANCE.mapper.readTree(rawJson);
    
    PlayerStats decodedStats = PlayerStats.CODEC.decode(JsonOps.INSTANCE, parsedNode);
    System.out.println("Kills: " + decodedStats.kills());
} catch (Exception e) {
    e.printStackTrace();
}
```

<note>
If you are using the <code>Config</code> API, you do not need to manually call <code>YamlOps.INSTANCE</code>. The <code>Config.Value&lt;T&gt;</code> wrapper handles the YAML encoding and decoding automatically when you pass a codec into its definition.
</note>

### Codec Modifiers (Sub-methods)
The `Codec` interface provides numerous default instance methods that allow you to modify how an existing codec behaves without needing to rewrite it.

<table>
<tr>
<th>Method</th>
<th>Description</th>
</tr>
<tr>
<td><code>list()</code></td>
<td>Transforms a <code>Codec&lt;T&gt;</code> into a <code>Codec&lt;List&lt;T&gt;&gt;</code>. It automatically handles iterating through serialized arrays.</td>
</tr>
<tr>
<td><code>collection(Supplier)</code></td>
<td>Similar to <code>list()</code>, but allows you to specify the exact collection type (e.g., <code>HashSet::new</code>).</td>
</tr>
<tr>
<td><code>optional()</code></td>
<td>Transforms a <code>Codec&lt;T&gt;</code> into a <code>Codec&lt;Optional&lt;T&gt;&gt;</code>. If the field is missing or empty, it safely returns <code>Optional.empty()</code>.</td>
</tr>
<tr>
<td><code>nullable()</code></td>
<td>Modifies the codec to safely handle <code>null</code> Java values. If it encounters a null during encoding, it writes an empty state. If it reads an empty state, it returns <code>null</code>.</td>
</tr>
<tr>
<td><code>orElse(T fallback)</code></td>
<td>Provides a default fallback value. If the decoding process fails, the codec catches the exception and returns your fallback.</td>
</tr>
<tr>
<td><code>xmap(forward, backward)</code></td>
<td>Transforms a <code>Codec&lt;T&gt;</code> into a <code>Codec&lt;R&gt;</code> by providing two-way conversion functions. Useful for wrapping primitive values into custom classes.</td>
</tr>
<tr>
<td><code>fieldOf(name, getter)</code></td>
<td>Binds this codec to a specific string key/name, returning a <code>Field</code> definition for use inside the <code>RecordCodecBuilder</code>.</td>
</tr>
<tr>
<td><code>unchecked()</code></td>
<td>Performs an unsafe cast of the codec to a different type. Use with extreme caution.</td>
</tr>
</table>

### Static Combiners and Factories
You can also combine multiple codecs together using the static factory methods provided on the `Codec` interface.

<table>
<tr>
<th>Method</th>
<th>Description</th>
</tr>
<tr>
<td><code>Codec.fallback(primary, secondary)</code></td>
<td>Attempts to decode using the primary codec. If it fails, it suppresses the error and tries the secondary codec. Excellent for backwards compatibility when data formats change.</td>
</tr>
<tr>
<td><code>Codec.either(left, right)</code></td>
<td>Creates a codec that returns an <code>Either&lt;A, B&gt;</code> object, testing the left format first, then the right.</td>
</tr>
<tr>
<td><code>Codec.oneOf(codecs...)</code></td>
<td>An expanded version of <code>fallback</code> that accepts a varargs array of codecs, trying them in sequence until one succeeds.</td>
</tr>
<tr>
<td><code>Codec.map(keyCodec, valCodec)</code></td>
<td>Creates a codec that maps dynamic key-value pairs (e.g., a <code>HashMap</code>).</td>
</tr>
</table>