package ru.mcfine.mycolony.mycolony.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.regions.Region;
import ru.mcfine.mycolony.mycolony.guis.RegionGui;

public class OpenChest implements Listener {

    @EventHandler
    public void onOpenChest(PlayerInteractEvent event){
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.CHEST) return;
        Block block = event.getClickedBlock();
        Region region = MyColony.regionManager.getRegion(block.getLocation());
        if(region == null) return;
        event.setCancelled(true);
        if(!region.canOpen(event.getPlayer())) return;
        RegionGui gui = new RegionGui(block, region, event.getPlayer());
        gui.show(event.getPlayer());

    }
}
