package ru.mcfine.mycolony.mycolony.city;

import javafx.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.config.MyConfig;
import ru.mcfine.mycolony.mycolony.regions.RegionManager;
import ru.mcfine.mycolony.mycolony.regions.RegionType;
import ru.mcfine.mycolony.mycolony.util.Utils;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.*;
import java.util.stream.Collectors;

public abstract class CityArea {

    private static Map<Player, BukkitTask> displayCityTasks = new HashMap<>();
    private static Map<Player, BukkitTask> displayInterCityTasks = new HashMap<>();

    public abstract boolean isInArea(Location location);

    public abstract boolean fitInArea(Pair<Location, Location> corners);

    public abstract Set<Chunk> getChunks();
    public abstract Set<BorderChunk> getBorderChunks();

    public Set<Location> getBorderLocations(Location location, int chunkRadius){
        Set<BorderChunk> chunks = this.getBorderChunks().stream().filter(bc ->
                Math.abs(location.getChunk().getX() - bc.getChunk().getX()) <= chunkRadius &&
                Math.abs(location.getChunk().getZ() - bc.getChunk().getZ()) <= chunkRadius).collect(Collectors.toSet());
        Set<Location> locations = new HashSet<>();
        for(BorderChunk borderChunk : chunks){
            locations.addAll(borderChunk.borderLocations(location.getY(), 8));
        }
        return locations;
    }

    public Set<Location> getChunkLocations(Location location, int chunkRadius){
        Set<Location> locations = new HashSet<>();
        Set<Chunk> nearbyChunks = this.getChunks().stream().filter( ch ->
            Math.abs(location.getChunk().getX() - ch.getX()) <= chunkRadius &&
            Math.abs(location.getChunk().getZ() - ch.getZ()) <= chunkRadius
        ).collect(Collectors.toSet());
        World world = location.getWorld();
        for(Chunk chunk : nearbyChunks){
            for(int i=0;i<=8;i++){
                for(int j=0;j<=8;j++){
                    locations.add(new Location(world, chunk.getX()*16+i*2, location.getY(), chunk.getZ()*16+j*2));
                }
            }
        }

        return locations;
    }

    public void showChunksNearLocation(Location location, Player player, int chunkRadius, ParticleEffect particleEffect, boolean intersect){
        if(intersect && displayInterCityTasks.get(player) != null && !displayInterCityTasks.get(player).isCancelled()){
            displayInterCityTasks.get(player).cancel();
        }
        if(!intersect && displayCityTasks.get(player) != null && !displayCityTasks.get(player).isCancelled()){
            displayCityTasks.get(player).cancel();
        }

        final int[] timer = {0};

        BukkitTask bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                if(timer[0] >= 20 || player == null){
                    this.cancel();
                    return;
                }
                Set<Location> locations = getChunkLocations(player.getLocation(), chunkRadius);
                locations.forEach( loc -> new ParticleBuilder(particleEffect, loc).setAmount(3).setSpeed(0f).setOffset(new Vector(0.1, 0.1, 0.1)).display(player));
                timer[0] ++;
            }
        }.runTaskTimerAsynchronously(MyColony.plugin, 0L, 10L);

        if(intersect){
            if(displayInterCityTasks.get(player) != null && !displayInterCityTasks.get(player).isCancelled()) displayInterCityTasks.get(player).cancel();
            displayInterCityTasks.put(player, bukkitTask);
        } else{
            if(displayCityTasks.get(player) != null && !displayCityTasks.get(player).isCancelled()) displayCityTasks.get(player).cancel();
            displayCityTasks.put(player, bukkitTask);
        }
    }

    public void showBorderNearLocation(Location location, Player player, int chunkRadius, ParticleEffect particleEffect, boolean intersect){
        if(intersect && displayInterCityTasks.get(player) != null && !displayInterCityTasks.get(player).isCancelled()){
            displayInterCityTasks.get(player).cancel();
        }
        if(!intersect && displayCityTasks.get(player) != null && !displayCityTasks.get(player).isCancelled()){
            displayCityTasks.get(player).cancel();
        }

        Set<Location> locations = getBorderLocations(location, chunkRadius);
        final int[] timer = {0};
        BukkitTask bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                if(timer[0] >= 20 || player == null){
                    this.cancel();
                    return;
                }
                locations.forEach( loc -> {
                    new ParticleBuilder(particleEffect, loc).setAmount(3).setSpeed(0f).setOffset(new Vector(0.1, 0.1, 0.1)).display(player);
                });
                timer[0] ++;
            }
        }.runTaskTimerAsynchronously(MyColony.plugin, 0L, 10L);

        if(intersect){
            if(displayInterCityTasks.get(player) != null && !displayInterCityTasks.get(player).isCancelled()) displayInterCityTasks.get(player).cancel();
            displayInterCityTasks.put(player, bukkitTask);
        } else{
            if(displayCityTasks.get(player) != null && !displayCityTasks.get(player).isCancelled()) displayCityTasks.get(player).cancel();
            displayCityTasks.put(player, bukkitTask);
        }

    }

    public boolean intersects(CityArea area){

        if(this instanceof SquareArea && area instanceof SquareArea) {
            Location loc1 = ((SquareArea) this).getCenter();
            Location loc2 = ((SquareArea) area).getCenter();

            int radius1 = ((SquareArea) this).getChunkRadius();
            int radius2 = ((SquareArea) area).getChunkRadius();

            return Math.abs(loc1.getChunk().getZ() - loc2.getChunk().getZ()) - radius1 - radius2 <= 0 &&
                    Math.abs(loc1.getChunk().getX() - loc2.getChunk().getX()) - radius1 - radius2 <= 0;
        } else {
            Set<Chunk> chunks1 = this.getChunks();
            Set<Chunk> chunks2 = new HashSet<>(area.getChunks());
            boolean inter = false;
            for (Chunk chunk : chunks1) {
                if (chunks2.contains(chunk)) {
                    inter = true;
                    break;
                }
            }
            return inter;
        }
    }

    public CityArea whichIntersects(){
        for(CityRegion cityRegion : RegionManager.getCityRegions()){
            //System.out.println("City regions: "+cityRegion);
            if(cityRegion.getCityArea().equals(this)) continue;
            if(cityRegion.getCityArea().intersects(this)) return cityRegion.getCityArea();
        }
        return null;
    }

    public static CityArea getAreaForCity(RegionType regionType, Location location){
        if(!regionType.isCity()) return null;
        if(MyColony.plugin.useLands){
            System.out.println(MyColony.plugin.landsHook.getLand(location));
            if(MyColony.plugin.landsHook.getLand(location) == null) return null;
            return new LandsArea(location);
        } else{
            return new SquareArea(regionType.getBaseChunkRadius(), location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        }
    }

    public static void clearCityBordersVis(Player player){
        if(displayInterCityTasks.get(player) != null && !displayInterCityTasks.get(player).isCancelled()){
            displayInterCityTasks.get(player).cancel();
            displayInterCityTasks.remove(player);
        }
        if(displayCityTasks.get(player) != null && !displayCityTasks.get(player).isCancelled()){
            displayCityTasks.get(player).cancel();
            displayCityTasks.remove(player);
        }
    }



}
