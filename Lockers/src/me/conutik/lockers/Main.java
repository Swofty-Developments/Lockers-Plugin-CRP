package me.conutik.lockers;

import net.minecraft.server.v1_16_R3.EnumChatFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Array;
import java.util.*;

public class Main extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        Database.setMain(this);
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getServer().getConsoleSender().sendMessage(EnumChatFormat.GREEN + "Lockers is now working");
    }

    @EventHandler
    public void onRightClick(final PlayerInteractEvent e) {

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {

            if(!e.getClickedBlock().getType().equals(Material.PUMPKIN)) return;

            if(e.getHand() == EquipmentSlot.OFF_HAND) return;

            Database.create();

            Database.load();

            Object thing = Database.config.get(String.valueOf("locker." + e.getClickedBlock().getLocation() + ".islocker"));

//            if(thing == null) return;

//            Boolean check =(Boolean) thing;

//            if(!check) return;

            Object key = Database.config.get(String.valueOf("locker." + e.getClickedBlock().getLocation() + ".keydata"));

            if(key == null) {

                e.setCancelled(true);

                char[] available = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
//
                    String let = "";
                    int i;
                    for (i = 0; i < 5; i++) {
                        int random = (int) Math.floor(Math.random() * (74 + 1) + 0);
                        let = let + available[random];
                    }

                    Database.config.set("locker." + e.getClickedBlock().getLocation() + ".keydata", let);

                ItemStack items = new ItemStack(Material.TRIPWIRE_HOOK);

                ItemMeta meta = items.getItemMeta();

                meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Locker Key");

                List<String> lores = new ArrayList<String>();

                lores.add("");

                lores.add(ChatColor.GOLD + "This key is used for locker: " + let);
//                lores.add(ChatColor.GOLD + "to forge powerful items, that were never");
//                lores.add(ChatColor.GOLD + "meant to not be discovered.");
//                lores.add(ChatColor.RED + "" + ChatColor.BOLD + ChatColor.UNDERLINE + "Use this item with caution");

                meta.setLore(lores);

                items.setItemMeta(meta);





                    e.getPlayer().getInventory().addItem(items);

            } else {

                e.setCancelled(true);
                ItemStack keyItem = e.getPlayer().getInventory().getItemInMainHand();
                ItemMeta keyMeta = keyItem.getItemMeta();
                if(keyMeta == null) {
                    e.getPlayer().sendMessage(EnumChatFormat.RED + "You try opening the locker, but the key isn't working!");
                    return;
                }
                if(keyMeta.getLore() == null) {
                    e.getPlayer().sendMessage(EnumChatFormat.RED + "You try opening the locker, but the key isn't working!");
                    return;
                }
                    String keyLore = keyMeta.getLore().get(1);
                    if(keyLore == null) {
                        e.getPlayer().sendMessage(EnumChatFormat.RED + "You try opening the locker, but the key isn't working!");
                        return;
                    }

                    String[] keyLoreSplit = keyLore.split(": ");

                    String keyCode = keyLoreSplit[1];

                if(!keyCode.equals(key)) {
                    e.getPlayer().sendMessage(EnumChatFormat.RED + "You try opening the locker, but the key isn't working!");
                    return;
                }
            }

            Inventory inv = Bukkit.createInventory(null, 18, "Locker");

            Object items = Database.config.get("locker." + e.getClickedBlock().getLocation() + ".data");
            ArrayList item = (ArrayList) items;

            if (items == null) {
                e.getPlayer().openInventory(inv);
            } else {

                for (int i = 0; i < item.size(); i++) {

                    inv.setItem(i, (ItemStack) item.get(i));

                }
                e.getPlayer().openInventory(inv);
            }

            Database.config.set("inlocker." + e.getPlayer().getUniqueId(), e.getClickedBlock().getLocation());

            Database.save();
        }

    }


    @EventHandler
    public void onInvClose(final InventoryCloseEvent e) {

        if(!e.getPlayer().getOpenInventory().getTitle().equals("Locker")) return;


        Database.create();

        Database.load();

        Object thing = Database.config.get("inlocker." + e.getPlayer().getUniqueId());


//        Thing: Temporary location of locker through player

//        Thing2: if locker or not

//        check2: boolean of locker or not

        if (thing == null) return;

        Location check = (Location) thing;

        thing = Database.config.get(String.valueOf(check));

//        if(thing == null) return;

//        Boolean check2 = (Boolean) thing;

//        if(!check2) return;

        Database.config.set("locker." + check + ".data", e.getInventory().getStorageContents());

        Database.save();


    }


}
