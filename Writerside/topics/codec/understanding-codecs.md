# Executing & Handling Codecs
<link-summary>Learn how to serialize, deserialize, and securely handle DataResults using DynamicOps</link-summary>

Codecs define *how* an object is serialized, but they do not process the data themselves. To execute a codec, you must provide a `DynamicOps` implementation, which tells the codec what format (JSON, YAML, NBT, etc.) it should read from or write to.

### DynamicOps Implementations
AbyssalLib provides several built-in format operations:

<table>
<tr>
<th>Implementation</th>
<th>Target Format</th>
</tr>
<tr>
<td><code>JsonOps.INSTANCE</code></td>
<td>Jackson <code>JsonNode</code></td>
</tr>
<tr>
<td><code>YamlOps.INSTANCE</code></td>
<td>SnakeYAML / Bukkit YAML</td>
</tr>
<tr>
<td><code>NbtOps.INSTANCE</code></td>
<td>Minecraft <code>Tag</code> (CompoundTag, ListTag, etc.)</td>
</tr>
<tr>
<td><code>ByteOps.INSTANCE</code></td>
<td><code>byte[]</code> (Custom binary format)</td>
</tr>
<tr>
<td><code>StringOps.INSTANCE</code></td>
<td><code>String</code> (Inline literal representation)</td>
</tr>
</table>

---

### Encoding and Decoding
When you run a codec, it does not immediately return your object or throw an exception. Instead, it returns a `DataResult<T>`, a monadic container that safely wraps either the successful value, partial successes (with warnings), or a detailed structural error.

<tabs>
<tab title="Decoding (Reading)">

To convert raw serialized data back into a Java object, use `decode`.

```Java
JsonNode json = getJsonFromSomewhere();

// Pass the operations instance and the raw input
DataResult<PlayerStats> result = PlayerStats.CODEC.decode(JsonOps.INSTANCE, json);

if (result.isSuccess()) {
    PlayerStats stats = result.getOrThrow();
    System.out.println("Loaded: " + stats.kills());
} else {
    // Gracefully handle the error without try/catch blocks
    System.err.println("Failed to load stats: " + result.error().get());
}
```

</tab>
<tab title="Encoding (Writing)">

To convert a Java object into serialized data, use `encode`.

```Java
PlayerStats stats = new PlayerStats(150, 12);

// Encode the object into a Jackson JsonNode
DataResult<JsonNode> result = PlayerStats.CODEC.encode(JsonOps.INSTANCE, stats);

if (result.isSuccess()) {
    JsonNode json = result.getOrThrow();
    saveToFile(json);
}
```

</tab>
<tab title="Asynchronous Execution">

If you are decoding massive configurations or chunk data, you can execute the codec off the main thread natively using the async variants. These return a standard `CompletableFuture`.

```Java
PlayerStats.CODEC.decodeAsync(JsonOps.INSTANCE, largeJsonInput).thenAccept(result -> {
    if (result.isSuccess()) {
        System.out.println("Decoded successfully in the background!");
    }
});
```

</tab>
</tabs>

---

### Handling DataResults
Because data structures can be deeply nested, `DataResult` acts as a safe container. It traces the exact path of failures (e.g., `Missing required field: 'config.weapons.damage'`) and allows you to provide fallbacks easily.

```Java
DataResult<Weapon> result = Weapon.CODEC.decode(YamlOps.INSTANCE, yamlNode);

// 1. Throw an exception if it fails (Classic behavior)
Weapon weapon = result.getOrThrow();

// 2. Provide a default fallback if it fails
Weapon safeWeapon = result.orElse(Weapon.DEFAULT);

// 3. Check for partial successes (Data loaded, but warnings were logged)
if (result.isPartial()) {
    Weapon loaded = result.getOrThrow();
    for (DataError warning : result.warnings()) {
        plugin.getLogger().warning("Non-fatal issue: " + warning.message());
    }
}

// 4. Map the result directly if successful
DataResult<Integer> damageResult = result.map(Weapon::getDamage);
```

---

### The Dynamic Wrapper
If you need to quickly extract, modify, or convert a specific nested value from a raw serialized object without writing a full `RecordCodec`, you can wrap it in a `Dynamic<T>`.

The `Dynamic` object binds the raw data to its `DynamicOps`, allowing fluent path querying and conversions.

```Java
JsonNode rawJson = getLargeConfigFile();

// Wrap the JSON in a Dynamic instance
Dynamic<JsonNode> dynamic = new Dynamic<>(JsonOps.INSTANCE, rawJson);

// Safely query nested paths
dynamic.get("server.database.port").ifPresent(portNode -> {
    int port = Codecs.INT.decode(JsonOps.INSTANCE, portNode.value()).orElse(3306);
    System.out.println("Database Port: " + port);
});

// Convert the entire JSON structure into NBT format instantly
Dynamic<Tag> nbtDynamic = dynamic.convert(NbtOps.INSTANCE);
```