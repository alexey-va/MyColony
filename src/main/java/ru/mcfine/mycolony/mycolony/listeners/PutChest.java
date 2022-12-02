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
import org.bukkit.scheduler.BukkitRunnable;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.regions.BuildGui;
import ru.mcfine.mycolony.mycolony.regions.BuildingMaterial;
import ru.mcfine.mycolony.mycolony.regions.Region;
import ru.mcfine.mycolony.mycolony.regions.RegionManager;

import java.util.*;

public class PutChest implements Listener {

    @EventHandler
    public void onPutChest(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() != Material.CHEST) return;

        ItemStack chest = event.getItemInHand();
        ItemMeta meta = chest.getItemMeta();
        NamespacedKey regionKey = new NamespacedKey(MyColony.plugin, "region_name");
        String regionName = meta.getPersistentDataContainer().get(regionKey, PersistentDataType.STRING);
        if (regionName == null) return;

        Block block = event.getBlockPlaced();
        List<BuildingMaterial> mats = BuildingMaterial.locationSatisfyBlocks(block.getLocation(), ((Directional) block.getBlockData()).getFacing(), MyColony.plugin.config.getRegionType(regionName));

        System.out.println(mats.size());

        if(mats.size() > 0){
            BuildGui gui = new BuildGui(mats);
            gui.show(event.getPlayer());
            event.setCancelled(true);
        } else {

            ArrayList<String> playerNames = (ArrayList<String>) List.of(event.getPlayer().getName());
            ArrayList<String> playerUUIDs = (ArrayList<String>) List.of(event.getPlayer().getUniqueId().toString());
            Region region = new Region(playerNames, 0, block.getX(), block.getY(), block.getZ(),
                    regionName, block.getWorld().getName(), playerUUIDs, MyColony.plugin.config.getRegionType(regionName), null);
            MyColony.regionManager.addRegion(block.getLocation(), region);

            new BukkitRunnable() {
                @Override
                public void run() {
                    MyColony.plugin.getJsonStorage().saveDataSync();
                }
            }.runTaskAsynchronously(MyColony.plugin);
        }
    }
}
