package com.mrpowergamerbr.picomoedas.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class CommandUtils {
    public static boolean arg(String[] arg, int i, String s) {
        try {
            if (arg[i - 1].equalsIgnoreCase(s)) {
                return true;
            } else {
                return false;
            }
        }
        catch (Exception e) {
            return false;
        }
    }

    public static boolean argExists(String[] arg, int i) {
        try {
            if (arg.length >= i) {
                return true;
            } else {
                return false;
            }
        }
        catch (Exception e) {
            return false;
        }
    }
    
    public static boolean isPlayer(CommandSender sender) {
        return !isConsole(sender);
    }
    
    public static boolean isConsole(CommandSender sender) {
        return sender instanceof ConsoleCommandSender;
    }
}
