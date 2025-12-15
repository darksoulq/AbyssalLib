# Creating a Block

### Extending CustomBlock class
To start off you need to extend the `CustomBlock` class as follows:
```Java
public class MyBlock extends CustomBlock {
    public MyBlock(Identifier id) {
        super(id, Material.GRASS_BLOCK);
    }
}
```

Next part is the same as in [](Creating-an-Item.md), simply make a `DeferredRegistry` and register the block as you would an Item.

### Adding behaviour to MyBlock
CustomBlock has several hook methods such as:
```Java
public class MyBlock extends CustomBlock {
    public MyBlock(Identifier id) {
        super(id, Material.GRASS_BLOCK);
    }
    
    @Override
    public ActionResult onBreak(Player player, Location loc, ItemStack tool) {
        player.sendMessage("Hello!");
    }
}
```

### Adding Loot Tables and EXP drops
```Java
public class MyBlock extends CustomBlock {
    public MyBlock(Identifier id) {
        super(id, Material.GRASS_BLOCK);
    }
    
    @Override
    public int getExpToDrop(Player player, int fortuneLevel, boolean silkTouch) {
        return 10;
    }
    
    @Override
    public LootTable getLootTable() {
        return // Construct LootTable;
    }
}
```

You can also enable physics for the block (if its a physics affected block, in a future version ALL blocks will be able to have gravel physics).

Next section will showcase usage of BlockEntity and Property<?> for data storage.