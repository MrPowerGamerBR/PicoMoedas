package com.mrpowergamerbr.picomoedas.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SimpleItemStack extends SimpleItemStack17 {
    String name;
    byte meta;
    int quantidade = 1;
    Material material;
    List<String> lore = new ArrayList<String>();
    List<ItemFlag> flags = new ArrayList<ItemFlag>();
    HashMap<String, Integer> enchants = new HashMap<>();
    
    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(material, quantidade, meta);

        ItemMeta meta = item.getItemMeta();

        if (name != null) {
            meta.setDisplayName(name);
        }
        
        meta.setLore(lore);
        for (ItemFlag flag : flags) {
            meta.addItemFlags(flag);
        }
        item.setItemMeta(meta);
        for (Entry<String, Integer> entry : enchants.entrySet()) {
            item.addUnsafeEnchantment(Enchantment.getByName(entry.getKey()), entry.getValue());
        }
        
        return item;
    }
}
