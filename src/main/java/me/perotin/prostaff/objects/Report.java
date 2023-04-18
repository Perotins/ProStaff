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

import java.util.UUID;

/* Created by Perotin on 8/28/18 */
public class Report {

    private final String nameOfReporter;
    private final UUID reporter;
    private final String date;
    private String nameOfResponder;
    private UUID responder;
    private final String message;
    private String server;
    private boolean hasResponder;
    public Report(String name, UUID reporter, String date, String message, String server){
        this.nameOfReporter = name;
        this.message = message;
        this.reporter = reporter;
        this.date = date;
        this.responder = null;
        if(!server.equals("")){
            this.server = server;
        }
        this.hasResponder = false;
    }

    public String getServer() {
        return server;
    }

    public String getNameOfReporter() {
        return nameOfReporter;
    }

    public UUID getReporter() {
        return reporter;
    }

    public String getDate() {
        return date;
    }

    public UUID getResponder() {
        return responder;
    }

    public boolean hasResponder() {
        return hasResponder;
    }

    public void setResponder(UUID responder, String name) {
        this.responder = responder;
        this.nameOfResponder = name;
        this.hasResponder = true;
    }

    public String getMessage() {
        return message;
    }

    public ItemStack getItemStack(){
        if(!server.equals("")) {
            if(getNameOfResponder() == null) {
                return ItemUtil.createItem(ChatColor.GRAY + "Report from: " + ChatColor.YELLOW + nameOfReporter, ChatColor.GRAY + "Claimed: " + ChatColor.RED + "None", ChatColor.GRAY + "Click to view", Material.PAPER);
            } else {
                return ItemUtil.createItem(ChatColor.GRAY + "Report from: " + ChatColor.YELLOW + nameOfReporter, ChatColor.GRAY + "Claimed: " + ChatColor.GREEN + getNameOfResponder(), ChatColor.GRAY + "Click to view", Material.PAPER);

            }
        } else {
            if(getNameOfResponder() == null) {
                return ItemUtil.createItem(ChatColor.GRAY + "Report from: " + ChatColor.YELLOW + nameOfReporter, ChatColor.GRAY + "Claimed: " + ChatColor.RED + "None", ChatColor.GRAY + "Click to view", Material.PAPER);
            } else {
                return ItemUtil.createItem(ChatColor.GRAY + "Report from: " + ChatColor.YELLOW + nameOfReporter, ChatColor.GRAY + "Claimed: " + ChatColor.GREEN + getNameOfResponder(), ChatColor.GRAY + "Click to view", Material.PAPER);

            }
        }
    }

    public String getNameOfResponder() {
        return nameOfResponder;
    }

    public void showReport(Player toShow){
        OfflinePlayer player = Bukkit.getOfflinePlayer(reporter);
        Inventory report = Bukkit.createInventory(null, 54, date + ": " + player.getName());
        report.setItem(4, ItemUtil.createItem(ChatColor.YELLOW + "Report from " + player.getName(), "", Material.PAPER));
        report.setItem(19, ItemUtil.createItem(ChatColor.GRAY + "Report:", ChatColor.YELLOW + message, Material.BOOK));
        if(responder == null) {
            ItemStack notClaimed = ItemUtil.createItem(ChatColor.GRAY + "Claimed: " + ChatColor.RED + "None", ChatColor.GRAY + "Click to claim", Material.INK_SAC);
            notClaimed.setDurability((short) 8);
            report.setItem(21, notClaimed);
        } else {

            ItemStack notClaimed = ItemUtil.createItem(ChatColor.GRAY + "Claimed: ", ChatColor.GREEN + getNameOfResponder(), Material.INK_SAC);
            notClaimed.setDurability((short) 10);
            report.setItem(21, notClaimed);
        }

        report.setItem(23, ItemUtil.createItem(ChatColor.GRAY + "Send a message to " + player.getName(), "", Material.MAP));
        if(ProStaff.BUNGEECORD) {
            report.setItem(25, ItemUtil.createItem(ChatColor.GRAY + "Teleport to server " + server, "", Material.FIREWORK_ROCKET));
            report.setItem(31, ItemUtil.createItem(ChatColor.GRAY +"Click to close the report", "", Material.CHEST));

        } else {
            report.setItem(25, ItemUtil.createItem(ChatColor.GRAY +"Click to close the report", "", Material.CHEST));
        }
        toShow.openInventory(report);

    }
    public static Report of(ItemStack item){
        for(Report report : ProStaff.getInstance().getReports()){

            if(item.isSimilar(report.getItemStack())){
                return report;
            }
        }
        return null;
    }
}
