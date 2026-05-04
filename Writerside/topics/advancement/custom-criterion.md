# Custom Criteria
<link-summary>Guide to creating and registering custom advancement criteria</link-summary>

If you need an advancement to unlock based on logic not covered by the default criteria, you can create a custom criterion by implementing the `AdvancementCriterion` interface.

### Creating a Custom Criterion
To create a custom criterion, implement `AdvancementCriterion`. Similar to rewards, you must provide a `Codec` for JSON serialization and a `CriterionType`.

You must implement the `isMet(Player)` method, which is evaluated to check if the player satisfies the condition.

For this example, we will create a `HasPermissionCriterion` that checks if the player holds a specific permission node.

```Java
public class HasPermissionCriterion implements AdvancementCriterion {

    // 1. Define the Codec for JSON serialization
    public static final Codec<HasPermissionCriterion> CODEC = new Codec<>() {
        @Override
        public <D> HasPermissionCriterion decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow();
            String node = Codecs.STRING.decode(ops, map.get(ops.createString("node")));
            return new HasPermissionCriterion(node);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, HasPermissionCriterion value) throws CodecException {
            return ops.createMap(Map.of(
                ops.createString("node"), Codecs.STRING.encode(ops, value.node)
            ));
        }
    };

    // 2. Define the CriterionType pointing to the Codec
    public static final CriterionType<HasPermissionCriterion> TYPE = () -> CODEC;

    private final String node;

    public HasPermissionCriterion(String node) {
        this.node = node;
    }

    @Override
    public CriterionType<?> getType() {
        return TYPE;
    }

    // 3. Define the logic that dictates if the criterion is met
    @Override
    public boolean isMet(Player player) {
        return player.hasPermission(node);
    }
}
```

<tip>
The <code>AdvancementCriterion</code> interface also provides a default <code>isMet(Player player, Event event)</code> method. You can override this if your condition needs to inspect a specific Bukkit event payload rather than just the player's static state.
</tip>

### Registering the Criterion
Custom criteria must be registered using a `DeferredRegistry` targeting `Registries.CRITERIA`.

```Java
public final class Criteria {
    
    public static final DeferredRegistry<CriterionType<?>> CRITERIA = DeferredRegistry.create(Registries.CRITERIA, "abyssallib_example");

    // Register the criterion using its TYPE
    public static final CriterionType<?> HAS_PERMISSION = CRITERIA.register("has_permission", id -> HasPermissionCriterion.TYPE);
}
```

<note>
Do not forget to call <code>Criteria.CRITERIA.apply()</code> in your plugin's <code>onEnable()</code> method.
</note>

Once registered, you can use `"abyssallib_example:has_permission"` as the criteria type in your JSON advancements, passing the `"node"` parameter as defined in your codec.