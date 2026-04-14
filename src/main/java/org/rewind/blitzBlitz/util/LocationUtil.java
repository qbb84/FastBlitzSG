package org.rewind.blitzBlitz.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class LocationUtil {

    private static final String SEPARATOR = ",";

    private LocationUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    @NotNull
    public static String serialize(@NotNull Location location) {
        return String.join(SEPARATOR,
                location.getWorld().getName(),
                String.valueOf(location.getX()),
                String.valueOf(location.getY()),
                String.valueOf(location.getZ()),
                String.valueOf(location.getYaw()),
                String.valueOf(location.getPitch())
        );
    }

    @Nullable
    public static Location deserialize(@NotNull String serialized) {
        String[] parts = serialized.split(SEPARATOR);
        if (parts.length < 4) {
            return null;
        }
        World world = Bukkit.getWorld(parts[0]);
        if (world == null) {
            return null;
        }
        double x = Double.parseDouble(parts[1]);
        double y = Double.parseDouble(parts[2]);
        double z = Double.parseDouble(parts[3]);
        float yaw = parts.length > 4 ? Float.parseFloat(parts[4]) : 0f;
        float pitch = parts.length > 5 ? Float.parseFloat(parts[5]) : 0f;
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static double horizontalDistance(@NotNull Location a, @NotNull Location b) {
        double dx = a.getX() - b.getX();
        double dz = a.getZ() - b.getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }
}
