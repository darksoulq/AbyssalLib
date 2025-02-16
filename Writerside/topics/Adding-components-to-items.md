# Adding components to items

By adding components to items, you can add more functionality to themz such as making food, armor, tools and so on.

<procedure title="Adding components to items" id="adding-components-to-items">
<step>
To start off, we will use the previous item as an example, for adding components you need to do it inside the setComponents() method.
</step>
<step>
Setting stack size:

```Java
public class MyItem extends AItem {
    public MyItem() {
        super(Material.TYPE, new NamespacedKey("namespace", "modelid"));
    }
    
    @Override
    public void setComponents() {
        setStackSize(6); // item can have only 6 in a stack.
    }
}
```
</step>
<step>
Unlike Stack Size, other components follow builder format, as an example, heres how you make a Food:

```Java
public class MyItem extends AItem {
    public MyItem() {
        super(Material.TYPE, new NamespacedKey("namespace", "modelid"));
    }
    
    @Override
    public void setComponents() {
        new AFood(this) // in me.darksoul.abyssalLib.item.component
                .canAlwaysEat(true)
                .nutrition(5)
                .saturation(5)
                .timeToEat(5)
                .build();
        setStackSize(6); // item can have only 6 in a stack.
    }
}
```
</step>
</procedure>