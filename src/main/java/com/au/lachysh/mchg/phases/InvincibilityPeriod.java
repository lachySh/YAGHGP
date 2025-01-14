package com.au.lachysh.mchg.phases;

import com.au.lachysh.mchg.managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import com.au.lachysh.mchg.Main;
import com.au.lachysh.mchg.managers.ChatManager;
import com.au.lachysh.mchg.managers.GamemapManager;
import com.au.lachysh.mchg.managers.SettingsManager;
import com.au.lachysh.mchg.tribute.Tribute;

public class InvincibilityPeriod extends Phase {
    private int timer;
    private ChatManager cm;
    private PlayerManager pm;
    private SettingsManager sm;
    private GamemapManager gm;
    private SharedPhaseLogic spl;
    private BukkitTask countdown;

    //region Phase Methods
    @Override
    public void onEnable() {
        sm = Main.getSm();
        cm = Main.getCm();
        pm = Main.getPlm();
        gm = Main.getGm();
        spl = Main.getSpl();
        timer = 120;
        for (Tribute tribute : pm.getRemainingTributesList()) {
            tribute.getPlayerObject().setGameMode(gm.getArenaGamemap().getGamemode());
            tribute.getPlayerObject().addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 2400, 2, false, false));
        }
        startCountdown();
        gm.getArenaWorld().setAutoSave(false);
        Main.getInstance().getLogger().info("InvincibilityPeriod phase has started successfully!");
    }

    @Override
    public void onDisable() {
        countdown.cancel();
    }

    @Override
    public Phase next() {
        return new FightPeriodStart();
    }

    //endregion
    //region Phase Listeners
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        spl.inGameOnJoin(e);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        spl.inGameOnLeave(e);
    }

    @EventHandler
    public void onWorldDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            e.setCancelled(true);
        }
    }

    //endregion
    //region Runnables
    void startCountdown() {
        countdown = new BukkitRunnable() {
            @Override
            public void run() {
                if (timer > 0) {
                    spl.playTimerAnnouncement(timer, cm.getPrefix() + cm.getInvincibilityTimer(timer));
                    timer--;
                } else {
                    for (Player p : Bukkit.getOnlinePlayers())
                        p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                    Bukkit.broadcastMessage(cm.getPrefix() + cm.getTimerend());
                    Main.getPm().nextPhase();
                    cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 20L, 20L);
    }
}
