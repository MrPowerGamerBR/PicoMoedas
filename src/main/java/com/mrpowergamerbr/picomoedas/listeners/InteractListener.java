package com.mrpowergamerbr.picomoedas.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

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
        Inventory inventory = null;
        try {
            inventory = e.getClickedInventory();
        } catch (Error err) {
            // Sim, a 1.5.2 não tem getClickedInventory().
            // Mais gambiarras que eu tenho certeza que irão gerar algum bug de dupe
            if (e.getWhoClicked().getOpenInventory() != null && e.getWhoClicked().getOpenInventory().getTopInventory() != null) {
                inventory = e.getWhoClicked().getOpenInventory().getTopInventory();
            }
        }

        if (inventory != null && inventory.getName() != null) {
            for (Loja loja : m.lojas) {
                if (inventory.getName().equals(loja.getGuiName())) {
                    e.setCancelled(true);    
                    Player p = (Player) e.getWhoClicked();
                    ItemInfo itemInfo = null;

                    if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
                        if (!m.canUseAttrStorage) {
                            // Gambiarras para que pessoas possam usar o PicoMoedas na 1.5.idadedaspedras
                            // Tenho certeza que isto poderá causar algum bug de dupe, mas eu não ligo.
                            //
                            // Afinal, não sou obrigado a suportar uma versão tão velha como a 1.5.2
                            // Mas bem, tá aí, espero que isto não cause problemas futuros no PicoMoedas
                            // (igual ao PowerMoedas que tem vários bugs de dupe por ter um código muito mal feito)
                            int slot = e.getRawSlot();
                            if (loja.items.containsKey(slot)) {
                                itemInfo = loja.items.get(slot).getItemInfo();
                            }
                        } else {
                            AttributeStorage attr = AttributeStorage.newTarget(e.getCurrentItem(), PicoMoedas.ID);

                            if (attr.getData(null) != null) {
                                itemInfo = (ItemInfo) PicoMoedas.gson.fromJson(attr.getData(null), ItemInfo.class);
                            }
                        }

                        if (itemInfo == null) {
                            return;
                        }
                        
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

                                if (itemInfo.isSellingItem()) {
                                    p.getInventory().addItem(itemInfo.getToGive().toItemStack());
                                }

                                ItemInfo finalItemInfo = itemInfo;
                                new BukkitRunnable() {
                                    public void run() {
                                        for (String command : finalItemInfo.getConsoleCommands()) {
                                            command = command.replace("{@player}", p.getName());
                                            command = command.replace("{@displayName}", p.getDisplayName());
                                            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
                                        }
                                    }
                                }.runTask(m);
                            } else {
                                String semGrana = ConfigValues.getFromConfigColorized(Type.SEM_MOEDAS_SUFICIENTES);
                                p.sendMessage(semGrana);
                            }
                        }
                    }
                    return;
                }
            }
        }
    }
}