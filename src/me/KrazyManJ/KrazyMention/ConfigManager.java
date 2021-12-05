package me.KrazyManJ.KrazyMention;

public class ConfigManager {

    public static boolean getBoolean(String path){
        return Main.getInstance().getConfig().getBoolean(path);
    }
    public static String getString(String path){
        return Main.getInstance().getConfig().getString(path);
    }
}
