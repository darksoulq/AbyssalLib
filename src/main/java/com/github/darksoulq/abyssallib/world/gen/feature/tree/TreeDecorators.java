package com.github.darksoulq.abyssallib.world.gen.feature.tree;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.gen.feature.tree.decorator.*;

public class TreeDecorators {
    public static final DeferredRegistry<TreeDecoratorType<?>> TREE_DECORATORS = DeferredRegistry.create(Registries.TREE_DECORATORS, AbyssalLib.PLUGIN_ID);

    public static final TreeDecoratorType<?> ALTER_GROUND = TREE_DECORATORS.register("alter_ground", id -> AlterGroundDecorator.TYPE);
    public static final TreeDecoratorType<?> LEAVE_VINE = TREE_DECORATORS.register("leave_vine", id -> LeaveVineDecorator.TYPE);
    public static final TreeDecoratorType<?> TRUNK_VINE = TREE_DECORATORS.register("trunk_vine", id -> TrunkVineDecorator.TYPE);
    public static final TreeDecoratorType<?> COCOA_BEAN = TREE_DECORATORS.register("cocoa_bean", id -> CocoaBeanDecorator.TYPE);
    public static final TreeDecoratorType<?> BEE_NEST = TREE_DECORATORS.register("bee_nest", id -> BeeNestDecorator.TYPE);
}