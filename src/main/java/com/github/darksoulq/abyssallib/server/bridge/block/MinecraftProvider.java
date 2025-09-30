package com.github.darksoulq.abyssallib.server.bridge.block;

import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.bridge.Provider;
import org.bukkit.Material;

import java.util.Locale;

public class MinecraftProvider extends Provider<BridgeBlock<?>> {
    public MinecraftProvider() {
        super("minecraft");
    }

    @Override
    public boolean belongs(BridgeBlock<?> value) {
        return value.value() instanceof Material;
    }

    @Override
    public Identifier getId(BridgeBlock<?> value) {
        if (!(value.value() instanceof Material mat)) return null;
        return Identifier.of(getPrefix(), mat.name().toLowerCase(Locale.ROOT));
    }

    @Override
    public BridgeBlock<?> get(Identifier id) {
        return new BridgeBlock<>(id, getPrefix(), Material.valueOf(id.getPath().toUpperCase(Locale.ROOT)));
    }
}
