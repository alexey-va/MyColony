package ru.mcfine.mycolony.mycolony.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.city.CityRegion;
import ru.mcfine.mycolony.mycolony.regions.Region;

public class BreakChest implements Listener {

    @EventHandler
    public void onBreakChest(BlockBreakEvent event){
        if(event.getBlock().getType() != Material.CHEST) return;
        if(!MyColony.regionManager.isRegionBlock(event.getBlock())){
            return;
        }
        Region region = MyColony.regionManager.getRegion(event.getBlock().getLocation());
        if(!region.canDestroy(event.getPlayer())){
            event.setCancelled(true);
            return;
        }


        MyColony.protection.removeProtectionArea(region.getWgRegionName(), region.getLocation().getWorld());
        if(region instanceof CityRegion cityRegion) MyColony.protection.removeProtectionArea(cityRegion.getCityWgName(), region.getLocation().getWorld());
        MyColony.regionManager.removeRegion(event.getBlock().getLocation());

        new BukkitRunnable() {
            @Override
            public void run() {
                MyColony.plugin.getJsonStorage().saveRegions();
                MyColony.plugin.getJsonStorage().savePlayers();
            }
        }.runTaskAsynchronously(MyColony.plugin);
    }

}
