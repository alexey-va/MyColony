package ru.mcfine.mycolony.mycolony.city;

import javafx.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class SquareArea extends CityArea{

    private int chunkRadius;
    private String worldName;
    private int x, y, z;
    private Location center;

    public SquareArea(int chunkRadius, String worldName, int x, int y, int z) {
        this.chunkRadius = chunkRadius;
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.center = new Location(Bukkit.getWorld(worldName), x, y, z);
    }

    @Override
    public boolean isInArea(Location location) {
        Chunk chunk = location.getChunk();
        Chunk cityChunk = this.center.getChunk();

        int xx = chunk.getX() - cityChunk.getX();
        int yy = chunk.getZ() - cityChunk.getZ();

        return (Math.abs(xx) + Math.abs(yy) <= chunkRadius);
    }

    @Override
    public boolean fitInArea(Pair<Location, Location> corners) {
        return (isInArea(corners.getKey()) && isInArea(corners.getValue()));
    }

    public List<Chunk> getChunks(){
        if(this.center == null)return null;
        Chunk centerChunk = this.center.getChunk();
        List<Chunk> chunks = new ArrayList<>();
        for(int i=-chunkRadius; i<=chunkRadius;i++){
            for(int j=-chunkRadius; j<=chunkRadius;j++){
                Chunk chunk = centerChunk.getWorld().getChunkAt(centerChunk.getX() + i, centerChunk.getZ()+j);
                chunks.add(chunk);
            }
        }
        return chunks;
    }


    public int getChunkRadius() {
        return chunkRadius;
    }

    public Location getCenter() {
        return center;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }
}
