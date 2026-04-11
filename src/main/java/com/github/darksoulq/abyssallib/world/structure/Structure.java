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
 *
 * <p>This implementation supports:
 * <ul>
 *     <li>Palette-based block storage for deduplication</li>
 *     <li>Relative positioning for blocks and entities</li>
 *     <li>Transformation via {@link StructureRotation} and {@link Mirror}</li>
 *     <li>Synchronous and asynchronous placement</li>
 *     <li>Custom processing via {@link StructureProcessor}</li>
 *     <li>JSON serialization and deserialization</li>
 * </ul>
 */
public class Structure {

    /**
     * Shared {@link JsonNodeFactory} used to construct JSON nodes.
     */
    private static final JsonNodeFactory FACTORY = JsonNodeFactory.instance;

    /**
     * Palette of unique block state entries.
     *
     * <p>Each entry represents a unique block identifier and optional state data.</p>
     */
    private final List<PaletteEntry> palette = new ArrayList<>();

    /**
     * Collection of blocks stored in this structure.
     *
     * <p>Each block references an index into {@link #palette}.</p>
     */
    private final List<StructureBlock> blocks = new ArrayList<>();

    /**
     * Collection of entities stored in this structure.
     */
    private final List<StructureEntity> entities = new ArrayList<>();

    /**
     * List of processors applied during placement.
     */
    private final List<StructureProcessor> processors = new ArrayList<>();

    /**
     * Dimensions of the structure (width, height, depth).
     */
    private Vector size;

    /**
     * Captures all blocks within the defined region.
     *
     * @param corner1 the first corner of the region
     * @param corner2 the opposite corner of the region
     * @param origin  the origin used for relative positioning
     */
    public void fill(@NotNull Location corner1, @NotNull Location corner2, @NotNull Location origin) {
        fill(corner1, corner2, origin, true);
    }

    /**
     * Captures blocks and optionally entities within the defined region.
     *
     * @param corner1         the first corner of the region
     * @param corner2         the opposite corner of the region
     * @param origin          the origin used for relative positioning
     * @param includeEntities whether entities should also be captured
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

                    if (block.getType() == Material.STRUCTURE_VOID) continue;

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
                if (entity instanceof Player) continue;
                Vector relativePos = entity.getLocation().toVector().subtract(origin.toVector());
                SavedEntity savedEntity = SavedEntity.create(entity, JsonOps.INSTANCE);
                this.entities.add(new StructureEntity(relativePos, savedEntity));
            }
        }
    }

    /**
     * Places this structure asynchronously using a scheduled task.
     *
     * @param plugin        the plugin used for scheduling
     * @param origin        the placement origin
     * @param rotation      the rotation to apply
     * @param mirror        the mirror transformation
     * @param integrity     chance [0–1] for each block to be placed
     * @param blocksPerTick maximum number of blocks placed per tick
     * @return a {@link CompletableFuture} that completes when placement finishes
     */
    public CompletableFuture<Void> placeAsync(@NotNull Plugin plugin, @NotNull Location origin, @NotNull StructureRotation rotation, @NotNull Mirror mirror, float integrity, int blocksPerTick) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        new Paster(origin, rotation, mirror, integrity, blocksPerTick, future)
            .runTaskTimer(plugin, 0L, 1L);
        return future;
    }

    /**
     * Places this structure synchronously in a Bukkit world.
     *
     * @param origin    the placement origin
     * @param rotation  the rotation to apply
     * @param mirror    the mirror transformation
     * @param integrity chance [0–1] for each block to be placed
     */
    public void place(@NotNull Location origin, @NotNull StructureRotation rotation, @NotNull Mirror mirror, float integrity) {
        Random random = new Random();

        for (StructureBlock sb : blocks) {
            if (integrity < 1.0f && random.nextFloat() > integrity) continue;
            Vector transformed = transform(new Vector(sb.x(), sb.y(), sb.z()), mirror, rotation);
            Location target = origin.clone().add(transformed);
            if (!target.isChunkLoaded()) target.getChunk().load(true);
            placeBlock(origin.getWorld(), origin, sb, rotation, mirror);
        }
        placeEntities(origin.getWorld(), origin, rotation, mirror, entities);
    }

    /**
     * Places this structure in a {@link WorldGenAccess} context.
     *
     * @param level     the world generation access
     * @param origin    the placement origin
     * @param rotation  the rotation to apply
     * @param mirror    the mirror transformation
     * @param integrity chance [0–1] for each block to be placed
     */
    public void place(@NotNull WorldGenAccess level, @NotNull Location origin, @NotNull StructureRotation rotation, @NotNull Mirror mirror, float integrity) {
        Random random = new Random();
        List<StructureBlock> deferredBlocks = new ArrayList<>();
        List<StructureEntity> deferredEntities = new ArrayList<>();

        boolean isNms = level instanceof NMSWorldGenAccess;
        NMSWorldGenAccess nmsLevel = isNms ? (NMSWorldGenAccess) level : null;

        for (StructureBlock sb : blocks) {
            if (integrity < 1.0f && random.nextFloat() > integrity) continue;

            Vector transformed = transform(new Vector(sb.x(), sb.y(), sb.z()), mirror, rotation);
            Location target = origin.clone().add(transformed);

            if (nmsLevel != null && !nmsLevel.isInRegion(target.getBlockX(), target.getBlockY(), target.getBlockZ())) {
                deferredBlocks.add(sb);
                continue;
            }
            placeBlock(level, origin, sb, rotation, mirror);
        }

        for (StructureEntity se : entities) {
            Vector transformed = transformEntityPos(se.pos().clone(), mirror, rotation);
            Location target = origin.clone().add(transformed);

            if (nmsLevel != null && !nmsLevel.isInRegion(target.getBlockX(), target.getBlockY(), target.getBlockZ())) {
                deferredEntities.add(se);
                continue;
            }
            placeEntities(level, origin, rotation, mirror, List.of(se));
        }

        if (!deferredBlocks.isEmpty() || !deferredEntities.isEmpty()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (StructureBlock sb : deferredBlocks) {
                        Vector transformed = transform(new Vector(sb.x(), sb.y(), sb.z()), mirror, rotation);
                        Location target = origin.clone().add(transformed);
                        if (!target.isChunkLoaded()) target.getChunk().load(true);
                        placeBlock(origin.getWorld(), origin, sb, rotation, mirror);
                    }
                    placeEntities(origin.getWorld(), origin, rotation, mirror, deferredEntities);
                }
            }.runTask(AbyssalLib.getInstance());
        }
    }

    /**
     * Adds a {@link StructureProcessor} to this structure.
     *
     * @param processor the processor to add
     */
    public void addProcessor(StructureProcessor processor) {
        processors.add(processor);
    }

    /**
     * Internal logic for placing a single block.
     *
     * @param worldOrLevel the world or generation context
     * @param origin       the placement origin
     * @param sb           the structure block definition
     * @param rotation     the rotation to apply
     * @param mirror       the mirror transformation
     */
    private void placeBlock(Object worldOrLevel, Location origin, StructureBlock sb, StructureRotation rotation, Mirror mirror) {
        if (sb.stateIndex() < 0 || sb.stateIndex() >= palette.size()) return;

        Vector transformed = transform(new Vector(sb.x(), sb.y(), sb.z()), mirror, rotation);
        PaletteEntry entry = palette.get(sb.stateIndex());

        Object blockObject = null;

        if (entry.id().startsWith("minecraft:")) {
            try {
                Material mat = Material.valueOf(entry.id().substring(10).toUpperCase());
                if (mat.isBlock()) blockObject = mat.createBlockData();
            } catch (Exception ignored) {}
        } else {
            CustomBlock cb = Registries.BLOCKS.get(entry.id());
            if (cb != null) blockObject = cb.clone();
        }

        if (blockObject == null) return;

        BlockInfo original = new BlockInfo(null, blockObject, entry.states(), sb.properties(), sb.nbt());
        BlockInfo current = new BlockInfo(transformed, blockObject, entry.states(), sb.properties(), sb.nbt());

        for (StructureProcessor processor : processors) {
            current = (worldOrLevel instanceof WorldGenAccess level)
                ? processor.process(level, origin, current, original)
                : processor.process((org.bukkit.World) worldOrLevel, origin, current, original);

            if (current == null) return;
        }

        if (current.pos() == null) return;

        Location target = origin.clone().add(current.pos());
        WorldGenAccess level = worldOrLevel instanceof WorldGenAccess w ? w : null;

        WorldGenUtils.placeBlock(level, target, current, rotation, mirror);
    }

    /**
     * Places all stored entities.
     *
     * @param worldOrLevel the world or generation context
     * @param origin       the placement origin
     * @param rotation     the rotation to apply
     * @param mirror       the mirror transformation
     * @param ents         the entities to place
     */
    private void placeEntities(Object worldOrLevel, Location origin, StructureRotation rotation, Mirror mirror, List<StructureEntity> ents) {
        for (StructureEntity se : ents) {
            Vector transformed = transformEntityPos(se.pos().clone(), mirror, rotation);
            Location target = origin.clone().add(transformed);

            if (worldOrLevel instanceof WorldGenAccess level) {
                se.entity().spawn(level, target);
            } else if (worldOrLevel instanceof org.bukkit.World world) {
                if (!target.isChunkLoaded()) target.getChunk().load(true);
                se.entity().spawn(target);
            }
        }
    }

    /**
     * Applies mirror and rotation transformations to a block position.
     *
     * @param pos      the original relative position
     * @param mirror   the mirror transformation
     * @param rotation the rotation transformation
     * @return the transformed position
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
     * Applies mirror and rotation transformations to an entity position.
     *
     * @param pos      the original relative position
     * @param mirror   the mirror transformation
     * @param rotation the rotation transformation
     * @return the transformed position
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
     * Serializes this structure into a JSON representation.
     *
     * @return the serialized {@link ObjectNode}
     */
    public ObjectNode serialize() {
        ObjectNode root = FACTORY.objectNode();
        root.putPOJO("size", new int[]{size.getBlockX(), size.getBlockY(), size.getBlockZ()});

        ArrayNode paletteArray = root.putArray("palette");
        for (PaletteEntry entry : palette) {
            ObjectNode node = paletteArray.addObject();
            node.put("Name", entry.id());
            if (entry.states() != null) node.set("States", entry.states());
        }

        ArrayNode blockArray = root.putArray("blocks");
        for (StructureBlock sb : blocks) {
            ObjectNode b = blockArray.addObject();
            b.putArray("pos").add(sb.x()).add(sb.y()).add(sb.z());
            b.put("state", sb.stateIndex());
            if (sb.properties() != null) b.set("properties", sb.properties());
            if (sb.nbt() != null) b.set("nbt", sb.nbt());
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
     * Deserializes a {@link Structure} from JSON.
     *
     * @param root the root JSON node
     * @return the reconstructed structure
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
     * Runnable responsible for asynchronous structure placement.
     */
    private class Paster extends BukkitRunnable {

        /** Placement origin. */
        private final Location origin;

        /** Rotation applied during placement. */
        private final StructureRotation rotation;

        /** Mirror transformation applied during placement. */
        private final Mirror mirror;

        /** Placement integrity value. */
        private final float integrity;

        /** Maximum blocks processed per tick. */
        private final int limit;

        /** Completion future. */
        private final CompletableFuture<Void> future;

        /** Iterator over structure blocks. */
        private final Iterator<StructureBlock> iterator;

        /** Random instance used for integrity checks. */
        private final Random random = new Random();

        /**
         * Constructs a new asynchronous paster.
         *
         * @param origin    the placement origin
         * @param rotation  the rotation to apply
         * @param mirror    the mirror transformation
         * @param integrity the placement chance
         * @param limit     blocks processed per tick
         * @param future    completion future
         */
        public Paster(Location origin, StructureRotation rotation, Mirror mirror, float integrity, int limit, CompletableFuture<Void> future) {
            this.origin = origin;
            this.rotation = rotation;
            this.mirror = mirror;
            this.integrity = integrity;
            this.limit = limit;
            this.future = future;
            this.iterator = blocks.iterator();
        }

        /**
         * Executes a single tick of placement.
         */
        @Override
        public void run() {
            int count = 0;

            while (iterator.hasNext() && count < limit) {
                StructureBlock sb = iterator.next();
                if (integrity < 1.0f && random.nextFloat() > integrity) continue;

                Vector transformed = transform(new Vector(sb.x(), sb.y(), sb.z()), mirror, rotation);
                Location target = origin.clone().add(transformed);

                if (!target.isChunkLoaded()) target.getChunk().load(true);

                placeBlock(origin.getWorld(), origin, sb, rotation, mirror);
                count++;
            }

            if (!iterator.hasNext()) {
                placeEntities(origin.getWorld(), origin, rotation, mirror, entities);
                future.complete(null);
                cancel();
            }
        }
    }

    /**
     * Represents a unique palette entry.
     *
     * @param id     the block identifier
     * @param states optional block state data
     */
    private record PaletteEntry(String id, @Nullable ObjectNode states) {}

    /**
     * Represents a block within the structure.
     *
     * @param x          relative X coordinate
     * @param y          relative Y coordinate
     * @param z          relative Z coordinate
     * @param stateIndex index into the palette
     * @param properties custom properties
     * @param nbt        tile entity data
     */
    private record StructureBlock(int x, int y, int z, int stateIndex, @Nullable ObjectNode properties, @Nullable ObjectNode nbt) {}

    /**
     * Represents a stored entity.
     *
     * @param pos    relative position
     * @param entity serialized entity data
     */
    public record StructureEntity(Vector pos, SavedEntity entity) {}
}