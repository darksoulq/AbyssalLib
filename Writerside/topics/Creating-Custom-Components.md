# Creating Custom Components

> Creating custom data components allows you to store data on items and get them at any time.

### Creating Data Components
```Java
public class MyComponent extends DataComponent<Float> {
    private static final Codec<DataComponent<Float>> CODEC = Codecs.FLOAT.xmap(
            MyComponent::new, (inst) -> inst.value
    );

    public CustomMarker(float value) {
        super(Identifier.of("plugin_id", "my_component"), value, CODEC);
    }
}
```

This creates a custom component with the id `plugin_id:my_component` and its value will be float. However the component cannot be used on items yet, while you CAN apply it onto item using #setData it will afterwards fail to load during #getData so you must register the component first.

### Registering Data Components
```Java
public class MyComponents {
    public static final DeferredRegistry<DataComponent<?>> DATA_COMPONENTS = DeferredRegistry.create(Registries.DATA_COMPONENTS, "plugin_id");
    
    public static Holder<DataComponent<?>> MY_COMPONENT = DATA_COMPONENTS.register("my_component", MyComponent.class);
}
```

Lastly in your main class call DATA_COMPONENTS.apply() in onEnable:

```Java
public void onEnable{} {
    MyComponents.DATA_COMPONENTS.apply();
}
```

Now you can set and get it on items using .setData and .getData.