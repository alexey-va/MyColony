package ru.mcfine.mycolony.mycolony.city;

import javafx.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;

public class SquareArea extends CityArea{

    private final int chunkRadius;
    private String worldName;
    private int x, y, z;
    private final Location center;
    Set<Chunk> chunks = new HashSet<>();
    Set<BorderChunk> borderChunks = new HashSet<>();

    public SquareArea(int chunkRadius, String worldName, int x, int y, int z) {
        this.chunkRadius = chunkRadius;
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.center = new Location(Bukkit.getWorld(worldName), x, y, z);

        this.chunks = generateChunks();
        this.borderChunks = generateBorderChunks();
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

    @Override
    public Set<Chunk> getChunks(){
        return chunks;
    }

    public Set<Chunk> generateChunks(){
        if(this.center == null)return null;
        Chunk centerChunk = this.center.getChunk();
        Set<Chunk> chunks = new HashSet<>();
        for(int i=-chunkRadius; i<=chunkRadius;i++){
            for(int j=-chunkRadius; j<=chunkRadius;j++){
                Chunk chunk = centerChunk.getWorld().getChunkAt(centerChunk.getX() + i, centerChunk.getZ()+j);
                chunks.add(chunk);
            }
        }
        return chunks;
    }


    @Override
    public Set<BorderChunk> getBorderChunks(){
        return this.borderChunks;
    }

    public Set<BorderChunk> generateBorderChunks() {
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
