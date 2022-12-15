package ru.mcfine.mycolony.mycolony.regions;

import com.jeff_media.customblockdata.CustomBlockData;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataType;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.city.CityArea;
import ru.mcfine.mycolony.mycolony.city.CityRegion;
import ru.mcfine.mycolony.mycolony.city.LandsArea;
import ru.mcfine.mycolony.mycolony.city.SquareArea;
import ru.mcfine.mycolony.mycolony.players.ColonyPlayer;
import ru.mcfine.mycolony.mycolony.players.PlayerMock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegionManager {


    private Map<Chunk, HashMap<Location,Region>> regionMap = new HashMap<>();
    public static Map<String, ColonyPlayer> colonyPlayers = new HashMap<>();
    public static List<CityRegion> cityRegions = new ArrayList<>();
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
            if(colonyPlayers.get(playerName) == null){
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
                colonyPlayers.put(playerName, new ColonyPlayer(playerName, offlinePlayer.getUniqueId().toString(), new ArrayList<>()));
            }
            colonyPlayers.get(playerName).getRegions().add(region);
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

    public Map<Chunk, HashMap<Location, Region>> getRegionMap() {
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
            //System.out.println(regionMock.regionName);

            if(regionType == null){
                MyColony.plugin.getLogger().severe("Region type "+regionMock.regionName+" not found!");
                continue;
            }

            Region region;
            if(regionType.isCity()){

                CityArea cityArea = null;
                if(regionMock.chunkRadius != -1 && !MyColony.plugin.useLands){
                    cityArea = new SquareArea(regionMock.chunkRadius, regionMock.worldName, regionMock.x, regionMock.y, regionMock.z);
                } else{
                    cityArea = new LandsArea(location);
                    System.out.println(cityArea);
                }

                region = new CityRegion(regionMock.playerNames, regionMock.level, regionMock.x, regionMock.y, regionMock.z,
                        regionMock.regionName, regionMock.worldName, regionMock.playerUUIDs, regionType, regionMock.uuid, regionMock.wgName,
                        cityArea, regionMock.members, regionMock.cityWgName, regionMock.ownerName);
            } else{
                region = new Region(regionMock.playerNames, regionMock.level, regionMock.x, regionMock.y, regionMock.z,
                        regionMock.regionName, regionMock.worldName, regionMock.playerUUIDs, regionType, regionMock.uuid, regionMock.wgName, null);
            }
            region.setTimeSinceCreation(regionMock.timeSinceCreation);
            region.setBankDeposit(regionMock.bankDeposit);
            addRegion(location, region);

        }
        for(Region region : getAllRegions()){
            CityRegion cityRegion = RegionManager.getCityByLocation(region.getLocation());
            region.setCityRegion(cityRegion);
        }
    }

    public List<PlayerMock> getPlayerMock(){
        List<PlayerMock> playerMocks = new ArrayList<>();
        for(var temp : colonyPlayers.values()){
            playerMocks.add(new PlayerMock(temp));
        }
        return playerMocks;
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

    public List<Region> getAllRegions(){
        List<Region> regions = new ArrayList<>();
        for(var hashMap : regionMap.values()){
            if(hashMap == null) continue;
            regions.addAll(hashMap.values());
        }
        return regions;
    }

    public static CityRegion getCityByLocation(Location location){
        for(CityRegion cityRegion : cityRegions){
            if(cityRegion.getCityArea().isInArea(location)) return cityRegion;
        }
        return null;
    }

    public static List<Region> getPlayerRegions(String playerName){
        return colonyPlayers.get(playerName).getRegions();
    }

    public static List<CityRegion> getCityRegions(){ return cityRegions;}

    public void uploadPlayers(List<PlayerMock> fromJson) {
        if(fromJson == null) return;
        for(PlayerMock playerMock : fromJson){
            ColonyPlayer colonyPlayer = new ColonyPlayer(playerMock);
            colonyPlayers.put(colonyPlayer.getPlayerName(), colonyPlayer);
        }
    }
}
