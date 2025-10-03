# Events

> AbyssalLib introduce its own `EventBus` and `@SubscribeEvent` annotation which acts as a layer to the Bukkit event system so you dont have to implement Listener

### Registering Events
Registering events is mostly same as it is in Bukkit however you do not have to implement `Listener`.

```Java
public class MyListeners {
    @SubscribeEvent
    public void onMove(PlayerMoveEvent e) {}
}
```

Afterwards in your `onEnable()`.
```Java
public class MyPlugin implements JavaPlugin {
    @Override
    public void onEnable() {
        EventBus bus = new EventBus(this);
        bus.register(new MyListeners());
    }
}
```

Thats it! You have registered an event successfully.

> `@SubscribeEvent` also has parameter for `priority` and `ignoreCancelled`, which are LOW and true by default.

#### Firing events:
For firing events you need not create an instance of EventBus simply call `EventBus.post(Evemt)`.