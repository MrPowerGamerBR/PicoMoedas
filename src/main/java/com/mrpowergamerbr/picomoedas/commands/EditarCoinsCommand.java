package com.mrpowergamerbr.picomoedas.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.mrpowergamerbr.picomoedas.ConfigValues;
import com.mrpowergamerbr.picomoedas.PicoMoedasAPI;
import com.mrpowergamerbr.picomoedas.utils.AbstractCommand;
import com.mrpowergamerbr.picomoedas.utils.CommandUtils;
import com.mrpowergamerbr.picomoedas.utils.MoedaWrapper;

public class EditarCoinsCommand extends AbstractCommand {
    String comoUsa = null;
    
    public EditarCoinsCommand(String command, String usage, String description) {
        super(command, usage, description);
    }

    public EditarCoinsCommand(String command, String usage, String description, List<String> aliases, String comoUsa) {
        super(command, usage, description, aliases);
        this.comoUsa = ChatColor.translateAlternateColorCodes('&', comoUsa);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("PicoMoedas.EditarCoins")) {
            if (CommandUtils.argExists(args, 1) && CommandUtils.argExists(args, 2)) {
                MoedaWrapper wrapper = PicoMoedasAPI.getBalance(args[0]);
                double quantidade = Double.valueOf(args[1]);
                wrapper.setValue(wrapper.getValue() + quantidade);
                
                String mensagem = ConfigValues.getFromConfigColorized(ConfigValues.Type.VOCE_ADICIONOU_OP);
                if (0 > quantidade) {
                    mensagem = ConfigValues.getFromConfigColorized(ConfigValues.Type.VOCE_REMOVEU_OP);
                }
                mensagem = mensagem.replace("{@moedas}", String.valueOf(quantidade));
                mensagem = mensagem.replace("{@player}", args[0]);
                sender.sendMessage(mensagem);
            } else {
                String comoUsa = this.comoUsa;
                comoUsa = comoUsa.replace("{@comando}", label);
                sender.sendMessage(comoUsa);
            }
        }
        return true;
    }
}