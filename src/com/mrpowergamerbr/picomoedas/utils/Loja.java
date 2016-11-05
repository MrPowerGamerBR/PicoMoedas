package com.mrpowergamerbr.picomoedas.utils;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import com.mrpowergamerbr.picomoedas.PicoMoedas;
import com.mrpowergamerbr.picomoedas.utils.attributes.AttributeStorage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Loja {
    public HashMap<Integer, ItemWrapper> items = new HashMap<>(); // Slot, Item
    public String internalName;
    public String guiName;
    public int size = 9;
    
    public Loja(String internalName) {
        this.internalName = internalName;
    }
    
    public Inventory createGUI() {
        Inventory inventory = Bukkit.createInventory(null, size, guiName);
        
        for (Entry<Integer, ItemWrapper> entry : items.entrySet()) {
            AttributeStorage storage = AttributeStorage.newTarget(entry.getValue().getItemStack(), PicoMoedas.ID);
            storage.setData(PicoMoedas.gson.toJson(entry.getValue().getItemInfo()));
            
            inventory.setItem(entry.getKey(), storage.getTarget());
        }
        
        return inventory;
        
    }
}
