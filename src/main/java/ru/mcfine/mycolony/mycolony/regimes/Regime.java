package ru.mcfine.mycolony.mycolony.regimes;

import org.bukkit.Material;
import ru.mcfine.mycolony.mycolony.regions.Region;

import java.util.ArrayList;

public class Regime {

    private Material icon = Material.BEDROCK;
    private String displayName = "Null";
    private ArrayList<RegimeBuff> regimeBuffs;
    private ArrayList<RegimeTransition> regimeTransitions;

    public Regime(Material icon, String displayName, ArrayList<RegimeBuff> regimeBuffs, ArrayList<RegimeTransition> regimeTransitions) {
        this.icon = icon;
        this.displayName = displayName;
        this.regimeBuffs = regimeBuffs;
        this.regimeTransitions = regimeTransitions;
    }

    public Material getIcon() {
        return icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ArrayList<RegimeBuff> getRegimeBuffs() {
        return regimeBuffs;
    }

    public ArrayList<RegimeTransition> getRegimeTransitions() {
        return regimeTransitions;
    }
}
