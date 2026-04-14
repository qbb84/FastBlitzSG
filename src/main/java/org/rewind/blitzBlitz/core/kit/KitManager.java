package org.rewind.blitzBlitz.core.kit;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rewind.blitzBlitz.core.player.SGPlayer;
import org.rewind.blitzBlitz.util.ChatUtil;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class KitManager {

    private final KitFactory factory;
    private final Map<UUID, String> selectedKits;
    private final JavaPlugin plugin;

    public KitManager(@NotNull JavaPlugin plugin, @NotNull KitFactory factory) {
        this.plugin = plugin;
        this.factory = factory;
        this.selectedKits = new ConcurrentHashMap<>();
        loadSelections();
    }

    public void selectKit(@NotNull UUID playerId, @NotNull String kitId) {
        selectedKits.put(playerId, kitId.toLowerCase());
    }

    @Nullable
    public Kit getSelectedKit(@NotNull UUID playerId) {
        String kitId = selectedKits.get(playerId);
        if (kitId == null) return null;
        return factory.create(kitId);
    }

    @Nullable
    public String getSelectedKitId(@NotNull UUID playerId) {
        return selectedKits.get(playerId);
    }

    public void applyKit(@NotNull SGPlayer sgPlayer) {
        Kit kit = getSelectedKit(sgPlayer.getUuid());
        if (kit == null) {
            kit = factory.getAll().stream().findFirst().orElse(null);
        }
        if (kit == null) return;

        sgPlayer.setKit(kit);
        Player player = sgPlayer.getBukkitPlayer();
        if (player != null) {
            for (var item : kit.getStartingItems()) {
                player.getInventory().addItem(item);
            }
            kit.onGameStart(sgPlayer);
            ChatUtil.sendMessage(player, "&aYou are playing as &e" + kit.getDisplayName() + "&a!");
        }
    }

    public boolean tryActivateAbility(@NotNull SGPlayer sgPlayer) {
        Kit kit = sgPlayer.getKit();
        if (kit == null || !kit.hasActiveAbility()) return false;

        String abilityId = kit.getId() + "_active";
        if (sgPlayer.isAbilityOnCooldown(abilityId)) {
            Player player = sgPlayer.getBukkitPlayer();
            if (player != null) {
                long remaining = sgPlayer.getRemainingCooldown(abilityId) / 1000L;
                ChatUtil.sendMessage(player, "&cAbility on cooldown! &e" + remaining + "s &cremaining.");
            }
            return false;
        }

        kit.activateAbility(sgPlayer);
        sgPlayer.setAbilityCooldown(abilityId, kit.getAbilityCooldown().toMillis());
        return true;
    }

    public void forceActivateAbility(@NotNull SGPlayer sgPlayer) {
        Kit kit = sgPlayer.getKit();
        if (kit == null || !kit.hasActiveAbility()) return;
        kit.activateAbility(sgPlayer);
    }

    @NotNull
    public KitFactory getFactory() {
        return factory;
    }

    public void saveSelections() {
        File file = new File(plugin.getDataFolder(), "kit-selections.yml");
        FileConfiguration cfg = new YamlConfiguration();
        selectedKits.forEach((uuid, kitId) -> cfg.set(uuid.toString(), kitId));
        try {
            cfg.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("[KitManager] Failed to save kit selections: " + e.getMessage());
        }
    }

    private void loadSelections() {
        File file = new File(plugin.getDataFolder(), "kit-selections.yml");
        if (!file.exists()) return;
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                String kitId = cfg.getString(key);
                if (kitId != null) {
                    selectedKits.put(uuid, kitId);
                }
            } catch (IllegalArgumentException ignored) {}
        }
    }
}
