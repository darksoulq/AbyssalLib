package com.github.darksoulq.abyssallib.world.structure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.serialization.BlockInfo;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.serialization.SavedEntity;
import com.github.darksoulq.abyssallib.common.serialization.ops.JsonOps;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.gen.NMSWorldGenAccess;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenUtils;
import com.github.darksoulq.abyssallib.world.structure.processor.StructureProcessor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a fully captured, transformable, and replayable structure definition.
 * This implementation supports palette-based block storage for deduplication,
 * relative positioning for blocks and entities, transformation via rotations and
 * mirrors, and both synchronous and asynchronous placement routines.
 */
public class Structure {

    /**
     * Shared {@link JsonNodeFactory} used to efficiently construct JSON nodes during serialization.
     */
    private static final JsonNodeFactory FACTORY = JsonNodeFactory.instance;

    /**
     * Palette of unique block state entries.
     * Each entry represents a unique block identifier and its optional state data to prevent
     * redundant data storage across the structure.
     */
    private final List<PaletteEntry> palette = new ArrayList<>();

    /**
     * Collection of blocks stored in this structure.
     * Each block acts as a lightweight pointer referencing an index within the {@link #palette}.
     */
    private final List<StructureBlock> blocks = new ArrayList<>();

    /**
     * Collection of entities stored in this structure, mapped with relative positions.
     */
    private final List<StructureEntity> entities = new ArrayList<>();

    /**
     * List of processors applied sequentially during block placement to alter or filter the layout.
     */
    private final List<StructureProcessor> processors = new ArrayList<>();

    /**
     * Dimensions of the structure encapsulated as width, height, and depth.
     */
    private Vector size;

    /**
     * Captures all blocks within the defined region, excluding entities.
     *
     * @param corner1
     * The first corner {@link Location} bounding the region.
     * @param corner2
     * The opposite corner {@link Location} bounding the region.
     * @param origin
     * The designated origin {@link Location} utilized for relative positioning.
     */
    public void fill(@NotNull Location corner1, @NotNull Location corner2, @NotNull Location origin) {
        fill(corner1, corner2, origin, true);
    }

    /**
     * Captures blocks and optionally entities within the defined physical region.
     *
     * @param corner1
     * The first corner {@link Location} bounding the region.
     * @param corner2
     * The opposite corner {@link Location} bounding the region.
     * @param origin
     * The origin {@link Location} used for zero-point relative alignment.
     * @param includeEntities
     * True if entities within the bounding box should be captured and serialized.
     */
    public void fill(@NotNull Location corner1, @NotNull Location corner2, @NotNull Location origin, boolean includeEntities) {
        palette.clear();
        blocks.clear();
        entities.clear();

        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int minY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int maxY = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        this.size = new Vector(maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1);

        int originX = origin.getBlockX();
        int originY = origin.getBlockY();
        int originZ = origin.getBlockZ();

        Map<PaletteEntry, Integer> paletteLookup = new HashMap<>();

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {

                    Block block = corner1.getWorld().getBlockAt(x, y, z);

                    if (block.getType() == Material.STRUCTURE_VOID) {
                        continue;
                    }

                    BlockInfo info = BlockInfo.resolve(block);
                    PaletteEntry entry = new PaletteEntry(info.getAsString(), info.states());

                    int paletteIndex = paletteLookup.computeIfAbsent(entry, k -> {
                        palette.add(k);
                        return palette.size() - 1;
                    });

                    blocks.add(new StructureBlock(
                        x - originX,
                        y - originY,
                        z - originZ,
                        paletteIndex,
                        info.properties(),
                        info.nbt()
                    ));
                }
            }
        }

        if (includeEntities) {
            BoundingBox box = BoundingBox.of(corner1, corner2);
            for (org.bukkit.entity.Entity entity : corner1.getWorld().getNearbyEntities(box)) {
                if (entity instanceof Player) {
                    continue;
                }
                Vector relativePos = entity.getLocation().toVector().subtract(origin.toVector());
                SavedEntity savedEntity = SavedEntity.create(entity, JsonOps.INSTANCE);
                this.entities.add(new StructureEntity(relativePos, savedEntity));
            }
        }
    }

    /**
     * Places this structure asynchronously using a scheduled repeating task to prevent server lag.
     *
     * @param plugin
     * The {@link Plugin} utilized for scheduling the Bukkit task.
     * @param origin
     * The target placement origin {@link Location}.
     * @param rotation
     * The {@link StructureRotation} to apply.
     * @param mirror
     * The {@link Mirror} transformation to apply.
     * @param integrity
     * The survival chance [0.0 - 1.0] for each individual block.
     * @param blocksPerTick
     * The maximum number of blocks to process per server tick.
     * @return
     * A {@link CompletableFuture} that resolves when the entire placement operation completes.
     */
    public CompletableFuture<Void> placeAsync(@NotNull Plugin plugin, @NotNull Location origin, @NotNull StructureRotation rotation, @NotNull Mirror mirror, float integrity, int blocksPerTick) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        new Paster(origin, rotation, mirror, integrity, blocksPerTick, future).runTaskTimer(plugin, 0L, 1L);
        return future;
    }

    /**
     * Places this structure synchronously in a live Bukkit world.
     *
     * @param origin
     * The target placement origin {@link Location}.
     * @param rotation
     * The {@link StructureRotation} to apply.
     * @param mirror
     * The {@link Mirror} transformation to apply.
     * @param integrity
     * The survival chance [0.0 - 1.0] for each individual block.
     */
    public void place(@NotNull Location origin, @NotNull StructureRotation rotation, @NotNull Mirror mirror, float integrity) {
        Random random = new Random();
        BlockData[] bakedData = new BlockData[palette.size()];
        CustomBlock[] bakedCustom = new CustomBlock[palette.size()];
        bakePalette(bakedData, bakedCustom, rotation, mirror);

        for (StructureBlock sb : blocks) {
            if (integrity < 1.0f && random.nextFloat() > integrity) {
                continue;
            }
            processStructureBlock(null, origin, sb, rotation, mirror, bakedData, bakedCustom, null);
        }
        placeEntities(null, origin, rotation, mirror, entities, null);
    }

    /**
     * Places this structure inside an asynchronous or virtual {@link WorldGenAccess} context.
     * Handles deferring out-of-bounds placements back to the main thread if strictly required.
     *
     * @param level
     * The {@link WorldGenAccess} bridging chunk generation.
     * @param origin
     * The target placement origin {@link Location}.
     * @param rotation
     * The {@link StructureRotation} to apply.
     * @param mirror
     * The {@link Mirror} transformation to apply.
     * @param integrity
     * The survival chance [0.0 - 1.0] for each individual block.
     */
    public void place(@NotNull WorldGenAccess level, @NotNull Location origin, @NotNull StructureRotation rotation, @NotNull Mirror mirror, float integrity) {
        Random random = new Random();
        List<StructureBlock> deferredBlocks = new ArrayList<>();
        List<StructureEntity> deferredEntities = new ArrayList<>();

        BlockData[] bakedData = new BlockData[palette.size()];
        CustomBlock[] bakedCustom = new CustomBlock[palette.size()];
        bakePalette(bakedData, bakedCustom, rotation, mirror);

        for (StructureBlock sb : blocks) {
            if (integrity < 1.0f && random.nextFloat() > integrity) {
                continue;
            }
            processStructureBlock(level, origin, sb, rotation, mirror, bakedData, bakedCustom, deferredBlocks);
        }

        placeEntities(level, origin, rotation, mirror, entities, deferredEntities);

        if (!deferredBlocks.isEmpty() || !deferredEntities.isEmpty()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (StructureBlock sb : deferredBlocks) {
                        processStructureBlock(null, origin, sb, rotation, mirror, bakedData, bakedCustom, null);
                    }
                    placeEntities(null, origin, rotation, mirror, deferredEntities, null);
                }
            }.runTask(AbyssalLib.getInstance());
        }
    }

    /**
     * Appends a {@link StructureProcessor} to the placement pipeline.
     *
     * @param processor
     * The {@link StructureProcessor} to add.
     */
    public void addProcessor(StructureProcessor processor) {
        processors.add(processor);
    }

    /**
     * Pre-calculates transformed BlockData instances for every entry in the palette.
     * This avoids redundant calculations during the placement loop.
     *
     * @param dataOut
     * The output array for baked vanilla {@link BlockData}.
     * @param customOut
     * The output array for baked {@link CustomBlock} objects.
     * @param rotation
     * The specified {@link StructureRotation}.
     * @param mirror
     * The specified {@link Mirror} transform.
     */
    private void bakePalette(BlockData[] dataOut, CustomBlock[] customOut, StructureRotation rotation, Mirror mirror) {
        for (int i = 0; i < palette.size(); i++) {
            PaletteEntry entry = palette.get(i);
            Object blockObj = null;
            if (entry.id().startsWith("minecraft:")) {
                try {
                    Material mat = Material.valueOf(entry.id().substring(10).toUpperCase());
                    if (mat.isBlock()) {
                        blockObj = mat.createBlockData();
                    }
                } catch (Exception ignored) {}
            } else {
                CustomBlock cb = Registries.BLOCKS.get(entry.id());
                if (cb != null) {
                    blockObj = cb.clone();
                }
            }

            if (blockObj != null) {
                BlockInfo temp = new BlockInfo(null, blockObj, entry.states(), null, null);
                BlockData bd = WorldGenUtils.bakeData(temp, rotation, mirror);
                if (bd instanceof Leaves leaves) {
                    leaves.setPersistent(true);
                }
                dataOut[i] = bd;
                if (blockObj instanceof CustomBlock cb) {
                    customOut[i] = cb;
                }
            }
        }
    }

    /**
     * Core routine for evaluating, transforming, processing, and executing the placement of a single block.
     *
     * @param level
     * The generation context, or null for a live Bukkit world.
     * @param origin
     * The absolute origin {@link Location}.
     * @param sb
     * The lightweight {@link StructureBlock} reference.
     * @param rotation
     * The structural rotation logic.
     * @param mirror
     * The structural mirror logic.
     * @param bakedData
     * The cached array of vanilla block data.
     * @param bakedCustom
     * The cached array of custom block references.
     * @param deferred
     * A collection to catch blocks that fall outside safe generation chunk boundaries.
     */
    private void processStructureBlock(WorldGenAccess level, Location origin, StructureBlock sb, StructureRotation rotation, Mirror mirror, BlockData[] bakedData, CustomBlock[] bakedCustom, List<StructureBlock> deferred) {
        if (sb.stateIndex() < 0 || sb.stateIndex() >= palette.size()) {
            return;
        }
        BlockData bd = bakedData[sb.stateIndex()];
        if (bd == null) {
            return;
        }

        Vector transformed = transform(new Vector(sb.x(), sb.y(), sb.z()), mirror, rotation);
        Location target = origin.clone().add(transformed);

        if (target.getBlockY() < origin.getWorld().getMinHeight() || target.getBlockY() >= origin.getWorld().getMaxHeight()) {
            return;
        }

        if (level instanceof NMSWorldGenAccess nmsLevel && !nmsLevel.isInRegion(target.getBlockX(), target.getBlockY(), target.getBlockZ())) {
            if (deferred != null) {
                deferred.add(sb);
            }
            return;
        }

        if (processors.isEmpty() && sb.nbt() == null && sb.properties() == null) {
            if (level == null && !target.isChunkLoaded()) {
                target.getChunk().load(true);
            }
            BlockData current = level != null ? level.getBlockData(target.getBlockX(), target.getBlockY(), target.getBlockZ()) : target.getBlock().getBlockData();
            if (current.matches(bd)) {
                return;
            }

            CustomBlock cb = bakedCustom[sb.stateIndex()];
            WorldGenUtils.placeBlock(level, target, new BlockInfo(transformed, cb != null ? cb : bd, palette.get(sb.stateIndex()).states(), null, null), bd.clone(), cb != null ? cb.clone() : null);
        } else {
            CustomBlock cb = bakedCustom[sb.stateIndex()];
            BlockInfo original = new BlockInfo(null, cb != null ? cb : bd, palette.get(sb.stateIndex()).states(), sb.properties(), sb.nbt());
            BlockInfo current = new BlockInfo(transformed, cb != null ? cb : bd, palette.get(sb.stateIndex()).states(), sb.properties(), sb.nbt());

            for (StructureProcessor processor : processors) {
                current = (level != null) ? processor.process(level, origin, current, original) : processor.process(origin.getWorld(), origin, current, original);
                if (current == null) {
                    return;
                }
            }
            if (current.pos() == null) {
                return;
            }

            Location finalTarget = origin.clone().add(current.pos());
            if (finalTarget.getBlockY() < origin.getWorld().getMinHeight() || finalTarget.getBlockY() >= origin.getWorld().getMaxHeight()) {
                return;
            }

            if (level instanceof NMSWorldGenAccess nmsLevel && !nmsLevel.isInRegion(finalTarget.getBlockX(), finalTarget.getBlockY(), finalTarget.getBlockZ())) {
                final BlockInfo finalCurrent = current;
                new BukkitRunnable() {
                    public void run() {
                        if (!finalTarget.isChunkLoaded()) {
                            finalTarget.getChunk().load(true);
                        }
                        BlockData newBd = WorldGenUtils.bakeData(finalCurrent, rotation, mirror);
                        CustomBlock newCb = finalCurrent.block() instanceof CustomBlock c ? c.clone() : null;
                        WorldGenUtils.placeBlock(null, finalTarget, finalCurrent, newBd, newCb);
                    }
                }.runTask(AbyssalLib.getInstance());
                return;
            }

            if (level == null && !finalTarget.isChunkLoaded()) {
                finalTarget.getChunk().load(true);
            }
            BlockData newBd = WorldGenUtils.bakeData(current, rotation, mirror);
            CustomBlock newCb = current.block() instanceof CustomBlock c ? c.clone() : null;
            WorldGenUtils.placeBlock(level, finalTarget, current, newBd, newCb);
        }
    }

    /**
     * Transforms and spawns all stored entities tied to the structure.
     *
     * @param level
     * The generation context. Null if live Bukkit placement.
     * @param origin
     * The placement origin {@link Location}.
     * @param rotation
     * The spatial rotation.
     * @param mirror
     * The spatial mirror transform.
     * @param ents
     * The source list of {@link StructureEntity} definitions.
     * @param deferred
     * Collection to hold entities spawned out of bounds during async generation.
     */
    private void placeEntities(WorldGenAccess level, Location origin, StructureRotation rotation, Mirror mirror, List<StructureEntity> ents, List<StructureEntity> deferred) {
        for (StructureEntity se : ents) {
            Vector transformed = transformEntityPos(se.pos().clone(), mirror, rotation);
            Location target = origin.clone().add(transformed);

            if (target.getBlockY() < origin.getWorld().getMinHeight() || target.getBlockY() >= origin.getWorld().getMaxHeight()) {
                continue;
            }

            if (level instanceof NMSWorldGenAccess nmsLevel && !nmsLevel.isInRegion(target.getBlockX(), target.getBlockY(), target.getBlockZ())) {
                if (deferred != null) {
                    deferred.add(se);
                }
                continue;
            }

            if (level != null) {
                se.entity().spawn(level, target);
            } else {
                if (!target.isChunkLoaded()) {
                    target.getChunk().load(true);
                }
                se.entity().spawn(target);
            }
        }
    }

    /**
     * Applies matrix transformations (mirroring and rotation) to a discrete block vector.
     *
     * @param pos
     * The original relative {@link Vector} position.
     * @param mirror
     * The applied {@link Mirror}.
     * @param rotation
     * The applied {@link StructureRotation}.
     * @return
     * The newly calculated offset {@link Vector}.
     */
    private Vector transform(Vector pos, Mirror mirror, StructureRotation rotation) {
        int x = pos.getBlockX();
        int y = pos.getBlockY();
        int z = pos.getBlockZ();

        int sizeX = size.getBlockX();
        int sizeZ = size.getBlockZ();

        switch (mirror) {
            case LEFT_RIGHT -> z = sizeZ - 1 - z;
            case FRONT_BACK -> x = sizeX - 1 - x;
        }

        int newX = x;
        int newZ = z;

        switch (rotation) {
            case CLOCKWISE_90 -> {
                newX = sizeZ - 1 - z;
                newZ = x;
            }
            case CLOCKWISE_180 -> {
                newX = sizeX - 1 - x;
                newZ = sizeZ - 1 - z;
            }
            case COUNTERCLOCKWISE_90 -> {
                newX = z;
                newZ = sizeX - 1 - x;
            }
        }

        return new Vector(newX, y, newZ);
    }

    /**
     * Applies floating-point matrix transformations to an entity's relative position.
     *
     * @param pos
     * The original relative {@link Vector} position.
     * @param mirror
     * The applied {@link Mirror}.
     * @param rotation
     * The applied {@link StructureRotation}.
     * @return
     * The newly calculated offset {@link Vector}.
     */
    private Vector transformEntityPos(Vector pos, Mirror mirror, StructureRotation rotation) {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        double sizeX = size.getBlockX();
        double sizeZ = size.getBlockZ();

        switch (mirror) {
            case LEFT_RIGHT -> z = sizeZ - z;
            case FRONT_BACK -> x = sizeX - x;
        }

        double newX = x;
        double newZ = z;

        switch (rotation) {
            case CLOCKWISE_90 -> {
                newX = sizeZ - z;
                newZ = x;
            }
            case CLOCKWISE_180 -> {
                newX = sizeX - x;
                newZ = sizeZ - z;
            }
            case COUNTERCLOCKWISE_90 -> {
                newX = z;
                newZ = sizeX - x;
            }
        }

        return new Vector(newX, y, newZ);
    }

    /**
     * Serializes this entire structure into an optimized JSON node tree.
     *
     * @return
     * The serialized Jackson {@link ObjectNode}.
     */
    public ObjectNode serialize() {
        ObjectNode root = FACTORY.objectNode();
        root.putPOJO("size", new int[]{size.getBlockX(), size.getBlockY(), size.getBlockZ()});

        ArrayNode paletteArray = root.putArray("palette");
        for (PaletteEntry entry : palette) {
            ObjectNode node = paletteArray.addObject();
            node.put("Name", entry.id());
            if (entry.states() != null) {
                node.set("States", entry.states());
            }
        }

        ArrayNode blockArray = root.putArray("blocks");
        for (StructureBlock sb : blocks) {
            ObjectNode b = blockArray.addObject();
            b.putArray("pos").add(sb.x()).add(sb.y()).add(sb.z());
            b.put("state", sb.stateIndex());
            if (sb.properties() != null) {
                b.set("properties", sb.properties());
            }
            if (sb.nbt() != null) {
                b.set("nbt", sb.nbt());
            }
        }

        if (!entities.isEmpty()) {
            ArrayNode entityArray = root.putArray("entities");
            for (StructureEntity se : entities) {
                ObjectNode eNode = entityArray.addObject();
                eNode.putArray("pos").add(se.pos().getX()).add(se.pos().getY()).add(se.pos().getZ());
                try {
                    eNode.set("data", (JsonNode) ExtraCodecs.SAVED_ENTITY.encode(JsonOps.INSTANCE, se.entity()));
                } catch (Exception ignored) {}
            }
        }

        return root;
    }

    /**
     * Deserializes a complete {@link Structure} definition from a JSON node tree.
     *
     * @param root
     * The parsed root {@link JsonNode} generated by Jackson.
     * @return
     * The reconstructed {@link Structure} instance.
     */
    public static Structure deserialize(JsonNode root) {
        Structure structure = new Structure();

        if (root.has("size")) {
            JsonNode s = root.get("size");
            structure.size = new Vector(s.get(0).asInt(), s.get(1).asInt(), s.get(2).asInt());
        }

        if (root.has("palette")) {
            for (JsonNode node : root.get("palette")) {
                structure.palette.add(new PaletteEntry(
                    node.get("Name").asText(),
                    node.has("States") ? (ObjectNode) node.get("States") : null
                ));
            }
        }

        if (root.has("blocks")) {
            for (JsonNode node : root.get("blocks")) {
                JsonNode pos = node.get("pos");
                structure.blocks.add(new StructureBlock(
                    pos.get(0).asInt(),
                    pos.get(1).asInt(),
                    pos.get(2).asInt(),
                    node.get("state").asInt(),
                    node.has("properties") ? (ObjectNode) node.get("properties") : null,
                    node.has("nbt") ? (ObjectNode) node.get("nbt") : null
                ));
            }
        }

        if (root.has("entities")) {
            for (JsonNode node : root.get("entities")) {
                JsonNode pos = node.get("pos");
                Vector vector = new Vector(pos.get(0).asDouble(), pos.get(1).asDouble(), pos.get(2).asDouble());
                try {
                    SavedEntity saved = ExtraCodecs.SAVED_ENTITY.decode(JsonOps.INSTANCE, node.get("data"));
                    structure.entities.add(new StructureEntity(vector, saved));
                } catch (Exception ignored) {}
            }
        }

        return structure;
    }

    /**
     * Internal Runnable class responsible for throttling asynchronous structure placement
     * to avoid stressing the server thread during massive procedural generations.
     */
    private class Paster extends BukkitRunnable {

        /** The base reference location for placement offsets. */
        private final Location origin;

        /** The spatial rotation requested. */
        private final StructureRotation rotation;

        /** The spatial mirroring requested. */
        private final Mirror mirror;

        /** The probability threshold for placing each block. */
        private final float integrity;

        /** The maximum number of blocks the task is allowed to place per tick. */
        private final int limit;

        /** The future to complete once all blocks and entities are successfully placed. */
        private final CompletableFuture<Void> future;

        /** The internal cursor state used across repeating task executions. */
        private final Iterator<StructureBlock> iterator;

        /** Generates pseudo-random numbers to evaluate block integrity loss. */
        private final Random random = new Random();

        /** Internally cached baked vanilla block data to speed up placement logic. */
        private final BlockData[] bakedData;

        /** Internally cached baked custom block states to speed up placement logic. */
        private final CustomBlock[] bakedCustom;

        /**
         * Constructs a new chunk-safe, asynchronous structure paster task.
         *
         * @param origin
         * The base placement {@link Location}.
         * @param rotation
         * The active {@link StructureRotation}.
         * @param mirror
         * The active {@link Mirror} transformation.
         * @param integrity
         * The placement consistency threshold [0-1].
         * @param limit
         * The upper bound on blocks placed per execution slice.
         * @param future
         * The {@link CompletableFuture} to signal termination.
         */
        public Paster(Location origin, StructureRotation rotation, Mirror mirror, float integrity, int limit, CompletableFuture<Void> future) {
            this.origin = origin;
            this.rotation = rotation;
            this.mirror = mirror;
            this.integrity = integrity;
            this.limit = limit;
            this.future = future;
            this.iterator = blocks.iterator();
            this.bakedData = new BlockData[palette.size()];
            this.bakedCustom = new CustomBlock[palette.size()];
            bakePalette(bakedData, bakedCustom, rotation, mirror);
        }

        /**
         * Executes a single time-slice of the placement algorithm.
         */
        @Override
        public void run() {
            int count = 0;

            while (iterator.hasNext() && count < limit) {
                StructureBlock sb = iterator.next();
                if (integrity < 1.0f && random.nextFloat() > integrity) {
                    continue;
                }

                processStructureBlock(null, origin, sb, rotation, mirror, bakedData, bakedCustom, null);
                count++;
            }

            if (!iterator.hasNext()) {
                placeEntities(null, origin, rotation, mirror, entities, null);
                future.complete(null);
                cancel();
            }
        }
    }

    /**
     * A lightweight data record describing a unique combination of block type and properties.
     *
     * @param id
     * The namespaced identifier for the block material or custom implementation.
     * @param states
     * Serialized block-states in JSON format (nullable).
     */
    private record PaletteEntry(String id, @Nullable ObjectNode states) {}

    /**
     * A structural marker indicating the position and index configuration of a discrete block.
     *
     * @param x
     * The relative integer offset on the X axis.
     * @param y
     * The relative integer offset on the Y axis.
     * @param z
     * The relative integer offset on the Z axis.
     * @param stateIndex
     * The pointer index resolving to a definition inside the structure palette.
     * @param properties
     * Extra custom NBT-like properties bound directly to the block logic.
     * @param nbt
     * Standard vanilla tile-entity data (nullable).
     */
    private record StructureBlock(int x, int y, int z, int stateIndex, @Nullable ObjectNode properties, @Nullable ObjectNode nbt) {}

    /**
     * A structural marker holding a serialized entity and its exact relative spatial location.
     *
     * @param pos
     * The floating-point {@link Vector} detailing position offsets.
     * @param entity
     * The heavily abstracted {@link SavedEntity} container representing the deserialized mob or object.
     */
    public record StructureEntity(Vector pos, SavedEntity entity) {}
}