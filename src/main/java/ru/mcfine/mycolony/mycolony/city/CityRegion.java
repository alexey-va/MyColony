package ru.mcfine.mycolony.mycolony.city;

import ru.mcfine.mycolony.mycolony.regions.Region;
import ru.mcfine.mycolony.mycolony.regions.RegionType;

import java.util.HashSet;
import java.util.Set;

public class CityRegion extends Region {

    private CityArea cityArea;
    private Set<String> cityMembers = new HashSet<>();
    private int cityPopulation = 0;
    private String cityWgName;
    private String ownerName;


    public CityRegion(Set<String> playerNames, int level, int x, int y, int z, String regionName, String worldName,
                      Set<String> playerUUIDs, RegionType regionType, String uuid, String wgName, CityArea cityArea,
                      Set<String> cityMembers, String cityWgName, String ownerName) {
        super(playerNames, level, x, y, z, regionName, worldName, playerUUIDs, regionType, uuid, wgName, null);
        this.cityArea = cityArea;
        this.cityMembers = cityMembers;
        this.cityWgName = cityWgName;
        this.ownerName = ownerName;
        this.cityPopulation = cityMembers.size();
        this.setCityRegion(this);
    }

    public CityArea getCityArea() {
        return cityArea;
    }

    public void setCityArea(CityArea cityArea) {
        this.cityArea = cityArea;
    }

    public Set<String> getCityMembers() {
        return cityMembers;
    }

    public void setCityMembers(Set<String> cityMembers) {
        this.cityMembers = cityMembers;
    }

    public void addCityMember(String name){
        cityMembers.add(name);
    }

    public int getCityPopulation() {
        return cityMembers.size();
    }


    public String getCityWgName() {
        return cityWgName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public boolean cityHasPlayer(String playerName){
        return cityMembers.contains(playerName);
    }
}
