# DamageType

<warning>
This is a Bootstrap specific feature.
</warning>

> AbyssalLib comes with auto registration for DamageType and the DamageType object allows getting it as a DamageSource easily.

### Registering new DamageTypes
```Java
public class MyDamageTypes {
    public static final DeferredRegistry<DamageType> DAMAGE_TYPES = DeferredRegistry.create(Registries.DAMAGE_TYPES, "plugin_id");
    
    public static final Holder<DamageType> MY_TYPE = DAMAGE_TYPES.register("my_type", id -> DamageType.builder(id)
            .damageEffect(DamageEffect)
            .exhaustion(1)
            .damageScaling(DamageScaling)
            .deathMessageType(DeathMessageType)
            .build());
}
```

Next in you Bootstrap class apply the registry:
```Java
public class MyBootstrap implements PluginBootstrap {
    @Override
    public void bootstrap(BootstrapContext ctx) {
        MyDamageTypes.DAMAGE_TYPES.apply();
    }
}
```

<warning>Absolutely never call apply on <code>DamageType</code> registry in <code>onEnable()</code>, it must be done inside bootstrap</warning>

Now when you want to use it in, for example LivingEntity#damage, use one of the following methods to get the DamageSource:
- `MyDamageTypes.MY_TYPE.withCause(Entity)`
- `MyDamageTypes.MY_TYPE.withDirect(Entity)`
- `MyDamageTypes.MY_TYPE.withLocation(Location)`