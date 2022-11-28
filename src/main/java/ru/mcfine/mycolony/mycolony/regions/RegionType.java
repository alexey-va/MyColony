package ru.mcfine.mycolony.mycolony.regions;

import org.bukkit.Material;

import java.util.ArrayList;

public class RegionType {

    private ArrayList<ConsumableType> consumableList = new ArrayList<>();
    private int level = 1;
    private int cityLevelForUpgrade = 1;
    private double income = 0;
    private ArrayList<RegionType> upgrades;

    public RegionType(ArrayList<ConsumableType> consumableList, int level, int cityLevelForUpgrade, double income, ArrayList<RegionType> upgrades) {
        this.consumableList = consumableList;
        this.level = level;
        this.cityLevelForUpgrade = cityLevelForUpgrade;
        this.income = income;
        this.upgrades = upgrades;
    }

    public ArrayList<ConsumableType> getConsumableList() {
        return consumableList;
    }

    public void setConsumableList(ArrayList<ConsumableType> consumableList) {
        this.consumableList = consumableList;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getCityLevelForUpgrade() {
        return cityLevelForUpgrade;
    }

    public void setCityLevelForUpgrade(int cityLevelForUpgrade) {
        this.cityLevelForUpgrade = cityLevelForUpgrade;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    public ArrayList<RegionType> getUpgrades() {
        return upgrades;
    }

    public void setUpgrades(ArrayList<RegionType> upgrades) {
        this.upgrades = upgrades;
    }
}



class ConsumableType{
    public Material material = Material.STONE;
    public int amount = 0;

    public ConsumableType(Material material, int amount){
        this.material = material;
        this.amount = amount;
    }
}
