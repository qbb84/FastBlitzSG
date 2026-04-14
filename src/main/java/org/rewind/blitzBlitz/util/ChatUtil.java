package org.rewind.blitzBlitz.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class ChatUtil {

    public static final String PREFIX = "&6&lBlitz &8» &7";
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();

    private ChatUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    @NotNull
    public static String colorize(@NotNull String text) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', text);
    }

    @NotNull
    public static Component toComponent(@NotNull String text) {
        return LEGACY.deserialize(text);
    }

    public static void sendMessage(@NotNull Player player, @NotNull String message) {
        player.sendMessage(colorize(PREFIX + message));
    }

    public static void broadcast(@NotNull String message) {
        String formatted = colorize(PREFIX + message);
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(formatted));
    }

    public static void sendActionBar(@NotNull Player player, @NotNull String message) {
        player.sendActionBar(toComponent(message));
    }
}
