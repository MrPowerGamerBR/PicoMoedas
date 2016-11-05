package com.mrpowergamerbr.picomoedas;

public class ConfigValues {
    public static String getFromConfig(Type type) {
        String onConfig = type.name;
        return PicoMoedasAPI.m.getConfig().getString(onConfig);
    }
    
    public enum Type {
        DEFAULT_TO_OPEN("LojaPadrão"),
        SUAS_MOEDAS("Mensagens.SuasMoedas"),
        SEM_MOEDAS_SUFICIENTES("Mensagens.SemMoedas");
        
        String name;
        
        Type(String name) {
            this.name = name;
        }
    }
}
