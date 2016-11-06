package com.mrpowergamerbr.picomoedas.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mrpowergamerbr.picomoedas.PicoMoedasAPI;
import com.mrpowergamerbr.picomoedas.utils.AbstractCommand;

public class LojaGUICommand extends AbstractCommand {

    public LojaGUICommand(String command, String usage, String description) {
        super(command, usage, description);
    }

    public LojaGUICommand(String command, String usage, String description, List<String> aliases, String comoUsa) {
        super(command, usage, description, aliases);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;
        
        PicoMoedasAPI.abrirGUI(p);
        return true;
    }
}