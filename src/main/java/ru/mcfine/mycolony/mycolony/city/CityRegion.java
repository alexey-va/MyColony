package ru.mcfine.mycolony.mycolony.city;

import ru.mcfine.mycolony.mycolony.city.CityArea;
import ru.mcfine.mycolony.mycolony.regions.Region;
import ru.mcfine.mycolony.mycolony.regions.RegionType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CityRegion extends Region {

    private CityArea cityArea;
    private Set<String> cityMembers = new HashSet<>();
    private int cityPopulation = 0;
    private String cityWgName;


    public CityRegion(Set<String> playerNames, int level, int x, int y, int z, String regionName, String worldName,
                      Set<String> playerUUIDs, RegionType regionType, String uuid, String wgName, CityArea cityArea,
                      Set<String> cityMembers, int cityPopulation, String cityWgName) {
        super(playerNames, level, x, y, z, regionName, worldName, playerUUIDs, regionType, uuid, wgName);
        this.cityArea = cityArea;
        this.cityMembers = cityMembers;
        this.cityPopulation = cityPopulation;
        this.cityWgName = cityWgName;
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

    public int getCityPopulation() {
        return cityPopulation;
    }

    public void setCityPopulation(int cityPopulation) {
        this.cityPopulation = cityPopulation;
    }

    public String getCityWgName() {
        return cityWgName;
    }
}
