package org.rewind.blitzBlitz.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.rewind.blitzBlitz.util.ChatUtil;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class MessageConfig {

    private final Map<String, String> messages;

    public MessageConfig(@NotNull JavaPlugin plugin) {
        this.messages = new HashMap<>();

        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        InputStream defaultStream = plugin.getResource("messages.yml");
        if (defaultStream != null) {
            YamlConfiguration defaults = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
            cfg.setDefaults(defaults);
        }

        for (String key : cfg.getKeys(true)) {
            if (cfg.isString(key)) {
                messages.put(key, cfg.getString(key));
            }
        }

        plugin.getLogger().info("[Messages] Loaded " + messages.size() + " message entries.");
    }

    @NotNull
    public String getRaw(@NotNull String key) {
        return messages.getOrDefault(key, key);
    }

    @NotNull
    public String get(@NotNull String key, @NotNull String... replacements) {
        String msg = getRaw(key);
        for (int i = 0; i < replacements.length - 1; i += 2) {
            msg = msg.replace("{" + replacements[i] + "}", replacements[i + 1]);
        }
        return ChatUtil.colorize(msg);
    }
}
