package com.mrpowergamerbr.picomoedas.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.mrpowergamerbr.picomoedas.ConfigValues;
import com.mrpowergamerbr.picomoedas.PicoMoedas;
import com.mrpowergamerbr.picomoedas.PicoMoedasAPI;
import com.mrpowergamerbr.picomoedas.ConfigValues.Type;
import com.mrpowergamerbr.picomoedas.utils.ItemInfo;
import com.mrpowergamerbr.picomoedas.utils.Loja;
import com.mrpowergamerbr.picomoedas.utils.attributes.AttributeStorage;

public class InteractListener implements Listener {
    PicoMoedas m;

    public InteractListener(PicoMoedas m) {
        this.m = m;
        Bukkit.getPluginManager().registerEvents(this, m);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() != null && e.getClickedInventory().getName() != null) {
            for (Loja loja : m.lojas) {
                if (e.getClickedInventory().getName().equals(loja.getGuiName())) {
                    e.setCancelled(true);    
                    Player p = (Player) e.getWhoClicked();
                    if (e.getCurrentItem() != null) {
                        AttributeStorage attr = AttributeStorage.newTarget(e.getCurrentItem(), PicoMoedas.ID);

                        if (attr.getData(null) != null) {
                            ItemInfo itemInfo = (ItemInfo) PicoMoedas.gson.fromJson(attr.getData(null), ItemInfo.class);

                            if (itemInfo.isClosesGui()) {
                                e.getWhoClicked().closeInventory();
                                return;
                            }


                            if (itemInfo.getOpenGUI() != null) {
                                Loja toOpen = PicoMoedasAPI.getLoja(itemInfo.getOpenGUI());
                                e.getWhoClicked().closeInventory();
                                e.getWhoClicked().openInventory(toOpen.createGUI());
                                return;
                            }

                            
                            if (itemInfo.isSelling()) {
                                if (PicoMoedasAPI.getBalance(p).getValue() >= itemInfo.getPrice()) {
                                    if (p.getInventory().firstEmpty() == -1) {
                                        return;
                                    }
                                    
                                    PicoMoedasAPI.editBalance(p, -itemInfo.getPrice());
                                    if (itemInfo.getToGive() != null) {
                                        p.getInventory().addItem(itemInfo.getToGive().toItemStack());
                                    } else {
                                        p.getInventory().addItem(e.getCurrentItem());
                                    }
                                } else {
                                    String semGrana = ConfigValues.getFromConfig(Type.SEM_MOEDAS_SUFICIENTES);
                                    semGrana = ChatColor.translateAlternateColorCodes('&', semGrana);
                                    p.sendMessage(semGrana);
                                }
                            }
                        }
                    }
                    return;
                }
            }
        }   
    }
}