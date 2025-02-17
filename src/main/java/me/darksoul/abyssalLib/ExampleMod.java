package me.darksoul.abyssalLib;

import me.darksoul.abyssalLib.item.ExampleItem;

public class ExampleMod extends AMod {

    public ExampleMod() {
        super(AbyssalLib.getInstance());
    }

    @Override
    public void setRegistries() {
        register(RegistryType.ITEM, new ExampleItem());
    }
}
