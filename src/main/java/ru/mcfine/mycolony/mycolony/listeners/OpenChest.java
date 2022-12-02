package ru.mcfine.mycolony.mycolony.listeners;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.regions.Region;
import ru.mcfine.mycolony.mycolony.regions.RegionGUI;
import ru.mcfine.mycolony.mycolony.regions.RegionManager;
import ru.mcfine.mycolony.mycolony.tasks.TickerRunnable;

public class OpenChest implements Listener {

    @EventHandler
    public void onOpenChest(PlayerInteractEvent event){
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.CHEST) return;
        Block block = event.getClickedBlock();
        Region region = MyColony.regionManager.getRegion(block.getLocation());
        if(region == null) return;
        event.setCancelled(true);
        RegionGUI gui = new RegionGUI(block, region);
        TickerRunnable.mainMenuGuis.put(gui, region);
        gui.show(event.getPlayer());

    }
}
