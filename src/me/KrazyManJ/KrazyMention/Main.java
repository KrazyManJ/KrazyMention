package me.KrazyManJ.KrazyMention;

import me.KrazyManJ.KrazyMention.Listeners.MentionListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public final class Main extends JavaPlugin implements Listener {

    public static final String prefix = Format.whole("&#7875e0[&#9c9ae6KrazyMention&#7875e0]" );
    public static Main getInstance(){
        return instance;
    }
    private static Main instance;

    @Override
    public void onEnable(){
        this.saveDefaultConfig();
        instance = this;
        getServer().getPluginManager().registerEvents(new MentionListener(), this);
    }
    @Override
    public void onDisable(){

    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender,@NotNull Command command,@NotNull String label, String[] args) {
        if (label.equalsIgnoreCase("krazymention")){
            if (args[0].equals("reload")){
                this.reloadConfig();
                sender.sendMessage(prefix + Format.whole("&7 Config was successfully reloaded!"));
                return true;
            }
        }
        return false;
    }
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args){
        if (args.length == 1 && label.equalsIgnoreCase("krazymention"))
            if (sender.hasPermission("krazymention.admin")) return Collections.singletonList("reload");
        return null;
    }
    public static void consoleError(String string){
        Bukkit.getLogger().log(Level.SEVERE, string);
    }
}
