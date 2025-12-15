# Extending BlockEntity for Custom Data

Custom data storage (for blocks) is extremely simple using the BlockEntity class.

### Extending BlockEntity and Adding Some Properties
```Java
public class MyBlockEntity extends BlockEntity {
    public Property<Integer> myInt = new Property<>(Codecs.INT, 0);
    
    public MyBlockEntity(CustomBlock block) {
        super(block);
    }
}
```

> This makes a BlockEntity with the property `myInt` with default value 0.
> Codec is needed so the property can be serialized.
> value can be accessed/set by: `Property#get` and `Property#set`

However the entity is useless right now, you need to add it to a Custom Block as follows:

```Java
public class MyBlock extends CustomBlock {
    public MyBlock(Identifier id) {
        super(id, Material.GRASS_BLOCK);
    }
    
    @Override
    public MyBlockEntity createBlockEntity(Location lco) {
        return new MyBlockEntity(this);
    }
    
    // So that you dont have to cast everytime
    @Override
    public MyBlockEntity getEntity() {
        return (MyBlockEntity) super();
    }
}
```

Now the block `MyBlock` will use `MyBlockEntity` and so the property will be accessible (`block.getEntity().myInt.get()`)


BlockEntity also has the following hooks that you may override:
- `BlockEntity#onLoad`
- `BlockEntity#onSave`
- `BlockEntity#serverTick`
- `BlockEntity#randomTick`