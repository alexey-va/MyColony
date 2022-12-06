package ru.mcfine.mycolony.mycolony.listeners;

import javafx.util.Pair;
import org.bukkit.Location;
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
import org.bukkit.util.Vector;
import org.checkerframework.checker.units.qual.C;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.city.CityArea;
import ru.mcfine.mycolony.mycolony.city.CityRegion;
import ru.mcfine.mycolony.mycolony.city.SquareArea;
import ru.mcfine.mycolony.mycolony.config.Lang;
import ru.mcfine.mycolony.mycolony.config.MyConfig;
import ru.mcfine.mycolony.mycolony.regions.*;
import ru.mcfine.mycolony.mycolony.requirements.Requirement;
import ru.mcfine.mycolony.mycolony.requirements.RequirementGui;
import ru.mcfine.mycolony.mycolony.util.Utils;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

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


        System.out.println(RegionManager.getCityByLocation(event.getBlockPlaced().getLocation()));

        RegionType regionType = MyColony.plugin.config.getRegionType(regionName);
        List<Requirement> reqs = regionType.getReqs();
        List<Requirement> notSatisfy = new ArrayList<>();
        if (reqs != null && reqs.size() > 0) {
            for (Requirement req : reqs) {
                System.out.println("Req: " + req.getCityLevels() + " | " + req.getReqRegions() + " | " + req.getPermission() + "| " + req.getReq());
                if (!req.satisfy(event.getPlayer(), event.getBlockPlaced().getLocation())) {
                    System.out.println("Not satisfy! " + req.getReq() + " | " + req.getPermission() + " | " + req.getReqRegions() + " | " + req.getCityLevels());
                    event.setCancelled(true);
                    notSatisfy.add(req);
                }
            }
        }
        if (notSatisfy.size() > 0) {
            RequirementGui gui = new RequirementGui(notSatisfy);
            gui.show(event.getPlayer());
            return;
        }

        Block block = event.getBlockPlaced();
        Utils.showOutliner(block.getLocation(), ((Directional) block.getBlockData()).getFacing(), MyColony.plugin.config.getRegionType(regionName), event.getPlayer(), ParticleEffect.FLAME, false);

        Pair<Location, Location> corners = Utils.getRegionCorners(event.getBlockPlaced().getLocation(), ((Directional) block.getBlockData()).getFacing(), regionType);
        Region intersectRegion = Utils.ifRegionIntersects(corners);
        if(intersectRegion != null){
            event.getPlayer().sendMessage("Intersects!");
            event.setCancelled(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    Utils.showOutliner(intersectRegion, event.getPlayer(), ParticleEffect.SMOKE_NORMAL, true);
                }
            }.runTaskLater(MyColony.plugin, 1L);
            return;
        }

        List<BuildingMaterial> mats = Utils.locationSatisfyBlocks(corners,block.getLocation(), MyColony.plugin.config.getRegionType(regionName));
        if (mats.size() > 0) {
            BuildGui gui = new BuildGui(mats);
            gui.show(event.getPlayer());
            event.setCancelled(true);
            return;
        }


        ArrayList<String> playerNames = new ArrayList<>();
        ArrayList<String> playerUUIDs = new ArrayList<>();
        playerNames.add(event.getPlayer().getName());
        playerUUIDs.add(event.getPlayer().getUniqueId().toString());

        Region region;

        if (regionType.isCity()) {
            List<String> members = new ArrayList<>();
            members.add(event.getPlayer().getName());

            region = new CityRegion(playerNames, 1, block.getX(), block.getY(), block.getZ(),
                    regionName, block.getWorld().getName(), playerUUIDs, regionType, null, new SquareArea(5,
                    event.getBlockPlaced().getWorld().getName(), event.getBlockPlaced().getX(), event.getBlockPlaced().getY(),
                    event.getBlockPlaced().getZ()), members, 1);
        } else {
            region = new Region(playerNames, 1, block.getX(), block.getY(), block.getZ(),
                    regionName, block.getWorld().getName(), playerUUIDs, regionType, null);
        }

        MyColony.regionManager.addRegion(block.getLocation(), region);


        new BukkitRunnable() {
            @Override
            public void run() {
                MyColony.plugin.getJsonStorage().saveDataSync();
            }
        }.runTaskAsynchronously(MyColony.plugin);


        new ParticleBuilder(ParticleEffect.END_ROD, block.getLocation())
                .setOffset(new Vector(0.5, 0.5, 0.5))
                .setSpeed(0.1f)
                .setAmount(10)
                .display();

        event.getPlayer().sendMessage(Lang.get("region.region-placed"));

    }
}
