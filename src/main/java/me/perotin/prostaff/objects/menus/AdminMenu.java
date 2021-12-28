package me.perotin.prostaff.objects.menus;

import me.perotin.prostaff.ProStaff;
import me.perotin.prostaff.objects.StaffRank;
import me.perotin.prostaff.utils.ItemUtil;
import me.perotin.prostaff.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class AdminMenu {
    /*
    admin-inventory-name: "Configure Ranks"

info-sign-1: "&eRight click to view a rank"
info-sign-2: "&cLeft click to delete a rank"

create-rank: "&aClick here to create a rank"

     */

    private final Player viewer;
    private final Messages messages;
    private final ProStaff plugin;

    public AdminMenu(Player viewer) {
        this.viewer = viewer;
        this.messages = new Messages(viewer);
        this.plugin = ProStaff.getInstance();
    }



    public void showConfirmDeleteMenu(StaffRank rank){
        Inventory inventory = Bukkit.createInventory(null, 27, messages.getString("confirm-delete-rank")
        .replace("$rank$", rank.getName()));
        inventory.setItem(12, ItemUtil.createItem(messages.getString("cancel"), "", Material.EMERALD_BLOCK));
        inventory.setItem(14, ItemUtil.createItem(messages.getString("confirm"), "", Material.REDSTONE_BLOCK));
        viewer.openInventory(inventory);


    }
    public void showMainMenu(){
        Inventory inventory = Bukkit.createInventory(null, 54, messages.getString("admin-inventory-name"));
        inventory.setItem(4, ItemUtil.createItem(messages.getString("info-sign-1"), messages.getString("info-sign-2"), Material.NETHER_STAR));
        if(!plugin.getRanks().isEmpty()){
            int slot = 9;
            for(StaffRank rank : plugin.getRanks()){
                if(slot >= 45) break;

                inventory.setItem(slot, rank.getAdminDisplay());
                slot++;

            }
        }
        inventory.setItem(49, ItemUtil.createItem(messages.getString("create-rank"), "", Material.ANVIL));
        viewer.openInventory(inventory);

    }


}
