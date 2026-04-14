package org.rewind.blitzBlitz.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class SidebarBuilder {

    private String title;
    private final List<String> lines = new ArrayList<>();

    @NotNull
    public SidebarBuilder title(@NotNull String title) {
        this.title = org.rewind.blitzBlitz.util.ChatUtil.colorize(title);
        return this;
    }

    @NotNull
    public SidebarBuilder line(@NotNull String line) {
        lines.add(org.rewind.blitzBlitz.util.ChatUtil.colorize(line));
        return this;
    }

    @NotNull
    public SidebarBuilder blank() {
        int blanks = (int) lines.stream().filter(String::isBlank).count();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= blanks; i++) {
            sb.append(ChatColor.RESET);
        }
        lines.add(sb.toString());
        return this;
    }

    @NotNull
    public Scoreboard build() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("blitz", Criteria.DUMMY, title);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int score = lines.size() - i;
            objective.getScore(line).setScore(score);
        }

        return scoreboard;
    }
}
