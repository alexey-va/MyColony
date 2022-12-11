package ru.mcfine.mycolony.mycolony.city;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class BorderChunk {

    private Chunk chunk;
    private boolean xUp;
    private boolean xDown;
    private boolean zUp;
    private boolean zDown;

    public BorderChunk(Chunk chunk, boolean xUp, boolean xDown, boolean zUp, boolean zDown) {
        this.chunk = chunk;
        this.xUp = xUp;
        this.xDown = xDown;
        this.zUp = zUp;
        this.zDown = zDown;
    }

    public Set<Location> borderLocations(double y, int amount){
        double step = 16.0/amount;
        if(amount < 0) return null;
        Set<Location> locations = new HashSet<>();
        double x0 = this.chunk.getX()*16;
        double z0 = this.chunk.getZ()*16;
        World world = this.chunk.getWorld();
        for(double add = 0; add <= 16; add+=step){
            if(xUp){
                locations.add(new Location(world, x0+16, y, z0+add));
            }
            if(xDown){
                locations.add(new Location(world, x0, y, z0+add));
            }
            if(zUp){
                locations.add(new Location(world, x0+add, y, z0+16));
            }
            if(zDown){
                locations.add(new Location(world, x0+add, y, z0));
            }
        }
        return locations;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public boolean isxUp() {
        return xUp;
    }

    public boolean iszUp() {
        return zUp;
    }

    public boolean isxDown() {
        return xDown;
    }

    public boolean iszDown() {
        return zDown;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BorderChunk that = (BorderChunk) o;
        return xUp == that.xUp && xDown == that.xDown && zUp == that.zUp && zDown == that.zDown && chunk.equals(that.chunk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chunk, xUp, xDown, zUp, zDown);
    }
}
