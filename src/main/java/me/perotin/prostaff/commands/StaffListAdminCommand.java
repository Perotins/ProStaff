package me.perotin.prostaff.commands;

import me.perotin.prostaff.objects.menus.AdminMenu;
import me.perotin.prostaff.utils.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class StaffListAdminCommand  implements CommandExecutor {




    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player){
            Player player = (Player) sender;
            if(player.hasPermission("prostaff.admin")){
                // open up menus
                new AdminMenu(player).showMainMenu();
            } else {
                new Messages(player).sendMessage("no-permission");
            }
        } else
        {
            return true;
        }


        return true;
    }
}
