package me.perotin.prostaff.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;

/* Created by Perotin on 8/9/18 */
public class ItemUtil {

    public static ItemStack createItem(String display, String lore, Material material){
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(display);
        if(!lore.equals("")) {
            meta.setLore(Collections.singletonList(lore));
        }
        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack createItem(String display, String lore, String lore2, Material material){
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(display);
        if(!lore.equals("")) {
            meta.setLore(Arrays.asList(lore, lore2));
        }
        stack.setItemMeta(meta);
        return stack;
    }
}
