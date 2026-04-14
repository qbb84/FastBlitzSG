package org.rewind.blitzBlitz.gui;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.rewind.blitzBlitz.core.kit.KitFactory;
import org.rewind.blitzBlitz.core.kit.KitManager;

public final class GUIManager {

    private final KitSelectionGUI kitSelectionGUI;

    public GUIManager(@NotNull KitFactory kitFactory, @NotNull KitManager kitManager) {
        this.kitSelectionGUI = new KitSelectionGUI(kitFactory, kitManager);
    }

    public void openKitSelection(@NotNull Player player) {
        kitSelectionGUI.open(player);
    }

    @NotNull
    public KitSelectionGUI getKitSelectionGUI() {
        return kitSelectionGUI;
    }
}
