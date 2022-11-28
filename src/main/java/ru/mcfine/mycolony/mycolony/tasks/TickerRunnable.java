package ru.mcfine.mycolony.mycolony.tasks;

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

    @Override
    public void run() {
        HashMap<Chunk, HashMap<Location, Region>> regions = MyColony.regionManager.getRegionMap();
        for(Map.Entry<Chunk, HashMap<Location, Region>> entry : regions.entrySet()){
            if(!entry.getKey().isLoaded()) continue;
            for(Region region : entry.getValue().values()){
                region.incrementTime(increment);
            }
        }
    }
}
