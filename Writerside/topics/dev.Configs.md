# Configs

> The Config API is moreso a types wrapper around YAMLConfiguration which means if you want your custom objects to be serializable they must implement ConfigSerializable.

The below snippet showcases how to use the Config API:

```Java
public class MyPlugin implements JavaPlugin {
    @Override
    public void onEnable() {
        Config cfg = new Config("plugin_id", "config_name", "optional_subfolder_argument");
        Config.Value<String> myStr = cfg.value("some.key", defaultValue);
        myStr.get(); // Gets value
        myStr.set(str); // Sets value
    }
}
```

<warning>
<code>Map&lt;&gt;</code> and <code>List&lt;&gt;</code> return an copy of the original, so you must set them back using <code>#set()</code> after modifications.
</warning>

`optional_subfolder_argument` can be omitted, `config_name` is the config files name without .yml at end.