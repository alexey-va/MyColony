package ru.mcfine.mycolony.mycolony.listeners;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.regions.Region;
import ru.mcfine.mycolony.mycolony.regions.RegionGUI;
import ru.mcfine.mycolony.mycolony.regions.RegionManager;
import ru.mcfine.mycolony.mycolony.tasks.TickerRunnable;

public class OpenChest implements Listener {

    @EventHandler
    public void onOpenChest(InventoryOpenEvent event){
        if(event.getInventory().getLocation() == null || event.getInventory().getLocation().getBlock().getType() != Material.CHEST) return;
        Block block = event.getInventory().getLocation().getBlock();
        Region region = MyColony.regionManager.getRegion(block.getLocation());
        if(region == null) return;
        event.setCancelled(true);
        ChestGui gui = RegionGUI.getHomeGui(region, block);
        TickerRunnable.guis.put(event.getPlayer().getName(), gui);
        gui.show(event.getPlayer());
    }
}
