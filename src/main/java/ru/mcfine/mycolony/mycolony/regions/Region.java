package ru.mcfine.mycolony.mycolony.regions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
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

    public Region(String playerName, int level, int x, int y, int z, String regionName, String worldName, String playerUUID) {
        this.regionName = regionName;
        this.playerName = playerName;
        this.level = level;
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldName = worldName;
        this.playerUUID = playerUUID;

        this.location = new Location(Bukkit.getWorld(worldName), x, y, z);
    }

    public void incrementTime(double inc)
    {
        this.timeElapsed += inc;
        if(this.timeElapsed > maxTime){
            this.timeElapsed = 0;
            Chest chest = (Chest) location.getBlock().getState();
            chest.getBlockInventory().addItem(new ItemStack(Material.STRING));
        }
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
}
