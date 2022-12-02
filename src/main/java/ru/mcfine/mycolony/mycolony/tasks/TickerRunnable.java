package ru.mcfine.mycolony.mycolony.tasks;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import javafx.util.Pair;
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
import ru.mcfine.mycolony.mycolony.regions.RegionGUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TickerRunnable extends BukkitRunnable {

    private double increment = 1;

    public TickerRunnable(double increment){
        this.increment = increment;
    }

    public static HashMap<RegionGUI, Region> mainMenuGuis = new HashMap<>();

    @Override
    public void run() {
        HashMap<Chunk, HashMap<Location, Region>> regions = MyColony.regionManager.getRegionMap();
        for(Map.Entry<Chunk, HashMap<Location, Region>> entry : regions.entrySet()){
            if(entry == null|| entry.getValue() == null || entry.getKey() == null || !entry.getKey().isLoaded()) continue;
            for(Region region : entry.getValue().values()){
                region.incrementTime(increment);
            }
        }

        for(Map.Entry<RegionGUI, Region> entry : mainMenuGuis.entrySet()){
            if(entry.getKey() == null) continue;
            ItemStack item = entry.getKey().clockItem.getItem();
            ItemMeta meta = item.getItemMeta();
            Region region = entry.getValue();
            meta.setDisplayName(Color.ORANGE+"Next cycle in: "+ Color.GREEN+Math.ceil(region.getMaxTime() - region.getTimeElapsed())+"s");
            item.setItemMeta(meta);
            entry.getKey().update();
        }

        for(BuildGui buildGui : BuildGui.buildGuis){
            List<GuiItem> guiItemList = new ArrayList<>();
            boolean toUpdate = false;
            for(Pair<GroupItem, Integer> pair : buildGui.getGroupItems()){
                GroupItem groupItem = pair.getKey();
                if(groupItem.isGroup) toUpdate = true;
                GuiItem gi = groupItem.getNext();
                guiItemList.add(gi);
            }
            if(toUpdate) {
                buildGui.materialPane.clear();
                buildGui.materialPane.populateWithGuiItems(guiItemList);
                buildGui.update();
            }
        }
    }

    private void updateGui(){

    }
}
