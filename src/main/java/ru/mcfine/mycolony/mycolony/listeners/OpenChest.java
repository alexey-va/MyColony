package ru.mcfine.mycolony.mycolony.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.regions.Region;
import ru.mcfine.mycolony.mycolony.regions.RegionGUI;
import ru.mcfine.mycolony.mycolony.regions.RegionManager;

public class OpenChest implements Listener {

    @EventHandler
    public void onOpenChest(InventoryOpenEvent event){
        if(event.getInventory().getLocation() == null || event.getInventory().getLocation().getBlock().getType() != Material.CHEST) return;
        Block block = event.getInventory().getLocation().getBlock();
        Region region = MyColony.regionManager.getRegion(block.getLocation());
        if(region == null) return;
        event.setCancelled(true);
        RegionGUI.getHomeGui(region, event.getInventory()).show(event.getPlayer());
    }
}
