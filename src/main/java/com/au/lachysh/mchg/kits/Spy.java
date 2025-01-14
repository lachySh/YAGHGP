package com.au.lachysh.mchg.kits;

import com.au.lachysh.mchg.abilities.spy.SpyCompassTrack;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class Spy extends Kit {
    // NOTE: See CompassHandout for cancellation of normal compass handout
    public static final String itemName = ChatColor.RESET + "" + ChatColor.YELLOW + "Spy's Tracking Compass";

    public Spy() {
        super(
            "spy",
            "Spy",
            "You will be given the 'Spy's Tracking Compass'\nupon game start. It's an upgraded version of the\nstandard tracking compass. Use it to see opponent's\nexact distance away from you, with unlimited range.",
            true,
            KitType.UTILITY,
            Material.COMPASS,
            List.of(spyTrackingCompass()),
            List.of(new SpyCompassTrack())
        );
    }

    public static ItemStack spyTrackingCompass() {
        ItemStack item = new ItemStack(Material.COMPASS);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.YELLOW + "Spy's Tracking Compass");
        item.setItemMeta(itemMeta);

        return item;
    }
}