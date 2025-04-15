# Events

AbyssalLib provides an easy to use interface for registering events, this might be made to auto-discover so devs dont have to register the event classes.

## Create an EventBus

to start registering events, you need an `EventBus`, this can easily be made by creating an instance of `EventBus` in your `onEnable`.

```java
EventBus eventBus = new EventBus(plugin);
```

## Make some events.

Now you can create events similarly to Bukkit, instead of @EventListener you must use @Subscribe, the class that holds these methods doesnt need to implement Listener.

```java
    @SubscribeEvent
    public void onChat(AsyncChatEvent e) {
        Component result = e.message();
        for (String placeholder : GlyphManager.getChatMap().keySet()) {
            result = e.message().replaceText(TextReplacementConfig.builder()
                    .matchLiteral(placeholder)
                    .replacement(GlyphManager.getChatMap().get(placeholder).toString())
                    .build());
        }

        e.message(result);
    }
```

## Register the class holding the listeners

Now you just need to register an instance of the class that contains these methods, do this somewhere in you `onEnable` method.

```java
eventBus.register(new Events());
```

That's it, you have successfully registered your events!