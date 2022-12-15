package ru.dgrew.hg.managers;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import ru.dgrew.hg.Main;
import ru.dgrew.hg.Phase;
import ru.dgrew.hg.phases.Lobby;

public class PhaseManager {
    private Phase currentPhase;
    public PhaseManager() {
        setPhase(new Lobby());
    }
    public void nextPhase() {
        setPhase(currentPhase.next());
    }
    private void setPhase(Phase phase) {
        if (currentPhase != null) {
            currentPhase.onDisable();
            HandlerList.unregisterAll(currentPhase);
        }
        currentPhase = phase;
        Bukkit.getPluginManager().registerEvents(currentPhase,Main.getInstance());
        currentPhase.onEnable();
    }
}