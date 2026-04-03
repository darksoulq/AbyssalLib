# Server-Side Translations
<link-summary>Guide to implementing dynamic, server-side translations and translation providers</link-summary>

Server-Side Translations (SST) allow for dynamic text resolution with full MiniMessage support. This differs from standard Resource Pack-based translations, which are primarily suited for static client-side text like simple item names.

### Preparing a localization file
To start, you need to create a localization file. This can be placed anywhere, such as within your plugin's resources folder or directly on the file system. Translation files require a `.properties` extension and must use standard Minecraft locale naming conventions (e.g., `en_us.properties`).

```properties
some.lang.key=Some Value
```

### Using a translation key
You can resolve and use a translation key via one of two methods:

1. **Through code:** Using `Component.translatable("some.lang.key")`
2. **Nested within properties:** Embedding it inside another key: `some.other.key=Test <lang:some.lang.key>`

<tip>
You can register your own custom <code>TagResolver</code> using <code>ServerTranslator.registerGlobalResolver()</code>.
</tip>

---

### Example: Creating a Join Message
For this example, we will create a dynamic join message that parses the joining player's name.

First, add the formatted string to your `.properties` file:
```properties
message.abyssallib_example.player_join=<gray>Welcome to the server <green>{0}</green></gray>
```

Next, intercept the `PlayerJoinEvent` and use `Component.translatable` to apply the key. [Refer to <a href="events.md"></a>]

```Java
public final class Events {
    
    @SubscribeEvent
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.joinMessage(Component.translatable("message.abyssallib_example.player_join", event.getPlayer().name()));
    }
}
```

When you start the server and a player joins, the translated and formatted string will broadcast in chat.

<img src="ssr_1.png" alt="SST in-game chat result" style="block"/>

---

### Translation Providers
Translation Providers allow you to dynamically evaluate and resolve a translation key based on specific context, such as the player involved or the item being held. AbyssalLib provides two main types:

1. `GlobalTranslationProvider`
2. `ItemTranslationProvider`

#### Using a GlobalTranslationProvider
Global providers evaluate keys server-wide. To create one, implement the interface and define your resolution logic.

```Java
public final class ExampleProvider implements GlobalTranslationProvider {
    
    @Override 
    public @Nullable String resolve(String key, @Nullable Player player) {
        // Return early if the requested key does not match yours
        if (!"example.key".equals(key)) return null;
        
        return "Example Contextual String";
    }
}
```

Once your class is set up, register it on startup:
`ServerTranslator.registerGlobalProvider(new ExampleProvider());`

#### Using an ItemTranslationProvider
Item Translation Providers work similarly but are evaluated contextually based on an `ItemStack`. While you can register these globally, you can also define "local" Translation Providers directly inside your custom `Item` classes.

For this example, we will add a dynamic lore line to the `FireSword` and `HitCounter` components created in <a href="item-interactions.md"></a> and <a href="data-components.md"></a> respectively.

```Java
public class FireSword extends Item {
    public FireSword(Key id) {
        super(id);
        // ... previous component setup
        
        // Register a local translation provider for this specific item
        addTranslationProvider((key, stack, player, ctx) -> {
            // Ignore keys we don't care about
            if (!"lore.abyssallib_example.hit_counter".equals(key)) return null; 
            
            Item item = Item.resolve(stack);
            
            // Return the value as a String. Because this supports MiniMessage, 
            // you could also inject color tags directly into this return string!
            return String.valueOf(item.getData(HitCounter.TYPE).getValue()); 
        });

        createTooltip(tooltip);
        updateTooltip();
    }

    @Override
    public void createTooltip(Tooltip tooltip) {
        tooltip.lines.clear();
        tooltip.addLine(TextUtil.parse("<!i><white>Inflicts <yellow>fire</yellow> on hit target for <green>3</green> seconds"));
        
        // Use the <tr:key> tag to tell the parser to dynamically resolve this line 
        tooltip.addLine(TextUtil.parse("<!i><white>Enemies hit <yellow><tr:lore.abyssallib_example.hit_counter></yellow> times"));
    }
    
    // ... other methods
}
```

Now, load into the game and hit an entity. The tooltip will dynamically update its hit count using the provider.

<video src="ssr_2.mp4" preview-src="ssr_2.png"/>