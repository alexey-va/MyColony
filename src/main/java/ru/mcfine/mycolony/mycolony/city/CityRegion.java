package ru.mcfine.mycolony.mycolony.city;

import ru.mcfine.mycolony.mycolony.city.CityArea;
import ru.mcfine.mycolony.mycolony.regions.Region;
import ru.mcfine.mycolony.mycolony.regions.RegionType;

import java.util.ArrayList;
import java.util.List;

public class CityRegion extends Region {

    private CityArea cityArea;
    private List<String> cityMembers = new ArrayList<>();
    private int cityPopulation = 0;


    public CityRegion(ArrayList<String> playerNames, int level, int x, int y, int z, String regionName, String worldName,
                      ArrayList<String> playerUUIDs, RegionType regionType, String uuid, CityArea cityArea,
                      List<String> cityMembers, int cityPopulation) {
        super(playerNames, level, x, y, z, regionName, worldName, playerUUIDs, regionType, uuid);
        this.cityArea = cityArea;
        this.cityMembers = cityMembers;
        this.cityPopulation = cityPopulation;
    }

    public CityArea getCityArea() {
        return cityArea;
    }

    public void setCityArea(CityArea cityArea) {
        this.cityArea = cityArea;
    }

    public List<String> getCityMembers() {
        return cityMembers;
    }

    public void setCityMembers(ArrayList<String> cityMembers) {
        this.cityMembers = cityMembers;
    }

    public int getCityPopulation() {
        return cityPopulation;
    }

    public void setCityPopulation(int cityPopulation) {
        this.cityPopulation = cityPopulation;
    }
}
