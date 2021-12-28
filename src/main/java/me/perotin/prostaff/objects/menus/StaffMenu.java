package me.perotin.prostaff.objects.menus;


import me.perotin.prostaff.ProStaff;
import me.perotin.prostaff.objects.StaffRank;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class StaffMenu {

    // store pages
    private ArrayList<Inventory> pages = new ArrayList<>();
    // stores items
    private Map<ItemStack, StaffRank> items;
    private int pageNumber = 0;
    private final ProStaff plugin;
    private final Player toShow;
    public static HashMap<UUID, StaffMenu> users = new HashMap<>();


    public StaffMenu(ProStaff plugin, Map<ItemStack, StaffRank> items, Player toShow){
        this.plugin = plugin;
        this.items = items;


        this.toShow = toShow;
    }

    public int getPageNumber(){
        return this.pageNumber;
    }

    public void setPageNumber(int x){
        this.pageNumber = x;
    }

    public ArrayList<Inventory> getPages() {
        return pages;
    }

    public void showInventory(){
        Inventory pageToShow = getBlankInventory();



        int counter = plugin.getConfig().getInt("start-slot");
        int endSlot = plugin.getConfig().getInt("end-slot");
        for(ItemStack item : items.keySet()){


            // running checks before setting the item in the menu
            // checking if any other page has this item
            if(othersContain(item)){  continue;}
            if(pageToShow.contains(item)){  continue;}
            if (pageToShow.getItem(counter) != null) counter++;


            pageToShow.setItem(counter, item);

            counter++;

            // page is full, need to make a new page
            if(pageToShow.getItem(endSlot) != null){
                //repeat above process
                pages.add(pageToShow);
                pageToShow = getBlankInventory();
                counter = plugin.getConfig().getInt("start-slot");
                if(pageToShow.getItem(counter) != null) counter++;
                if(othersContain(item)) continue;
                if(pageToShow.contains(item)) continue;
                pageToShow.setItem(counter, item);
            }


        }
        pages.add(pageToShow);
        users.put(toShow.getUniqueId(), this);
        toShow.openInventory(pages.get(0));
    }

    private boolean othersContain(ItemStack item){
        for(Inventory page : pages){
            if(page.contains(item)){
                return true;
            }
        }
        return false;
    }
    private Inventory getBlankInventory() {
        Inventory blankPage = Bukkit.createInventory(null, plugin.getConfig().getInt("inventory-size"), ProStaff.getColorizedString("inventory-name"));

        blankPage.setItem(plugin.getConfiguration().getInt("previous-page-slot"), createItem(ProStaff.getColorizedString("previous-page-display")));
        blankPage.setItem(plugin.getConfiguration().getInt("next-page-slot"), createItem(ProStaff.getColorizedString("next-page-display")));

        // blankPage.setItem(22, createItem(ProStaff.getColorizedString("search-player-display"), Material.COMPASS, ProStaff.getColorizedString("search-player-lore")));
        blankPage.setItem(plugin.getConfiguration().getInt("info-star-slot"), createItem(Material.NETHER_STAR, ProStaff.getColorizedString("info-star-right"), ProStaff
        .getColorizedString("info-star-left")));
        for(StaffRank rank : plugin.getRanks()) {
            if(rank.getVanished().contains(toShow.getUniqueId())) {
                blankPage.setItem(plugin.getConfiguration().getInt("vanish-button-slot"), createItem(Material.ENDER_PEARL, ProStaff.getColorizedString("unvanish"), ProStaff.getColorizedString("vanish-lore")));

            } else if (!rank.getVanished().contains(toShow.getUniqueId()) && rank.getUuids().contains(toShow.getUniqueId())){
                blankPage.setItem(plugin.getConfiguration().getInt("vanish-button-slot"), createItem(Material.EYE_OF_ENDER, ProStaff.getColorizedString("vanish"), ProStaff.getColorizedString("vanish-lore")));
            }
        }


        return blankPage;
    }


    private static ItemStack createItem(Material material, String name, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(Collections.singletonList(lore));
        item.setItemMeta(itemMeta);

        return item;
    }



     private static ItemStack createItem(String name) {
        ItemStack item = new ItemStack(Material.STONE_BUTTON);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(name);
        item.setItemMeta(itemMeta);

        return item;
    }
}