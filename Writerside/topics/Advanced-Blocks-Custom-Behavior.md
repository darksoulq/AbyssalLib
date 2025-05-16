# Advanced Blocks: Custom Behavior

You can customize how your blocks behave in the world by subclassing the `Block` class and overriding any of its event methods such as `onInteract`, `onPlace`, `onExplode`, etc.

These hooks are called automatically by AbyssalLib when the corresponding events occur in-game.

## Step 1: Extend the Block class

Create a new class that extends `Block`, and override the behavior methods you want to customize.
```Java
public class JumpBlock extends Block {
    public JumpBlock(ResourceLocation id) {
        super(id, Material.SLIME_BLOCK);
    }

    @Override
    public void onStep(LivingEntity entity) {
        entity.setVelocity(entity.getVelocity().add(new Vector(0, 1.5, 0)));
    }
}
```

## Step 2: Register Your Custom Block

Just like normal blocks, register your custom class using the DeferredRegistry.
```Java
public static final DeferredObject<Block> JUMP_BLOCK = BLOCKS.register("jump_block", (name, id) ->
    new JumpBlock(id)
);
```

## Available Event Hooks
You can override any of the following to respond to specific block events:
- `onPlace(BlockPlaceContext ctx)`
Called when the block is placed in the world.
- `onBreak(BlockBreakContext ctx)`
Called when the block is broken.
- `onInteract(BlockInteractContext ctx)`
Called when a player or entity interacts with the block (right-click\left-click).
- `onStep(LivingEntity entity)`
Called when an entity steps on the block.
- `onExplode(ExplodeContext ctx)`
Called when the block is affected by an explosion.
- `onProjectileHit()`
Called when a projectile hits the block.

### Limitation
- Blocks dont have custom models but rather use vanilla blocks
- the class which extends Block MUST only take in the ID in the constructor!