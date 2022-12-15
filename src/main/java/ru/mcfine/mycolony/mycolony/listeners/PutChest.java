package ru.mcfine.mycolony.mycolony.listeners;

import javafx.util.Pair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.city.CityArea;
import ru.mcfine.mycolony.mycolony.city.CityRegion;
import ru.mcfine.mycolony.mycolony.city.SquareArea;
import ru.mcfine.mycolony.mycolony.config.Lang;
import ru.mcfine.mycolony.mycolony.guis.BuildGui;
import ru.mcfine.mycolony.mycolony.regions.*;
import ru.mcfine.mycolony.mycolony.requirements.Requirement;
import ru.mcfine.mycolony.mycolony.guis.RequirementGui;
import ru.mcfine.mycolony.mycolony.util.Utils;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.*;

public class PutChest implements Listener {

    private static Map<Player, Double> cooldownPutChest = new HashMap<>();
    private static BukkitTask cooldownTask = null;

    @EventHandler
    public void onPutChest(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() != Material.CHEST) return;

        ItemStack chest = event.getItemInHand();
        ItemMeta meta = chest.getItemMeta();
        String regionName = meta.getPersistentDataContainer().get(new NamespacedKey(MyColony.plugin, "region_name"), PersistentDataType.STRING);

        if (regionName == null) return;

        RegionType regionType = MyColony.plugin.config.getRegionType(regionName);

        if (regionType == null) return;

        Utils.clearRegionBordersVis(event.getPlayer());
        CityArea.clearCityBordersVis(event.getPlayer());

        if (cooldownPutChest.get(event.getPlayer()) != null) {
            event.getPlayer().sendMessage("Cooldown: " + cooldownPutChest.get(event.getPlayer()));
            event.setCancelled(true);
            return;
        } else {
            cooldownPutChest.put(event.getPlayer(), 0.5);
            createCooldownTask();
        }

        CityRegion cityRegion = RegionManager.getCityByLocation(event.getBlockPlaced().getLocation());
        if(cityRegion != null && !cityRegion.cityHasPlayer(event.getPlayer().getName())){
            event.getPlayer().sendMessage("Not part of the city!");
            event.setCancelled(true);
            return;
        }

        List<Requirement> reqs = regionType.getReqs();
        List<Requirement> notSatisfy = new ArrayList<>();
        if (reqs != null && reqs.size() > 0) {
            for (Requirement req : reqs) {
                if (!req.satisfy(event.getPlayer(), event.getBlockPlaced().getLocation())) notSatisfy.add(req);
            }
        }
        if (notSatisfy.size() > 0) {
            event.setCancelled(true);
            RequirementGui gui = new RequirementGui(notSatisfy);
            gui.show(event.getPlayer());
            return;
        }

        if (regionType.isCity()) {
            CityArea cityArea = CityArea.getAreaForCity(regionType, event.getBlockPlaced().getLocation());
            if (cityArea != null) {
                CityArea intersectArea = cityArea.whichIntersects();
                if (intersectArea != null) {
                    event.getPlayer().sendMessage("Your city intersects with another!");

                    //cityArea.showChunksNearLocation(event.getPlayer().getLocation(), event.getPlayer(), 1, ParticleEffect.FLAME, false);
                    intersectArea.showChunksNearLocation(event.getPlayer().getLocation().clone().add(0, -1, 0), event.getPlayer(), 1, ParticleEffect.SMOKE_LARGE, true);
                    event.setCancelled(true);
                    return;
                }
            } else{
                event.getPlayer().sendMessage("No area for city");
                event.setCancelled(true);
                return;
            }
        }

        Block block = event.getBlockPlaced();
        Pair<Location, Location> corners = Utils.getRegionCorners(event.getBlockPlaced().getLocation(), ((Directional) block.getBlockData()).getFacing(), regionType);

        Utils.showOutliner(block.getLocation(), ((Directional) block.getBlockData()).getFacing(), MyColony.plugin.config.getRegionType(regionName), event.getPlayer(), ParticleEffect.FLAME, false);
        if(checkRegionIntersection(event, corners)) return;
        if(checkBuildingMaterials(event, regionType, corners, block.getLocation())) return;
        createRegion(regionType, event, corners);
        highlightBlock(block.getLocation(), ParticleEffect.REDSTONE);
        saveChanges(event.getPlayer());



    }


    private boolean checkRegionIntersection(BlockPlaceEvent event, Pair<Location, Location> corners){
        Region intersectRegion = Utils.ifRegionIntersects(corners);
        if (intersectRegion != null) {
            event.getPlayer().sendMessage("Intersects!");
            event.setCancelled(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    Utils.showOutliner(intersectRegion, event.getPlayer(), ParticleEffect.SMOKE_NORMAL, true);
                }
            }.runTaskLater(MyColony.plugin, 1L);
            return true;
        }
        return false;
    }

    private boolean checkBuildingMaterials(BlockPlaceEvent event, RegionType regionType, Pair<Location, Location> corners, Location location){
        List<BuildingMaterial> mats = Utils.locationSatisfyBlocks(corners, location, regionType);
        if (mats.size() > 0) {
            BuildGui gui = new BuildGui(mats, event.getPlayer());
            gui.show(event.getPlayer());
            event.setCancelled(true);
            return true;
        }
        return false;
    }

    private void highlightBlock(Location location, ParticleEffect particleEffect){
        new ParticleBuilder(particleEffect, location)
                .setOffset(new Vector(0.6, 0.6, 0.6))
                .setSpeed(0.1f)
                .setAmount(20)
                .display();
    }

    private void saveChanges(Player player){
        player.sendMessage(Lang.getString("region.region-placed", player));
        new BukkitRunnable() {
            @Override
            public void run() {
                MyColony.plugin.getJsonStorage().saveRegions();
                MyColony.plugin.getJsonStorage().savePlayers();
            }
        }.runTaskAsynchronously(MyColony.plugin);
    }

    private void createRegion(RegionType regionType, BlockPlaceEvent event, Pair<Location, Location> corners){
        Region region;
        Block block = event.getBlockPlaced();
        Set<String> playerNames = new HashSet<>();
        Set<String> playerUUIDs = new HashSet<>();
        playerNames.add(event.getPlayer().getName());
        playerUUIDs.add(event.getPlayer().getUniqueId().toString());
        String regionName = regionType.getRegionId();

        if (regionType.isCity()) {
            if (MyColony.protection.ifIntersects(corners, event.getPlayer(), 10)) {
                event.getPlayer().sendMessage("WG intersects");
                event.setCancelled(true);
                return;
            }

            CityArea cityArea = CityArea.getAreaForCity(regionType, event.getBlockPlaced().getLocation());

            if (cityArea instanceof SquareArea squareArea) {
                if (MyColony.protection.ifIntersects(squareArea, event.getPlayer(), 20)) {
                    event.getPlayer().sendMessage("WG intersects");
                    event.setCancelled(true);
                    return;
                }
            }

            String protName = regionName + "_" + event.getPlayer().getName() + "_";
            for (int i = 1; i < 100; i++) {
                if (!MyColony.protection.ifAreaExists(protName + i, event.getBlockPlaced().getWorld())) {
                    protName += i;
                    break;
                }
            }

            Set<String> members = new HashSet<>();
            members.add(event.getPlayer().getName());

            MyColony.protection.addRegion(corners, event.getPlayer(), protName, 20, playerNames, new HashSet<>());

            String cityProtName = event.getPlayer().getName() + "_city_";
            for (int i = 1; i < 100; i++) {
                if (!MyColony.protection.ifAreaExists(cityProtName + i, event.getBlockPlaced().getWorld())) {
                    cityProtName += i;
                    break;
                }
            }

            if(cityArea instanceof SquareArea) MyColony.protection.createCityArea(cityArea, cityProtName, 10, playerNames, playerNames);

            region = new CityRegion(playerNames, 1, block.getX(), block.getY(), block.getZ(),
                    regionName, block.getWorld().getName(), playerUUIDs, regionType, null, protName, cityArea, members, cityProtName, event.getPlayer().getName());
        } else {
            if (MyColony.protection.ifIntersects(corners, event.getPlayer(), 20)) {
                event.getPlayer().sendMessage("WG intersects");
                event.setCancelled(true);
                return;
            }

            String protName = regionName + "_" + event.getPlayer().getName() + "_";
            for (int i = 1; i < 100; i++) {
                if (!MyColony.protection.ifAreaExists(protName + i, event.getBlockPlaced().getWorld())) {
                    protName += i;
                    break;
                }
            }

            MyColony.protection.addRegion(corners, event.getPlayer(), protName, 20, playerNames, new HashSet<>());

            region = new Region(playerNames, 1, block.getX(), block.getY(), block.getZ(),
                    regionName, block.getWorld().getName(), playerUUIDs, regionType, null, protName, RegionManager.getCityByLocation(event.getBlockPlaced().getLocation()));
        }

        MyColony.regionManager.addRegion(block.getLocation(), region);
    }


    private static void createCooldownTask() {
        if (cooldownTask == null || cooldownTask.isCancelled()) {
            cooldownTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (cooldownPutChest.size() == 0) {
                        this.cancel();
                        return;
                    }
                    for (Map.Entry<Player, Double> entry : cooldownPutChest.entrySet()) {
                        if (entry.getKey() == null || !entry.getKey().isOnline() || entry.getValue() <= 0.01)
                            cooldownPutChest.remove(entry.getKey());
                        else {
                            cooldownPutChest.put(entry.getKey(), entry.getValue() - 0.5);
                        }
                    }
                }
            }.runTaskTimerAsynchronously(MyColony.plugin, 10L, 10L);
        }
    }
}
