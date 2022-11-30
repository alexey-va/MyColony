package ru.mcfine.mycolony.mycolony.regions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Region {
    private String playerName;
    private int level;
    private int x, y, z;
    private String worldName;
    private String playerUUID;
    private String regionName;
    private double timeElapsed = 0;
    private double maxTime = 10;
    private Location location;
    private double totalIncome;
    private RegionType regionType;
    private boolean requiresMaterials = false;

    public Region(String playerName, int level, int x, int y, int z, String regionName, String worldName, String playerUUID, RegionType regionType) {
        this.regionName = regionName;
        this.playerName = playerName;
        this.level = level;
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldName = worldName;
        this.playerUUID = playerUUID;
        this.regionType = regionType;

        this.location = new Location(Bukkit.getWorld(worldName), x, y, z);
    }

    public void incrementTime(double inc)
    {
        this.timeElapsed += inc;
        if(this.timeElapsed > maxTime){
            this.timeElapsed = 0;
            Chest chest = (Chest) location.getBlock().getState();
            if(this.regionType.getProductionList() != null){
                if(ifHasMaterials()){

                }
            }
        }
    }

    public boolean takeConsumables(){
        Block block = location.getBlock();
        if(block instanceof Chest chest){
            Inventory inventory = chest.getBlockInventory();
            for(ConsumableType consumableType : this.regionType.getConsumableList()){
                int amount = consumableType.amount;
                while(amount > 0){
                    inventory.removeItemAnySlot (new ItemStack(consumableType.material, Math.min(64, amount)));
                    amount-=Math.min(64, amount);
                }
            }
        } else {
            System.out.println("Not chest! "+block);
        }
        return true;
    }

    public boolean putProduction(){

        Block block = location.getBlock();
        if(block instanceof Chest chest){
            Inventory inventory = chest.getBlockInventory();
            for(ConsumableType consumableType : this.regionType.getProductionList()){
                int amount = consumableType.amount;
                while(amount > 0){
                    inventory.addItem(new ItemStack(consumableType.material, Math.min(64, amount)));
                    amount-=Math.min(64, amount);
                }
            }
        } else {
            System.out.println("Not chest! "+block);
        }
        return true;
    }

    public boolean ifHasMaterials(){
        ArrayList<ConsumableType> consumables = new ArrayList<>(this.regionType.getConsumableList());
        if(location == null || !(location.getBlock() instanceof Chest)) return false;
        Inventory inventory = ((Chest)location.getBlock()).getBlockInventory();
        for(ItemStack itemStack : inventory.getStorageContents()){
            if(itemStack == null || itemStack.getType() == Material.AIR) continue;
            System.out.println(itemStack);
            for (ConsumableType consumableType : consumables) {
                if (consumableType.material == itemStack.getType()) {
                    consumableType.amount = Math.max(0, consumableType.amount - itemStack.getAmount());
                }
            }
        }
        for(ConsumableType consumableType : consumables){
            if(consumableType.amount >0) return false;
        }
        return false;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public String getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(String playerUUID) {
        this.playerUUID = playerUUID;
    }

    public double getTimeElapsed() {
        return timeElapsed;
    }

    public double getMaxTime() {
        return maxTime;
    }

    public double getTotalIncome() {
        return totalIncome;
    }
}
