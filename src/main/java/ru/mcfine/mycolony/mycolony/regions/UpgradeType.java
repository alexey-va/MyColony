package ru.mcfine.mycolony.mycolony.regions;

import ru.mcfine.mycolony.mycolony.regimes.Regime;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UpgradeType {

    private String regionTypeName;
    private String regimeName;
    private List<UpgradeCondition> conditionList = new ArrayList<>();
    private int priority = 1;
    private boolean automatic = false;

    public UpgradeType(String regionType, String regimeName, List<UpgradeCondition> conditionList, int priority, boolean automatic) {
        this.regionTypeName = regionTypeName;
        this.regimeName = regimeName;
        this.conditionList = conditionList;
        this.priority = priority;
        this.automatic = automatic;
    }

    public List<UpgradeCondition> getLackingConditions(Region region, String playerName) {
        boolean can = true;
        List<UpgradeCondition> lack = new ArrayList<>();
        for (UpgradeCondition upgradeCondition : conditionList) {
            if (!upgradeCondition.satisfy(playerName, region.getWorldName(), region, region.getCityRegion())) {
                lack.add(upgradeCondition);
            }
        }
        return lack;
    }

    public String getRegionTypeName() {
        return regionTypeName;
    }

    public String getRegimeName() {
        return regimeName;
    }

    public List<UpgradeCondition> getConditionList() {
        return conditionList;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isAutomatic() {
        return automatic;
    }
}
