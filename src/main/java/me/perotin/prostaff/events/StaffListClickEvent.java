package me.perotin.prostaff.events;

import com.google.common.io.ByteArrayDataOutput;
import me.perotin.prostaff.ProStaff;
import me.perotin.prostaff.objects.StaffRank;
import me.perotin.prostaff.objects.menus.StaffMenu;
import me.perotin.prostaff.utils.BungeeMessanger;
import me.perotin.prostaff.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;

public class StaffListClickEvent implements Listener {

    private ProStaff plugin;

    // better to store player objects in this instance
    // since if it persists the user may get confused

    private HashMap<Player, String> sendMessage = new HashMap<>();

    public StaffListClickEvent(ProStaff plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        InventoryView inventoryView = event.getView();
        if (event.getWhoClicked() instanceof Player) {
            Player clicker = (Player) event.getWhoClicked();
            Messages messages = new Messages(clicker);

            if (StaffMenu.users.containsKey(clicker.getUniqueId())) {

                StaffMenu paging = StaffMenu.users.get(clicker.getUniqueId());

                if (inventory == null) return;


                if (inventoryView.getTitle().equals(ProStaff.getColorizedString("inventory-name"))) {
                    event.setCancelled(true);
                    ItemStack clicked = event.getCurrentItem();
                    if(clicked == null || clicked.getType() == Material.AIR ) return;

                    if (clicked.getType() == Material.PLAYER_HEAD) {
                        String name = ChatColor.stripColor(clicked.getItemMeta().getLore().get(0)).trim();

                        if (clicker.getName().equals(name)) return;
                        if (event.getClick() == ClickType.RIGHT) {
                            SkullMeta skullMeta = (SkullMeta) clicked.getItemMeta();
                            String server = ChatColor.stripColor(skullMeta.getLore().get(2)).trim();
                            ByteArrayDataOutput out = plugin.getOut();
                            out.writeUTF("Connect");
                            out.writeUTF(server);
                            clicker.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
                        } else if (event.getClick() == ClickType.LEFT) {
                            sendMessage.put(clicker, name);
                            clicker.closeInventory();
                            messages.sendMessagePlaceholder("send-message", "$player$", name);
                            messages.sendMessage("send-message1");
                            return;
                        }
                    }
                    if (clicked.getType() == Material.STONE_BUTTON) {
                        if (event.getCurrentItem().getItemMeta().getDisplayName()
                                .equals(ProStaff.getColorizedString("next-page-display"))) {
                            // no more next pages
                            if (paging.getPageNumber() >= paging.getPages().size() - 1) {
                                return;
                            } else {
                                paging.setPageNumber(paging.getPageNumber() + 1);
                                Inventory inv = paging.getPages().get(paging.getPageNumber());
                                clicker.openInventory(inv);

                            }
                        } else if (event.getCurrentItem().getItemMeta().getDisplayName()
                                .equals(ProStaff.getColorizedString("previous-page-display"))) {
                            if (paging.getPageNumber() > 0) {
                                paging.setPageNumber(paging.getPageNumber() - 1);
                                Inventory inv = paging.getPages().get(paging.getPageNumber());

                                clicker.openInventory(inv);
                            }
                        }
                    }
                    if (clicked.getType() == Material.ENDER_PEARL) {
                        StaffRank rank = StaffRank.getRank(clicker.getUniqueId());
                        if (rank == null) {
                            clicker.closeInventory();
                            clicker.sendMessage(ProStaff.getColorizedString("error"));

                            throw new NullPointerException(clicker.getName() + " was able to click on the [Vanish] button but he is not registered in a staff rank!");
                        }
                        if (rank.getVanished().contains(clicker.getUniqueId())) {
                            BungeeMessanger.sendRemoveVanish(clicker.getUniqueId(), rank);
                            rank.getVanished().remove(clicker.getUniqueId());
                            clicker.sendMessage(ProStaff.getColorizedString("now-unvanish")
                                    .replace("$prefix$", plugin.getPrefix()));
                            Bukkit.dispatchCommand(clicker, ProStaff.getInstance().getConfiguration().getString("command-name"));

                        }

                    } else if (clicked.getType() == Material.ENDER_EYE) {
                        StaffRank rank = StaffRank.getRank(clicker.getUniqueId());
                        if (rank == null) {
                            clicker.closeInventory();
                            clicker.sendMessage(ProStaff.getColorizedString("error"));

                            throw new NullPointerException(clicker.getName() + " was able to click on the [Vanish] button but he is not registered in a staff rank!");
                        }
                        if (!rank.getVanished().contains(clicker.getUniqueId())) {
                            BungeeMessanger.sendAddVanish(clicker.getUniqueId(), rank);
                            rank.getVanished().add(clicker.getUniqueId());
                            clicker.sendMessage(ProStaff.getColorizedString("now-vanish")
                                    .replace("$prefix$", plugin.getPrefix()));
                            Bukkit.dispatchCommand(clicker, ProStaff.getInstance().getConfiguration().getString("command-name"));


                        }
                    }

                }
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player chatter = event.getPlayer();
        if (sendMessage.get(chatter) != null) {
            event.setCancelled(true);
            // get their message
            String message = event.getMessage();
            Messages messages = new Messages(chatter);
            if (message.equalsIgnoreCase("cancel")) {
                sendMessage.remove(chatter);
                messages.sendMessagePlaceholder("cancelled-send-message", "$player$", sendMessage.get(chatter));
            } else {
                ByteArrayDataOutput out = plugin.getOut();
                out.writeUTF("Message");
                out.writeUTF(sendMessage.get(chatter));
                out.writeUTF(messages.getString("cross-server-message-format-receiver").replace("$message$", message)
                        .replace("$name$", chatter.getName()));
                chatter.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
                messages.sendMessagePlaceholder("sent-message", "$name$", sendMessage.get(chatter));

                sendMessage.remove(chatter);

            }

        }
    }

}
