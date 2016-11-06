package com.mrpowergamerbr.picomoedas;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import com.mrpowergamerbr.picomoedas.ConfigValues.Type;
import com.mrpowergamerbr.picomoedas.utils.Loja;
import com.mrpowergamerbr.picomoedas.utils.MoedaWrapper;

public class PicoMoedasAPI {
    public static PicoMoedas m;

    public PicoMoedasAPI(PicoMoedas m) {
        PicoMoedasAPI.m = m;    
    }

    public static MoedaWrapper getBalance(Player p) {
        return getOrCreateBalance(p);
    }

    public static MoedaWrapper getBalance(String name) {
        return getOrCreateBalance(name);
    }
    
    private static MoedaWrapper getOrCreateBalance(Player p) {
        return getOrCreateBalance(p.getName());
    }

    public static void editBalance(Player p, double newValue) {
        MoedaWrapper wrapper = getOrCreateBalance(p);
        getOrCreateBalance(p).setValue(wrapper.getValue() + newValue);
    }
    
    private static MoedaWrapper getOrCreateBalance(String name) {
        for (MoedaWrapper wrapper : m.balances) {
            if (wrapper.getName().equals(name)) {
                return wrapper;
            }
        }

        MoedaWrapper wrapper = new MoedaWrapper(name, 0);

        m.balances.add(wrapper);

        return wrapper;
    }

    public static void abrirGUI(Player p) {
        abrirGUI(p, ConfigValues.getFromConfig(Type.DEFAULT_TO_OPEN));
    }

    public static void abrirGUI(Player p, String internalName) {
        Loja loja = getLoja(internalName);

        if (loja != null) {
            Inventory inventory = loja.createGUI();

            p.openInventory(inventory);
        }
    }

    public static Loja getLoja(String internalName) {
        for (Loja loja : m.lojas) {
            if (loja.getInternalName().equals(internalName)) {
                return loja;
            }
        }
        return null;
    }
}
