package org.rewind.blitzBlitz.util;

import org.jetbrains.annotations.NotNull;

public final class TimeUtil {

    private static final int SECONDS_PER_MINUTE = 60;
    private static final int SECONDS_PER_HOUR = 3600;

    private TimeUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    @NotNull
    public static String formatTime(int totalSeconds) {
        if (totalSeconds < 0) {
            return "0:00";
        }
        int hours = totalSeconds / SECONDS_PER_HOUR;
        int minutes = (totalSeconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE;
        int seconds = totalSeconds % SECONDS_PER_MINUTE;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format("%d:%02d", minutes, seconds);
    }

    @NotNull
    public static String formatTicks(long ticks) {
        return formatTime((int) (ticks / 20L));
    }

    @NotNull
    public static String formatMillis(long millis) {
        return formatTime((int) (millis / 1000L));
    }
}
