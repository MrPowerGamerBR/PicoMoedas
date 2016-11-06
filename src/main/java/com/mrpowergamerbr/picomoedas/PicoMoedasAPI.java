package com.mrpowergamerbr.picomoedas;

import org.bukkit.Bukkit;
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
        if (ConfigValues.getFromConfigBool(Type.USE_VAULT) && m.econ != null) {
            // Utilizar Vault caso o "UsarEconomiaDoVault" seja true
            if (0 > newValue) {
                newValue = -newValue;
                m.econ.withdrawPlayer(p, newValue);
            } else {
                m.econ.depositPlayer(p, newValue);
            }
        } else {
            MoedaWrapper wrapper = getOrCreateBalance(p);
            getOrCreateBalance(p).setValue(wrapper.getValue() + newValue);
        }
    }

    private static MoedaWrapper getOrCreateBalance(String name) {
        if ((boolean) ConfigValues.getFromConfigBool(Type.USE_VAULT) && m.econ != null) {
            // Utilizar Vault caso o "UsarEconomiaDoVault" seja true
            MoedaWrapper wrapper = new MoedaWrapper(name, m.econ.getBalance(name));
            return wrapper;
        } else {
            for (MoedaWrapper wrapper : m.balances) {
                if (wrapper.getName().equals(name)) {
                    return wrapper;
                }
            }

            MoedaWrapper wrapper = new MoedaWrapper(name, 0);

            m.balances.add(wrapper);

            return wrapper;
        }
    }

    public static void abrirGUI(Player p) {
        abrirGUI(p, (String) ConfigValues.getFromConfig(Type.DEFAULT_TO_OPEN));
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
