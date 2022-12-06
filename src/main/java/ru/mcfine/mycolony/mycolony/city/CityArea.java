package ru.mcfine.mycolony.mycolony.city;

import javafx.util.Pair;
import org.bukkit.Chunk;
import org.bukkit.Location;
import ru.mcfine.mycolony.mycolony.regions.RegionManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class CityArea {

    public abstract boolean isInArea(Location location);

    public abstract boolean fitInArea(Pair<Location, Location> corners);

    public abstract List<Chunk> getChunks();

    public boolean intersects(CityArea area){
        List<Chunk> chunks1 = this.getChunks();
        Set<Chunk> chunks2 = new HashSet<>(area.getChunks());
        boolean inter = false;
        for(Chunk chunk : chunks1){
            if (chunks2.contains(chunk)) {
                inter = true;
                break;
            }
        }
        return inter;
    }

    public boolean intersectAny(){
        for(CityRegion cityRegion : RegionManager.getCityRegions()){
            if(cityRegion.getCityArea().equals(this)) continue;
            if(cityRegion.getCityArea().intersects(this)) return true;
        }
        return false;
    }



}
