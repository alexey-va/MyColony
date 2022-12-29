package ru.mcfine.mycolony.mycolony.regimes;

import ru.mcfine.mycolony.mycolony.config.Lang;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RegimeBuff {

    public String key;
    public Type type;
    public double amount;
    public String displayName;
    public List<String> lore;

    public RegimeBuff(Type type, double amount, String displayName, List<String> lore, String key) {
        this.type = type;
        this.amount = amount;
        this.displayName = Lang.translate(displayName);
        this.lore = lore.stream().map(Lang::translate).collect(Collectors.toList());
        this.key = key;
    }

    public static Type matchType(String s){
        if(s == null) return null;
        if(s.equalsIgnoreCase("COST_MULTIPLIER")) return Type.COST_MULTIPLIER;
        if(s.equalsIgnoreCase("MONEY_INCOME_MULTIPLIER")) return Type.MONEY_INCOME_MULTIPLIER;
        if(s.equalsIgnoreCase("CYCLE_TIME_MULTIPLIER")) return Type.CYCLE_TIME_MULTIPLIER;
        return null;
    }
    public enum Type{
        COST_MULTIPLIER,
        MONEY_INCOME_MULTIPLIER,
        CYCLE_TIME_MULTIPLIER
    }
}
