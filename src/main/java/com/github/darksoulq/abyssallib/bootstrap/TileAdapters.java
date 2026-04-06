package com.github.darksoulq.abyssallib.bootstrap;

import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.types.*;

public class TileAdapters {
    public static void register() {
        TileAdapter.register("container", new ContainerTileAdapter(), new TileStateInventoryHolderAdapter());
        TileAdapter.register("nameable", new NameableTileAdapter());
        TileAdapter.register("lockable", new LockableTileAdapter());
        TileAdapter.register("persistent_data", new PDCTileAdapter());

        TileAdapter.register("campfire", new CampfireTileAdapter());
        TileAdapter.register("jukebox", new JukeboxTileAdapter());
        TileAdapter.register("sign", new SignTileAdapter());
        TileAdapter.register("banner", new BannerTileAdapter());
        TileAdapter.register("beacon", new BeaconTileAdapter());
        TileAdapter.register("beehive", new BeehiveTileAdapter());
        TileAdapter.register("brewing_stand", new BrewingStandTileAdapter());
        TileAdapter.register("command_block", new CommandBlockTileAdapter());
        TileAdapter.register("lectern", new LecternTileAdapter());
        TileAdapter.register("skull", new SkullTileAdapter());
        TileAdapter.register("decorated_pot", new DecoratedPotTileAdapter());
        TileAdapter.register("brushable_block", new BrushableBlockTileAdapter());
        TileAdapter.register("structure_block", new StructureTileAdapter());
        TileAdapter.register("end_gateway", new EndGatewayTileAdapter());
        TileAdapter.register("sculk_shrieker", new SculkShriekerTileAdapter());
        TileAdapter.register("furnace", new FurnaceTileAdapter());
        TileAdapter.register("sculk_sensor", new SculkSensorTileAdapter());
        TileAdapter.register("crafter", new CrafterTileAdapter());
        TileAdapter.register("trial_spawner", new TrialSpawnerTileAdapter());
        TileAdapter.register("vault", new VaultTileAdapter());
        TileAdapter.register("conduit", new ConduitTileAdapter());
    }
}
