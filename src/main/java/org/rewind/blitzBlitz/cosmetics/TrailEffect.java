package org.rewind.blitzBlitz.cosmetics;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Deque;
import java.util.Iterator;

public enum TrailEffect {

    VOID_RIFT(
            "Void Rift",
            Material.ENDER_PEARL,
            500,
            TrailEffect::renderVoidRift
    ),

    SOLAR_FLARE(
            "Solar Flare",
            Material.BLAZE_POWDER,
            750,
            TrailEffect::renderSolarFlare
    ),

    CRYSTAL_BLOOM(
            "Crystal Bloom",
            Material.AMETHYST_SHARD,
            600,
            TrailEffect::renderCrystalBloom
    ),

    GHOST_ECHO(
            "Ghost Echo",
            Material.PHANTOM_MEMBRANE,
            900,
            TrailEffect::renderGhostEcho
    ),

    STORM_WALKER(
            "Storm Walker",
            Material.LIGHTNING_ROD,
            1000,
            TrailEffect::renderStormWalker
    );

    private static final double TWO_PI = Math.PI * 2.0;
    private static final double HELIX_RADIUS = 0.4;
    private static final double HELIX_STEP = 0.15;
    private static final int CRYSTAL_CYCLE_TICKS = 20;
    private static final double CRYSTAL_MIN_RADIUS = 0.1;
    private static final double CRYSTAL_MAX_RADIUS = 0.5;
    private static final int RING_POINTS = 8;
    private static final double VELOCITY_THRESHOLD_SQ = 0.001;
    private static final double SPARK_OFFSET_RANGE = 0.15;
    private static final int SPARK_POINTS = 4;

    private final String displayName;
    private final Material icon;
    private final int coinCost;
    private final TrailRenderer renderer;

    TrailEffect(@NotNull String displayName, @NotNull Material icon, int coinCost, @NotNull TrailRenderer renderer) {
        this.displayName = displayName;
        this.icon = icon;
        this.coinCost = coinCost;
        this.renderer = renderer;
    }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public Material getIcon() { return icon; }

    public int getCoinCost() { return coinCost; }

    @NotNull
    public TrailRenderer getRenderer() { return renderer; }

    private static void renderVoidRift(@NotNull Player player, @NotNull TrailContext ctx) {
        int tick = ctx.getTickCount();
        Location base = player.getLocation();

        if (tick % 3 == 0) {
            double angle = (tick * HELIX_STEP) % TWO_PI;
            double x = Math.cos(angle) * HELIX_RADIUS;
            double z = Math.sin(angle) * HELIX_RADIUS;
            player.getWorld().spawnParticle(Particle.END_ROD, base.clone().add(x, 0.1, z), 1, 0, 0, 0, 0);
            player.getWorld().spawnParticle(Particle.END_ROD, base.clone().add(-x, 0.3, -z), 1, 0, 0, 0, 0);
        }

        if (tick % 10 == 0) {
            player.getWorld().spawnParticle(Particle.PORTAL, base, 15, 0.2, 0.4, 0.2, 0.05);
        }
    }

    private static void renderSolarFlare(@NotNull Player player, @NotNull TrailContext ctx) {
        int tick = ctx.getTickCount();
        Location base = player.getLocation();

        if (tick % 2 == 0) {
            Vector dir = base.getDirection().normalize().multiply(0.3);
            Location offset = base.clone().add(dir).add(0, 1.0, 0);
            player.getWorld().spawnParticle(Particle.FLAME, offset, 3, 0.05, 0.05, 0.05, 0.01);
        }

        if (tick % 8 == 0) {
            Location headTop = base.clone().add(0, 2.2, 0);
            for (int i = 0; i < 5; i++) {
                double spread = (i - 2) * 0.15;
                double height = -Math.abs(spread) * 0.5;
                player.getWorld().spawnParticle(Particle.LAVA, headTop.clone().add(spread, height, spread), 1);
            }
        }
    }

    private static void renderCrystalBloom(@NotNull Player player, @NotNull TrailContext ctx) {
        int tick = ctx.getTickCount();
        Location base = player.getLocation();

        if (tick % 4 == 0) {
            int cyclePos = tick % CRYSTAL_CYCLE_TICKS;
            double progress = (double) cyclePos / CRYSTAL_CYCLE_TICKS;
            double radius = CRYSTAL_MIN_RADIUS + (CRYSTAL_MAX_RADIUS - CRYSTAL_MIN_RADIUS) * progress;

            for (int i = 0; i < RING_POINTS; i++) {
                double angle = (TWO_PI / RING_POINTS) * i;
                double x = Math.cos(angle) * radius;
                double z = Math.sin(angle) * radius;
                player.getWorld().spawnParticle(Particle.CRIT, base.clone().add(x, 0.5, z), 1, 0, 0, 0, 0);
            }
        }

        if (tick % 12 == 0) {
            for (double y = 0; y <= 2.0; y += 0.4) {
                player.getWorld().spawnParticle(Particle.WITCH, base.clone().add(0, y, 0), 2, 0.1, 0.05, 0.1, 0);
            }
        }
    }

    private static void renderGhostEcho(@NotNull Player player, @NotNull TrailContext ctx) {
        int tick = ctx.getTickCount();

        if (tick % 5 == 0) {
            Deque<Location> history = ctx.getPositionHistory();
            if (history.size() < 2) return;

            Iterator<Location> it = history.iterator();
            int shown = 0;
            while (it.hasNext() && shown < 3) {
                Location ghostLoc = it.next();
                if (ghostLoc.getWorld() == null || !ghostLoc.getWorld().equals(player.getWorld())) continue;
                double opacity = 0.3 + (0.2 * shown);
                player.getWorld().spawnParticle(Particle.EFFECT, ghostLoc.clone().add(0, 1.0, 0),
                        8, 0.15, 0.5, 0.15, opacity);
                shown++;
            }
        }
    }

    private static void renderStormWalker(@NotNull Player player, @NotNull TrailContext ctx) {
        int tick = ctx.getTickCount();
        Vector movement = ctx.getMovementDirection();

        if (movement.lengthSquared() < VELOCITY_THRESHOLD_SQ) return;

        if (tick % 2 == 0 && ctx.isSprinting()) {
            Location base = player.getLocation();
            player.getWorld().spawnParticle(Particle.CLOUD, base.clone().add(0, 0.05, 0),
                    3, 0.2, 0.02, 0.2, 0.01);
        }

        if (tick % 6 == 0) {
            Vector behind = movement.clone().normalize().multiply(-1.0);
            Location base = player.getLocation().add(behind);

            for (int i = 0; i < SPARK_POINTS; i++) {
                double offsetX = (Math.random() - 0.5) * SPARK_OFFSET_RANGE * 2;
                double offsetY = Math.random() * 0.6;
                double offsetZ = (Math.random() - 0.5) * SPARK_OFFSET_RANGE * 2;
                player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK,
                        base.clone().add(offsetX, offsetY, offsetZ), 1, 0, 0, 0, 0);
            }
        }
    }
}
