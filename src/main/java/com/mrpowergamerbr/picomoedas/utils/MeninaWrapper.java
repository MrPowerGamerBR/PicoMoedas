package com.mrpowergamerbr.picomoedas.utils;

import org.bukkit.entity.Player;

public class MeninaWrapper {
    public static boolean isGirl(Player p) {
        return isGirl(p.getName());
    }

    public static boolean isGirl(String name) {
        // TODO: Adicionar suporte ao MeninaAPI
        return false;
    }

    public static String getCorrectArtigo(Player p) {
        return getCorrectArtigo(p.getName());
    }

    public static String getCorrectArtigo(String name) {
        if (isGirl(name)) {
            return "a";
        } else {
            return "o";
        }
    }

    public static String getCorrectPronome(Player p) {
        return getCorrectPronome(p.getName());
    }

    public static String getCorrectPronome(String name) {
        if (isGirl(name)) {
            return "ela";
        } else {
            return "ele";
        }
    }
}
