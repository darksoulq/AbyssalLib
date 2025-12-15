# Multiblock

### Basic Concepts
Before we make a Multiblock, we need to know of two simple classes, that is `MultiblockChoice`, this is a class which you can also extend for adding more choice types and is used in defining the block at a location in a multiblock.
AbyssalLib contains `MaterialChoice`, `BlockChoice` and `EmptyChoice` by default which each do as follows:

> - `MaterialChoice` -> is used to refer to a vanilla Block.
> - `BlockChoice` -> is used to refer to a AbyssalLib CustomBlock
> - `EmptyChoice` -> is used to make sure the block is empty (air/cave air)

You can extend `MultiblockChoice` for example in order to force a certain facing direction.

and the other class being `RelativeBlockPos`, this class is used to define the location of block from origin (which may be 0, 0, 0 or any other value).

as an example, if origin is `(0, 1, 0) `and we add a MaterialChoice at `(1, 0, 0)` then the block will be one block lower and at any of the 4 cardinal directions exactly 1 block away from origin.


### Extending Multiblock class and defining the structure
```Java
public class MyMultiblock extends Multiblock {
    public MyMultiblock(Identifier id) {
        super(id);
        // Here we define the structure
        pattern.put(new RelativeBlockPos(0, 0, 0), new MaterialChoice(Material.RESPAWN_ANCHOR)); // or a list of Material
        pattern.put(new RelativeBlockPos(2, 0, 0), new BlockChoice(Blocks.MY_BLOCK.get())); // Choices always support lists
        pattern.put(new RelativeBlockPos(-2, 0, 0), new BlockChoice(Blocks.MY_BLOCK.get()));
        pattern.put(new RelativeBlockPos(0, 0, 2), new BlockChoice(Blocks.MY_BLOCK.get()));
        pattern.put(new RelativeBlockPos(0, 0, -2), new BlockChoice(Blocks.MY_BLOCK.get()));
    }
    
    @Override
    public MultiblockChoice getTriggerChoice() {
        return MaterialChoice(Material.RESPAWN_ANCHOR); // This MUST be a unique block within the pattern.
    }
}
```

Afterwards you can override the hooks:
> - Multiblock#onConstruct
> - Multiblock#onBreak
> - Multiblock#onDestroyedByExplosion
> - Multiblock#onRedstone
> - Multiblock#onProjectileHit

In order to easily get when a Multiblock has been interacted with, you may use the `MultiblockInteractionEvent`

For storage of data you need to extend `MultiblockEntity` which is 1:1 with BlockEntity (and adding to a Multiblock is the same too), refer to [](Extending-BlockEntity-for-Custom-Data.md)