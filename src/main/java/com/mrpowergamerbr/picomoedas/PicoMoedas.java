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
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mrpowergamerbr.picomoedas.commands.PicoMoedasCommand;
import com.mrpowergamerbr.picomoedas.listeners.InteractListener;
import com.mrpowergamerbr.picomoedas.utils.AbstractCommand;
import com.mrpowergamerbr.picomoedas.utils.ItemInfo;
import com.mrpowergamerbr.picomoedas.utils.ItemWrapper;
import com.mrpowergamerbr.picomoedas.utils.Loja;
import com.mrpowergamerbr.picomoedas.utils.MoedaWrapper;
import com.mrpowergamerbr.picomoedas.utils.SimpleItemStack;
import com.mrpowergamerbr.picomoedas.utils.SimpleItemStack17;
import com.mrpowergamerbr.picomoedas.utils.attributes.AttributeStorage;

import net.milkbowl.vault.economy.Economy;

public class PicoMoedas extends JavaPlugin implements Listener {
    public ArrayList<MoedaWrapper> balances = new ArrayList<MoedaWrapper>();
    public ArrayList<Loja> lojas = new ArrayList<Loja>();

    public static final UUID ID = UUID.fromString("990648ce-a988-4191-9057-626650ae4267");
    public static final Gson gson = new Gson();
    public static final String info = "Se voc� quer o c�digo-fonte do PicoMoedas, veja aqui! https://github.com/MrPowerGamerBR/PicoMoedas";

    public boolean canUseItemFlags = false;
    public boolean canUseAttrStorage = false;

    public Economy econ = null;
    
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            setupEconomy();
        }
        
        // Verificar se a vers�o do servidor tem suporte a ItemFlags (1.8+)
        try {
            ItemFlag.HIDE_ATTRIBUTES.getClass();
            canUseItemFlags = true;
        } catch (Error e) {
            // Se der erro, quer dizer que a classe n�o foi encontrada, ent�o n�s n�o iremos usar Item Flags no PicoMoedas
        }

        // Verificar se a vers�o do servidor tem suporte a atributos (1.7+)
        try {
            ItemStack item = new ItemStack(Material.STONE);
            AttributeStorage attr = AttributeStorage.newTarget(item, ID);
            attr.setData("Katy Kat :3");
            canUseAttrStorage = true;
        } catch (Exception e) {
            // Se der exception, quer dizer que o servidor n�o suporta atributos no item... Hora da super gambiarra...
            Bukkit.getLogger().log(Level.WARNING, "[PicoMoedas] A vers�o do seu servidor n�o suporta atributos nos itens!");
            Bukkit.getLogger().log(Level.WARNING, "[PicoMoedas] Enquanto o PicoMoedas ir� funcionar, ele usar� uma gambiarra");
            Bukkit.getLogger().log(Level.WARNING, "[PicoMoedas] que poder� afetar o seu servidor!");
        }

        try(BufferedReader br = new BufferedReader(new FileReader(getDataFolder() + "/balances.json"))) {
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

            String comoUsa = "/<command> [args]";
            if (getConfig().contains(confStr + "ComoUsa")) {
                comoUsa = getConfig().getString(confStr + "ComoUsa");
            }

            try {
                Class<?> c = Class.forName("com.mrpowergamerbr.picomoedas.commands." + clazzStr);
                AbstractCommand t = (AbstractCommand) c.getDeclaredConstructor(String.class, String.class, String.class, List.class, String.class).newInstance(cmdName, "/<command> [args]", "Descri��o", aliases, comoUsa);
                t.register();
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // Registrar comando de cr�ditos
        PicoMoedasCommand creditsCommand = new PicoMoedasCommand("picomoedas", "/<command> [args]", "Descri��o");
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
                SimpleItemStack17 simple;
                SimpleItemStack17 onGui;
                
                if (canUseItemFlags) {
                    simple = new SimpleItemStack();
                    onGui = new SimpleItemStack();
                } else {
                    simple = new SimpleItemStack17();
                    onGui = new SimpleItemStack17();
                }

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
                onGui.setMaterial(Material.valueOf(getConfig().getString(slotConf + "Material")));
                onGui.setQuantidade(quantidade);
                onGui.setMeta(metadata);
                
                if (getConfig().contains(slotConf + "Nome")) {
                    simple.setName(ChatColor.translateAlternateColorCodes('&', getConfig().getString(slotConf + "Nome")));
                    onGui.setName(ChatColor.translateAlternateColorCodes('&', getConfig().getString(slotConf + "Nome")));
                    
                    boolean showPreco = true;

                    if (getConfig().contains(slotConf + "MostrarPreco")) {
                        showPreco = getConfig().getBoolean(slotConf + "MostrarPreco");
                    }
                    
                    if (showPreco) {
                        if (itemInfo.getPrice() != 0) { 
                            onGui.setName(onGui.getName() + ConfigValues.getFromConfigColorized(ConfigValues.Type.CUSTO_NOME).replace("{@custo}", String.valueOf(itemInfo.getPrice())));
                        }
                    }
                }

                if (getConfig().contains(slotConf + "Lore")) {
                    List<String> lore = getConfig().getStringList(slotConf + "Lore");
                    ArrayList<String> coloredLore = new ArrayList<String>();

                    for (String loreStr : lore) {
                        coloredLore.add(ChatColor.translateAlternateColorCodes('&', loreStr));
                    }
                    simple.setLore(coloredLore);
                    onGui.setLore(coloredLore);
                }

                if (canUseItemFlags && getConfig().contains(slotConf + "ItemFlags")) {
                    SimpleItemStack simple18 = (SimpleItemStack) simple;
                    SimpleItemStack onGui18 = (SimpleItemStack) simple;
                    List<String> flags = getConfig().getStringList(slotConf + "ItemFlags");

                    for (String itemFlagStr : flags) {
                        simple18.getFlags().add(ItemFlag.valueOf(itemFlagStr.toUpperCase()));
                        onGui18.getFlags().add(ItemFlag.valueOf(itemFlagStr.toUpperCase()));
                    }
                }

                if (getConfig().contains(slotConf + "Enchants")) {
                    List<String> enchants = getConfig().getStringList(slotConf + "Enchants");

                    for (String enchant : enchants) {
                        String toEnum = enchant.toUpperCase();
                        String[] split = toEnum.split(" ");

                        simple.getEnchants().put(split[0], Integer.parseInt(split[1]));
                        onGui.getEnchants().put(split[0], Integer.parseInt(split[1]));
                    }
                }

                itemInfo.setToGive(simple);

                // T�, isto est� ficando *meio* desorganizado...
                if (getConfig().contains(slotConf + "ItemSeparado")) {
                    slotConf = "Lojas." + lojaStr + ".Slots." + slot + ".ItemSeparado.";
                    SimpleItemStack17 subItem = new SimpleItemStack17();

                    if (canUseItemFlags) {
                        subItem = new SimpleItemStack();
                    } else {
                        subItem = new SimpleItemStack17();
                    }

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

                    if (canUseItemFlags && getConfig().contains(slotConf + "ItemFlags")) {
                        SimpleItemStack simple18 = (SimpleItemStack) simple;

                        List<String> flags = getConfig().getStringList(slotConf + "ItemFlags");

                        for (String itemFlagStr : flags) {
                            simple18.getFlags().add(ItemFlag.valueOf(itemFlagStr.toUpperCase()));
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

                if (canUseItemFlags) {
                    // Caso esteja usando a 1.8+, � necess�rio esconder os atributos do item
                    // Se n�o esconder, o Minecraft ir� mostrar v�rias informa��es in�teis na
                    // lore do item.
                    SimpleItemStack simple18 = (SimpleItemStack) simple;
                    simple18.getFlags().add(ItemFlag.HIDE_ATTRIBUTES);
                }
                
                loja.items.put(slot - 1, new ItemWrapper(itemInfo, onGui.toItemStack()));
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
    
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
}
