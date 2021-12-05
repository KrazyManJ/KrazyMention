package me.KrazyManJ.KrazyMention.Listeners;

import me.KrazyManJ.KrazyMention.ConfigManager;
import me.KrazyManJ.KrazyMention.Format;
import me.KrazyManJ.KrazyMention.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MentionListener implements Listener {



    @EventHandler (priority = EventPriority.HIGHEST)
    public void on(AsyncPlayerChatEvent event){
        if (event.getPlayer().hasPermission("krazymention.mention")) {
            String msg = Format.deformatHex(event.getMessage());


            //getting players mentioned in this message
            String regex = "";
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (event.getMessage().contains(player.getName()) && event.getPlayer().canSee(player)) regex = regex + player.getName() + "|";
            }

            //if contains name of player
            if (!regex.isEmpty()) {
                regex = regex.substring(0, regex.length() - 1);
                String fP = msg;
                Matcher matcher = Pattern.compile(regex).matcher(fP);
                List<String> mplayers = new ArrayList<>();
                while (matcher.find()) {
                    String cpl = fP.substring(matcher.start(), matcher.end());
                    mplayers.add(cpl);
                    fP = fP.replaceFirst(cpl, "");
                    matcher = Pattern.compile(regex).matcher(fP);
                }


                //removing duplicates
                Set<String> set = new LinkedHashSet<>(mplayers);


                String defaultColor = Format.whole(ConfigManager.getString("chat mention format.default chat color"));
                String symbol = ConfigManager.getString("chat mention format.mention prefix");


                for (String player : set) {
                    String mentionColor = Format.whole(ConfigManager.getString("chat mention format.mention color"));
                    String mentionerColor = mentionColor;
                    if (Main.getInstance().getConfig().getConfigurationSection("players mention color").getKeys(false).size() > 0){
                        if (Main.getInstance().getConfig().getConfigurationSection("players mention color").contains(player))
                            mentionColor = Format.whole(ConfigManager.getString("players mention color."+player));
                        if (Main.getInstance().getConfig().getConfigurationSection("players mention color").contains(event.getPlayer().getName()))
                            mentionerColor = Format.whole(ConfigManager.getString("players mention color."+event.getPlayer().getName()));
                    }


                    if (ConfigManager.getBoolean("chat mention format.change format in chat")){
                        String lastcolor = "";
                        String[] s = msg.split(player, 2);
                        if (!s[0].isEmpty() && ConfigManager.getBoolean("chat mention format.sci.enabled"))
                            lastcolor = org.bukkit.ChatColor.getLastColors(ChatColor.translateAlternateColorCodes('&', flipChars(s[0], '&', '§'))).replaceAll("§", "&");
                        msg = msg.replaceFirst(player, mentionColor+symbol+player+defaultColor+lastcolor);
                    }

                    if (ConfigManager.getBoolean("mention notification.send notification text")) {
                        Player mentionedPlayer = Bukkit.getPlayer(player);
                        String notificationMessage = ConfigManager.getString("mention notification.notification text").replace("{mentioner}", event.getPlayer().getName()).replace("{displayMentioner}", mentionerColor+symbol+event.getPlayer().getName());
                        switch (ConfigManager.getString("mention notification.send notification to").toLowerCase(Locale.ROOT)) {
                            case "action bar": mentionedPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, Format.toBaseComponent(notificationMessage));break;
                            case "title": mentionedPlayer.sendTitle(Format.whole(notificationMessage), Format.emptyString, 10, 30, 10);break;
                            case "subtitle": mentionedPlayer.sendTitle(Format.emptyString, Format.whole(notificationMessage), 10, 30, 10);break;
                            case "title and subtitle":
                                if (notificationMessage.contains("%newline%")) {
                                    String[] titleAndSubtitle = Format.whole(notificationMessage).split("%newline%");
                                    mentionedPlayer.sendTitle(titleAndSubtitle[0], titleAndSubtitle[1], 10, 30, 10);
                                } else
                                    Main.consoleError("Not found %newline% in notification text in config.yml! If you need to use only title " +
                                            "or subtitle, then change option 'send notification to' to only 'title' or 'subtitle'!");
                                break;
                        }
                    }
                    if (ConfigManager.getBoolean("mention notification.use sound")) {
                        try {
                            Bukkit.getPlayer(player).playSound(Bukkit.getPlayer(player).getLocation(), Sound.valueOf(ConfigManager.getString("mention notification.sound").toUpperCase(Locale.ROOT)), 1, 1);
                        } catch (Exception e) {
                            Main.consoleError("Invalid sound name in config.yml! Look at https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html to choose sound and copy it into config!");
                        }
                    }

                    if (!event.getPlayer().hasPermission("krazymention.mention.multiple")) break;
                }
            }

            //cleaner
            if (ConfigManager.getBoolean("chat mention format.sci.cleancolors.enabled")) {
                String legperm = ConfigManager.getString("chat mention format.sci.cleancolors.legacy permission");
                String hexperm = ConfigManager.getString("chat mention format.sci.cleancolors.hex permission");
                if (hexperm.length()!=0 && !event.getPlayer().hasPermission(hexperm)) msg = msg.replaceAll("(?i)&x(&[0-9a-f]){6}", "");
                if (legperm.length()!=0 && !event.getPlayer().hasPermission(legperm)) msg = msg.replaceAll("(?i)&[0-9a-fklmnorx]", "");
                msg = ChatColor.translateAlternateColorCodes('&',msg);
            }
            event.setMessage(msg);
        }
    }
    public static String flipChars(String text, char one, char two){
        StringBuilder fIndex = new StringBuilder(text);
        List<Integer> indexes = new ArrayList<>();
        while (fIndex.indexOf(String.valueOf(one)) >= 0){
            indexes.add(fIndex.indexOf(String.valueOf(one)));
            fIndex.setCharAt(fIndex.indexOf(String.valueOf(one)), two);
        }
        text = text.replace(String.valueOf(two),String.valueOf(one));
        StringBuilder result = new StringBuilder(text);
        for (int Ind : indexes) result.setCharAt(Ind, two);
        return result.toString();
    }
}
