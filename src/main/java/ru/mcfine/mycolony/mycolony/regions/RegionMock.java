package ru.mcfine.mycolony.mycolony.regions;

public class RegionMock {
    public String playerName;
    public int level;
    public int x, y, z;
    public String worldName;
    public String playerUUID;
    public String regionName;
    public double timeElapsed;
    public double totalIncome;

    public RegionMock(Region region){
        this.playerName = region.getPlayerName();
        this.level = region.getLevel();
        this.x = region.getX();;
        this.y = region.getY();
        this.z = region.getZ();
        this.worldName = region.getWorldName();
        this.playerUUID = region.getPlayerUUID();
        this.regionName = region.getRegionName();
        this.timeElapsed = region.getTimeElapsed();
        this.totalIncome = region.getTotalIncome();
    }
}
