package org.rewind.blitzBlitz.cosmetics;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface TrailRenderer {

    void render(@NotNull Player player, @NotNull TrailContext ctx);
}
