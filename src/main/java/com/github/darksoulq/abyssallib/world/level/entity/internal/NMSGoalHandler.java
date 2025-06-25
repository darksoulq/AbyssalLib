package com.github.darksoulq.abyssallib.world.level.entity.internal;

import com.github.darksoulq.abyssallib.world.level.entity.Goal;
import net.minecraft.world.entity.Mob;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;

public class NMSGoalHandler {
    public static void clearGoals(LivingEntity entity) {
        Mob nms = (Mob) ((CraftLivingEntity) entity).getHandle();
        nms.goalSelector.removeAllGoals(g -> true);
        nms.targetSelector.removeAllGoals(g -> true);
    }

    public static void addGoal(LivingEntity entity, Goal goal, int priority) {
        Mob nms = (Mob) ((CraftLivingEntity) entity).getHandle();
        nms.goalSelector.addGoal(priority, goal);
    }
    public static void addTargetGoal(LivingEntity entity, Goal goal, int priority) {
        Mob nms = (Mob) ((CraftLivingEntity) entity).getHandle();
        nms.targetSelector.addGoal(priority, goal);
    }
}
