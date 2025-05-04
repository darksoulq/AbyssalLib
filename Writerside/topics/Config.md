# Config

you can use AbyssalLibs config system if you wish to, this also makes your config appear in the /abyssallib modmenu gui!

![image.png](image.png)

## Step 1: make your ConfigSpec.
the ConfigSpec will be used to define defaults, aswell as be updated if different from the config file, you can update it from Config class if you want to make it synced even after changes ingame.

```Java
ConfigSpec CONFIG = new ConfigSpec();
CONFIG.define("test.boolean", true);
CONFIG.define("test.string", "127.0.0.1");
CONFIG.define("test.int", 8080);
```

- .define() is used to define default values for the config.

## Step 2: Registering the config
now you need to actually load it in (save it, or load if file exists)

```Java
Config.register(MODID, CONFIG);
```

That's it!, you can access the values by `.getInt/Boolean/String` etc.
The Config file will be located in: `server/config/`, NOT in `server/plugins/<yourplugin>/`
### Bonus: syncing the config every 10 seconds:

simply:

```Java
new BukkitRunnable() {
    @Override
    public void run() {
        CONFIG = Config.get(MODID);
    }
}.runTaskTimer(plugin, 0L, 200L); // 200 ticks = 10 seconds
```
