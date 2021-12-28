package me.perotin.prostaff.events;

import com.google.common.io.ByteArrayDataOutput;
import me.perotin.prostaff.ProStaff;
import me.perotin.prostaff.commands.BungeeStaffChatCommand;
import me.perotin.prostaff.objects.StaffRank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

/* Created by Perotin on 9/7/18 */
public class BungeeStaffChatEvent implements Listener {

    private ProStaff plugin;

    public BungeeStaffChatEvent(ProStaff plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        Player chatter = event.getPlayer();
        if(BungeeStaffChatCommand.bungeeChatStaff.contains(chatter.getUniqueId())){
            event.setCancelled(true);
            ProStaff.staffChatCommand.put(chatter.getName(), event.getMessage());
            ByteArrayDataOutput out = plugin.getOut();
            out.writeUTF("GetServer");
            chatter.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
            chatter.sendMessage(ChatColor.GRAY + "[STAFF] " + ChatColor.WHITE + "me -> " + ChatColor.LIGHT_PURPLE + event.getMessage());
        } else if(BungeeStaffChatCommand.localChatStaff.contains(chatter.getUniqueId())){
            event.setCancelled(true);
            for(StaffRank rank : plugin.getRanks()){
                for(UUID uuid  : rank.getUuids()){
                    if(Bukkit.getPlayer(uuid) != null){
                        Player staff = Bukkit.getPlayer(uuid);
                        if(staff.getName().equalsIgnoreCase(chatter.getName())) continue;
                        staff.sendMessage(ChatColor.GRAY + "[STAFF] " +  ChatColor.WHITE + chatter.getName() + " -> " + ChatColor.LIGHT_PURPLE + event.getMessage());
                    }
                }
            }
        }
    }
}
