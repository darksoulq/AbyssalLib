package com.github.darksoulq.abyssallib.world.menu;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface ContainerListener {
    void containerChanged(Container container);
}