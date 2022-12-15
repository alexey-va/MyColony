package ru.mcfine.mycolony.mycolony.compat;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import javafx.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.city.CityArea;
import ru.mcfine.mycolony.mycolony.city.SquareArea;

import java.util.List;
import java.util.Set;

public class ColonyProtection {

    private WgHook wgHook = null;


    public ColonyProtection(){
        if(Bukkit.getPluginManager().isPluginEnabled("WorldGuard") && !MyColony.plugin.useLands){
            wgHook = new WgHook();
        }
    }

    public boolean ifAreaExists(String name, org.bukkit.World world){
        if(wgHook != null){
            return wgHook.exists(world, name);
        }
        return false;
    }

    public void createCityArea(CityArea cityArea, String name, int priority, Set<String> members, Set<String> owners){
        if(cityArea instanceof SquareArea && wgHook != null){
            wgHook.createCityArea(cityArea, name, priority, members, owners);
        }
    }

    public boolean ifIntersects(Pair<Location, Location> corners, Player player, int priority){
        if(wgHook != null){
            wgHook.intersects(corners, player, priority);
        }
        return false;
    }

    public void removeProtectionArea(String name, org.bukkit.World world){
        if(wgHook != null) {
            wgHook.removeArea(world, name);
        }
    }

    public boolean ifIntersects(SquareArea squareArea, Player player, int priority){
        if(wgHook != null){
            return wgHook.intersects(squareArea, player, priority);
        }
        return false;
    }

    public void addRegion(Pair<Location, Location> corners, Player player, String regionName, int priority,
                          Set<String> playerNamesMembers, Set<String> playerNamesOwners){
        if(wgHook != null){
            wgHook.addArea(corners, regionName, priority, playerNamesMembers, playerNamesOwners);
        }
    }

}
