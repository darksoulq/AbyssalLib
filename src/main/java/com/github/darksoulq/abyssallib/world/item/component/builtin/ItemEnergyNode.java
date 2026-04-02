package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.energy.EnergyNode;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.DataComponentType;

/**
 * {@link DataComponent} implementation storing an {@link EnergyNode} inside an item.
 *
 * <p>This allows items to carry fully functional energy containers, enabling
 * portable storage, transfer, and interaction with energy systems.</p>
 */
public class ItemEnergyNode extends DataComponent<EnergyNode> {

    /**
     * Codec used to serialize and deserialize this component.
     */
    public static final Codec<ItemEnergyNode> CODEC = EnergyNode.CODEC.xmap(ItemEnergyNode::new, ItemEnergyNode::getValue);

    /**
     * The registered component type for item energy nodes.
     */
    public static final DataComponentType<ItemEnergyNode> TYPE = DataComponentType.simple(CODEC);

    /**
     * Creates a new component wrapping the given energy node.
     *
     * @param container the energy node instance stored in the item
     */
    public ItemEnergyNode(EnergyNode container) {
        super(container);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataComponentType<?> getType() {
        return TYPE;
    }
}