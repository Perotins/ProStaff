package me.perotin.prostaff.utils;

import me.perotin.prostaff.ProStaff;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Messages {


    private CommandSender toSend;

    public Messages(CommandSender player) {
        this.toSend = player;
    }

    public void sendMessage(String path) {
        String msg = ChatColor.translateAlternateColorCodes('&', ProStaff.getColorizedString(path));
        toSend.sendMessage(msg);
    }

    public void sendMessagePlaceholder(String path, String placeholder, String newString) {
        String msg = ProStaff.getColorizedString(path).replace(placeholder, newString);
        String send = ChatColor.translateAlternateColorCodes('&', msg);
        toSend.sendMessage(send);
    }

    public String getString(String path){
        return ProStaff.getColorizedString(path);
    }
}