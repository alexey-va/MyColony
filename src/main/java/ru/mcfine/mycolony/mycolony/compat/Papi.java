package ru.mcfine.mycolony.mycolony.compat;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class Papi {

    public String parse(String s, Player p){
        if(s == null) return null;
        return PlaceholderAPI.setPlaceholders(p, s);
    }

}
