package me.perotin.prostaff.commands;

import com.google.common.io.ByteArrayDataOutput;
import me.perotin.prostaff.ProStaff;
import me.perotin.prostaff.utils.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class StaffListCommandLegacy extends Command {


    private ProStaff plugin;


    public StaffListCommandLegacy(String name, String description, String usageMessage, List<String> aliases, ProStaff plugin) {
        super(name, description, usageMessage, aliases);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender commandSender, String commandLabel, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            Messages messages = new Messages(player);
            if (player.hasPermission("prostaff.use")) {
                if(ProStaff.BUNGEECORD) {
                    if (args.length == 0) {
                        plugin.items.clear();
                        ByteArrayDataOutput out = plugin.getOut();
                        out.writeUTF("GetServers");
                        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
                        return true;

                    }

                } else {

                }
            } else {
                messages.sendMessage("no-permission");
            }
            return true;
        }
        return true;

    }

//    @Override
//    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
//        if (commandSender instanceof Player) {
//            Player player = (Player) commandSender;
//            Messages messages = new Messages(player);
//            if (player.hasPermission("prostaff.use")) {
//                if (args.length == 0) {
//                    plugin.items.clear();
//                    ByteArrayDataOutput out = plugin.getOut();
//                    out.writeUTF("GetServers");
//                    player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
//                    return true;
//
//                } else {
//                    messages.sendMessage("no-permission");
//                }
//
//            }
//            return true;
//        }
//
//        return true;
//    }


}
