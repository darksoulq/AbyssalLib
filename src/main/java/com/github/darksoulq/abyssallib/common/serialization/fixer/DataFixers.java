package com.github.darksoulq.abyssallib.common.serialization.fixer;

import com.github.darksoulq.abyssallib.AbyssalLib;
import net.kyori.adventure.key.Key;

public final class DataFixers {
    public static final DataFixerRegistry FIXERS = new DataFixerRegistry(1);

    public static final Key STRUCTURE = FIXERS.registerFixer(Key.key(AbyssalLib.PLUGIN_ID, "structure"), 0, DataFixer.compose());
}
