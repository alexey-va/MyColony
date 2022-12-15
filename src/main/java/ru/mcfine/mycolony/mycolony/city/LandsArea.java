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

    private Land land;
    private Location center;
    private World world;

    public LandsArea(Location location){
        this.land = MyColony.plugin.landsHook.getLand(location);
        this.center = location;
        this.world = location.getWorld();
    }

    @Override
    public boolean isInArea(Location location) {
        return this.land.hasChunk(location.getWorld(), location.getChunk().getX(), location.getChunk().getZ());
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
        if(chunks == null) return result;
        for(ChunkCoordinate chunkCoordinate : chunks){
            result.add(world.getChunkAt(chunkCoordinate.getX(), chunkCoordinate.getZ()));
        }
        return result;
    }

    @Override
    public Set<BorderChunk> getBorderChunks() {
        Set<Chunk> chunks = getChunks();
        Set<BorderChunk> result = new HashSet<>();
        for(Chunk chunk : chunks){
            boolean xUp =  !chunks.contains(chunk.getWorld().getChunkAt(chunk.getX()+1, chunk.getZ()));
            boolean xDown = !chunks.contains(chunk.getWorld().getChunkAt(chunk.getX()-1, chunk.getZ()));
            boolean zUp = !chunks.contains(chunk.getWorld().getChunkAt(chunk.getX(), chunk.getZ()+1));
            boolean zDown = !chunks.contains(chunk.getWorld().getChunkAt(chunk.getX(), chunk.getZ()-1));

            if(xUp || xDown || zUp || zDown){
                BorderChunk borderChunk = new BorderChunk(chunk, xUp, xDown, zUp, zDown );
                result.add(borderChunk);
            }
        }
        return result;
    }

    public Land getLand() {
        return land;
    }

    public Location getCenter() {
        return center;
    }

    public World getWorld() {
        return world;
    }
}
