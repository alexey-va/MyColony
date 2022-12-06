package ru.mcfine.mycolony.mycolony.city;

import javafx.util.Pair;
import me.angeschossen.lands.api.land.ChunkCoordinate;
import me.angeschossen.lands.api.land.Land;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import ru.mcfine.mycolony.mycolony.MyColony;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    public List<Chunk> getChunks() {
        Collection<ChunkCoordinate> chunks = this.land.getChunks(world);
        List<Chunk> result = new ArrayList<>();
        for(ChunkCoordinate chunkCoordinate : chunks){
            result.add(world.getChunkAt(chunkCoordinate.getX(), chunkCoordinate.getZ()));
        }
        return result;
    }
}
