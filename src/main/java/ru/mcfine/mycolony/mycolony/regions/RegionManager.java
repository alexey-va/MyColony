package ru.mcfine.mycolony.mycolony.regions;

import it.unimi.dsi.fastutil.Hash;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashMap;

public class RegionManager {

    private HashMap<String, RegionType> regionTypes = new HashMap<>();

    private HashMap<Chunk, HashMap<Location,Region>> regionMap = new HashMap<>();

    public RegionManager(HashMap<Chunk, HashMap<Location, Region>> regionMap) {
        this.regionMap = regionMap;
    }

    public RegionManager(){

        //RegionType regionType = new RegionType();

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
    }

    public void removeRegion(Location location){
        HashMap<Location, Region> regions = regionMap.get(location.getChunk());
        regions.remove(location);
    }

    public HashMap<Chunk, HashMap<Location, Region>> getRegionMap() {
        return regionMap;
    }

    public boolean isRegionBlock(Block block){
        HashMap<Location, Region> regions = regionMap.get(block.getLocation().getChunk());
        if(regions == null) return false;
        return regions.get(block.getLocation()) != null;
    }
}
