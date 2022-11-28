package ru.mcfine.mycolony.mycolony.listeners;

import io.papermc.paper.event.block.BlockBreakBlockEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import ru.mcfine.mycolony.mycolony.MyColony;

import java.util.List;

public class BreakChest implements Listener {

    @EventHandler
    public void onBreakChest(BlockBreakEvent event){
        if(event.getBlock().getType() != Material.CHEST) return;
        if(!MyColony.regionManager.isRegionBlock(event.getBlock())) return;
        MyColony.regionManager.removeRegion(event.getBlock().getLocation());
        System.out.println("Region removed");
    }

}
