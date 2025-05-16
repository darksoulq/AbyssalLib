package com.github.darksoulq.abyssallib.event.context.block;

import com.github.darksoulq.abyssallib.event.context.Context;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EntityExplodeContext extends Context<EntityExplodeEvent> implements ExplodeContext {
    private boolean shouldExplode = false;

    public EntityExplodeContext(EntityExplodeEvent event) {
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
