package com.github.darksoulq.abyssallib.world.item;

import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.server.event.internal.UseContext;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class TestItem extends Item {
    public TestItem(Identifier id) {
        super(id, Material.STICK);
    }

    @Override
    public ActionResult postHit(LivingEntity source, Entity target) {
        source.sendRichMessage("YEAHHH");
        return ActionResult.PASS;
    }

    @Override
    public ActionResult onUseOn(UseContext ctx) {
        ctx.getSource().sendRichMessage("Hello");
        return ActionResult.PASS;
    }
}
