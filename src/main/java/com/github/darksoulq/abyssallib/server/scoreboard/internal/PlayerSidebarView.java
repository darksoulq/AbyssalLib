package com.github.darksoulq.abyssallib.server.scoreboard.internal;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.network.chat.numbers.BlankFormat;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundResetScorePacket;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Optional;

public class PlayerSidebarView {

    private static final Optional<NumberFormat> BLANK_FORMAT = Optional.of(BlankFormat.INSTANCE);
    private static final Optional<NumberFormat> EMPTY_FORMAT = Optional.empty();
    private static final String[] OWNERS = new String[15];

    static {
        for (int i = 0; i < 15; i++) {
            OWNERS[i] = String.valueOf(i);
        }
    }

    private final ServerGamePacketListenerImpl connection;
    private final String objectiveId;
    private final Objective objective;

    private Component lastKyoriTitle;
    private final Component[] lastKyoriLines = new Component[15];

    private boolean lastShowNumbers = false;
    private boolean initialized = false;

    public PlayerSidebarView(Player player) {
        this.connection = ((CraftPlayer) player).getHandle().connection;
        this.objectiveId = "sb_" + player.getEntityId();

        this.objective = new Objective(
            new Scoreboard(),
            this.objectiveId,
            ObjectiveCriteria.DUMMY,
            net.minecraft.network.chat.Component.empty(),
            ObjectiveCriteria.RenderType.INTEGER,
            false,
            BlankFormat.INSTANCE
        );
    }

    public void show() {
        if (!initialized) {
            send(new ClientboundSetObjectivePacket(this.objective, 0));
            send(new ClientboundSetDisplayObjectivePacket(DisplaySlot.SIDEBAR, this.objective));
            initialized = true;
        }
    }

    public void hide() {
        if (initialized) {
            send(new ClientboundSetObjectivePacket(this.objective, 1));
            initialized = false;
            this.lastKyoriTitle = null;
            for (int i = 0; i < 15; i++) {
                this.lastKyoriLines[i] = null;
            }
        }
    }

    public void update(Component title, Component[] lines, boolean showNumbers) {
        if (!initialized) show();

        boolean updateObjective = false;
        boolean forceScores = false;

        if (title == null) title = Component.empty();

        if (this.lastKyoriTitle == null || !this.lastKyoriTitle.equals(title)) {
            this.lastKyoriTitle = title;
            this.objective.setDisplayName(PaperAdventure.asVanilla(title));
            updateObjective = true;
        }

        if (this.lastShowNumbers != showNumbers) {
            this.lastShowNumbers = showNumbers;
            this.objective.setNumberFormat(showNumbers ? null : BlankFormat.INSTANCE);
            updateObjective = true;
            forceScores = true;
        }

        if (updateObjective) {
            send(new ClientboundSetObjectivePacket(this.objective, 2));
        }

        Optional<NumberFormat> format = showNumbers ? EMPTY_FORMAT : BLANK_FORMAT;

        for (int i = 0; i < 15; i++) {
            Component newLine = lines[i];
            Component oldLine = this.lastKyoriLines[i];

            if (newLine == null) {
                if (oldLine != null) {
                    this.lastKyoriLines[i] = null;
                    send(new ClientboundResetScorePacket(OWNERS[i], this.objectiveId));
                }
            } else {
                if (forceScores || !newLine.equals(oldLine)) {
                    this.lastKyoriLines[i] = newLine;
                    send(new ClientboundSetScorePacket(
                        OWNERS[i],
                        this.objectiveId,
                        15 - i,
                        Optional.of(PaperAdventure.asVanilla(newLine)),
                        format
                    ));
                }
            }
        }
    }

    private void send(Packet<?> packet) {
        this.connection.send(packet);
    }
}