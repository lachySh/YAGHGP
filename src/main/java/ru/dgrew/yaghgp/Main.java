package ru.dgrew.yaghgp;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import ru.dgrew.yaghgp.commands.Start;
import ru.dgrew.yaghgp.managers.ChatManager;
import ru.dgrew.yaghgp.managers.PhaseManager;
import ru.dgrew.yaghgp.managers.PlayerManager;
import ru.dgrew.yaghgp.managers.SettingsManager;

public class Main extends JavaPlugin implements Listener {
    private static Main instance;
    private static ChatManager cm;
    private static PhaseManager pm;
    private static PlayerManager plm;
    private static SettingsManager sm;
    World lobby;
    World arena;
    public void onEnable() {
        this.saveDefaultConfig();
        instance = this;
        plm = new PlayerManager();
        pm = new PhaseManager();
        sm = new SettingsManager(this.getConfig());
        cm = new ChatManager(this.getConfig());
        lobby = Bukkit.createWorld(WorldCreator.name(this.getConfig().getString("settings.lobby", "arena")));
        arena = Bukkit.createWorld(WorldCreator.name(this.getConfig().getString("settings.arena", "arena")));
        lobby.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        lobby.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        lobby.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        lobby.setGameRule(GameRule.DO_FIRE_TICK, false);
        lobby.setGameRule(GameRule.DO_TILE_DROPS, false);
        arena.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        arena.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        arena.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        arena.setGameRule(GameRule.DO_FIRE_TICK, false);
        arena.setGameRule(GameRule.DO_TILE_DROPS, false);
        lobby.setTime(5000);
        arena.setTime(5000);
        arena.setDifficulty(Difficulty.HARD);
        arena.getWorldBorder().setSize(10000);
        for(Entity e : lobby.getEntities()) e.remove();
        for(Entity e : arena.getEntities()) e.remove();
        this.getCommand("start").setExecutor(new Start());
        new UpdateChecker(this, 106792).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                System.out.println("There are no YAGHGP updates available!");
            } else {
                System.out.println("There is a new YAGHGP update available! Check it out here: https://www.spigotmc.org/resources/yaghgp-yet-another-generic-hunger-games-plugin.106792/updates");
            }
        });
    }
    public void onDisable() {

    }
    public static Main getInstance() {
        return instance;
    }
    public static ChatManager getCm() { return cm; }
    public static PhaseManager getPm() { return pm; }
    public static PlayerManager getPlm() {return plm; }
    public static SettingsManager getSm() { return sm; }
}
