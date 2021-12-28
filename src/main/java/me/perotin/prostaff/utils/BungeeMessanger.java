package me.perotin.prostaff.utils;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import me.perotin.prostaff.ProStaff;
import me.perotin.prostaff.objects.Report;
import me.perotin.prostaff.objects.StaffRank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class BungeeMessanger {

    public static void sendAddMemberMessage(UUID uuid, StaffRank rank){
        ByteArrayDataOutput out = ProStaff.getInstance().getOut();
        out.writeUTF("Forward");
        out.writeUTF("ALL");
        out.writeUTF("Add_"+rank.getName());

        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);
        try {
            msgout.writeUTF(uuid.toString()); // You can do anything you want with msgout
        } catch (IOException e){
            e.printStackTrace();
        }
        out.writeShort(msgbytes.toByteArray().length);
        out.write(msgbytes.toByteArray());

        Iterables.getFirst(Bukkit.getOnlinePlayers(), null).sendPluginMessage(ProStaff.getInstance(), "BungeeCord", out.toByteArray());

    }


    public static void sendStaffChatMessage(String message, String name, String server){
        ByteArrayDataOutput out = ProStaff.getInstance().getOut();
        out.writeUTF("Forward");
        out.writeUTF("ALL");
        out.writeUTF("StaffChat");

        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);
        try {
            msgout.writeUTF(name);
            msgout.writeUTF(message);
            msgout.writeUTF(server);
        } catch (IOException e){
            e.printStackTrace();
        }
        out.writeShort(msgbytes.toByteArray().length);
        out.write(msgbytes.toByteArray());
        Iterables.getFirst(Bukkit.getOnlinePlayers(), null).sendPluginMessage(ProStaff.getInstance(), "BungeeCord", out.toByteArray());

    }

    public static void sendModMessasge(Player player, String message, String server){
        ByteArrayDataOutput out = ProStaff.getInstance().getOut();

        out.writeUTF("Forward");
        out.writeUTF("ALL");
        out.writeUTF("ModTask");

        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);
        try {
            msgout.writeUTF(player.getName());
            msgout.writeUTF(player.getUniqueId().toString());
            msgout.writeUTF(message);
            msgout.writeUTF(server);
        } catch (IOException e){
            e.printStackTrace();
        }
        out.writeShort(msgbytes.toByteArray().length);
        out.write(msgbytes.toByteArray());
        Iterables.getFirst(Bukkit.getOnlinePlayers(), null).sendPluginMessage(ProStaff.getInstance(), "BungeeCord", out.toByteArray());

    }


    public static void sendRemoveMemberMessage(UUID uuid, StaffRank rank){
        ByteArrayDataOutput out = ProStaff.getInstance().getOut();
        out.writeUTF("Forward");
        out.writeUTF("ALL");
        out.writeUTF("Remove_"+rank.getName());

        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);
        try {
            msgout.writeUTF(uuid.toString()); // You can do anything you want with msgout
        } catch (IOException e){
            e.printStackTrace();
        }
        out.writeShort(msgbytes.toByteArray().length);
        out.write(msgbytes.toByteArray());

        Iterables.getFirst(Bukkit.getOnlinePlayers(), null).sendPluginMessage(ProStaff.getInstance(), "BungeeCord", out.toByteArray());
    }

    public static void sendAddVanish(UUID uuid, StaffRank rank){
        ByteArrayDataOutput out = ProStaff.getInstance().getOut();
        out.writeUTF("Forward");
        out.writeUTF("ALL");
        out.writeUTF("AddVanish_"+rank.getName());

        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);
        try {
            msgout.writeUTF(uuid.toString()); // You can do anything you want with msgout
        } catch (IOException e){
            e.printStackTrace();
        }
        out.writeShort(msgbytes.toByteArray().length);
        out.write(msgbytes.toByteArray());

        Iterables.getFirst(Bukkit.getOnlinePlayers(), null).sendPluginMessage(ProStaff.getInstance(), "BungeeCord", out.toByteArray());
    }

    public static void sendRemoveVanish(UUID uuid, StaffRank rank){
        ByteArrayDataOutput out = ProStaff.getInstance().getOut();
        out.writeUTF("Forward");
        out.writeUTF("ALL");
        out.writeUTF("RemoveVanish_"+rank.getName());

        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);
        try {
            msgout.writeUTF(uuid.toString()); // You can do anything you want with msgout
        } catch (IOException e){
            e.printStackTrace();
        }
        out.writeShort(msgbytes.toByteArray().length);
        out.write(msgbytes.toByteArray());

        Iterables.getFirst(Bukkit.getOnlinePlayers(), null).sendPluginMessage(ProStaff.getInstance(), "BungeeCord", out.toByteArray());
    }


    public static void deleteRank(StaffRank rank){
        ByteArrayDataOutput out = ProStaff.getInstance().getOut();
        out.writeUTF("Forward");
        out.writeUTF("ALL");
        out.writeUTF("Delete_"+rank.getName());

        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();

        out.writeShort(msgbytes.toByteArray().length);
        out.write(msgbytes.toByteArray());

        Iterables.getFirst(Bukkit.getOnlinePlayers(), null).sendPluginMessage(ProStaff.getInstance(), "BungeeCord", out.toByteArray());

    }


    public static void deleteReport(Report report){
        ByteArrayDataOutput out = ProStaff.getInstance().getOut();
        out.writeUTF("Forward");
        out.writeUTF("ALL");
        out.writeUTF("DeleteReport_"+report.getNameOfReporter());

        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();

        out.writeShort(msgbytes.toByteArray().length);
        out.write(msgbytes.toByteArray());

        Iterables.getFirst(Bukkit.getOnlinePlayers(), null).sendPluginMessage(ProStaff.getInstance(), "BungeeCord", out.toByteArray());

    }

}
