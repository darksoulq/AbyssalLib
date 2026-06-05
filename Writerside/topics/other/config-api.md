# Configuration API
<link-summary>Guide to creating, managing, migrating, and serializing YAML configuration files</link-summary>

The Config API provides a fluent, strongly-typed wrapper around Bukkit's standard `YamlConfiguration`. It simplifies file management, supports custom multi-line comments on any path, natively integrates with the `Codec` system to automatically serialize complex Java objects, and includes built-in schema migration support.

### Creating a Configuration File
To create or load a configuration file, instantiate the `Config` class. The file will be automatically generated inside your plugin's <path>plugins/&lt;plugin_name&gt;/</path> directory.

```Java
// Creates or loads: plugins/abyssallib_example/settings.yml
Config config = new Config("abyssallib_example", "settings");

// Creates or loads inside a subfolder: plugins/abyssallib_example/data/users.yml
Config dataConfig = new Config("abyssallib_example", "users", "data");
```

---

### Defining Configuration Values
Instead of repeatedly typing out string paths across your codebase, the API uses a `Value<T>` wrapper. This allows you to define your configuration keys, their default values, and their comments in one centralized location.

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
        // Automatically creates the file, writes missing default values, and injects comments
        CONFIG.save();
    }
}
```

<tip>
Always call <code>save()</code> after defining your <code>Value</code> fields during startup. This ensures any missing default values and your custom comments are immediately written to the physical file.
</tip>

---

### Migrating Configurations (Schemas)
As your plugin updates, your configuration files will need to change. The `Config` API natively integrates with the `DataFixer` system, allowing you to seamlessly upgrade old YAML files without resetting user data.

Use `.schema(targetVersion)` to start a `MigrationChain`, apply your `DataFixer` steps, and call `.apply()`. **Do this before calling `.save()` in your load sequence.**

```Java
public static void load() {
    // Upgrade the config to version 2
    CONFIG.schema(2)
        // From V0 -> V1: Rename a key
        .fix(0, DataFixer.renameKey("general.logging", "general.debug"))
        // From V1 -> V2: Group some values together
        .fix(1, DataFixer.nestKeys("general", "debug", "language"))
        .apply();

    // Save missing defaults and comments for the newly updated layout
    CONFIG.save();
}
```
*Note: This will automatically add a `config_version` integer to the root of your YAML file to track the current state.*

---

### Reading and Writing Data
Once your `Value<T>` fields are defined, you can easily read or update them from anywhere in your code. If your value uses a `Codec`, the API handles the decoding and encoding automatically behind the scenes.

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
If you or a server owner modify the configuration file manually while the server is running, you can call <code>Settings.CONFIG.reload()</code> to refresh the cached values directly from the disk.
</note>

---

### Method Reference

#### Config Methods
<table>
<tr>
<th>Method</th>
<th>Description</th>
</tr>
<tr>
<td><code>value(path, defaultValue)</code></td>
<td>Defines a standard configuration value (e.g., String, Int, Boolean, Double).</td>
</tr>
<tr>
<td><code>value(path, defaultValue, codec)</code></td>
<td>Defines a complex configuration value that requires encoding/decoding.</td>
</tr>
<tr>
<td><code>schema(targetVersion)</code></td>
<td>Initiates a migration chain to upgrade legacy configuration layouts.</td>
</tr>
<tr>
<td><code>addComment(path, comments...)</code></td>
<td>Associates multi-line comments with a specific YAML path.</td>
</tr>
<tr>
<td><code>save()</code></td>
<td>Writes the current configuration state to disk and safely injects custom comments.</td>
</tr>
<tr>
<td><code>reload()</code></td>
<td>Reloads the configuration data directly from the physical file.</td>
</tr>
</table>

#### MigrationChain Methods
<table>
<tr>
<th>Method</th>
<th>Description</th>
</tr>
<tr>
<td><code>fix(fromVersion, dataFixer)</code></td>
<td>Registers a <code>DataFixer</code> transformation step to execute when migrating from the specified version.</td>
</tr>
<tr>
<td><code>apply()</code></td>
<td>Evaluates the current <code>config_version</code> and applies all necessary fixers sequentially, saving the results.</td>
</tr>
</table>

#### Value Methods
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
<td><code>set(value)</code></td>
<td>Updates the value in memory. Will encode the object if a codec was provided.</td>
</tr>
<tr>
<td><code>withComment(comments...)</code></td>
<td>A chainable method that adds comments above this specific value's path in the file.</td>
</tr>
</table>