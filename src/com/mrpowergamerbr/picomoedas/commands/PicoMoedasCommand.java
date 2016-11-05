package com.mrpowergamerbr.picomoedas.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import com.mrpowergamerbr.picomoedas.utils.AbstractCommand;

public class PicoMoedasCommand extends AbstractCommand {

    public PicoMoedasCommand(String command, String usage, String description) {
        super(command, usage, description);
    }

    public PicoMoedasCommand(String command, String usage, String description, List<String> aliases) {
        super(command, usage, description, aliases);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return true;
    }
}