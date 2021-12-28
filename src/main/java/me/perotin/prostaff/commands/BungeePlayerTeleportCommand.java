package me.perotin.prostaff.commands;

import com.google.common.io.ByteArrayDataOutput;
import me.perotin.prostaff.ProStaff;
import me.perotin.prostaff.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/* Created by Perotin on 9/7/18 */
public class BungeePlayerTeleportCommand extends Command {

    private ProStaff plugin;

    public BungeePlayerTeleportCommand(String name, String description, String usageMessage, List<String> aliases, ProStaff plugin) {

        super(name, description, usageMessage, aliases);
        this.plugin = plugin;

    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if(sender instanceof Player){
            Player staff = (Player) sender;
            boolean perm = false;
            Messages messages = new Messages(staff);
            if(!ProStaff.getOnlineStaff().isEmpty()) {
                for (Player player : ProStaff.getOnlineStaff()) {
                    if(player.getUniqueId().equals(staff.getUniqueId())){
                        perm = true;
                    }
                }
                if(!perm && !staff.hasPermission("prostaff.bungeetp")){
                    messages.sendMessage("no-permission");
                    return true;
                }
                Bukkit.broadcastMessage("!");

                if(args.length != 1){
                    staff.sendMessage(ChatColor.RED + "Improper syntax! /"+ProStaff.getColorizedString("stafftp-command")+" <player>");
                    return true;
                } else {
                    String player = args[0];

                    ByteArrayDataOutput out = plugin.getOut();
                    out.writeUTF("GetServers");
                    staff.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
                    staff.sendMessage(ChatColor.GREEN + "Sending you to the server of " + player + " if he is online. If you do not get teleported he is offline.");

                    ProStaff.staffTpCommand.put(sender.getName(), player);

                }


            }
        }
        return true;
    }
}
