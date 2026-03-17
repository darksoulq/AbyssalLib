package com.github.darksoulq.abyssallib.bootstrap;

import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.*;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.types.unique.*;

public final class BlockAdapterRegistry {

    public static void register() {
        Adapter.register("age", new AgeableAdapter());
        Adapter.register("power", new AnaloguePowerableAdapter());
        Adapter.register("attached", new AttachableAdapter());
        Adapter.register("half", new BisectedAdapter());
        Adapter.register("dusted", new BrushableAdapter());
        Adapter.register("facing", new DirectionalAdapter());
        Adapter.register("face", new FaceAttachableAdapter());
        Adapter.register("hanging", new HangableAdapter());
        Adapter.register("hatch", new HatchableAdapter());
        Adapter.register("level", new LevelledAdapter());
        Adapter.register("lit", new LightableAdapter());
        Adapter.register("faces", new MultipleFacingAdapter());
        Adapter.register("open", new OpenableAdapter());
        Adapter.register("axis", new OrientableAdapter());
        Adapter.register("powered", new PowerableAdapter());
        Adapter.register("rotation", new RotatableAdapter());
        Adapter.register("segment_amount", new SegmentableAdapter());
        Adapter.register("side_chain", new SideChainingAdapter());
        Adapter.register("snowy", new SnowableAdapter());
        Adapter.register("waterlogged", new WaterloggedAdapter());
        Adapter.register("has_bottle", new BrewingStandAdapter());
        Adapter.register("drag", new BubbleColumnAdapter());
        Adapter.register("bites", new CakeAdapter());
        Adapter.register("berries", new CaveVinesPlantAdapter());
        Adapter.register("copper_golem_pose", new CopperGolemStatueAdapter());
        Adapter.register("moisture", new FarmlandAdapter());
        Adapter.register("tip", new HangingMossAdapter());
        Adapter.register("orientation", new CrafterOrientationAdapter(), new JigsawAdapter());
        Adapter.register("has_record", new JukeboxAdapter());
        Adapter.register("charges", new RespawnAnchorAdapter());
        Adapter.register("stage", new SaplingAdapter());
        Adapter.register("bloom", new SculkCatalystAdapter());
        Adapter.register("layers", new SnowAdapter());
        Adapter.register("mode", new StructureBlockAdapter(), new TestBlockAdapter());
        Adapter.register("unstable", new TNTAdapter());
        Adapter.register("trial_spawner_state", new TrialSpawnerStateAdapter());
        Adapter.register("ominous", new TrialSpawnerOminousAdapter());
        Adapter.register("bottom", new MossyCarpetBottomAdapter());
        Adapter.register("height", new MossyCarpetHeightAdapter());
        Adapter.register("crafting", new CrafterStateAdapter());
        Adapter.register("triggered", new CrafterPowerAdapter());
    }
}