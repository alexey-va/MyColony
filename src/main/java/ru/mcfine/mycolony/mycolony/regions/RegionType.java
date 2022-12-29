package ru.mcfine.mycolony.mycolony.regions;

import org.bukkit.Material;
import ru.mcfine.mycolony.mycolony.production.ProductionEntry;
import ru.mcfine.mycolony.mycolony.requirements.Requirement;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RegionType {

    private List<ProductionEntry> productionEntries = new ArrayList<>();
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
    private boolean isCity = false;
    private Material dynmapMarker = Material.CAKE;
    private Material shopIcon = Material.BEDROCK;
    private double price = 1000;
    private String displayName = "No_name";
    private final String regionId;
    private boolean enabled = true;
    private List<Requirement> reqs;
    private final String shopGroup;
    private final int shopAmount;
    private List<String> groupNames;
    private final List<UpgradeType> upgradeTypes = new ArrayList<>();



    private int baseChunkRadius;
    private boolean absolutePower=false;


    public RegionType(List<String> groupNames, List<ProductionEntry> productionEntries, int level, int cityLevelForUpgrade, double income,
                      ArrayList<RegionType> upgrades, ArrayList<BuildingMaterial> buildingMaterials, int xRight, int xLeft,
                      int zBackward, int zForward, int yDown, int yUp, boolean isCity, Material dynmapMarker,
                      Material shopIcon, double price, String displayName, boolean enabled, List<Requirement> reqs, String shopGroup, int shopAmount, String regionId) {

        if(productionEntries != null) this.productionEntries = productionEntries.stream().
                sorted(Comparator.comparingInt(ProductionEntry::getPriority)).collect(Collectors.toList());
        else this.productionEntries = null;

        this.groupNames = groupNames;
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
        this.isCity = isCity;
        this.dynmapMarker = dynmapMarker;
        this.shopIcon = shopIcon;
        this.price = price;
        this.displayName = displayName;
        this.enabled = enabled;
        this.reqs = reqs;
        this.shopGroup = shopGroup;
        this.shopAmount = shopAmount;
        this.regionId = regionId;
    }

    public RegionType(List<String> groupNames, List<ProductionEntry> productionEntries, int level, int cityLevelForUpgrade, double income,
                      ArrayList<RegionType> upgrades, ArrayList<BuildingMaterial> buildingMaterials, int xRight, int xLeft,
                      int zBackward, int zForward, int yDown, int yUp, boolean isCity, Material dynmapMarker,
                      Material shopIcon, double price, String displayName, boolean enabled, List<Requirement> reqs, String shopGroup,
                      int shopAmount, String regionId ,int chunkRadius, boolean absolutePower) {

        if(productionEntries != null) this.productionEntries = productionEntries.stream().
                sorted(Comparator.comparingInt(ProductionEntry::getPriority)).collect(Collectors.toList());
        else this.productionEntries = null;

        this.groupNames = groupNames;
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
        this.isCity = isCity;
        this.dynmapMarker = dynmapMarker;
        this.shopIcon = shopIcon;
        this.price = price;
        this.displayName = displayName;
        this.enabled = enabled;
        this.reqs = reqs;
        this.shopGroup = shopGroup;
        this.shopAmount = shopAmount;
        this.regionId = regionId;

        this.baseChunkRadius = chunkRadius;
        this.absolutePower=absolutePower;
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

    public List<ProductionEntry> getProductionEntries() {
        return productionEntries;
    }

    public void setProductionEntries(ArrayList<ProductionEntry> productionEntries) {
        this.productionEntries = productionEntries;
    }

    public void setProductionEntries(List<ProductionEntry> productionEntries) {
        this.productionEntries = productionEntries;
    }

    public boolean isCity() {
        return isCity;
    }

    public void setCity(boolean city) {
        isCity = city;
    }

    public Material getDynmapMarker() {
        return dynmapMarker;
    }

    public void setDynmapMarker(Material dynmapMarker) {
        this.dynmapMarker = dynmapMarker;
    }

    public Material getShopIcon() {
        return shopIcon;
    }

    public void setShopIcon(Material shopIcon) {
        this.shopIcon = shopIcon;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<Requirement> getReqs() {
        return reqs;
    }

    public void setReqs(List<Requirement> reqs) {
        this.reqs = reqs;
    }

    public String getShopGroup() {
        return shopGroup;
    }

    public int getShopAmount() {
        return shopAmount;
    }

    public int getBaseChunkRadius() {
        return baseChunkRadius;
    }

    public String getRegionId() {
        return regionId;
    }

    public List<UpgradeType> getUpgradeTypes() {
        return upgradeTypes;
    }

    public boolean isAbsolutePower() {
        return absolutePower;
    }
}


