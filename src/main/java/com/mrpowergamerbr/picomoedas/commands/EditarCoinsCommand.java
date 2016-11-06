package com.mrpowergamerbr.picomoedas.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import com.mrpowergamerbr.picomoedas.PicoMoedasAPI;
import com.mrpowergamerbr.picomoedas.utils.AbstractCommand;
import com.mrpowergamerbr.picomoedas.utils.MoedaWrapper;

public class EditarCoinsCommand extends AbstractCommand {

    public EditarCoinsCommand(String command, String usage, String description) {
        super(command, usage, description);
    }

    public EditarCoinsCommand(String command, String usage, String description, List<String> aliases) {
        super(command, usage, description, aliases);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("PicoMoedas.EditarCoins")) {
            MoedaWrapper wrapper = PicoMoedasAPI.getBalance(args[0]);
            wrapper.setValue(wrapper.getValue() + Double.valueOf(args[1]));
        }
        return true;
    }
}