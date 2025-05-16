package com.github.darksoulq.abyssallib.event.context.block;

import com.github.darksoulq.abyssallib.event.context.Context;
import org.bukkit.event.block.BlockExplodeEvent;

public class BlockExplodeContext extends Context<BlockExplodeEvent> implements ExplodeContext {
    private boolean shouldExplode = false;

    public BlockExplodeContext(BlockExplodeEvent event) {
        super(event);
    }

    @Override
    public boolean shouldExplode() {
        return shouldExplode;
    }

    public void shouldExplode(boolean v) {
        shouldExplode = v;
    }
}
