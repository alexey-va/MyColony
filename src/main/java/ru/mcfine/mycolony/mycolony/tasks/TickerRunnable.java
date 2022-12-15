package ru.mcfine.mycolony.mycolony.tasks;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.config.Lang;
import ru.mcfine.mycolony.mycolony.guis.BuildGui;
import ru.mcfine.mycolony.mycolony.regions.GroupItem;
import ru.mcfine.mycolony.mycolony.regions.Region;
import ru.mcfine.mycolony.mycolony.guis.RegionGui;

import java.util.*;
import java.util.stream.Collectors;

public class TickerRunnable extends BukkitRunnable {

    private double increment = 1;

    public TickerRunnable(double increment){
        this.increment = increment;
    }

    public static HashMap<RegionGui, Region> mainMenuGuis = new HashMap<>();
    public static Set<GroupItem> groupItemList = new HashSet<>();

    @Override
    public void run() {
        Map<Chunk, HashMap<Location, Region>> regions = MyColony.regionManager.getRegionMap();
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
            Map<String, String> rep = Map.of("%time%", ""+(Math.ceil(region.getMaxTime() - region.getTimeElapsed())));
            meta.setDisplayName(Lang.getString("menu.time-to-next-display", rep, entry.getKey().p));
            item.setItemMeta(meta);
            entry.getKey().update();
        }

        List<GroupItem> toRemove = new ArrayList<>();
        for(GroupItem groupItem : groupItemList){
            //System.out.println(groupItem +" | "+groupItem.getParent());
            if(groupItem.getParent() == null || groupItem.getParent().getViewerCount() == 0){
                toRemove.add(groupItem);
                continue;
            }
            groupItem.setNext();
            if(!groupItem.getParent().isUpdating()) groupItem.getParent().update();
        }
        toRemove.forEach(groupItemList::remove);
    }

    public static void trimGroupItems(){
        groupItemList = groupItemList.stream().filter(s -> s.getParent().getViewerCount() > 0).collect(Collectors.toSet());
    }
}
