package ru.mcfine.mycolony.mycolony.tasks;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.regions.Region;

import java.util.HashMap;
import java.util.Map;

public class TickerRunnable extends BukkitRunnable {

    private double increment = 1;

    public TickerRunnable(double increment){
        this.increment = increment;
    }

    public static HashMap<String, ChestGui> guis = new HashMap<>();

    @Override
    public void run() {
        HashMap<Chunk, HashMap<Location, Region>> regions = MyColony.regionManager.getRegionMap();
        for(Map.Entry<Chunk, HashMap<Location, Region>> entry : regions.entrySet()){
            if(entry == null|| entry.getValue() == null || entry.getKey() == null || !entry.getKey().isLoaded()) continue;
            for(Region region : entry.getValue().values()){
                region.incrementTime(increment);
            }
        }

        for(ChestGui gui : guis.values()){
            if(gui == null) continue;
            gui.update();
        }
    }
}
