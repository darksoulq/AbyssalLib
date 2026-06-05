# Modifiers & Validation
<link-summary>Transforming codecs and applying built-in schema validation rules</link-summary>

The Codec interface provides dozens of default instance methods that allow you to modify how an existing codec behaves without needing to rewrite it. It also features a powerful schema validation system.

### Basic Modifiers
These modifiers change the structural type of the codec, returning a newly wrapped codec instance.

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
<td>Transforms a <code>Codec&lt;T&gt;</code> into a <code>Codec&lt;Optional&lt;T&gt;&gt;</code>. If the field is missing or empty, it safely yields <code>Optional.empty()</code>.</td>
</tr>
<tr>
<td><code>nullable()</code></td>
<td>Modifies the codec to safely handle <code>null</code> Java values. If it encounters a null during encoding, it writes an empty state. If it reads an empty state, it yields <code>null</code>.</td>
</tr>
<tr>
<td><code>orElse(T fallback)</code></td>
<td>Provides a default fallback value. If the decoding process fails, the codec catches the error, attaches it to the <code>DataResult</code> as a warning, and yields your fallback value.</td>
</tr>
<tr>
<td><code>xmap(forward, backward)</code></td>
<td>Transforms a <code>Codec&lt;T&gt;</code> into a <code>Codec&lt;R&gt;</code> by providing two-way conversion functions. Useful for wrapping primitive values into custom classes.</td>
</tr>
<tr>
<td><code>flatXmap(forward, backward)</code></td>
<td>Similar to <code>xmap</code>, but the conversion functions return a <code>DataResult</code>. Useful when the conversion process itself can fail and you need to provide a contextual error message.</td>
</tr>
<tr>
<td><code>fieldOf(name)</code></td>
<td>Binds this codec to a specific string key/name, returning a <code>FieldBuilder</code> definition for use inside the <code>RecordBuilder</code>.</td>
</tr>
<tr>
<td><code>unchecked()</code></td>
<td>Performs an unsafe cast of the codec to a different type. Use with extreme caution.</td>
</tr>
</table>

---

### Schema Validation
AbyssalLib's Codec API includes a robust schema validation engine. These modifiers do not change the *type* of the codec, but they attach strict rules that are checked during both encoding and decoding.

If validation fails, the codec will yield an error `DataResult` with detailed bounds information.

<table>
<tr>
<th>Method</th>
<th>Description</th>
</tr>
<tr>
<td><code>range(min, max)</code></td>
<td>Restricts a numerical codec (Int, Double, Float, Long) to an inclusive minimum and maximum.</td>
</tr>
<tr>
<td><code>positive()</code></td>
<td>Shorthand for requiring a numerical codec to yield a value strictly greater than <code>0</code>.</td>
</tr>
<tr>
<td><code>minLength(len)</code></td>
<td>Enforces a minimum character length on String codecs, or a minimum element size on List and Map codecs.</td>
</tr>
<tr>
<td><code>maxLength(len)</code></td>
<td>Enforces a maximum length constraint.</td>
</tr>
<tr>
<td><code>regex(pattern)</code></td>
<td>Requires a String codec to strictly match a provided regular expression.</td>
</tr>
<tr>
<td><code>oneOf(values...)</code></td>
<td>Restricts a String or primitive codec to only accept values from the provided array.</td>
</tr>
</table>

#### Example: Validated Field
Validation modifiers are typically chained directly onto the base codec before defining it as a field.

```Java
public static final Codec<WeaponConfig> CODEC = RecordBuilder.create(instance -> 
    instance.group(
        // Must be exactly "sword", "axe", or "mace"
        Codecs.STRING.oneOf("sword", "axe", "mace")
            .fieldOf("weapon_type")
            .forGetter(WeaponConfig::type),

        // Must be between 1.0 and 100.0
        Codecs.DOUBLE.range(1.0, 100.0)
            .fieldOf("damage")
            .forGetter(WeaponConfig::damage),

        // Value must be greater than 0
        Codecs.INT.positive()
            .fieldOf("durability")
            .forGetter(WeaponConfig::durability),

        // Array must contain at least 1 enchantment, max 5
        EnchantCodec.CODEC.list().minLength(1).maxLength(5)
            .optionalFieldOf("enchants", List.of())
            .forGetter(WeaponConfig::enchants)
            
    ).apply(instance, WeaponConfig::new)
);
```

#### Custom Validation
If the built-in validators do not meet your requirements, you can write custom validation logic using `.validate()`. This allows you to check state against external systems or write complex cross-variable logic.

```Java
// Creates a String codec that fails if the string is empty
Codec<String> NOT_EMPTY_STRING = Codecs.STRING.validate(
    str -> !str.trim().isEmpty(),
    str -> "String cannot be blank or only whitespace!"
);
```