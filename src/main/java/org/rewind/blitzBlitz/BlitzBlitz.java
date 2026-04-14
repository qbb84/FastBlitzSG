package org.rewind.blitzBlitz;

import org.bukkit.plugin.java.JavaPlugin;

public final class BlitzBlitz extends JavaPlugin {

    private static BlitzBlitz instance;
    private BlitzPlugin blitzPlugin;

    @Override
    public void onEnable() {
        instance = this;
        blitzPlugin = new BlitzPlugin(this);
        blitzPlugin.enable();
    }

    @Override
    public void onDisable() {
        if (blitzPlugin != null) {
            blitzPlugin.disable();
        }
        instance = null;
    }

    public static BlitzBlitz getInstance() {
        return instance;
    }

    public BlitzPlugin getBlitzPlugin() {
        return blitzPlugin;
    }
}
