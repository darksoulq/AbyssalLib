# ChestGui

ChestGuis are something any plugin dev would be familiar with, they allow alot of freedom for creativity, and Abyssal lib doesnt take away from said freedom.

## Step 1: make a class extending ChestGui 

```Java
public class MyMenu extends ChestGui {
    public MyMenu() {
        super(title, rows);
    }
    @Override
    public void init(Player player) {
        // setup slots here for the first time, you can clear the list of slots and repopulate them in other methods aswell.
    }
}
```

- init method is for setting slots, however slots can be repopulated later on.

## Step 2: add some slots to your gui
```Java
public class MyMenu extends ChestGui {
    public MyMenu() {
        super(title, rows);
    }
    @Override
    public void init(Player player) {
        slot(new StaticSlot(index, item));
        slot(new ButtonSlot(index, item, consumerOf<GuiClickContext>));
    }
}
```

- slot() method adds the slot to the slot list.
- Static/ButtonSlot are builtin slots, you can look at the implementations to understand how they work.

## Step 3: opening the Gui
To open the Gui you MUST use the gui manager, otherwise context actions like onClick will NOT work!

```Java
AbyssalLib.GUI_MANAGER.openGui(player new MyMenu());
```

Thats basically it!, you have successfully made you gui