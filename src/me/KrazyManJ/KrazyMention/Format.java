package me.KrazyManJ.KrazyMention;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Format {

    public static String emptyString = ChatColor.translateAlternateColorCodes('&',"&r");

    public static String whole(String text){
        Matcher match = Pattern.compile("&#[a-fA-F0-9]{6}").matcher(text);
        while (match.find()) {
            String colorMatch = text.substring(match.start(), match.end());
            String color = colorMatch.replace("&", "");
            text = text.replace(colorMatch, ChatColor.of(color) + "");
            match = Pattern.compile("&#[a-fA-F0-9]{6}").matcher(text);
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static BaseComponent toBaseComponent(String text){
        String rg = "(&#[0-9a-f]{6}|&x(&[0-9a-f]){6})";
        if (!Pattern.compile(rg).matcher(text).find()) return new TextComponent(text);
        List<String> S = new LinkedList<>(Arrays.asList(text.split("(?="+rg+")")));
        BaseComponent r;
        if (Pattern.compile(rg).matcher(S.get(0)).find()) r = new TextComponent("");
        else r = new TextComponent(ChatColor.translateAlternateColorCodes('&',S.remove(0)));
        for (String s:S){
            String[] c = s.split("(?<="+rg+")");
            BaseComponent C = new TextComponent(ChatColor.translateAlternateColorCodes('&',c[1]));
            C.setColor(ChatColor.of(c[0].replaceAll("&","").replace("x","#")));
            r.addExtra(C);
        }
        return r;
    }
    public static String deformatHex(String text){
        String rg = "(?i)&#[0-9a-f]{6}";
        Matcher match = Pattern.compile(rg).matcher(text);
        while (match.find()){
            String fT = text.substring(match.start(),match.end());
            String rT = fT.replaceFirst("&#", "&x").replaceAll("(?i)(?<!&)(?=[0-9a-f])","&");
            text = text.replaceAll(fT, rT);
            match = Pattern.compile(rg).matcher(text);
        }
        return text;
    }
}
