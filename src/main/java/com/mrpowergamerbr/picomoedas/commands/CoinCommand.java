package com.mrpowergamerbr.picomoedas.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mrpowergamerbr.picomoedas.ConfigValues;
import com.mrpowergamerbr.picomoedas.PicoMoedasAPI;
import com.mrpowergamerbr.picomoedas.ConfigValues.Type;
import com.mrpowergamerbr.picomoedas.utils.AbstractCommand;
import com.mrpowergamerbr.picomoedas.utils.CommandUtils;
import com.mrpowergamerbr.picomoedas.utils.MeninaWrapper;

public class CoinCommand extends AbstractCommand {

    public CoinCommand(String command, String usage, String description) {
        super(command, usage, description);
    }

    public CoinCommand(String command, String usage, String description, List<String> aliases, String comoUsa) {
        super(command, usage, description, aliases);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (CommandUtils.argExists(args, 1)) {
            String moedas = ConfigValues.getFromConfigColorized(Type.SUAS_MOEDAS);
            moedas = moedas.replace("{@moedas}", String.valueOf(PicoMoedasAPI.getBalance(args[0]).getValue()));
            moedas = moedas.replace("{@artigo}", MeninaWrapper.getCorrectArtigo(args[0]));
            sender.sendMessage(moedas);
            return true;
        } else {
            if (CommandUtils.isPlayer(sender)) {
                Player p = (Player) sender;
                String moedas = ConfigValues.getFromConfigColorized(Type.SUAS_MOEDAS);
                moedas = moedas.replace("{@moedas}", String.valueOf(PicoMoedasAPI.getBalance(p).getValue()));
                sender.sendMessage(moedas);
                return true;
            }
        }
        return true;
    }
}