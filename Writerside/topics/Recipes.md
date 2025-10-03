# Recipes

> While no utility for registration exists for devs, you can extend `CustomRecipe` for your recipes that are not shaped or any vanilla type and register them in your own registries, you can also register their `Codec` to `RecipeLoader` to allow server admins to add more recipes of your type.

### Adding a custom recipe to RecipeLoader

```java
RecipeLoader.registerHandler("myplugin:custom", data -> {
    MyCustomRecipe r = MyCustomRecipe.getCodec.decode(YamlOps.INSTANCE, data);
    if (r != null) MyCustomRegistry.register(r.getId().toString(), r);
});
```

The above code assumes you have extended CustomRecipe or have a getId() and getCodec() method.

<warning>
This doc doesnt contain how to extend <code>CustomRecipe</code> as it's very simple, as in you just make a Codec and pass in the id in the constructor.
</warning>