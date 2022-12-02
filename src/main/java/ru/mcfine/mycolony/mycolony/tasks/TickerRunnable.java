package ru.mcfine.mycolony.mycolony.tasks;

import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.regions.BuildGui;
import ru.mcfine.mycolony.mycolony.regions.GroupItem;
import ru.mcfine.mycolony.mycolony.regions.Region;
import ru.mcfine.mycolony.mycolony.regions.RegionGui;

import java.util.HashMap;
import java.util.Map;

public class TickerRunnable extends BukkitRunnable {

    private double increment = 1;

    public TickerRunnable(double increment){
        this.increment = increment;
    }

    public static HashMap<RegionGui, Region> mainMenuGuis = new HashMap<>();

    @Override
    public void run() {
        HashMap<Chunk, HashMap<Location, Region>> regions = MyColony.regionManager.getRegionMap();
        for(Map.Entry<Chunk, HashMap<Location, Region>> entry : regions.entrySet()){
            if(entry == null|| entry.getValue() == null || entry.getKey() == null || !entry.getKey().isLoaded()) continue;
            for(Region region : entry.getValue().values()){
                region.incrementTime(increment);
            }
        }

        for(Map.Entry<RegionGui, Region> entry : mainMenuGuis.entrySet()){
            if(entry.getKey() == null) continue;
            ItemStack item = entry.getKey().clockItem.getItem();
            ItemMeta meta = item.getItemMeta();
            Region region = entry.getValue();
            meta.setDisplayName(Color.ORANGE+"Next cycle in: "+ Color.GREEN+Math.ceil(region.getMaxTime() - region.getTimeElapsed())+"s");
            item.setItemMeta(meta);
            entry.getKey().update();
        }

        for(BuildGui buildGui : BuildGui.buildGuis){
            boolean toUpdate = false;
            for(GroupItem groupItem : buildGui.getGroupItems()){
                if(groupItem.isGroup) toUpdate = true;
                groupItem.setNext();
            }
            if(toUpdate) {
                buildGui.update();
            }
        }
    }

    private void updateGui(){

    }
}
