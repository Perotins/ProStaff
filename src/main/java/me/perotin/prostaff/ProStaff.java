package me.perotin.prostaff;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.perotin.prostaff.commands.*;
import me.perotin.prostaff.events.AdminMenuClickEvent;
import me.perotin.prostaff.events.BungeeStaffChatEvent;
import me.perotin.prostaff.events.ReportsMenuClickEvent;
import me.perotin.prostaff.events.StaffListClickEvent;
import me.perotin.prostaff.objects.MySQLWrapper;
import me.perotin.prostaff.objects.Report;
import me.perotin.prostaff.objects.menus.AdminMenu;
import me.perotin.prostaff.objects.menus.StaffMenu;
import me.perotin.prostaff.objects.StaffRank;
import me.perotin.prostaff.utils.BungeeMessanger;
import me.perotin.prostaff.utils.Messages;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ProStaff extends JavaPlugin implements PluginMessageListener {


    /*
    TODO:
            Test everything
            things to test:
            - Message feature
            - Teleport to server feature
            - Multiple staff with powers working
            - Synchronous updates
            - Buttons work


        NPE on AdminMenuClickEvent l:49 and StaffListClickEvent l:45
        Also SLA creae rank type name 2nd time does nothing

        Finish staff chat command
     */
    private static ProStaff instance;

    private List<StaffRank> ranks;
    private FileConfiguration configuration;
    private MySQLWrapper sql;
    public HashMap<ItemStack, StaffRank> items;
    public static HashMap<String, String> modCommand = new HashMap<>();
    public static HashMap<String, String> staffChatCommand = new HashMap<>();
    public static HashMap<String, String> staffTpCommand =  new HashMap<>();

    public static boolean BUNGEECORD = false;
    private ArrayList<Report> reports;

    @Override
    public void onEnable() {

        instance = this;
        ranks = new ArrayList<>();
        items = new HashMap<>();
        saveDefaultConfig();
        configuration = getConfig();
        reports = new ArrayList<>();
        BUNGEECORD = configuration.getBoolean("bungeecord");
        if (BUNGEECORD) {
            this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
        }
        // setting up MySQL
        sql = new MySQLWrapper(this);
        sql.init();
        sql.createTables();
        ranks.addAll(sql.loadRanks());
        updateRanks();
        registerCommand(new StaffListCommandLegacy(configuration.getString("command-name"), configuration.getString("command-description"),
                configuration.getString("command-usage"), configuration.getStringList("command-aliases"), this));
        registerCommand(new ModCommand(configuration.getString("mod-command"), configuration.getString("mod-description"),
                configuration.getString("mod-usage"), configuration.getStringList("mod-aliases"), this));
        registerCommand(new ReportsCommand(configuration.getString("reports-command"), configuration.getString("reports-description"),
                configuration.getString("reports-usage"), configuration.getStringList("reports-aliases"), this));
        registerCommand(new BungeeStaffChatCommand(configuration.getString("staffchat-command"), configuration.getString("staffchat-description")
        , configuration.getString("staffchat-usage"), configuration.getStringList("staffchat-aliases"), this));
        registerCommand(new BungeePlayerTeleportCommand(configuration.getString("stafftp-command"), configuration.getString("stafftp-description"),
                configuration.getString("stafftp-usage"), configuration.getStringList("stafftp-aliases"), this));
        registerCommand(new BungeeStaffHelpCommand(this, configuration.getString("bstaff-command"), configuration.getString("bstaff-description"), configuration.getString("bstaff-usage"),
        configuration.getStringList("bstaff-aliases")));


//        registerCommand(new StaffListCommandLegacy("test", "Test1",
//            "/test", Arrays.asList("t", "t"), this));

        Bukkit.getPluginManager().registerEvents(new StaffListClickEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new AdminMenuClickEvent(), this);
        Bukkit.getPluginManager().registerEvents(new ReportsMenuClickEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new BungeeStaffChatEvent(this), this);
        getCommand("confirmrank").setExecutor(new ConfirmRankCommand());
        getCommand("stafflistadmin").setExecutor(new StaffListAdminCommand());



    }

    public void updateRanks(){
        this.ranks = sortByPower(ranks);

    }

    public void addReport(Report report){
        reports.add(report);
    }


    @Override
    public void onDisable() {
        sql.addAllRanks();
        if (instance != null) {
            instance = null;
        }
    }

    private static List<StaffRank> sortByPower(List<StaffRank> ranks){
        return ranks.stream().sorted((rank, rank2) -> Integer.compare(rank.getPower(), rank2.getPower())).collect(Collectors.toList());
    }



    public static ProStaff getInstance() {
        return instance;
    }

    public List<StaffRank> getRanks() {
        return ranks;
    }

    public FileConfiguration getConfiguration() {
        return configuration;
    }

    public static ArrayList<Player> getOnlineStaff(){
        ArrayList<Player> staff = new ArrayList<>();
        for(StaffRank rank : ProStaff.getInstance().getRanks()){
            for(UUID uuid : rank.getUuids()){
                if(Bukkit.getPlayer(uuid) != null) staff.add(Bukkit.getPlayer(uuid));
            }
        }

        return staff;
    }

    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (channel.equals("BungeeCord")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            String subChannel = in.readUTF();
            if (subChannel.equals("GetServers")) {

                String[] serverList = in.readUTF().split(", ");
                for (String server : serverList) {
                    ByteArrayDataOutput out = getOut();
                    out.writeUTF("PlayerList");
                    out.writeUTF(server);
                    player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
                }

            }
            if(subChannel.equals("GetServer")){
                if(modCommand.containsKey(player.getName())) {
                    String serverName = in.readUTF();
                    String modMessage = modCommand.get(player.getName());
                    BungeeMessanger.sendModMessasge(player, modMessage, serverName);
                    modCommand.remove(player.getName());
                } else {
                    String serverName = in.readUTF();
                    String staffMessage = staffChatCommand.get(player.getName());
                    BungeeMessanger.sendStaffChatMessage(staffMessage, player.getName(), serverName);
                }

            }

            if(subChannel.equals("StaffChat")){

                short len = in.readShort();
                byte[] msgbytes = new byte[len];
                in.readFully(msgbytes);

                DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
                try {
                    String modName = msgin.readUTF();
                    String staffMessage = msgin.readUTF();
                    String serverName = msgin.readUTF();

                    for(StaffRank rank : getRanks()){
                        for(UUID uuid : rank.getUuids()){
                            if(Bukkit.getPlayer(uuid) != null){
                                if(Bukkit.getPlayer(uuid).getName().equalsIgnoreCase(modName)){ continue;}
                                Bukkit.getPlayer(uuid).sendMessage(ChatColor.GRAY + "[STAFF] " + ChatColor.WHITE+ serverName + " " + modName + " -> " + ChatColor.LIGHT_PURPLE + staffMessage);
                            }
                        }
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
            if (subChannel.equals("PlayerList")) {
                String server = in.readUTF();
                String[] playerList = in.readUTF().split(", ");

                if(staffTpCommand.containsKey(player.getName())){
                    String toFind = staffTpCommand.get(player.getName());
                    if(playerList.length != 0) {
                        for (String name : playerList) {
                            if(name.equalsIgnoreCase(toFind)){
                                ByteArrayDataOutput out = getOut();
                                out.writeUTF("ConnectOther");
                                out.writeUTF(player.getName());
                                out.writeUTF(server);
                                player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
                                staffTpCommand.remove(player.getName());
                                return;
                            }
                        }
                    }
                    // not found


                    return;
                }

                if(playerList.length != 0) {
                    for (String name : playerList) {
                        OfflinePlayer offlinePlayer;
                        try {
                            if (name == null || name.isEmpty()) {
                                continue;
                            }
                            offlinePlayer = Bukkit.getOfflinePlayer(name);
                            for (StaffRank rank : getRanks()) {
                                if (rank.getUuids().contains(offlinePlayer.getUniqueId())) {
                                    if (!rank.getVanished().contains(offlinePlayer.getUniqueId())) {
                                        items.put(rank.getHead(name, server), rank);
                                    }
                                }
                            }
                        } catch (IllegalArgumentException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                // opens the menu
                new StaffMenu(this, sortByValue(items), player).showInventory();

            }
            for(Report report : getReports()){
                if(subChannel.equalsIgnoreCase(report.getNameOfReporter()+"_closed")){
                    getReports().remove(report);
                }
                if(subChannel.equalsIgnoreCase(report.getNameOfReporter()+"_claimed")){
                    short len = in.readShort();
                    byte[] msgbytes = new byte[len];
                    in.readFully(msgbytes);

                    DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
                    try {
                        String name = msgin.readUTF();
                        UUID uuid = UUID.fromString(msgin.readUTF());
                        report.setResponder(uuid, name);
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
            for(StaffRank rank : getRanks()){
                if(subChannel.equals("Add_"+rank.getName())){
                    short len = in.readShort();
                    byte[] msgbytes = new byte[len];
                    in.readFully(msgbytes);

                    DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
                    try {
                        UUID add = UUID.fromString(msgin.readUTF());
                        rank.getUuids().add(add);
                        for(Player update : Bukkit.getOnlinePlayers()){
                            if(update.getInventory().getName().equals(ChatColor.stripColor(rank.getName() + " Members"))){
                                rank.showMembers(update);
                            }
                        }

                    } catch (IOException ex){
                        ex.printStackTrace();
                    }
                }
                if(subChannel.equals("Remove_"+rank.getName())){
                    short len = in.readShort();
                    byte[] msgbytes = new byte[len];
                    in.readFully(msgbytes);

                    DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
                    try {
                        UUID add = UUID.fromString(msgin.readUTF());
                        rank.getUuids().remove(add);
                        for(Player update : Bukkit.getOnlinePlayers()){
                            if(update.getInventory().getName().equals(ChatColor.stripColor(rank.getName() + " Members"))){
                                rank.showMembers(update);
                            }
                        }

                    } catch (IOException ex){
                        ex.printStackTrace();
                    }
                }
                if(subChannel.equals("AddVanish_"+rank.getName())){
                    short len = in.readShort();
                    byte[] msgbytes = new byte[len];
                    in.readFully(msgbytes);

                    DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
                    try {
                        UUID add = UUID.fromString(msgin.readUTF());
                        rank.getVanished().add(add);


                    } catch (IOException ex){
                        ex.printStackTrace();
                    }
                }
                if(subChannel.equals("RemoveVanish_"+rank.getName())){
                    short len = in.readShort();
                    byte[] msgbytes = new byte[len];
                    in.readFully(msgbytes);

                    DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
                    try {
                        UUID add = UUID.fromString(msgin.readUTF());
                        rank.getVanished().remove(add);


                    } catch (IOException ex){
                        ex.printStackTrace();
                    }
                }
                if(subChannel.equals("Delete_"+rank.getName())){
                    short len = in.readShort();
                    byte[] msgbytes = new byte[len];
                    in.readFully(msgbytes);

                    for(Player update : Bukkit.getOnlinePlayers()){
                        if(update.getInventory().getName().equals(getColorizedString("admin-inventory-name"))
                                || update.getInventory().getName().equals(ChatColor.stripColor(rank.getName() + " Members"))){
                            new AdminMenu(update).showMainMenu();
                        }
                    }

                    DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
                    getRanks().remove(rank);

                    break;
                }
            }
            if(subChannel.equals("NewRank")){
                short len = in.readShort();
                byte[] msgbytes = new byte[len];
                in.readFully(msgbytes);


                DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
                try {
                    String name = msgin.readUTF();
                    int power = msgin.readInt();
                    int color = msgin.readInt();

                    getRanks().add(new StaffRank(name, power, new ArrayList<>(), color));
                    updateRanks();
                    for(Player update : Bukkit.getOnlinePlayers()){
                        if(update.getInventory().getName().equals(getColorizedString("admin-inventory-name"))){
                            new AdminMenu(update).showMainMenu();
                        }
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }

            }

            if(subChannel.equals("ModTask")){
                short len = in.readShort();
                byte[] msgbytes = new byte[len];
                in.readFully(msgbytes);


                DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
                try {
                    String name = msgin.readUTF();
                    String uniqueId = msgin.readUTF();
                    String modMessage = msgin.readUTF();
                    String server = msgin.readUTF();
                    Date date = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    Report report = new Report(name,UUID.fromString(uniqueId) , format.format(date), modMessage, server);
                    addReport(report);
                    TextComponent formatMessage = new TextComponent(TextComponent.fromLegacyText(new Messages(player).getString("mod-message-format")
                            .replace("$server$", server).replace("$name$", name).replace("$message$", modMessage)));
                    formatMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/"+configuration.getString("reports-command") + " "+ name));
                    formatMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(configuration.getString("mod-message-hover")).create()));



                    for(Player rec : Bukkit.getOnlinePlayers()){

                        if(rec.hasPermission("prostaff.notify")){

                            rec.spigot().sendMessage(formatMessage);
                        }
                    }


                } catch (IOException e){
                    e.printStackTrace();
                }

            }
        }
    }


     private void registerCommand(Command command) {
        try {

            final Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            final CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
            commandMap.register(command.getLabel(), command);

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public String getPrefix() {
        return getColorizedString("prefix");
    }

     static <K, V extends Comparable<? super StaffRank>> Map<K, StaffRank> sortByValue(Map<K, StaffRank> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(/*Collections.reverseOrder()*/))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public ArrayList<Report> getReports(){
        return this.reports;
    }


    public static String getColorizedString(String path) {
        return ChatColor.translateAlternateColorCodes('&', getInstance().configuration.getString(path));
    }

    public ByteArrayDataOutput getOut() {
        return ByteStreams.newDataOutput();
    }
}
