package com.mrpowergamerbr.picomoedas.utils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class RetroUtils {
	public static Method getOP;
	public static Method setHealth;

	static {
		try {
			getOP = Bukkit.getServer().getClass().getMethod("getOnlinePlayers");
			setHealth = Class.forName("org.bukkit.entity.LivingEntity").getMethod("setHealth");
		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			// Qual versão você está usando?
			e.printStackTrace();
		}
	}

	public static Collection<? extends Player> getOnlinePlayers() {
		try {
			Object object = getOP.invoke(Bukkit.getServer());

			if (object instanceof Collection) {
				return (Collection<? extends Player>) object;
			} else {
				// Ah não... isto é um Player[]! Maldita *insira versão antes da 1.7.9-R0.3-SNAPSHOT!*
				Player[] oldList = (Player[]) object;

				return Arrays.asList(oldList); 
			}
		} catch (Exception e) {
			// Oh no! Deu problema! Vamos usar o Bukkit.getOnlinePlayers() e torcer para que dê certo...
			return Bukkit.getOnlinePlayers();
		}
	}

	// Yay, workarounds!
	public static void setHealth(LivingEntity entity, double d) {
		try {
			// 1.6+
			setHealth.invoke(entity, d);
		} catch (Exception e) {
			try {
				int i = (int) d;
				setHealth.invoke(entity, (int) i);
			} catch (Exception e2) {
				entity.setHealth(d);
			}
		}
	}
}
