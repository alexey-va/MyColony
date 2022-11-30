package ru.mcfine.mycolony.mycolony.listeners;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.regions.BuildingMaterial;
import ru.mcfine.mycolony.mycolony.regions.Region;
import ru.mcfine.mycolony.mycolony.regions.RegionManager;

import java.util.HashMap;
import java.util.Map;

public class PutChest implements Listener {

    @EventHandler
    public void onPutChest(BlockPlaceEvent event){
        if(event.getBlockPlaced().getType() != Material.CHEST) return;

        ItemStack chest = event.getItemInHand();
        ItemMeta meta = chest.getItemMeta();
        NamespacedKey regionKey = new NamespacedKey(MyColony.plugin, "region_name");
        String regionName = meta.getPersistentDataContainer().get(regionKey, PersistentDataType.STRING);
        if(regionName == null) return;

        Block block = event.getBlockPlaced();
        HashMap<BuildingMaterial, Integer> mats = BuildingMaterial.locationSatisfyBlocks(block.getLocation(), ((Directional)block.getBlockData()).getFacing(), MyColony.plugin.config.getRegionType(regionName));

        for(Map.Entry<BuildingMaterial, Integer> entry : mats.entrySet()){
            System.out.println(entry.getKey() + " | "+entry.getValue());
        }

        Region region = new Region(event.getPlayer().getName(), 0, block.getX(), block.getY(), block.getZ(),
                regionName, block.getWorld().getName(), event.getPlayer().getUniqueId().toString(), MyColony.plugin.config.getRegionType(regionName));
        MyColony.regionManager.addRegion(block.getLocation(), region);
    }
}
