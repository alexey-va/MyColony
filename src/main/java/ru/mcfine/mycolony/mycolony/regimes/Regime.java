package ru.mcfine.mycolony.mycolony.regimes;

import org.bukkit.Material;
import ru.mcfine.mycolony.mycolony.config.Lang;
import ru.mcfine.mycolony.mycolony.regions.UpgradeType;

import java.util.ArrayList;
import java.util.List;

public class Regime {

    private Material icon = Material.BEDROCK;
    private String displayName = "Null";
    private final List<RegimeBuff> regimeBuffs;
    private final List<UpgradeType> regimeTransitions;

    public Regime(Material icon, String displayName, List<RegimeBuff> regimeBuffs, List<UpgradeType> regimeTransitions) {
        this.icon = icon;
        this.displayName = Lang.translate(displayName);
        this.regimeBuffs = regimeBuffs;
        this.regimeTransitions = regimeTransitions;
    }

    public Material getIcon() {
        return icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<RegimeBuff> getRegimeBuffs() {
        return regimeBuffs;
    }

    public List<UpgradeType> getRegimeTransitions() {
        return regimeTransitions;
    }
}
