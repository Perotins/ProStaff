package me.perotin.prostaff.commands;

import me.perotin.prostaff.ProStaff;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ProStaffReloadCommand implements CommandExecutor {

    private final ProStaff plugin;

    public ProStaffReloadCommand(ProStaff plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


        if (!sender.hasPermission("prostaff.reload")) {
            sender.sendMessage("You don't have permission to perform this command!");
            return false;
        }

        plugin.reloadConfig();
        sender.sendMessage("Configuration reloaded!");
        return true;
    }
}
