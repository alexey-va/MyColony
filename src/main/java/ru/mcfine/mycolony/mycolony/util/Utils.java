package ru.mcfine.mycolony.mycolony.util;

import javafx.util.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.regions.BuildingMaterial;
import ru.mcfine.mycolony.mycolony.regions.Region;
import ru.mcfine.mycolony.mycolony.regions.RegionType;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.*;

public class Utils {

    private static MiniMessage mm = MiniMessage.miniMessage();
    private static HashMap<Player, BukkitTask> showRegionTasks = new HashMap<>();
    private static HashMap<Player, BukkitTask> showBlockingRegionTasks = new HashMap<>();
    public static ItemStack getBackground() {
        return getBackground(Material.BLACK_STAINED_GLASS_PANE);
    }

    public static ItemStack getNextPage(Material material){

    }

    public static ItemStack getBackground(Material material) {
        ConfigurationSection section = MyColony.plugin.getConfig().getConfigurationSection("background-item");
        Component displayName = Component.text(" ");
        int modelData = 0;
        if (section != null) {
            displayName = mm.deserialize(section.getString("display-name", " "));
            modelData = section.getInt("model-data", 0);
        }


        ItemStack bg = new ItemStack(material, 1);
        ItemMeta meta = bg.getItemMeta();
        meta.setCustomModelData(modelData);
        meta.displayName(displayName);
        bg.setItemMeta(meta);

        return bg;
    }

    public static Pair<Location, Location> getRegionCorners(Location location, BlockFace blockFace, RegionType regionType) {
        int xCorner = location.getBlockX();
        int yCorner = location.getBlockY();
        int zCorner = location.getBlockZ();

        int xCorner2 = location.getBlockX();
        int yCorner2 = location.getBlockY();
        int zCorner2 = location.getBlockZ();
        if (blockFace == BlockFace.WEST) {
            zCorner += regionType.getxRight();
            zCorner2 -= regionType.getxLeft();

            xCorner -= regionType.getzForward();
            xCorner2 += regionType.getzBackward();

            yCorner -= regionType.getyUp();
            yCorner2 += regionType.getyDown();
        } else if (blockFace == BlockFace.NORTH) {
            zCorner -= regionType.getzForward();
            zCorner2 += regionType.getzBackward();

            xCorner -= regionType.getxRight();
            xCorner2 += regionType.getxLeft();

            yCorner -= regionType.getyUp();
            yCorner2 += regionType.getyDown();
        } else if (blockFace == BlockFace.EAST) {
            zCorner -= regionType.getxRight();
            zCorner2 += regionType.getxLeft();

            xCorner += regionType.getzForward();
            xCorner2 -= regionType.getzBackward();

            yCorner -= regionType.getyUp();
            yCorner2 += regionType.getyDown();
        } else if (blockFace == BlockFace.SOUTH) {
            zCorner += regionType.getzForward();
            zCorner2 -= regionType.getzBackward();

            xCorner += regionType.getxRight();
            xCorner2 -= regionType.getxLeft();

            yCorner -= regionType.getyUp();
            yCorner2 += regionType.getyDown();
        }

        //z1 always <= z2
        if (zCorner > zCorner2) {
            int temp = zCorner2;
            zCorner2 = zCorner;
            zCorner = temp;
        }

        //x1 always <= x2
        if (xCorner > xCorner2) {
            int temp = xCorner2;
            xCorner2 = xCorner;
            xCorner = temp;
        }

        return new Pair<>(new Location(location.getWorld(), xCorner, yCorner, zCorner),
                new Location(location.getWorld(), xCorner2, yCorner2, zCorner2));
    }

    public static List<BuildingMaterial> locationSatisfyBlocks(Pair<Location, Location> corners, Location location, RegionType regionType) {
        List<BuildingMaterial> result = new ArrayList<>();
        List<Block> blocks = new ArrayList<>();


        int xCorner = corners.getKey().getBlockX();
        int yCorner = corners.getKey().getBlockY();
        int zCorner = corners.getKey().getBlockZ();

        int xCorner2 = corners.getValue().getBlockX();
        int yCorner2 = corners.getValue().getBlockY();
        int zCorner2 = corners.getValue().getBlockZ();

        for (int x = xCorner; x <= xCorner2; x++) {
            for (int y = yCorner; y <= yCorner2; y++) {
                for (int z = zCorner; z <= zCorner2; z++) {
                    blocks.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }

        for (BuildingMaterial buildingMaterial : regionType.getBuildingMaterials()) {
            BuildingMaterial bm = new BuildingMaterial(buildingMaterial);
            bm.setAmount(buildingMaterial.materialSatisfy(blocks));
            if (bm.getAmount() != 0) result.add(bm);
        }

        return result;
    }

    public static List<Location> getOutliner(Pair<Location, Location> corners, World world, int steps) {
        List<Location> result = new ArrayList<>();

        int xCorner = corners.getKey().getBlockX();
        int yCorner = corners.getKey().getBlockY();
        int zCorner = corners.getKey().getBlockZ();

        int xCorner2 = corners.getValue().getBlockX() + 1;
        int yCorner2 = corners.getValue().getBlockY() + 1;
        int zCorner2 = corners.getValue().getBlockZ() + 1;

        double stepX = (xCorner2 - xCorner) / (double) steps;
        double stepY = (yCorner2 - yCorner) / (double) steps;
        double stepZ = (zCorner2 - zCorner) / (double) steps;

        System.out.println(stepX + " | " + stepY + " | " + stepZ);

        for (int i = 0; i <= steps; i++) {
            // 111
            result.add(new Location(world, xCorner + stepX * i, yCorner, zCorner));
            result.add(new Location(world, xCorner, yCorner + i * stepY, zCorner));
            result.add(new Location(world, xCorner, yCorner, zCorner + i * stepZ));
            //122
            result.add(new Location(world, xCorner2, yCorner2, zCorner + i * stepZ));
            result.add(new Location(world, xCorner2 - i * stepX, yCorner2, zCorner));
            result.add(new Location(world, xCorner2, yCorner2 - i * stepY, zCorner));
            //212
            result.add(new Location(world, xCorner2, yCorner + stepY * i, zCorner2));
            result.add(new Location(world, xCorner2 - i * stepX, yCorner, zCorner2));
            result.add(new Location(world, xCorner2, yCorner, zCorner2 - i * stepZ));
            //221
            result.add(new Location(world, xCorner, yCorner2 - stepY * i, zCorner2));
            result.add(new Location(world, xCorner, yCorner2, zCorner2 - i * stepZ));
            result.add(new Location(world, xCorner + i * stepX, yCorner2, zCorner2));
        }

        return result;
    }

    public static void showOutliner(Location location, BlockFace blockFace, RegionType regionType, Player player, ParticleEffect particleEffect, boolean blockingRegion) {
        Pair<Location, Location> corners = getRegionCorners(location, blockFace, regionType);
        List<Location> outliner = getOutliner(corners, location.getWorld(), 20);
        final Integer[] counter = {20};
        if(!blockingRegion) {
            if (showRegionTasks.get(player) != null && !showRegionTasks.get(player).isCancelled()) {
                showRegionTasks.get(player).cancel();
            }
        } else{
            if (showBlockingRegionTasks.get(player) != null && !showBlockingRegionTasks.get(player).isCancelled()) {
                showBlockingRegionTasks.get(player).cancel();
            }
        }
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (player == null || counter[0] <= 0) {
                    this.cancel();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if(!blockingRegion) showRegionTasks.remove(player);
                            else showBlockingRegionTasks.remove(player);
                        }
                    }.runTaskLater(MyColony.plugin, 0L);
                    return;
                }
                for (Location loc : outliner) {
                    new ParticleBuilder(particleEffect, loc)
                            .setOffset(new Vector(0.1, 0.1, 0.1))
                            .setSpeed(0F)
                            .display(player);
                }
                counter[0]--;
            }
        }.runTaskTimerAsynchronously(MyColony.plugin, 0L, 10L);
        if(!blockingRegion) showRegionTasks.put(player, task);
        else showBlockingRegionTasks.put(player, task);
    }

    public static void showOutliner(Region region, Player player, ParticleEffect particleEffect, boolean blockingRegion){
        showOutliner(region.getLocation(), region.getBlockFace(), region.getRegionType(), player, particleEffect, blockingRegion);
    }

    public Set<Chunk> getChunksInRadius(Chunk chunk, int radius){
        Set<Chunk> chunks = new HashSet<>();
        //chunks.add(chunk);

        int x0 = chunk.getX() - radius;
        int x1 = chunk.getX() + radius;
        int z0 = chunk.getZ() - radius;
        int z1 = chunk.getZ() + radius;
        World world = chunk.getWorld();

        for (int i = x0; i <= x1; i++) {
            for (int j = z0; j <= z1; j++) {
                chunks.add(world.getChunkAt(i, j));
            }
        }

        return chunks;
    }

    public static Region ifRegionIntersects(Pair<Location, Location> corners) {

        Chunk chunk1 = corners.getKey().getChunk();
        Chunk chunk2 = corners.getValue().getChunk();
        Set<Chunk> chunks = new HashSet<>();
        int x0 = Math.min(chunk1.getX() - 3, chunk2.getX() - 3);
        int x1 = Math.max(chunk1.getX() + 3, chunk2.getX() + 3);
        int z0 = Math.min(chunk1.getZ() - 3, chunk2.getZ() - 3);
        int z1 = Math.max(chunk1.getZ() + 3, chunk2.getZ() + 3);
        for (int i = x0; i <= x1; i++) {
            for (int j = z0; j <= z1; j++) {
                chunks.add(chunk1.getWorld().getChunkAt(i, j));
            }
        }


        boolean intersects = false;
        for (Chunk chunk : chunks) {
            HashMap<Location, Region> regions = MyColony.regionManager.getRegions(chunk);
            if (regions == null) continue;
            for (Region region : regions.values()) {
                Pair<Location, Location> corners2 = region.getCorners();
                if (ifPolygonsIntersect(corners, corners2)) return region;
            }
        }
        return null;

    }

    private static boolean ifPolygonsIntersect(Pair<Location, Location> corners1, Pair<Location, Location> corners2) {
        Location corner11 = corners1.getKey();
        Location corner12 = corners1.getValue();
        Location corner21 = corners2.getKey();
        Location corner22 = corners2.getValue();

        int aMinX = corner11.getBlockX();
        int aMinY = corner11.getBlockY();
        int aMinZ = corner11.getBlockZ();

        int aMaxX = corner12.getBlockX();
        int aMaxY = corner12.getBlockY();
        int aMaxZ = corner12.getBlockZ();

        int bMinX = corner21.getBlockX();
        int bMinY = corner21.getBlockY();
        int bMinZ = corner21.getBlockZ();

        int bMaxX = corner22.getBlockX();
        int bMaxY = corner22.getBlockY();
        int bMaxZ = corner22.getBlockZ();

        return (aMaxX >= bMinX && aMinX <= bMaxX) && (aMaxY >= bMinY && aMinY <= bMaxY) && (aMaxZ >= bMinZ && aMinZ <= bMaxZ);
    }

    public static void clearRegionBordersVis(Player player){
        if(showRegionTasks.get(player) != null && !showRegionTasks.get(player).isCancelled()){
            showRegionTasks.get(player).cancel();
            showRegionTasks.remove(player);
        }
        if(showBlockingRegionTasks.get(player) != null && !showBlockingRegionTasks.get(player).isCancelled()){
            showBlockingRegionTasks.get(player).cancel();
            showBlockingRegionTasks.remove(player);
        }
    }
}
