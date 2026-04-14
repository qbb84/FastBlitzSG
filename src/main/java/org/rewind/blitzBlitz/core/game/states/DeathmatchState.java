package org.rewind.blitzBlitz.core.game.states;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.rewind.blitzBlitz.config.BlitzConfig;
import org.rewind.blitzBlitz.core.game.Game;
import org.rewind.blitzBlitz.core.game.GameState;
import org.rewind.blitzBlitz.core.game.GameStateHandler;
import org.rewind.blitzBlitz.core.player.SGPlayer;
import org.rewind.blitzBlitz.util.ChatUtil;

@SuppressWarnings("removal")
public final class DeathmatchState implements GameStateHandler {

    @Override
    @NotNull
    public GameState getState() { return GameState.DEATHMATCH; }

    @Override
    public void onEnter(@NotNull Game game) {
        BlitzConfig config = game.getPlugin().getBlitzConfig();
        int borderTime = config.getDeathmatchBorderTime();
        int finalSize = config.getDeathmatchFinalSize();
        game.setTimer(borderTime);

        game.broadcast("&4&lDEATHMATCH! &cThe border is closing in over &e" + (borderTime / 60) + " minutes&c!");

        Location dmCentre = game.getArena().getDeathmatchCentre();
        if (dmCentre == null) {
            dmCentre = game.getArena().getCentre();
        }
        if (dmCentre != null) {
            World world = dmCentre.getWorld();
            if (world != null) {
                WorldBorder border = world.getWorldBorder();
                border.setCenter(dmCentre);
                border.setSize(border.getSize());
                border.setSize(finalSize, borderTime);
                border.setDamageAmount(config.getDeathmatchBorderDamage());
                border.setDamageBuffer(0);
                border.setWarningDistance(10);
                border.setWarningTime(15);
            }
        }
    }

    @Override
    public void onTick(@NotNull Game game) {
        game.decrementTimer();

        if (game.getAliveCount() <= 1) {
            game.transition(GameState.ENDED);
            return;
        }

        int remaining = game.getTimer();
        if (remaining > 0 && remaining % 30 == 0) {
            game.broadcast("&cBorder closing! &e" + remaining + "s &cremaining!");
        }
    }

    @Override
    public void onExit(@NotNull Game game) {
        Location dmCentre = game.getArena().getDeathmatchCentre();
        if (dmCentre == null) dmCentre = game.getArena().getCentre();
        if (dmCentre != null && dmCentre.getWorld() != null) {
            WorldBorder border = dmCentre.getWorld().getWorldBorder();
            border.reset();
        }
    }
}
