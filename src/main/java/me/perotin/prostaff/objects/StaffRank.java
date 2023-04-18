package me.perotin.prostaff.objects;

import me.perotin.prostaff.ProStaff;
import me.perotin.prostaff.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class StaffRank implements Comparable<StaffRank> {


    private final String name;
    private final int power;
    private List<UUID> uuids;
    private List<UUID> vanished;
    private final String color;


    public StaffRank(String name, int power, List<UUID> uuids, String color) {
        this.name = name;
        this.power = power;
        this.uuids = uuids;
        this.color = color;
        this.vanished = new ArrayList<>();

    }


    @Override
    public int compareTo(StaffRank o) {
        return Integer.compare(getPower(), o.getPower());
    }


    public static StaffRank getRank(UUID uuid) {
        for (StaffRank rank : ProStaff.getInstance().getRanks()) {
            for (UUID uuid1 : rank.getUuids()) {
                if (uuid.equals(uuid1)) return rank;
            }
        }
        return null;
    }

    public String getName() {
        return ChatColor.translateAlternateColorCodes('&', name);
    }


    String getColor() {
        return color;
    }

    public int getPower() {
        return power;
    }

    public List<UUID> getUuids() {
        return uuids;
    }

    public void addUuid(UUID uuid) {
        this.uuids.add(uuid);
    }

    public List<UUID> getVanished() {
        return vanished;
    }

    public void addVanished(UUID vanished) {
        this.vanished.add(vanished);
    }


    public void showMembers(Player player){
        Inventory inventory = Bukkit.createInventory(null, 54, ChatColor.stripColor(getName())+" Members");
        inventory.setItem(4, ItemUtil.createItem(ProStaff.getColorizedString("star-click"),ProStaff.getColorizedString("left-click"), Material.NETHER_STAR));
        int slot = 9;
        if(!getUuids().isEmpty()) {
            for (UUID uuid : getUuids()) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                ItemStack stack = new ItemStack(Material.PLAYER_HEAD, 1, (byte) 3);
                SkullMeta meta = (SkullMeta) stack.getItemMeta();
                meta.setOwner(offlinePlayer.getName());
                meta.setDisplayName(ChatColor.YELLOW + offlinePlayer.getName());
                meta.setLore(Collections.singletonList(ChatColor.RED + "Click to remove"));
                stack.setItemMeta(meta);
                if (slot == 45) slot++;
                inventory.setItem(slot, stack);
                slot++;
            }
        }
        inventory.setItem(45, ItemUtil.createItem(ChatColor.YELLOW+"Previous page","", Material.EMERALD));
        player.openInventory(inventory);

    }
    public ItemStack getHead(String player, String server) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, (short) 1, (byte) 3);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        skullMeta.setDisplayName(ProStaff.getColorizedString("staff-lore-0").replace("$rank$", getName()));
        ArrayList<String> lores = new ArrayList<>();
        lores.add(0, ProStaff.getColorizedString("staff-lore-1").replace("$name$", player));
        lores.add(1, ProStaff.getColorizedString("staff-lore-2"));
        lores.add(2, ProStaff.getColorizedString("staff-lore-3").replace("$server$", server));
        skullMeta.setLore(lores);
        skullMeta.setOwner(player);
        head.setItemMeta(skullMeta);
        return head;
    }

    public ItemStack getAdminDisplay() {
        Material parsedWool = Material.getMaterial(getColor().toUpperCase()+"_WOOL");
        ItemStack wool = new ItemStack(parsedWool);
        ItemMeta woolMeta = wool.getItemMeta();
        woolMeta.setDisplayName(getName());
        // not exactly sure what the commented out code is for, seems redundant
//        ArrayList<String> lores = new ArrayList<>();
//                int x = 0;
//                for(UUID uuid : getUuids()){
//                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
//                    lores.add(x, ChatColor.YELLOW + "- " + offlinePlayer.getName());
//                    x++;
//                }
        woolMeta.setLore(Arrays.asList(ChatColor.YELLOW + "Power: " + ChatColor.GREEN + getPower(), ChatColor.YELLOW+"Members: " + ChatColor.GREEN + getUuids().size()));
        wool.setItemMeta(woolMeta);
        return wool;
    }


}
