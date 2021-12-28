package me.perotin.prostaff.commands;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import me.perotin.prostaff.ProStaff;
import me.perotin.prostaff.objects.Report;
import me.perotin.prostaff.utils.Messages;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/* Created by Perotin on 8/26/18 */
public class ModCommand extends Command {

    private ProStaff plugin;
    private HashSet<UUID> cooldownSet;

    public ModCommand(String name, String description, String usageMessage, List<String> aliases, ProStaff plugin) {

        super(name, description, usageMessage, aliases);
        this.plugin = plugin;
        this.cooldownSet = new HashSet<>();

    }
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        Messages messages = new Messages(sender);
        if(sender.hasPermission("prostaff.mod.pm")){
            if(args.length >= 2){
                String playerName = args[0];
                String message = "";
                for(String s : args){
                    if(s.equals(args[0])) continue;
                    message += s + " ";
                }
                message = message.trim();
                ByteArrayDataOutput out = plugin.getOut();
                out.writeUTF("Message");
                out.writeUTF(playerName);
                out.writeUTF(ChatColor.WHITE + sender.getName() +" -> " + ChatColor.LIGHT_PURPLE + message);
                Iterables.getFirst(Bukkit.getOnlinePlayers(), null).sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
                sender.sendMessage(ChatColor.WHITE + "(me -> " + playerName + ") " + ChatColor.LIGHT_PURPLE + message);
            } else {
                sender.sendMessage(ChatColor.RED + "Incorrect arguments! First argument should be the player and the rest should be your message!");
                return true;
            }
        } else if(sender.hasPermission("prostaff.mod")) {

            //if (sender instanceof Player) {
                Player player = (Player) sender;
               // if (args.length > 0) {
                    String message = "";
                    for (String s : args) {
                        message += s + " ";
                    }

                    if(ProStaff.BUNGEECORD) {
                        Report report = null;
                        for(Report r1 : ProStaff.getInstance().getReports()){
                            if(r1.getNameOfReporter().equalsIgnoreCase(player.getName())){
                                // already a report
                                report = r1;

                            }
                        }
                        // refactor to be in plugin message so includes server name
                        if(report != null && report.hasResponder()) {
                            ByteArrayDataOutput out = plugin.getOut();
                            out.writeUTF("Message");
                            out.writeUTF(report.getNameOfResponder());
                            out.writeUTF(ChatColor.WHITE + sender.getName() + " -> " + ChatColor.LIGHT_PURPLE + message);

                            Iterables.getFirst(Bukkit.getOnlinePlayers(), null).sendPluginMessage(plugin, "BungeeCord", out.toByteArray());

                            return true;
                        }
                        if(!cooldownSet.contains(player.getUniqueId())) {
                            ByteArrayDataOutput out = plugin.getOut();
                            out.writeUTF("GetServer");
                            player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
                            messages.sendMessage("mod-message-sent");
                            ProStaff.modCommand.put(player.getName(), message);
                            int cooldown = ProStaff.getInstance().getConfiguration().getInt("cooldown-mod");
                            if(cooldown >= 0) {
                                cooldownSet.add(player.getUniqueId());

                                new BukkitRunnable(){
                                    @Override
                                    public void run(){
                                        cooldownSet.remove(player.getUniqueId());

                                    }
                                }.runTaskLaterAsynchronously(plugin, cooldown * 20);
                            }
                        } else {
                            int cooldown = ProStaff.getInstance().getConfiguration().getInt("cooldown-mod");

                            player.sendMessage(ChatColor.RED + "You must wait " + cooldown + " seconds before doing this!");
                            return true;
                        }
                    } else {
                        FileConfiguration configuration = ProStaff.getInstance().getConfiguration();
                        Date date = new Date();
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        Report report = new Report(player.getName(), player.getUniqueId(), format.format(date), message, "");
                        ProStaff.getInstance().addReport(report);
                        messages.sendMessage("mod-message-sent");
                        TextComponent formatMessage = new TextComponent(TextComponent.fromLegacyText(new Messages(player).getString("mod-message-format")
                                .replace("$server$", "").replace("$name$", player.getName()).replace("$message$", message)));
                        formatMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/"+configuration.getString("reports-command")));
                        formatMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(configuration.getString("mod-message-hover")).create()));


                        for(Player rec : Bukkit.getOnlinePlayers()){
                            if(rec.hasPermission("prostaff.notify")){
                                rec.spigot().sendMessage(formatMessage);
                            }
                        }



                    }


            } else {
            messages.sendMessage("no-permission");
            return true;
        }

        return true;
    }
}
