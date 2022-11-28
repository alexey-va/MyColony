package ru.mcfine.mycolony.mycolony.listeners;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.regions.Region;
import ru.mcfine.mycolony.mycolony.regions.RegionManager;

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
        Region region = new Region(event.getPlayer().getName(), 0, block.getX(), block.getY(), block.getZ(), regionName,block.getWorld().getName(), event.getPlayer().getUniqueId().toString());
        MyColony.regionManager.addRegion(block.getLocation(), region);
    }
}
