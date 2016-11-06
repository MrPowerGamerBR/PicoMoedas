package com.mrpowergamerbr.picomoedas.utils;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mrpowergamerbr.picomoedas.PicoMoedas;
import com.mrpowergamerbr.picomoedas.PicoMoedasAPI;
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
            ItemStack item = entry.getValue().getItemStack();
            if (PicoMoedasAPI.m.canUseAttrStorage) { // Verificar se o servidor suporta atributos antes de usar o AttrStorage
                AttributeStorage storage = AttributeStorage.newTarget(entry.getValue().getItemStack(), PicoMoedas.ID);
                storage.setData(PicoMoedas.gson.toJson(entry.getValue().getItemInfo()));
                item = storage.getTarget();
            } // Se o servidor não suportar, ele irá pegar o item se baseando nos slots que o player clicou
            inventory.setItem(entry.getKey(), item);
        }
        
        return inventory;
        
    }
}
