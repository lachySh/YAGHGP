package ru.dgrew.yaghgp.phases;

import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ru.dgrew.yaghgp.Main;
import ru.dgrew.yaghgp.Phase;
import ru.dgrew.yaghgp.managers.ChatManager;
import ru.dgrew.yaghgp.managers.LootManager;
import ru.dgrew.yaghgp.managers.PlayerManager;

public class InGame extends Phase {
    private int timer;
    private ChatManager cm;
    private LootManager lm;
    private PlayerManager pm;
    private BukkitTask gameTimer;
    //region Phase Methods
    @Override
    public void onEnable() {
        timer = 900;
        cm = Main.getCm();
        lm = new LootManager();
        pm = Main.getPlm();
        pm.updatePlayersList();
        for (Player p : pm.getRemainingPlayersList()) p.setGameMode(GameMode.SURVIVAL);
        startTimer();
        System.out.println("InGame phase has started successfully!");
    }

    @Override
    public void onDisable() {
        gameTimer.cancel();
    }
    @Override
    public Phase next() {
        return new Deathmatch();
    }
    //endregion
    //region Phase Listeners
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!e.getBlock().getType().name().endsWith("_LEAVES") && !(e.getBlock().getType().name().endsWith("FIRE")) && !(e.getBlock().getType().name().endsWith("GRASS")) && !(e.getBlock().getType().name().endsWith("FERN"))) e.setCancelled(true);
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (e.getItemInHand().equals(new ItemStack(Material.FLINT_AND_STEEL))) e.setCancelled(true);
    }
    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent e) {
        if (!e.getCause().equals(BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL)) e.setCancelled(true);
    }
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() != null && e.getAction() == Action.RIGHT_CLICK_BLOCK)
            if (e.getClickedBlock().getType().name().startsWith("POTTED_") ||
                    e.getClickedBlock().getType() == Material.FLOWER_POT ||
                    e.getClickedBlock().getType().name().endsWith("_LOG"))
                e.setCancelled(true);
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        e.getPlayer().kickPlayer("Game already started!");
    }
    @EventHandler
    public void onLeave(PlayerQuitEvent e){
        e.setQuitMessage(ChatColor.YELLOW + e.getPlayer().getName() + " has left!");
        Main.getPlm().removeOnDC(e.getPlayer());
        checkForPlayerCount();
    }
    @EventHandler
    public void onChestOpen(InventoryOpenEvent e) {
        if(e.getInventory().getHolder() instanceof Chest c){
            if (lm.getChestContents(c.getLocation()) == null) {
                c.getInventory().clear();
                lm.createRandomChest(c.getInventory());
                lm.storeChestContents(c.getLocation(), c.getInventory());
            }
        }
        if(e.getInventory().getHolder() instanceof DoubleChest c){
            Chest left = (Chest)c.getLeftSide();
            Chest right = (Chest)c.getRightSide();
            if (lm.getChestContents(c.getLocation()) == null &&
                    (lm.getChestContents(left.getLocation()) == null || lm.getChestContents(right.getLocation()) == null)) {
                c.getInventory().clear();
                left.getInventory().clear();
                right.getInventory().clear();
                lm.createRandomChest(c.getInventory());
                lm.createRandomChest(left.getInventory());
                lm.createRandomChest(right.getInventory());
                lm.storeChestContents(c.getLocation(), c.getInventory());
                lm.storeChestContents(left.getLocation(), left.getInventory());
                lm.storeChestContents(right.getLocation(), right.getInventory());
            }
        }
    }
    @EventHandler
    public void onChestClose(InventoryCloseEvent e){
        if(e.getInventory().getHolder() instanceof Chest c){
            lm.storeChestContents(c.getLocation(), c.getInventory());
        }
        if(e.getInventory().getHolder() instanceof DoubleChest c){
            Chest left = (Chest)c.getLeftSide();
            Chest right = (Chest)c.getRightSide();
            lm.storeChestContents(c.getLocation(), c.getInventory());
            lm.storeChestContents(left.getLocation(), left.getInventory());
            lm.storeChestContents(right.getLocation(), right.getInventory());
        }
    }
    @EventHandler
    public void onKill(PlayerDeathEvent e){
        e.getEntity();
        Player killed = e.getEntity();
        if (e.getEntity().getKiller() instanceof Player) {
            Player killer = e.getEntity().getKiller();
            killer.sendMessage(cm.getPrefix() + cm.getKill().replace("{player}",killed.getName()));
            killed.sendMessage(cm.getPrefix() + cm.getKilled().replace("{player}",killer.getName()));
        }
        else killed.sendMessage(cm.getPrefix() + cm.getKillednat());
        killed.setHealth(20);
        killed.getWorld().strikeLightningEffect(killed.getLocation());
        pm.transferToSpectators(killed);
        e.setDeathMessage(cm.getGlobalkill().replace("{players}", String.valueOf(pm.getRemainingPlayersList().size())));
        checkForPlayerCount();
    }
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) { e.setCancelled(true); }
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        e.setCancelled(true);
    }
    @EventHandler
    public void onLeafDecay(LeavesDecayEvent e){
        e.setCancelled(true);
    }
    //endregion
    //region Runnables
    void startTimer() {
        gameTimer = new BukkitRunnable() {
            @Override
            public void run() {
                if (timer > 0) {
                    if (timer == 600 || timer == 540 || timer == 480 || timer == 450)
                        Bukkit.broadcastMessage(cm.getPrefix() + cm.getRefill(timer-420));
                    if (timer == 420){
                        lm.refillAllChests();
                        for(Player p : Bukkit.getOnlinePlayers()) p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        Bukkit.broadcastMessage(cm.getPrefix() + cm.getRefillcommencing());
                    }
                    if (timer == 300 || timer == 180 || timer == 120 || timer == 60)
                        Bukkit.broadcastMessage(cm.getPrefix() + cm.getDeathmatch(timer));
                    if (timer == 30 || timer == 15 || timer == 10)
                        Bukkit.broadcastMessage(cm.getPrefix() + cm.getDeathmatch(timer));
                    if (timer <= 5) {
                        for(Player p : Bukkit.getOnlinePlayers()) p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                        Bukkit.broadcastMessage(cm.getPrefix() + cm.getDeathmatch(timer));
                    }
                    timer--;
                } else {
                    Main.getPm().nextPhase();
                }
            }
        }.runTaskTimer(Main.getInstance(),20L, 20L);
    }
    //endregion
    void checkForPlayerCount() {
        if (pm.getRemainingPlayersList().size() == 1) {
            timer = 0;
            return;
        }
        if (pm.getRemainingPlayersList().size() <= 3 && timer >= 30) {
            timer = 60;
            return;
        }
        if (pm.getRemainingPlayersList().size() <= 7) {
            timer = 480;
        }
    }
}