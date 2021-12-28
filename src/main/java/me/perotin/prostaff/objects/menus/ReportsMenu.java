package me.perotin.prostaff.objects.menus;

import com.google.common.collect.Iterables;
import me.perotin.prostaff.ProStaff;
import me.perotin.prostaff.objects.Report;
import me.perotin.prostaff.utils.ItemUtil;
import me.perotin.prostaff.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ReportsMenu {

    private ArrayList<Inventory> pages;
    private int pageNumber = 1;
    private ArrayList<Report> reports;
    public static HashMap<UUID, ReportsMenu> users = new HashMap<>();
    private final Player toShow;




    public ReportsMenu(Player toShow, ArrayList<Report> reports) {
        this.toShow = toShow;
        this.reports = reports;
        this.pages = new ArrayList<>();

    }


    public void remove(Report report){
        this.reports.remove(report);
    }

    public void showMenu(){
        int counter = 9;
        Inventory pageToShow = getBlankPage();
        for(Report report : reports){
            // checking if any other page has this item
            ItemStack item = report.getItemStack();
            if(othersContain(report.getItemStack())){  continue;}
            if(pageToShow.contains(item)){  continue;}
            if (pageToShow.getItem(counter) != null) counter++;


            pageToShow.setItem(counter, item);

            counter++;

            // page is full, need to make a new page
            if(pageToShow.getItem(53) != null){
                //repeat above process
                pages.add(pageToShow);
                pageToShow = getBlankPage();
                counter = 9;
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




    private Inventory getBlankPage(){
        Messages messages = new Messages(Iterables.getFirst(Bukkit.getOnlinePlayers(), null));

        Inventory inventory = Bukkit.createInventory(null, 54, ProStaff.getInstance().getConfiguration().getString("reports-menu"));
        inventory.setItem(4, ItemUtil.createItem(messages.getString("reports-info-display"), messages.getString("reports-info-lore-1"),
                messages.getString("reports-info-lore-right"), Material.NETHER_STAR));
        inventory.setItem(0, ItemUtil.createItem(messages.getString("close-menu"), "", Material.FEATHER));
        inventory.setItem(53, ItemUtil.createItem(ProStaff.getColorizedString("next-page-display"), "", Material.SIGN));
        return inventory;
    }

}
