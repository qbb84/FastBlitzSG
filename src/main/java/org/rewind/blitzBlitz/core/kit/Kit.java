package org.rewind.blitzBlitz.core.kit;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.rewind.blitzBlitz.core.player.SGPlayer;

import java.time.Duration;
import java.util.List;

public abstract class Kit {

    @NotNull
    public abstract String getId();

    @NotNull
    public abstract String getDisplayName();

    @NotNull
    public abstract ItemStack getIcon();

    @NotNull
    public abstract List<ItemStack> getStartingItems();

    @NotNull
    public abstract String getPassiveDescription();

    @NotNull
    public abstract String getActiveDescription();

    public abstract void onGameStart(@NotNull SGPlayer player);

    public abstract void onPlayerKill(@NotNull SGPlayer killer, @NotNull SGPlayer victim);

    public abstract void onPlayerDamaged(@NotNull SGPlayer player, @NotNull SGPlayer attacker, double damage);

    public abstract boolean hasActiveAbility();

    public abstract void activateAbility(@NotNull SGPlayer player);

    @NotNull
    public abstract Duration getAbilityCooldown();
}
