package org.rewind.blitzBlitz.listeners;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;
import org.rewind.blitzBlitz.BlitzPlugin;
import org.rewind.blitzBlitz.core.game.Game;
import org.rewind.blitzBlitz.core.game.GameState;
import org.rewind.blitzBlitz.core.kit.Kit;
import org.rewind.blitzBlitz.core.kit.kits.ArcherKit;
import org.rewind.blitzBlitz.core.kit.kits.PyroKit;
import org.rewind.blitzBlitz.core.player.SGPlayer;

public final class PlayerDamageListener implements Listener {

    private final BlitzPlugin plugin;

    public PlayerDamageListener(@NotNull BlitzPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;

        Game game = plugin.getGameByPlayer(victim.getUniqueId());
        if (game == null) return;

        if (game.getState() == GameState.GRACE_PERIOD) {
            Player attacker = resolveAttacker(event);
            if (attacker != null && game.getPlayer(attacker.getUniqueId()) != null) {
                event.setCancelled(true);
                return;
            }
        }

        Player attacker = resolveAttacker(event);
        if (attacker == null) return;

        SGPlayer sgVictim = game.getPlayer(victim.getUniqueId());
        SGPlayer sgAttacker = game.getPlayer(attacker.getUniqueId());
        if (sgVictim == null || sgAttacker == null) return;
        if (!sgVictim.isAlive() || !sgAttacker.isAlive()) {
            event.setCancelled(true);
            return;
        }

        sgVictim.setLastAttacker(attacker.getUniqueId());
        sgVictim.getSessionStats().addDamageReceived(event.getFinalDamage());
        sgAttacker.getSessionStats().addDamageDealt(event.getFinalDamage());

        game.getCombatTracker().recordDamage(attacker.getUniqueId(), victim.getUniqueId(),
                event.getFinalDamage(), event.getCause().name());

        Kit attackerKit = sgAttacker.getKit();
        if (attackerKit != null) {
            attackerKit.onPlayerDamaged(sgVictim, sgAttacker, event.getFinalDamage());
        }

        if (event.getDamager() instanceof Arrow) {
            if (attackerKit instanceof ArcherKit) {
                ArcherKit.applySlowness(victim);
            }
            if (attackerKit instanceof PyroKit) {
                PyroKit.applyFire(victim);
            }
        }
    }

    private Player resolveAttacker(@NotNull EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            return player;
        }
        if (event.getDamager() instanceof Arrow arrow && arrow.getShooter() instanceof Player player) {
            return player;
        }
        return null;
    }
}
