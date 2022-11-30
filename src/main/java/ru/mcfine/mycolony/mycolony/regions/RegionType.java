package ru.mcfine.mycolony.mycolony.regions;

import org.bukkit.Material;

import java.util.ArrayList;

public class RegionType {

    private ArrayList<ConsumableType> consumableList = new ArrayList<>();
    private ArrayList<ConsumableType> productionList = new ArrayList<>();
    private int level = 1;
    private int cityLevelForUpgrade = 1;
    private double income = 0;
    private ArrayList<RegionType> upgrades;
    private ArrayList<BuildingMaterial> buildingMaterials;
    private int xRight = 0;
    private int xLeft = 0;
    private int zBackward = 0;
    private int zForward = 0;
    private int yUp = 0;
    private int yDown = 0;

    public RegionType(ArrayList<ConsumableType> consumableList, ArrayList<ConsumableType> productionList, int level, int cityLevelForUpgrade, double income,
                      ArrayList<RegionType> upgrades, ArrayList<BuildingMaterial> buildingMaterials, int xRight, int xLeft,
                      int zBackward, int zForward, int yDown, int yUp) {
        this.productionList = productionList;
        this.consumableList = consumableList;
        this.level = level;
        this.cityLevelForUpgrade = cityLevelForUpgrade;
        this.income = income;
        this.upgrades = upgrades;
        this.buildingMaterials = buildingMaterials;
        this.xLeft = xLeft;
        this.xRight = xRight;
        this.zBackward = zBackward;
        this.zForward = zForward;
        this.yUp = yUp;
        this.yDown = yDown;
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

    public ArrayList<BuildingMaterial> getBuildingMaterials() {
        return buildingMaterials;
    }

    public void setBuildingMaterials(ArrayList<BuildingMaterial> buildingMaterials) {
        this.buildingMaterials = buildingMaterials;
    }

    public int getxRight() {
        return xRight;
    }

    public void setxRight(int xRight) {
        this.xRight = xRight;
    }

    public int getxLeft() {
        return xLeft;
    }

    public void setxLeft(int xLeft) {
        this.xLeft = xLeft;
    }

    public int getzBackward() {
        return zBackward;
    }

    public void setzBackward(int zBackward) {
        this.zBackward = zBackward;
    }

    public int getzForward() {
        return zForward;
    }

    public void setzForward(int zForward) {
        this.zForward = zForward;
    }

    public int getyUp() {
        return yUp;
    }

    public void setyUp(int yUp) {
        this.yUp = yUp;
    }

    public int getyDown() {
        return yDown;
    }

    public void setyDown(int yDown) {
        this.yDown = yDown;
    }

    public ArrayList<ConsumableType> getProductionList() {
        return productionList;
    }
}


