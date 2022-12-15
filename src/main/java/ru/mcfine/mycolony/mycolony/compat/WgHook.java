package ru.mcfine.mycolony.mycolony.compat;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import javafx.util.Pair;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.mcfine.mycolony.mycolony.city.CityArea;
import ru.mcfine.mycolony.mycolony.city.SquareArea;

import java.util.List;
import java.util.Set;

public class WgHook {

    public RegionContainer container;

    public WgHook(){
        container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    }

    public boolean intersects(SquareArea squareArea, Player player, int priority){
        World wgWorld = BukkitAdapter.adapt(squareArea.getCenter().getWorld());
        RegionManager manager = this.container.get(wgWorld);

        if(manager == null){
            System.out.println("No regions in this world...");
            return false;
        }
        Chunk centerChunk = squareArea.getCenter().getChunk();
        int x1 = (centerChunk.getX()- squareArea.getChunkRadius())*16;
        int x2 = (centerChunk.getX()+ squareArea.getChunkRadius())*16 + 15;

        int z1 = (centerChunk.getZ()- squareArea.getChunkRadius())*16;
        int z2 = (centerChunk.getZ()+ squareArea.getChunkRadius())*16 +15;

        BlockVector3 min = BlockVector3.at(x1, wgWorld.getMinY(), z1);
        BlockVector3 max = BlockVector3.at(x2, wgWorld.getMaxY(), z2);
        ProtectedRegion region = new ProtectedCuboidRegion("dummy", min, max);

        List<ProtectedRegion> inter = region.getIntersectingRegions(manager.getRegions().values());

        boolean intersects = false;
        for(ProtectedRegion protectedRegion : inter){
            if(!protectedRegion.getMembers().contains(player.getName()) && !protectedRegion.getMembers().contains(player.getUniqueId())){
                intersects = true;
                break;
            } else if(protectedRegion.getPriority() >= priority){
                intersects = true;
                break;
            }
        }

        return intersects;
    }

    public boolean exists(org.bukkit.World world, String name){
        World wgWorld = BukkitAdapter.adapt(world);
        RegionManager manager = this.container.get(wgWorld);
        if(manager == null) return false;
        return manager.getRegion(name) != null;
    }

    public void createCityArea(CityArea cityArea, String name, int priority, Set<String> members, Set<String> owners){
        World wgWorld = BukkitAdapter.adapt(((SquareArea) cityArea).getCenter().getWorld());
        RegionManager manager = this.container.get(wgWorld);
        if(manager == null) return;

        Chunk centerChunk = ((SquareArea) cityArea).getCenter().getChunk();
        int x1 = (centerChunk.getX()- ((SquareArea) cityArea).getChunkRadius())*16;
        int x2 = (centerChunk.getX()+ ((SquareArea) cityArea).getChunkRadius())*16 + 15;

        int z1 = (centerChunk.getZ()- ((SquareArea) cityArea).getChunkRadius())*16;
        int z2 = (centerChunk.getZ()+ ((SquareArea) cityArea).getChunkRadius())*16 +15;

        BlockVector3 min = BlockVector3.at(x1, wgWorld.getMinY(), z1);
        BlockVector3 max = BlockVector3.at(x2, wgWorld.getMaxY(), z2);

        ProtectedRegion region = new ProtectedCuboidRegion(name, min, max);
        region.setPriority(priority);
        DefaultDomain membersDomain = region.getMembers();
        if(members != null) members.forEach(membersDomain::addPlayer);
        DefaultDomain ownersDomain = region.getMembers();
        if(owners != null) owners.forEach(ownersDomain::addPlayer);

        region.setMembers(membersDomain);
        region.setOwners(ownersDomain);
        manager.addRegion(region);
    }

    public boolean intersects(Pair<Location, Location> corners, Player player, int priority){
        World wgWorld = BukkitAdapter.adapt(corners.getKey().getWorld());
        RegionManager manager = this.container.get(wgWorld);

        if(manager == null){
            System.out.println("No regions in this world...");
            return false;
        }

        BlockVector3 min = BlockVector3.at(corners.getKey().getBlockX(), corners.getKey().getBlockY(), corners.getKey().getBlockZ());
        BlockVector3 max = BlockVector3.at(corners.getValue().getBlockX(), corners.getValue().getBlockY(), corners.getValue().getBlockZ());
        ProtectedRegion region = new ProtectedCuboidRegion("dummy", min, max);

        List<ProtectedRegion> inter = region.getIntersectingRegions(manager.getRegions().values());

        boolean intersects = false;
        for(ProtectedRegion protectedRegion : inter){
            if(!protectedRegion.getMembers().contains(player.getName()) && !protectedRegion.getMembers().contains(player.getUniqueId())){
                intersects = true;
                break;
            } else if(protectedRegion.getPriority() >= priority){
                intersects = true;
                break;
            }
        }

        return intersects;
    }

    public void removeArea(org.bukkit.World world, String name){
        World wgWorld = BukkitAdapter.adapt(world);
        RegionManager manager = this.container.get(wgWorld);
        if(manager == null){
            System.out.println("No regions in this world...");
            return;
        }
        manager.removeRegion(name);
    }

    public void addArea(Pair<Location, Location> corners, String regionName, int priority, Set<String> playerNamesMembers, Set<String> playerNamesOwners){
        World wgWorld = BukkitAdapter.adapt(corners.getKey().getWorld());
        RegionManager manager = this.container.get(wgWorld);
        if(manager == null){
            System.out.println("No regions in this world...");
            return;
        }
        BlockVector3 min = BlockVector3.at(corners.getKey().getBlockX(), corners.getKey().getBlockY(), corners.getKey().getBlockZ());
        BlockVector3 max = BlockVector3.at(corners.getValue().getBlockX(), corners.getValue().getBlockY(), corners.getValue().getBlockZ());
        ProtectedRegion region = new ProtectedCuboidRegion(regionName, min, max);
        region.setPriority(priority);

        DefaultDomain members = region.getMembers();
        if(playerNamesMembers != null) playerNamesMembers.forEach(members::addPlayer);
        DefaultDomain owners = region.getOwners();
        if(playerNamesOwners != null) playerNamesOwners.forEach(members::addPlayer);

        region.setOwners(owners);
        region.setMembers(members);

        manager.addRegion(region);
    }

}
