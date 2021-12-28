package me.perotin.prostaff.commands;

import me.perotin.prostaff.ProStaff;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

/* Created by Perotin on 9/7/18 */
public class BungeeStaffHelpCommand extends Command {


    // TODO
    // main help command with json
    private ProStaff plugin;

    public BungeeStaffHelpCommand(ProStaff plugin, String command, String usage, String description, List<String> aliases){
        super(command, description, usage, aliases);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {

        FileConfiguration configuration = ProStaff.getInstance().getConfiguration();
        sender.sendMessage(ChatColor.AQUA + "ProStaff >> Click to copy and hover to see descriptions ");
        TextComponent tpCommand = new TextComponent(TextComponent.fromLegacyText(ChatColor.AQUA + configuration.getString("stafftp-usage")));
        tpCommand.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.AQUA + configuration.getString("stafftp-description")).create()));
        tpCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/"+configuration.getString("stafftp-command")));

        TextComponent modCommand = new TextComponent(TextComponent.fromLegacyText(ChatColor.AQUA + configuration.getString("mod-usage")));
        modCommand.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.AQUA + configuration.getString("mod-description")).create()));
        modCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/"+configuration.getString("mod-command")));

        TextComponent reportsCommand = new TextComponent(TextComponent.fromLegacyText(ChatColor.AQUA + configuration.getString("reports-usage")));
        reportsCommand.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.AQUA + configuration.getString("reports-description")).create()));
        reportsCommand.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/"+configuration.getString("reports-command")));

        TextComponent staffChat = new TextComponent(TextComponent.fromLegacyText(ChatColor.AQUA +  configuration.getString("staffchat-usage")));
        staffChat.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.AQUA + configuration.getString("staffchat-description")).create()));
        staffChat.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/"+configuration.getString("staffchat-command")));

        TextComponent staffList = new TextComponent(TextComponent.fromLegacyText(ChatColor.AQUA +  configuration.getString("command-usage")));
        staffList.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.AQUA + configuration.getString("command-description")).create()));
        staffList.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/"+configuration.getString("command-name")));


        if(sender.hasPermission("prostaff.reports")){
            sender.spigot().sendMessage(reportsCommand);
        }
        if(sender.hasPermission("prostaff.mod.pm")){
            sender.spigot().sendMessage(modCommand);
        }
        if(sender.hasPermission("prostaff.bungeetp")){
            sender.spigot().sendMessage(tpCommand);
        }
        if(sender.hasPermission("prostaff.bungeechat")){
            sender.spigot().sendMessage(staffChat);
        }
        if(sender.hasPermission("prostaff.use")){
            sender.spigot().sendMessage(staffList);
        }
        return true;
    }
}
