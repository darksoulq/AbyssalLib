# Events
<link-summary>Guide to using AbyssalLib's event wrapper for Bukkit events</link-summary>

AbyssalLib provides a lightweight wrapper around the standard Bukkit event system. This allows you to create and register event handlers without requiring your classes to implement the standard `Listener` interface.

### Registering an Event Handler
To listen for an event, use the `@SubscribeEvent` annotation on your method instead of the standard Bukkit `@EventHandler`.

```Java
public final class ExampleEvents {
    
    @SubscribeEvent
    public void onInventoryClick(InventoryClickEvent event) {
        // Event logic here
    }
}
```

<note>
The <code>@SubscribeEvent</code> annotation accepts the exact same arguments (such as priority and ignoreCancelled) as Bukkit's standard <code>@EventHandler</code>.
</note>

Once your handler methods are defined, you must register the class using the `EventBus` inside your plugin's main class.

```Java
public final class MyPlugin extends JavaPlugin {
    
    @Override
    public void onEnable() {
        EventBus bus = new EventBus(this);
        bus.register(new ExampleEvents());
    }
}
```

<tip>
If you need to fire events manually, you can use <code>EventBus.post(event)</code>. However, this is not necessary as events can be triggered using <code>event.callEvent()</code>.
</tip>