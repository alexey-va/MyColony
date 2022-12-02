package ru.mcfine.mycolony.mycolony.regions;

import java.util.ArrayList;

public class RegionMock {
    public ArrayList<String> playerNames;
    public int level;
    public int x, y, z;
    public String worldName;
    public ArrayList<String> playerUUIDs;
    public String regionName;
    public double timeElapsed;
    public double totalIncome;
    public String uuid;

    public RegionMock(Region region){
        this.playerNames = region.getPlayerNames();
        this.level = region.getLevel();
        this.x = region.getX();;
        this.y = region.getY();
        this.z = region.getZ();
        this.worldName = region.getWorldName();
        this.playerUUIDs = region.getPlayerUUIDs();
        this.regionName = region.getRegionName();
        this.timeElapsed = region.getTimeElapsed();
        this.totalIncome = region.getTotalIncome();
        this.uuid = region.getUuid();
    }
}
