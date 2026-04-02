package com.github.darksoulq.abyssallib.world.data.loot;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Represents a complete loot table capable of synthesizing item collections and intelligently
 * populating external containers using context-driven probability logic.
 */
public class LootTable {

    /**
     * The internal collection of configured loot pools dictating sequential evaluation phases.
     */
    private final List<LootPool> pools;

    /**
     * The targeted configuration strategy resolving potential overlap when mirroring vanilla namespace identities.
     */
    private final MergeStrategy mergeStrategy;

    /**
     * The optional designated string identifier establishing native parity overrides.
     */
    private final String vanillaId;

    /**
     * Constructs a new LootTable incorporating probability pools and structural overrides.
     *
     * @param pools         The collection of functional pools generating drops.
     * @param mergeStrategy The conflict resolution strategy mapped against vanilla identifiers.
     * @param vanillaId     The targeted namespace mapping dictating override bounds.
     */
    public LootTable(List<LootPool> pools, MergeStrategy mergeStrategy, @Nullable String vanillaId) {
        this.pools = pools;
        this.mergeStrategy = mergeStrategy;
        this.vanillaId = vanillaId;
    }

    /**
     * Evaluates probabilistic algorithms to materialize a discrete list of items.
     *
     * @param context The functional environment encompassing actors and mathematical modifiers.
     * @return A list of items synthesized across all constituent pools.
     */
    public List<ItemStack> generate(LootContext context) {
        List<ItemStack> items = new ArrayList<>();
        for (LootPool pool : pools) {
            pool.generate(context, items::add);
        }
        return items;
    }

    /**
     * Generates and distributes physical loot dynamically across a target inventory, strictly
     * adhering to mathematical slot availability thresholds.
     *
     * @param inventory The operational inventory target receiving the synthesized payload.
     * @param context   The functional environment governing generation variables.
     */
    public void fill(Inventory inventory, LootContext context) {
        List<ItemStack> items = generate(context);
        if (items.isEmpty()) return;

        List<Integer> availableSlots = new ArrayList<>();
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack current = inventory.getItem(i);
            if (current == null || current.getType().isAir()) {
                availableSlots.add(i);
            }
        }

        Collections.shuffle(availableSlots, context.random());
        Iterator<Integer> slotIterator = availableSlots.iterator();

        for (ItemStack item : items) {
            if (!slotIterator.hasNext()) break;
            inventory.setItem(slotIterator.next(), item);
        }
    }

    /**
     * Retrieves the conflict resolution strategy bridging procedural and structural overlaps.
     *
     * @return The operational merge strategy configuration.
     */
    public MergeStrategy getMergeStrategy() {
        return mergeStrategy;
    }

    /**
     * Retrieves the linked internal namespace key utilized by vanilla parity overlays.
     *
     * @return The targeted string identifier, or null.
     */
    @Nullable
    public String getVanillaId() {
        return vanillaId;
    }

    /**
     * The codec managing the serialization conversion linking active memory representations to storage payloads.
     */
    public static final Codec<LootTable> CODEC = new Codec<>() {
        @Override
        public <D> LootTable decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            List<LootPool> pools = LootPool.CODEC.list().decode(ops, map.get(ops.createString("pools")));
            MergeStrategy strategy = Codec.enumCodec(MergeStrategy.class).orElse(MergeStrategy.NONE).decode(ops, map.get(ops.createString("merge_strategy")));
            Optional<String> vid = Codecs.STRING.optional().decode(ops, map.get(ops.createString("vanilla_id")));
            return new LootTable(pools, strategy, vid.orElse(null));
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, LootTable value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("pools"), LootPool.CODEC.list().encode(ops, value.pools));
            map.put(ops.createString("merge_strategy"), Codec.enumCodec(MergeStrategy.class).encode(ops, value.mergeStrategy));
            if (value.vanillaId != null) {
                map.put(ops.createString("vanilla_id"), Codecs.STRING.encode(ops, value.vanillaId));
            }
            return ops.createMap(map);
        }
    };
}