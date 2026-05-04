# Configuration API
<link-summary>Guide to creating, managing, and serializing YAML configuration files</link-summary>

The Config API provides a fluent, strongly-typed wrapper around Bukkit's standard `YamlConfiguration`. It simplifies file management, supports custom multi-line comments on any path, and natively integrates with the `Codec` system to automatically serialize and deserialize complex Java objects.

### Creating a Configuration File
To create or load a configuration file, instantiate the `Config` class. The file will be automatically generated inside your plugin's `config/` directory.

```Java
// Creates or loads: plugins/config/abyssallib_example/settings.yml
Config config = new Config("abyssallib_example", "settings");

// Creates or loads inside a subfolder: plugins/config/abyssallib_example/data/users.yml
Config dataConfig = new Config("abyssallib_example", "users", "data");
```

### Defining Configuration Values
Instead of constantly typing out string paths, the API uses a `Value<T>` wrapper. This allows you to define your configuration keys, their default values, and their comments in one centralized location.

If a path does not exist when `value()` is called, the default value is automatically written to memory.

```Java
public final class Settings {
    
    public static final Config CONFIG = new Config("abyssallib_example", "settings");

    // A standard primitive value
    public static final Config.Value<Boolean> DEBUG_MODE = CONFIG.value("general.debug", false)
        .withComment("Enable verbose console logging.", "Do not use in production!");

    // A complex object that uses a Codec for serialization
    public static final Config.Value<ItemStack> STARTER_ITEM = CONFIG.value("items.starter", new ItemStack(Material.APPLE), Codecs.ITEM_STACK)
        .withComment("The item given to players on first join.");

    public static void load() {
        // Automatically creates the file, writes default values, and injects comments
        CONFIG.save();
    }
}
```

<tip>
Always call <code>save()</code> after defining your <code>Value</code> fields during startup. This ensures any missing default values and your custom comments are immediately written to the physical file.
</tip>

### Reading and Writing Data
Once your `Value<T>` fields are defined, you can easily read or update them from anywhere in your code.

If your value uses a `Codec`, the API handles the decoding and encoding automatically behind the scenes.

```Java
// Reading a value
boolean isDebug = Settings.DEBUG_MODE.get();
ItemStack item = Settings.STARTER_ITEM.get();

// Updating a value dynamically
Settings.DEBUG_MODE.set(true);

// You must call save() on the parent Config object to write changes to disk!
Settings.CONFIG.save();
```

<note>
If you modify the configuration file manually while the server is running, you can call <code>Settings.CONFIG.reload()</code> to refresh the cached values from the disk.
</note>

---

### Config Methods
Below are the available methods on the root `Config` object.

<table>
<tr>
<th>Method</th>
<th>Description</th>
</tr>
<tr>
<td><code>value(String path, T defaultValue)</code></td>
<td>Defines a standard configuration value (e.g., String, Int, Boolean, Double).</td>
</tr>
<tr>
<td><code>value(String path, T defaultValue, Codec&lt;T&gt; codec)</code></td>
<td>Defines a complex configuration value that requires encoding/decoding.</td>
</tr>
<tr>
<td><code>addComment(String path, String... comments)</code></td>
<td>Associates comment lines with a specific YAML path.</td>
</tr>
<tr>
<td><code>save()</code></td>
<td>Writes the current configuration state to disk and safely injects your custom comments.</td>
</tr>
<tr>
<td><code>reload()</code></td>
<td>Reloads the configuration data directly from the disk.</td>
</tr>
</table>

### Value Methods
Below are the available methods on the `Value<T>` instances.

<table>
<tr>
<th>Method</th>
<th>Description</th>
</tr>
<tr>
<td><code>get()</code></td>
<td>Retrieves the strongly-typed value. Throws a <code>RuntimeException</code> if decoding fails.</td>
</tr>
<tr>
<td><code>set(T value)</code></td>
<td>Updates the value in memory. Will encode the object if a codec was provided.</td>
</tr>
<tr>
<td><code>withComment(String... comments)</code></td>
<td>A chainable method that adds comments above this specific value's path in the file.</td>
</tr>
</table>