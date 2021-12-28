package me.perotin.prostaff.events;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import me.perotin.prostaff.ProStaff;
import me.perotin.prostaff.objects.Report;
import me.perotin.prostaff.objects.menus.ReportsMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

/* Created by Perotin on 9/1/18 */
public class ReportsMenuClickEvent implements Listener {

    private ProStaff plugin;

    private HashMap<UUID, Report> sendMessageMod = new HashMap<>();
    public ReportsMenuClickEvent(ProStaff plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (event.getWhoClicked() instanceof Player) {
            Player clicker = (Player) event.getWhoClicked();
            ItemStack clicked = event.getCurrentItem();
            if(clicked == null || clicked.getType() == Material.AIR || !clicked.hasItemMeta()) return;

            if (inventory.getName().equals(plugin.getConfiguration().getString("reports-menu"))) {
                event.setCancelled(true);
                ReportsMenu paging = ReportsMenu.users.get(clicker.getUniqueId());
                if(clicked.getType() == Material.PAPER){
                    // ITS A REPORT
                    Report report = Report.of(clicked);
                    report.showReport(clicker);
                    return;
                }
            }
            Report report = null;

            for(Report r : ProStaff.getInstance().getReports()){
                if(inventory.getName().equals(r.getDate()+": " + r.getNameOfReporter())){
                    report = r;
                }
            }

            if(report != null){
                event.setCancelled(true);
                if(clicked.getType() == Material.FIREWORK){
                    ByteArrayDataOutput out = plugin.getOut();
                    out.writeUTF("Connect");
                    out.writeUTF(report.getServer());
                    clicker.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());

                } else if(clicked.getType() == Material.INK_SACK && clicked.getDurability() == (short) 8){
                    report.setResponder(clicker.getUniqueId(), clicker.getName());
                    report.showReport(clicker);
                    if(ProStaff.BUNGEECORD) {
                        ByteArrayDataOutput out = plugin.getOut();
                        out.writeUTF("FORWARD");
                        out.writeUTF("ALL");
                        out.writeUTF(report.getNameOfReporter() + "_claimed");
                        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
                        DataOutputStream msgout = new DataOutputStream(msgbytes);
                        try {
                            msgout.writeUTF(clicker.getName());
                            msgout.writeUTF(clicker.getUniqueId().toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        out.writeShort(msgbytes.toByteArray().length);
                        out.write(msgbytes.toByteArray());
                        Iterables.getFirst(Bukkit.getOnlinePlayers(), null).sendPluginMessage(ProStaff.getInstance(), "BungeeCord", out.toByteArray());

                    }



                }else if(clicked.getType() == Material.CHEST){
                    ProStaff.getInstance().getReports().remove(report);
                    ByteArrayDataOutput out = plugin.getOut();
                    if(ProStaff.BUNGEECORD) {
                        out.writeUTF("FORWARD");
                        out.writeUTF("ALL");
                        out.writeUTF(report.getNameOfReporter() + "_closed");
                        Iterables.getFirst(Bukkit.getOnlinePlayers(), null).sendPluginMessage(ProStaff.getInstance(), "BungeeCord", out.toByteArray());
                    }

                    for(ReportsMenu menu : ReportsMenu.users.values()){
                        menu.remove(report);
                    }

                    // make bungeecord close report happen all servers
                    new ReportsMenu(clicker, ProStaff.getInstance().getReports()).showMenu();
                } else if(clicked.getType() == Material.MAP){
                    sendMessageMod.put(clicker.getUniqueId(), report);
                    clicker.sendMessage(ChatColor.GRAY + "Type in chat the message you want to send to " + report.getNameOfReporter()+". Type cancel to cancel.");
                    clicker.closeInventory();
                }

            }
        }
    }

    @EventHandler
    public void sendMessageMod(AsyncPlayerChatEvent event){
        Player chatter = event.getPlayer();
        if(sendMessageMod.containsKey(chatter.getUniqueId())){
            event.setCancelled(true);
            String message = event.getMessage();
            if(message.equalsIgnoreCase("cancel")){
                chatter.sendMessage(ChatColor.GRAY + "No longer sending a message.");
                sendMessageMod.remove(chatter.getUniqueId());
            } else {
                ByteArrayDataOutput out = plugin.getOut();
                out.writeUTF("Message");
                out.writeUTF(sendMessageMod.get(chatter.getUniqueId()).getNameOfReporter());
                out.writeUTF(ChatColor.WHITE + chatter.getName() + " -> " + ChatColor.LIGHT_PURPLE + message);
                chatter.sendMessage(ChatColor.GREEN + "Message sent to " + sendMessageMod.get(chatter.getUniqueId()).getNameOfReporter()+"!");
                sendMessageMod.remove(chatter.getUniqueId());
                Iterables.getFirst(Bukkit.getOnlinePlayers(), null).sendPluginMessage(plugin, "BungeeCord", out.toByteArray());


            }
        }
    }
}
