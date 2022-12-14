package ru.mcfine.mycolony.mycolony.regions;

import ru.mcfine.mycolony.mycolony.city.CityRegion;
import ru.mcfine.mycolony.mycolony.city.SquareArea;

import java.util.Set;

public class RegionMock {
    public Set<String> playerNames;
    public int level;
    public int x, y, z;
    public String worldName;
    public Set<String> playerUUIDs;
    public String regionName;
    public double timeElapsed;
    public double totalIncome;
    public String uuid;
    public double bankDeposit;
    public String wgName;
    public String cityWgName;

    /// AREA
    public int chunkRadius = -1;

    // CITY
    public Set<String> members;
    public double timeSinceCreation;
    public String ownerName;

    public RegionMock(Region region){
        this.playerNames = region.getPlayerNames();
        this.level = region.getLevel();
        this.x = region.getX();
        this.y = region.getY();
        this.z = region.getZ();
        this.worldName = region.getWorldName();
        this.playerUUIDs = region.getPlayerUUIDs();
        this.regionName = region.getRegionName();
        this.timeElapsed = region.getTimeElapsed();
        this.totalIncome = region.getTotalIncome();
        this.uuid = region.getUuid();
        this.bankDeposit = region.getBankDeposit();
        this.wgName = region.getWgRegionName();
        this.timeSinceCreation = region.getTimeSinceCreation();
        if(region instanceof CityRegion cityRegion){
            if(cityRegion.getCityArea() instanceof SquareArea squareArea){
                this.chunkRadius = squareArea.getChunkRadius();
            }

            this.members = cityRegion.getCityMembers();
            this.cityWgName= cityRegion.getCityWgName();
            this.ownerName = cityRegion.getOwnerName();
        }
    }
}
