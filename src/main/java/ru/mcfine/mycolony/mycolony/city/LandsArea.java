package ru.mcfine.mycolony.mycolony.city;

import javafx.util.Pair;
import me.angeschossen.lands.api.land.ChunkCoordinate;
import me.angeschossen.lands.api.land.Land;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import ru.mcfine.mycolony.mycolony.MyColony;

import java.util.*;

public class LandsArea extends CityArea{

    public Land land;
    public Location center;
    public World world;

    public LandsArea(Location location){
        this.land = MyColony.plugin.landsHook.getLand(location);
        this.center = location;
        this.world = location.getWorld();
    }

    @Override
    public boolean isInArea(Location location) {
        return MyColony.plugin.landsHook.getLand(location) != null;
    }

    @Override
    public boolean fitInArea(Pair<Location, Location> corners) {
        return (MyColony.plugin.landsHook.getLand(corners.getValue()) == this.land &&
                MyColony.plugin.landsHook.getLand(corners.getKey()) == this.land);
    }

    @Override
    public Set<Chunk> getChunks() {
        Collection<ChunkCoordinate> chunks = this.land.getChunks(world);
        Set<Chunk> result = new HashSet<>();
        for(ChunkCoordinate chunkCoordinate : chunks){
            result.add(world.getChunkAt(chunkCoordinate.getX(), chunkCoordinate.getZ()));
        }
        return result;
    }

    @Override
    public Set<BorderChunk> getBorderChunks() {
        return null;
    }
}
