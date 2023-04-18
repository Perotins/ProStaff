package me.perotin.prostaff.commands;

import me.perotin.prostaff.ProStaff;
import me.perotin.prostaff.api.ProStaffAPI;
import me.perotin.prostaff.utils.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/* Created by Perotin on 9/6/18 */
public class BungeeStaffChatCommand extends Command {

    private ProStaff plugin;
    public static HashSet<UUID> bungeeChatStaff;
    public static HashSet<UUID> localChatStaff;


    public BungeeStaffChatCommand(String name, String description, String usageMessage, List<String> aliases, ProStaff plugin) {

        super(name, description, usageMessage, aliases);
        this.plugin = plugin;
        bungeeChatStaff = new HashSet<>();
        localChatStaff = new HashSet<>();

    }
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            if(ProStaffAPI.getRank(player.getUniqueId()) == null && !player.hasPermission("prostaff.bungeechat")){
                player.sendMessage(new Messages(player).getString("no-permission"));
                return true;
            }
            if(args.length == 0 || (args.length != 1 && args[0].equalsIgnoreCase("local"))){
                if(ProStaff.BUNGEECORD){
                    if(bungeeChatStaff.contains(player.getUniqueId())){
                        bungeeChatStaff.remove(player.getUniqueId());
                        player.sendMessage(ChatColor.GREEN + "No longer chatting in Bungee Staff Chat");
                    } else {
                        bungeeChatStaff.add(player.getUniqueId());
                        player.sendMessage(ChatColor.GREEN + "Now chatting in the Bungee Staff Chat");
                    }
                } else {
                    if(localChatStaff.contains(player.getUniqueId())){
                        localChatStaff.remove(player.getUniqueId());
                        player.sendMessage(ChatColor.GREEN + "No longer chatting in local staff chat");
                    } else {
                        localChatStaff.add(player.getUniqueId());
                        player.sendMessage(ChatColor.GREEN+ "Now chatting in local staff chat");
                    }
                }
            } else if(args.length == 1){
                if(args[0].equalsIgnoreCase("local") || args[0].equalsIgnoreCase("lc")){
                    if(localChatStaff.contains(player.getUniqueId())){
                        localChatStaff.remove(player.getUniqueId());
                        player.sendMessage(ChatColor.GREEN + "No longer chatting in local staff chat");
                    } else {
                        localChatStaff.add(player.getUniqueId());
                        player.sendMessage(ChatColor.GREEN+ "Now chatting in local staff chat");
                    }
                }

            }
        }

        return true;
    }
}
