package ru.mcfine.mycolony.mycolony.requirements;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.mcfine.mycolony.mycolony.city.CityRegion;
import ru.mcfine.mycolony.mycolony.regions.Region;
import ru.mcfine.mycolony.mycolony.regions.RegionManager;
import ru.mcfine.mycolony.mycolony.regions.RegionType;

import java.time.LocalTime;
import java.util.*;

public class Requirement {

    private List<Integer> cityLevels;
    private String permission;
    private Map<String, Integer> reqRegions;
    private Req req;

    public Requirement(List<Integer> cityLevels, Req req) {
        if (req == Req.CITY_LEVEL) {
            this.cityLevels = cityLevels;
            this.req = req;
        }
    }

    public Requirement(String permission, Req req) {
        if (req == Req.HAS_PERMISSION) {
            this.permission = permission;
            this.req = req;
        }
    }

    public Requirement(Map<String, Integer> reqRegions, Req req) {
        if (req == Req.HAS_REGIONS) {
            this.reqRegions = reqRegions;
            this.req = req;
        }
    }


    public boolean satisfy(Player player, Location location) {
        if (this.req == Req.HAS_PERMISSION) {
            return player.hasPermission(this.permission);
        } else if (this.req == Req.CITY_LEVEL) {
            CityRegion cityRegion = RegionManager.getCityByLocation(location);
            return (cityRegion != null && cityLevels.contains(cityRegion.getLevel()));
        } else if (this.req == Req.HAS_REGIONS) {
            Map<String, Integer> reqRegions = new HashMap<>(this.reqRegions);
            for (Region region : RegionManager.getPlayerRegions(player.getName())) {
                if (reqRegions.containsKey(region.getRegionName())) {
                    int newAmount = reqRegions.get(region.getRegionName()) - 1;
                    if (newAmount <= 0) reqRegions.remove(region.getRegionName());
                    else reqRegions.put(region.getRegionName(), newAmount);
                }
            }
            return reqRegions.size() <= 0;
        }
        return false;
    }


    public List<Integer> getCityLevels() {
        return cityLevels;
    }

    public void setCityLevels(List<Integer> cityLevels) {
        this.cityLevels = cityLevels;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public Map<String, Integer> getReqRegions() {
        return reqRegions;
    }

    public void setReqRegions(Map<String, Integer> reqRegions) {
        this.reqRegions = reqRegions;
    }

    public Req getReq() {
        return req;
    }

    public void setReq(Req req) {
        this.req = req;
    }

    public enum Req {
        CITY_LEVEL,
        HAS_REGIONS,
        HAS_PERMISSION
    }
}
