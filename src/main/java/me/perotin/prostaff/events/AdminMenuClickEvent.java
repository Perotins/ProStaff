package me.perotin.prostaff.events;

import me.perotin.prostaff.ProStaff;
import me.perotin.prostaff.objects.menus.AdminMenu;
import me.perotin.prostaff.objects.StaffRank;
import me.perotin.prostaff.utils.BungeeMessanger;
import me.perotin.prostaff.utils.Messages;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/* Created by Perotin on 8/9/18 */
public class AdminMenuClickEvent implements Listener {

    private Messages messages;
    public static HashMap<UUID, AdminMenuClickEvent> createMode = new HashMap<>();
    private String name = null;
    private int power = -1;
    private int color = -1;
    private static HashMap<UUID, StaffRank> addMember = new HashMap<>();



    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory clicked = event.getInventory();
        if (event.getWhoClicked() instanceof Player) {
            Player clicker = (Player) event.getWhoClicked();
            this.messages = new Messages(clicker);
            ItemStack itemStack = event.getCurrentItem();


            if (clicked.getName().equals(messages.getString("admin-inventory-name"))) {

                event.setCancelled(true);
                //if(itemStack == null || itemStack.getType() == Material.AIR) return;
                if (itemStack.getType() == Material.ANVIL) {
                    // create a new rank
                    clicker.closeInventory();
                    createMode.put(clicker.getUniqueId(), this);
                    messages.sendMessage("create-rank11");
                    messages.sendMessage("create-rank-1");
                    clicker.sendMessage(" ");
                    messages.sendMessage("create-rank-2");
                    return;
                }
                if (itemStack.getType() == Material.WOOL) {
                    if (event.getClick() == ClickType.LEFT) {
                        for (StaffRank rank : ProStaff.getInstance().getRanks()) {
                            if (rank.getAdminDisplay().getItemMeta().getDisplayName().equals(itemStack.getItemMeta().getDisplayName())) {
                                rank.showMembers(clicker);
                                return;
                            }
                        }
                    } else if(event.getClick() == ClickType.RIGHT){

                        for (StaffRank rank : ProStaff.getInstance().getRanks()) {

                            if (rank.getAdminDisplay().getItemMeta().getDisplayName().equals(itemStack.getItemMeta().getDisplayName())) {
                               // bring up menu asking if delete or not delete

                                new AdminMenu(clicker).showConfirmDeleteMenu(rank);
                                return;

                            }
                        }
                    }
                }
            }
            for(StaffRank rank : ProStaff.getInstance().getRanks()) {
                if (clicked.getName().equals(messages.getString("confirm-delete-rank")
                .replace("$rank$", rank.getName()))) {
                    if(itemStack.getType() == Material.REDSTONE_BLOCK){

                        if(ProStaff.BUNGEECORD){
                            BungeeMessanger.deleteRank(rank);
                        }
                        clicker.closeInventory();
                        clicker.sendMessage(ChatColor.RED + "Deleted rank " + rank.getName());
                        ProStaff.getInstance().getRanks().remove(rank);
                        return;
                    } else {
                        if(itemStack.getType() == Material.EMERALD_BLOCK){
                            new AdminMenu(clicker).showMainMenu();
                            return;
                        }
                    }
                }
            }
            boolean manageClick = false;
            StaffRank clickedIn = null;
            for (StaffRank rank : ProStaff.getInstance().getRanks()) {
                if (clicked.getName().equals(ChatColor.stripColor(rank.getName()) + " Members")) {
                    manageClick = true;
                    clickedIn = rank;
                }
            }
            if (manageClick) {
                // managed click in a manage menu
                event.setCancelled(true);
                if (itemStack.getType() == Material.NETHER_STAR) {
                    addMember.put(clicker.getUniqueId(), clickedIn);
                    clicker.closeInventory();
                    IntStream.range(0, 20).forEach(i -> clicker.sendMessage(" "));
                    messages.sendMessage("add-member-1");
                    messages.sendMessage("add-member-2");
                    messages.sendMessage("add-member-3");
                } else if(itemStack.getType() == Material.SKULL_ITEM){
                    String name = itemStack.getItemMeta().getDisplayName();
                    for(OfflinePlayer player : clickedIn.getUuids().stream().map(Bukkit::getOfflinePlayer).collect(Collectors.toList())){

                        if(player.getName().equals(ChatColor.stripColor(name))){
                            // remove em
                            if(ProStaff.BUNGEECORD){
                                BungeeMessanger.sendRemoveMemberMessage(player.getUniqueId(), clickedIn);
                            }
                            clickedIn.getUuids().remove(player.getUniqueId());
                            clickedIn.showMembers(clicker);
                        }
                    }

                } else if(itemStack.getType() == Material.EMERALD){
                    new AdminMenu(clicker).showMainMenu();
                }
            }

        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player chatter = event.getPlayer();
        String message = event.getMessage();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (createMode.keySet().contains(player.getUniqueId()) || addMember.keySet().contains(player.getUniqueId())) {
                event.getRecipients().remove(player);
            }
        }
        if (createMode.keySet().contains(chatter.getUniqueId())) {
            this.messages = new Messages(chatter);
            event.setCancelled(true);
            if (message.equalsIgnoreCase("cancel")) {
                createMode.remove(chatter.getUniqueId());
                this.name = null;
                this.power = -1;
                this.color = -1;
                messages.sendMessage("cancelled-create-message");
            } else {
                if (name == null) {
                    if (!message.trim().isEmpty()) {
                        // check for dupe
                        for(StaffRank rank : ProStaff.getInstance().getRanks()){
                            if(rank.getName().equalsIgnoreCase(message)){
                                // its a dupe
                                messages.sendMessagePlaceholder("create-rank-dupe", "$rank$", rank.getName());
                                return;
                            }
                        }
                        this.name = message;
                        IntStream.range(0, 20).forEach(i -> chatter.sendMessage(" "));
                        messages.sendMessage("create-rank-3");
                        chatter.sendMessage(" ");
                        messages.sendMessagePlaceholder("name-placeholder", "$name$", name);
                    } else {
                        messages.sendMessage("empty-string");
                    }
                } else if (power == -1) {
                    if (!message.trim().isEmpty()) {
                        try {
                            this.power = Integer.parseInt(message);
                            IntStream.range(0, 20).forEach(i -> chatter.sendMessage(" "));
                            messages.sendMessage("create-rank-4");
                            messages.sendMessage("create-rank-41");
                            chatter.sendMessage(" ");
                            messages.sendMessagePlaceholder("name-placeholder", "$name$", name);
                            messages.sendMessagePlaceholder("power-placeholder", "$power$", power + "");


                        } catch (NumberFormatException e) {
                            messages.sendMessage("invalid-number");

                        }
                    } else {
                        messages.sendMessage("empty-string");
                    }
                } else if (color == -1) {
                    if (!message.trim().isEmpty()) {
                        try {
                            this.color = Integer.parseInt(message);
                            if (color < 0 || color > 15) {
                                messages.sendMessage("invalid-wool-data");
                                this.color = -1;
                                return;
                            }
                            IntStream.range(0, 20).forEach(i -> chatter.sendMessage(" "));
                            messages.sendMessagePlaceholder("name-placeholder", "$name$", name);
                            messages.sendMessagePlaceholder("power-placeholder", "$power$", power + "");
                            messages.sendMessagePlaceholder("wool-placeholder", "$wool$", color + "");
                            TextComponent confirm = new TextComponent(TextComponent.fromLegacyText(messages.getString("create-rank-5")));
                            confirm.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + "Click to confirm").create()));
                            confirm.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/confirmrank " + name + " " + power + " " + color));

                            chatter.spigot().sendMessage(confirm);


                        } catch (NumberFormatException e) {
                            messages.sendMessage("invalid-number");

                        }
                    } else {
                        messages.sendMessage("empty-string");
                    }
                }
            }

        } else if (addMember.get(chatter.getUniqueId()) != null) {
            event.setCancelled(true);
            // Add a member to rank
            StaffRank rank = addMember.get(chatter.getUniqueId());
            if(message.equalsIgnoreCase("cancel")){
                addMember.remove(chatter.getUniqueId());
                messages.sendMessage("cancelled-add-member");
                return;
            }
            if (message.split(" ").length == 1) {
                if (message.contains("-") && message.length() == 36) {
                    try {
                       UUID player = UUID.fromString(message);
                            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
                            if(offlinePlayer != null) {
                                if(offlinePlayer.getName() == null){
                                    messages.sendMessage("incorrect-player");
                                    return;
                                }
                                List<UUID> uuiList = ProStaff.getInstance().getRanks().stream()
                                        .flatMap(staffRank -> staffRank.getUuids().stream()).collect(Collectors.toList());
                                for(UUID uuid : uuiList){
                                    if(uuid.equals(player)){
                                        // dupe
                                        chatter.sendMessage(messages.getString(
                                                "add-member-dupe")
                                        .replace("$name$", offlinePlayer.getName()));
                                        return;
                                    }
                                }
                                if(ProStaff.BUNGEECORD){
                                    BungeeMessanger.sendAddMemberMessage(offlinePlayer.getUniqueId(), rank);
                                }
                                rank.addUuid(offlinePlayer.getUniqueId());
                                rank.showMembers(chatter);
                                IntStream.range(0, 20).forEach(i -> chatter.sendMessage(" "));
                                addMember.remove(chatter.getUniqueId());
                            } else {
                                messages.sendMessage("incorrect-uuid");
                            }

                    } catch (IllegalArgumentException e) {
                        messages.sendMessage("incorrect-uuid");
                    }

                } else {
                    messages.sendMessage("incorrect-uuid");

                }
            } else {
                messages.sendMessage("incorrect-uuid");
            }
        }
    }



    public void setName(String name) {
        this.name = name;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public void setColor(int color) {
        this.color = color;
    }
}

