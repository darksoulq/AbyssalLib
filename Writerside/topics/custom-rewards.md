# Custom Rewards
<link-summary>Guide to creating and registering custom advancement rewards</link-summary>

If the built-in advancement rewards do not meet your requirements, you can create custom ones by implementing the `AdvancementReward` interface.

### Creating a Custom Reward
To create a custom reward, implement `AdvancementReward`. You must also define a `Codec` so the reward can be serialized and loaded from JSON files, and a `RewardType` to link the codec to your class.

For this example, we will create a `HealReward` that restores a specific amount of health to the player when the advancement is granted.

```Java
public class HealReward implements AdvancementReward {

    // 1. Define the Codec for JSON serialization
    public static final Codec<HealReward> CODEC = new Codec<>() {
        @Override
        public <D> HealReward decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow();
            double amount = Codecs.DOUBLE.decode(ops, map.get(ops.createString("amount")));
            return new HealReward(amount);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, HealReward value) throws CodecException {
            return ops.createMap(Map.of(
                ops.createString("amount"), Codecs.DOUBLE.encode(ops, value.amount)
            ));
        }
    };

    // 2. Define the RewardType pointing to the Codec
    public static final RewardType<HealReward> TYPE = () -> CODEC;

    private final double amount;

    public HealReward(double amount) {
        this.amount = amount;
    }

    @Override
    public RewardType<?> getType() {
        return TYPE;
    }

    // 3. Define the actual reward logic
    @Override
    public void grant(Player player) {
        double newHealth = Math.min(player.getMaxHealth(), player.getHealth() + amount);
        player.setHealth(newHealth);
    }
}
```

### Registering the Reward
Just like items, custom rewards must be registered using a `DeferredRegistry` targeting `Registries.REWARDS`.

```Java
public final class Rewards {
    
    public static final DeferredRegistry<RewardType<?>> REWARDS = DeferredRegistry.create(Registries.REWARDS, "abyssallib_example");

    // Register the reward using its TYPE
    public static final RewardType<?> HEAL = REWARDS.register("heal", id -> HealReward.TYPE);
}
```

<note>
Do not forget to call <code>Rewards.REWARDS.apply()</code> in your plugin's <code>onEnable()</code> method.
</note>

Once registered, you can use `"abyssallib_example:heal"` as the type in your JSON advancements, passing the `"amount"` parameter as defined in your codec.