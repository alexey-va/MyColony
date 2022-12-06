package ru.mcfine.mycolony.mycolony.regions;

import com.jeff_media.customblockdata.CustomBlockData;
import it.unimi.dsi.fastutil.Hash;
import me.angeschossen.lands.api.land.Land;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.city.CityArea;
import ru.mcfine.mycolony.mycolony.city.CityRegion;
import ru.mcfine.mycolony.mycolony.city.LandsArea;
import ru.mcfine.mycolony.mycolony.city.SquareArea;
import ru.mcfine.mycolony.mycolony.config.MyConfig;
import ru.mcfine.mycolony.mycolony.util.JsonStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegionManager {


    private HashMap<Chunk, HashMap<Location,Region>> regionMap = new HashMap<>();
    private static HashMap<String, List<Region>> playerRegions = new HashMap<>();
    private static List<CityRegion> cityRegions = new ArrayList<>();
    public RegionManager(HashMap<Chunk, HashMap<Location, Region>> regionMap) {
        this.regionMap = regionMap;
    }

    public RegionManager(){
    }

    public HashMap<Location, Region> getRegions(Chunk chunk){
        return regionMap.get(chunk);
    }

    public Region getRegion(Location location){
        HashMap<Location, Region> regions = regionMap.get(location.getChunk());
        if(regions == null) return null;
        return regions.get(location);
    }

    public void addRegion(Location location, Region region){
        HashMap<Location, Region> regionHashMap = regionMap.get(location.getChunk());
        if(regionHashMap == null) regionHashMap = new HashMap<>();
        if(regionHashMap.containsKey(location)){
            System.out.println("Same location");
            return;
        }
        regionHashMap.put(location, region);
        regionMap.put(location.getChunk(), regionHashMap);

        if(region instanceof CityRegion cityRegion) cityRegions.add(cityRegion);

        for(String playerName : region.getPlayerNames()){
            List<Region> reg = playerRegions.get(playerName);
            if(reg == null) reg = new ArrayList<>();
            if(!reg.contains(region)) reg.add(region);
            playerRegions.put(playerName, reg);
        }

        CustomBlockData container = new CustomBlockData(location.getBlock(), MyColony.plugin);
        container.set(new NamespacedKey(MyColony.plugin, "region_name"), PersistentDataType.STRING, region.getRegionName());
        container.set(new NamespacedKey(MyColony.plugin, "region_id"), PersistentDataType.STRING, region.getUuid());
    }

    public void removeRegion(Location location){
        HashMap<Location, Region> regions = regionMap.get(location.getChunk());
        Region region = regions.get(location);
        if(region instanceof CityRegion){
            cityRegions.remove(region);
        }
        regions.remove(location);
        if(regions.size() == 0){
            regionMap.remove(location.getChunk());
        }else regionMap.put(location.getChunk(), regions);

        CustomBlockData container = new CustomBlockData(location.getBlock(), MyColony.plugin);
        container.clear();
    }

    public HashMap<Chunk, HashMap<Location, Region>> getRegionMap() {
        return regionMap;
    }

    public boolean isRegionBlock(Block block){
        HashMap<Location, Region> regions = regionMap.get(block.getLocation().getChunk());
        if(regions == null) return false;
        return regions.get(block.getLocation()) != null;
    }

    public void uploadRegions(ArrayList<RegionMock> fromJson) {
        HashMap<Chunk, HashMap<Location,Region>> regions = new HashMap<>();
        System.out.println("Uploading regions");
        if(fromJson == null){
            this.regionMap = regions;
            return;
        }
        for(RegionMock regionMock : fromJson){
            World world = MyColony.plugin.getServer().getWorld(regionMock.worldName);
            if(world == null){
                MyColony.plugin.getLogger().severe("World "+regionMock.worldName+" not found!");
                continue;
            }
            Location location = new Location(world, regionMock.x, regionMock.y, regionMock.z);
            //Chunk chunk = location.getChunk();
            RegionType regionType = MyColony.plugin.config.getRegionType(regionMock.regionName);
            System.out.println(regionMock.regionName);

            if(regionType == null){
                MyColony.plugin.getLogger().severe("Region type "+regionMock.regionName+" not found!");
                continue;
            }

            Region region;
            if(regionType.isCity()){

                CityArea cityArea = null;
                if(regionMock.chunkRadius != -1){
                    cityArea = new SquareArea(regionMock.chunkRadius, regionMock.worldName, regionMock.x, regionMock.y, regionMock.z);
                }

                region = new CityRegion(regionMock.playerNames, regionMock.level, regionMock.x, regionMock.y, regionMock.z,
                        regionMock.regionName, regionMock.worldName, regionMock.playerUUIDs, regionType, regionMock.uuid,
                        cityArea, regionMock.members, regionMock.population);
            } else{
                region = new Region(regionMock.playerNames, regionMock.level, regionMock.x, regionMock.y, regionMock.z,
                        regionMock.regionName, regionMock.worldName, regionMock.playerUUIDs, regionType, regionMock.uuid);
            }
            System.out.println(region);
            addRegion(location, region);

        }
    }

    public ArrayList<RegionMock> getRegionsMock(){
        ArrayList<RegionMock> mockList = new ArrayList<>();
        for(HashMap<Location, Region> temp : regionMap.values()){
            for(Region region : temp.values()){
                RegionMock mock = new RegionMock(region);
                mockList.add(mock);
            }
        }
        return mockList;
    }

    public static CityRegion getCityByLocation(Location location){
        for(CityRegion cityRegion : cityRegions){
            if(cityRegion.getCityArea().isInArea(location)) return cityRegion;
        }
        return null;
    }

    public static List<Region> getPlayerRegions(String playerName){
        return playerRegions.get(playerName);
    }

    public static List<CityRegion> getCityRegions(){ return cityRegions;}
}
