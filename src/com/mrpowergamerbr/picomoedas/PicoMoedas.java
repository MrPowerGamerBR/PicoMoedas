package com.mrpowergamerbr.picomoedas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;
import com.mrpowergamerbr.picomoedas.commands.CoinCommand;
import com.mrpowergamerbr.picomoedas.commands.EditarCoinsCommand;
import com.mrpowergamerbr.picomoedas.commands.LojaGUICommand;
import com.mrpowergamerbr.picomoedas.listeners.InteractListener;
import com.mrpowergamerbr.picomoedas.utils.ItemInfo;
import com.mrpowergamerbr.picomoedas.utils.ItemWrapper;
import com.mrpowergamerbr.picomoedas.utils.Loja;
import com.mrpowergamerbr.picomoedas.utils.MoedaWrapper;
import com.mrpowergamerbr.picomoedas.utils.SimpleItemStack;

public class PicoMoedas extends JavaPlugin implements Listener {
    public ArrayList<MoedaWrapper> balances = new ArrayList<MoedaWrapper>();
    public ArrayList<Loja> lojas = new ArrayList<Loja>();

    public static final UUID ID = UUID.fromString("990648ce-a988-4191-9057-626650ae4267");
    public static final Gson gson = new Gson();
    public static final String info = "Se você quer o código-fonte do PicoMoedas, veja aqui! https://github.com/MrPowerGamerBR/PicoMoedas";
    
    public void onEnable() {
        lojas.clear();

        saveDefaultConfig();

        new PicoMoedasAPI(this);
        new InteractListener(this);
        List<String> aliases;
        
        aliases = Arrays.asList("moedas", "cash", "coin");
        CoinCommand myCommand = new CoinCommand("coins", "/<command> [args]", "Descrição", aliases);
        myCommand.register();
        LojaGUICommand lojaGuiCommand = new LojaGUICommand("lojagui", "/<command> [args]", "Descrição");
        lojaGuiCommand.register();

        EditarCoinsCommand editarCoinsCommand = new EditarCoinsCommand("editarcoins", "/<command> [args]", "Descrição");
        editarCoinsCommand.register();
        
        Set<String> lojasStr = getConfig().getConfigurationSection("Lojas").getKeys(false);

        for (String lojaStr : lojasStr) {
            Loja loja = new Loja(lojaStr);
            loja.setGuiName(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Lojas." + lojaStr + ".Nome")));
            loja.setSize(getConfig().getInt("Lojas." + lojaStr + ".Tamanho"));
            Set<String> slotsStr = getConfig().getConfigurationSection("Lojas." + lojaStr + ".Slots").getKeys(false);


            for (String slotStr : slotsStr) {
                int slot = Integer.parseInt(slotStr);
                String slotConf = "Lojas." + lojaStr + ".Slots." + slot + ".";

                ItemInfo itemInfo = new ItemInfo();

                if (getConfig().contains(slotConf + "Grana")) {
                    itemInfo.setSelling(true);
                    itemInfo.setPrice(getConfig().getDouble(slotConf + "Grana"));
                }

                if (getConfig().contains(slotConf + "AbrirMenu")) {
                    itemInfo.setOpenGUI(getConfig().getString(slotConf + "AbrirMenu"));
                }
                

                if (getConfig().contains(slotConf + "FecharMenu")) {
                    itemInfo.setClosesGui(getConfig().getBoolean(slotConf + "FecharMenu"));
                }

                int quantidade = 1;
                
                if (getConfig().contains(slotConf + "Quantidade")) {
                    quantidade = getConfig().getInt(slotConf + "Quantidade");
                }
                
                byte metadata = 0;
                
                if (getConfig().contains(slotConf + "Meta")) {
                    metadata = ((byte) getConfig().getInt(slotConf + "Meta"));
                }
                
                ItemStack item = new ItemStack(Material.valueOf(getConfig().getString(slotConf + "Material")), quantidade, metadata);

                ItemMeta meta = item.getItemMeta();

                if (getConfig().contains(slotConf + "Nome")) {
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', getConfig().getString(slotConf + "Nome")));
                }

                if (getConfig().contains(slotConf + "Lore")) {
                    List<String> lore = getConfig().getStringList(slotConf + "Lore");
                    ArrayList<String> coloredLore = new ArrayList<String>();

                    for (String loreStr : lore) {
                        coloredLore.add(ChatColor.translateAlternateColorCodes('&', loreStr));
                    }
                    meta.setLore(coloredLore);
                }

                if (getConfig().contains(slotConf + "ItemFlags")) {
                    List<String> flags = getConfig().getStringList(slotConf + "ItemFlags");

                    for (String itemFlagStr : flags) {
                        meta.addItemFlags(ItemFlag.valueOf(itemFlagStr.toUpperCase()));
                    }
                }

                item.setItemMeta(meta);

                if (getConfig().contains(slotConf + "Enchants")) {
                    List<String> enchants = getConfig().getStringList(slotConf + "Enchants");

                    for (String enchant : enchants) {
                        String toEnum = enchant.toUpperCase();
                        String[] split = toEnum.split(" ");
                        Enchantment enchantment = Enchantment.getByName(split[0]);

                        item.addUnsafeEnchantment(enchantment, Integer.parseInt(split[1]));
                    }
                }

                // Tá, isto está ficando *meio* desorganizado...
                if (getConfig().contains(slotConf + "ItemSeparado")) {
                    slotConf = "Lojas." + lojaStr + ".Slots." + slot + ".ItemSeparado.";
                    SimpleItemStack simple = new SimpleItemStack();
                    
                    simple.setMaterial(Material.valueOf(getConfig().getString(slotConf + "Material")));
                    if (getConfig().contains(slotConf + "Meta")) {
                        simple.setMeta((byte) getConfig().getInt(slotConf + "Meta"));
                    }
                    
                    quantidade = 1;
                    
                    if (getConfig().contains(slotConf + "Quantidade")) {
                        quantidade = getConfig().getInt(slotConf + "Quantidade");
                    }
                    
                    simple.setQuantidade(quantidade);
                    
                    if (getConfig().contains(slotConf + "Nome")) {
                        simple.setName(ChatColor.translateAlternateColorCodes('&', getConfig().getString(slotConf + "Nome")));
                    }

                    if (getConfig().contains(slotConf + "Lore")) {
                        List<String> lore = getConfig().getStringList(slotConf + "Lore");
                        ArrayList<String> coloredLore = new ArrayList<String>();

                        for (String loreStr : lore) {
                            coloredLore.add(ChatColor.translateAlternateColorCodes('&', loreStr));
                        }
                        simple.setLore(coloredLore);
                    }

                    if (getConfig().contains(slotConf + "ItemFlags")) {
                        List<String> flags = getConfig().getStringList(slotConf + "ItemFlags");

                        for (String itemFlagStr : flags) {
                            simple.getFlags().add(ItemFlag.valueOf(itemFlagStr.toUpperCase()));
                        }
                    }

                    item.setItemMeta(meta);

                    if (getConfig().contains(slotConf + "Enchants")) {
                        List<String> enchants = getConfig().getStringList(slotConf + "Enchants");

                        for (String enchant : enchants) {
                            String toEnum = enchant.toUpperCase();
                            String[] split = toEnum.split(" ");

                            simple.getEnchants().put(split[0], Integer.parseInt(split[1]));
                        }
                    }
                    
                    itemInfo.setToGive(simple);
                }
                
                loja.items.put(slot - 1, new ItemWrapper(itemInfo, item));
            }
            lojas.add(loja);
        }
    }
}
