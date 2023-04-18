package me.perotin.prostaff.commands;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import me.perotin.prostaff.ProStaff;
import me.perotin.prostaff.events.AdminMenuClickEvent;
import me.perotin.prostaff.objects.menus.AdminMenu;
import me.perotin.prostaff.objects.StaffRank;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * class for creating a rank from the staff admin menu, not meant to be used by players but only for this
 * plugin as we want to use the ClickEvent
 */

/* Created by Perotin on 8/10/18 */
public class ConfirmRankCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.hasPermission("prostaff.admin")) {
            if (args.length == 3) {
                String name = args[0];
                String power = args[1];
                String color = args[2];
                if (Material.getMaterial(color+"_WOOL") == null) {
                    sender.sendMessage("Not a valid color! Look at https://minecraft.fandom.com/wiki/Wool for a list" +
                            "of all colored wool.");
                    sender.sendMessage("Only use the color part of the item. Example: RED_WOOL, type 'red'.");
                    return true;
                }

                int powerInt;
                powerInt = Integer.parseInt(power);

                StaffRank newRank = new StaffRank(name, powerInt, new ArrayList<>(), color);
                ProStaff.getInstance().getRanks().add(newRank);
                ProStaff.getInstance().updateRanks();
                if(ProStaff.BUNGEECORD) {
                    ByteArrayDataOutput out = ProStaff.getInstance().getOut();
                    out.writeUTF("Forward");
                    out.writeUTF("ALL");
                    out.writeUTF("NewRank");

                    ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
                    DataOutputStream msgout = new DataOutputStream(msgbytes);
                    try {
                        msgout.writeUTF(name); // You can do anything you want with msgout
                        msgout.writeInt(powerInt);
                        msgout.writeUTF(color);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    out.writeShort(msgbytes.toByteArray().length);
                    out.write(msgbytes.toByteArray());

                    Iterables.getFirst(Bukkit.getOnlinePlayers(), null).sendPluginMessage(ProStaff.getInstance(), "BungeeCord", out.toByteArray());

                }

                if (sender instanceof Player) {
                    Player open = (Player) sender;
                    new AdminMenu(open).showMainMenu();
                    AdminMenuClickEvent click = AdminMenuClickEvent.createMode.get(open.getUniqueId());
                    click.setPower(-1);
                    click.setColor(-1);
                    click.setName(null);


                    AdminMenuClickEvent.createMode.remove(open.getUniqueId());

                }

            } else {
                return false;
            }
        }

        return true;
    }
}
