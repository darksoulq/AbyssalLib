# Registry and Holder

> Registries are AbyssalLib's way of storing objects tied to an id, whereas Holders are what "hold" the Suppliers for said objects (inside DeferredRegistry).
> People who are familiar with Fabric and NeoForge should have an easier time understanding how it works, IF you intend to make your own registries.

### Making your own Registry
```Java
public class MyRegistries {
    public static final Registry<MyObject> MY_OBJECTS = new Registry<>();
}
```

That's It!

> Registries have specific useful methods as well:
> - `#get(String id)`
> - `#getId(T object)`
> - `#contains(String id)`
> - `#getAll()`
> 