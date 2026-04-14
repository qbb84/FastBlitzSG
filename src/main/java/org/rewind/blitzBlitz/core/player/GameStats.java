package org.rewind.blitzBlitz.core.player;

public final class GameStats {

    private int kills;
    private int deaths;
    private double damageDealt;
    private double damageReceived;
    private int chestsLooted;
    private int blitzStarsUsed;
    private int assists;
    private int gamesPlayed;
    private int wins;

    public GameStats() {}

    public int getKills() { return kills; }
    public void addKill() { kills++; }
    public void setKills(int kills) { this.kills = kills; }

    public int getDeaths() { return deaths; }
    public void addDeath() { deaths++; }
    public void setDeaths(int deaths) { this.deaths = deaths; }

    public double getDamageDealt() { return damageDealt; }
    public void addDamageDealt(double amount) { damageDealt += amount; }
    public void setDamageDealt(double damageDealt) { this.damageDealt = damageDealt; }

    public double getDamageReceived() { return damageReceived; }
    public void addDamageReceived(double amount) { damageReceived += amount; }
    public void setDamageReceived(double damageReceived) { this.damageReceived = damageReceived; }

    public int getChestsLooted() { return chestsLooted; }
    public void addChestLooted() { chestsLooted++; }
    public void setChestsLooted(int chestsLooted) { this.chestsLooted = chestsLooted; }

    public int getBlitzStarsUsed() { return blitzStarsUsed; }
    public void addBlitzStarUsed() { blitzStarsUsed++; }
    public void setBlitzStarsUsed(int blitzStarsUsed) { this.blitzStarsUsed = blitzStarsUsed; }

    public int getAssists() { return assists; }
    public void addAssist() { assists++; }
    public void setAssists(int assists) { this.assists = assists; }

    public int getGamesPlayed() { return gamesPlayed; }
    public void addGamePlayed() { gamesPlayed++; }
    public void setGamesPlayed(int gamesPlayed) { this.gamesPlayed = gamesPlayed; }

    public int getWins() { return wins; }
    public void addWin() { wins++; }
    public void setWins(int wins) { this.wins = wins; }

    public void merge(@org.jetbrains.annotations.NotNull GameStats other) {
        this.kills += other.kills;
        this.deaths += other.deaths;
        this.damageDealt += other.damageDealt;
        this.damageReceived += other.damageReceived;
        this.chestsLooted += other.chestsLooted;
        this.blitzStarsUsed += other.blitzStarsUsed;
        this.assists += other.assists;
        this.gamesPlayed += other.gamesPlayed;
        this.wins += other.wins;
    }
}
