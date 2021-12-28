package me.perotin.prostaff.commands;

import me.perotin.prostaff.ProStaff;
import me.perotin.prostaff.objects.Report;
import me.perotin.prostaff.objects.menus.ReportsMenu;
import me.perotin.prostaff.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/* Created by Perotin on 8/28/18 */
public class ReportsCommand extends Command {

    private ProStaff plugin;


    public ReportsCommand(String name, String description, String usageMessage, List<String> aliases, ProStaff plugin) {
        super(name, description, usageMessage, aliases);
        this.plugin = plugin;
    }


    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            Messages messages = new Messages(player);
            if(player.hasPermission("prostaff.reports")){
                // open reports menu
                if(args.length == 0) {
                    new ReportsMenu(player, ProStaff.getInstance().getReports()).showMenu();
                } else {
                    String name = args[0];
                    for(Report report :  ProStaff.getInstance().getReports()){
                        if(report.getNameOfReporter().equalsIgnoreCase(name)){
                            report.showReport(player);
                            return true;
                        }
                    }
                    player.sendMessage(ChatColor.RED + "Report from " + name + " was not found!");
                }
            } else {
                messages.sendMessage("no-permission");
            }
        }



        return true;
    }
}
