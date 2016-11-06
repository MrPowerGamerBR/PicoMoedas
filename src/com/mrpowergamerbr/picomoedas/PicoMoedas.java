package com.mrpowergamerbr.picomoedas;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.mrpowergamerbr.picomoedas.commands.CoinCommand;
import com.mrpowergamerbr.picomoedas.commands.EditarCoinsCommand;
import com.mrpowergamerbr.picomoedas.commands.LojaGUICommand;
import com.mrpowergamerbr.picomoedas.commands.PicoMoedasCommand;
import com.mrpowergamerbr.picomoedas.listeners.InteractListener;
import com.mrpowergamerbr.picomoedas.utils.AbstractCommand;
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
        try(BufferedReader br = new BufferedReader(new FileReader(getDataFolder() + "/saves.json"))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();
            
            Type t = new TypeToken<ArrayList<MoedaWrapper>>() {}.getType();
            balances = (ArrayList<MoedaWrapper>) gson.fromJson(everything, t);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        saveDefaultConfig();

        new PicoMoedasAPI(this);
        new InteractListener(this);
        
        Set<String> commandsStr = getConfig().getConfigurationSection("Comandos").getKeys(false);
        
        for (String commandStr : commandsStr) {
            String confStr = "Comandos." + commandStr + ".";
            String clazzStr = getConfig().getString(confStr + "Class");
            String cmdName = getConfig().getString(confStr + "Comando");
            List<String> aliases = new ArrayList<String>();
            
            if (getConfig().contains(confStr + "Aliases")) {
                aliases = getConfig().getStringList(confStr + "Aliases");
            }
            
            
            try {
                Class<?> c = Class.forName("com.mrpowergamerbr.picomoedas.commands." + clazzStr);
                AbstractCommand t = (AbstractCommand) c.getDeclaredConstructor(String.class, String.class, String.class, List.class).newInstance(cmdName, "/<command> [args]", "Descrição", aliases);
                t.register();
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        // Registrar comando de créditos
        PicoMoedasCommand creditsCommand = new PicoMoedasCommand("picomoedas", "/<command> [args]", "Descrição");
        creditsCommand.register();
        
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
                SimpleItemStack simple = new SimpleItemStack();
                
                if (getConfig().contains(slotConf + "Grana")) {
                    itemInfo.setSelling(true);
                    itemInfo.setPrice(getConfig().getDouble(slotConf + "Grana"));
                }
                
                if (getConfig().contains(slotConf + "VendeItem")) {
                    itemInfo.setSellingItem(getConfig().getBoolean(slotConf + "VendeItem"));
                }

                if (getConfig().contains(slotConf + "ExecutarComandoConsole")) {
                    itemInfo.setConsoleCommands(getConfig().getStringList(slotConf + "ExecutarComandoConsole"));
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

                simple.setMaterial(Material.valueOf(getConfig().getString(slotConf + "Material")));
                simple.setQuantidade(quantidade);
                simple.setMeta(metadata);
                
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

                if (getConfig().contains(slotConf + "Enchants")) {
                    List<String> enchants = getConfig().getStringList(slotConf + "Enchants");

                    for (String enchant : enchants) {
                        String toEnum = enchant.toUpperCase();
                        String[] split = toEnum.split(" ");

                        simple.getEnchants().put(split[0], Integer.parseInt(split[1]));
                    }
                }

                itemInfo.setToGive(simple);
                
                // Tá, isto está ficando *meio* desorganizado...
                if (getConfig().contains(slotConf + "ItemSeparado")) {
                    slotConf = "Lojas." + lojaStr + ".Slots." + slot + ".ItemSeparado.";
                    SimpleItemStack subItem = new SimpleItemStack();
                    
                    subItem.setMaterial(Material.valueOf(getConfig().getString(slotConf + "Material")));
                    if (getConfig().contains(slotConf + "Meta")) {
                        subItem.setMeta((byte) getConfig().getInt(slotConf + "Meta"));
                    }
                    
                    quantidade = 1;
                    
                    if (getConfig().contains(slotConf + "Quantidade")) {
                        quantidade = getConfig().getInt(slotConf + "Quantidade");
                    }
                    
                    subItem.setQuantidade(quantidade);
                    
                    if (getConfig().contains(slotConf + "Nome")) {
                        subItem.setName(ChatColor.translateAlternateColorCodes('&', getConfig().getString(slotConf + "Nome")));
                    }

                    if (getConfig().contains(slotConf + "Lore")) {
                        List<String> lore = getConfig().getStringList(slotConf + "Lore");
                        ArrayList<String> coloredLore = new ArrayList<String>();

                        for (String loreStr : lore) {
                            coloredLore.add(ChatColor.translateAlternateColorCodes('&', loreStr));
                        }
                        subItem.setLore(coloredLore);
                    }

                    if (getConfig().contains(slotConf + "ItemFlags")) {
                        List<String> flags = getConfig().getStringList(slotConf + "ItemFlags");

                        for (String itemFlagStr : flags) {
                            subItem.getFlags().add(ItemFlag.valueOf(itemFlagStr.toUpperCase()));
                        }
                    }

                    if (getConfig().contains(slotConf + "Enchants")) {
                        List<String> enchants = getConfig().getStringList(slotConf + "Enchants");

                        for (String enchant : enchants) {
                            String toEnum = enchant.toUpperCase();
                            String[] split = toEnum.split(" ");

                            subItem.getEnchants().put(split[0], Integer.parseInt(split[1]));
                        }
                    }
                    
                    itemInfo.setToGive(subItem);
                }
                
                loja.items.put(slot - 1, new ItemWrapper(itemInfo, simple.toItemStack()));
            }
            lojas.add(loja);
        }
        
        new BukkitRunnable() {
            public void run() {
                String json = gson.toJson(balances);
                
                List<String> lines = Arrays.asList(json);
                Path file = Paths.get(getDataFolder() + "/");
                file.toFile().mkdirs();
                try {
                    file.toFile().createNewFile();
                    Files.write(Paths.get(getDataFolder() + "/balances.json"), lines, Charset.forName("UTF-8"));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }.runTaskTimerAsynchronously(this, 18000L, 18000L);
        
        List<String> filosofia = Arrays.asList("Será que alguém está lendo isto? ...", "Eu espero que não...");
        filosofia = Arrays.asList("Será que algum dia nós iremos ficar juntos? ...não, não estou falando com você, pessoa que está lendo o meu código-fonte.");
        filosofia = Arrays.asList("Sei lá, eu acho que não... Mesmo que eu queria bastante que isto acontecesse...");
        filosofia = Arrays.asList("Mas é assim a vida, isto que acontece com pessoas que demoram demais para obter coragem para realizar as coisas...");
        filosofia = Arrays.asList("...");
        filosofia = Arrays.asList("Mas isto não quer dizer que não tenha alguma chance, né?");
        filosofia = Arrays.asList("...");
        filosofia = Arrays.asList("<3");
        
        filosofia.size();
    }
    
    public void onDisable() {
        String json = gson.toJson(balances);
        
        List<String> lines = Arrays.asList(json);
        Path file = Paths.get(getDataFolder() + "/");
        file.toFile().mkdirs();
        try {
            file.toFile().createNewFile();
            Files.write(Paths.get(getDataFolder() + "/balances.json"), lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
