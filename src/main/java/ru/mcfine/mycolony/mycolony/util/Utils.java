package ru.mcfine.mycolony.mycolony.util;

import javafx.util.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
import ru.mcfine.mycolony.mycolony.config.MyConfig;
import ru.mcfine.mycolony.mycolony.regions.BuildingMaterial;
import ru.mcfine.mycolony.mycolony.regions.RegionType;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Utils {

    private static MiniMessage mm = MiniMessage.miniMessage();
    private static HashMap<Player, BukkitTask> bukkitTasks = new HashMap<>();

    public static ItemStack getBackground(){
        ConfigurationSection section = MyColony.plugin.getConfig().getConfigurationSection("background-item");
        Material material = null;
        Component displayName = Component.text(" ");
        int modelData = 0;
        if(section != null) {
            material = Material.matchMaterial(section.getString("material", "BLACK_STAINED_GLASS_PANE"));
            displayName = mm.deserialize(section.getString("display-name", " "));
            modelData = section.getInt("model-data", 0);
        }

        if(material == null) material = Material.BLACK_STAINED_GLASS_PANE;

        ItemStack bg = new ItemStack(material, 1);
        ItemMeta meta = bg.getItemMeta();
        meta.setCustomModelData(modelData);
        meta.displayName(displayName);
        bg.setItemMeta(meta);

        return bg;
    }

    public static Pair<Location, Location> getRegionCorners(Location location, BlockFace blockFace, RegionType regionType){
        int xCorner = location.getBlockX();
        int yCorner = location.getBlockY();
        int zCorner = location.getBlockZ();

        int xCorner2 = location.getBlockX();
        int yCorner2 = location.getBlockY();
        int zCorner2 = location.getBlockZ();
        if(blockFace == BlockFace.WEST){
            zCorner+= regionType.getxRight();
            zCorner2-= regionType.getxLeft();

            xCorner-=regionType.getzForward();
            xCorner2+=regionType.getzBackward();

            yCorner-=regionType.getyUp();
            yCorner2+=regionType.getyDown();
        } else if (blockFace == BlockFace.NORTH) {
            zCorner-= regionType.getzForward();
            zCorner2+= regionType.getzBackward();

            xCorner-=regionType.getxRight();
            xCorner2+=regionType.getxLeft();

            yCorner-=regionType.getyUp();
            yCorner2+=regionType.getyDown();
        } else if(blockFace == BlockFace.EAST){
            zCorner-= regionType.getxRight();
            zCorner2+= regionType.getxLeft();

            xCorner+=regionType.getzForward();
            xCorner2-=regionType.getzBackward();

            yCorner-=regionType.getyUp();
            yCorner2+=regionType.getyDown();
        } else if (blockFace == BlockFace.SOUTH) {
            zCorner+= regionType.getzForward();
            zCorner2-= regionType.getzBackward();

            xCorner+=regionType.getxRight();
            xCorner2-=regionType.getxLeft();

            yCorner-=regionType.getyUp();
            yCorner2+=regionType.getyDown();
        }

        //z1 always <= z2
        if(zCorner > zCorner2){
            int temp = zCorner2;
            zCorner2 = zCorner;
            zCorner = temp;
        }

        //x2 always <= x2
        if(xCorner > xCorner2){
            int temp = xCorner2;
            xCorner2 = xCorner;
            xCorner = temp;
        }

        return new Pair<>(new Location(location.getWorld(), xCorner, yCorner, zCorner),
                new Location(location.getWorld(), xCorner2, yCorner2, zCorner2));
    }

    public static List<BuildingMaterial> locationSatisfyBlocks(Location location, BlockFace blockFace, RegionType regionType){
        List<BuildingMaterial> result = new ArrayList<>();
        List<Block> blocks = new ArrayList<>();

        Pair<Location, Location> corners = getRegionCorners(location, blockFace, regionType);

        int xCorner = corners.getKey().getBlockX();
        int yCorner = corners.getKey().getBlockY();
        int zCorner = corners.getKey().getBlockZ();

        int xCorner2 = corners.getValue().getBlockX();
        int yCorner2 = corners.getValue().getBlockY();
        int zCorner2 = corners.getValue().getBlockZ();

        for(int x = xCorner; x<= xCorner2; x++){
            for(int y = yCorner; y<=yCorner2; y++){
                for(int z = zCorner; z<=zCorner2; z++){
                    blocks.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }

        for(BuildingMaterial buildingMaterial : regionType.getBuildingMaterials()){
            BuildingMaterial bm = new BuildingMaterial(buildingMaterial);
            bm.setAmount(buildingMaterial.materialSatisfy(blocks));
            if(bm.getAmount() != 0) result.add(bm);
        }

        return result;
    }

    public static List<Location> getOutliner(Pair<Location, Location> corners, World world , int steps){
        List<Location> result = new ArrayList<>();

        int xCorner = corners.getKey().getBlockX();
        int yCorner = corners.getKey().getBlockY();
        int zCorner = corners.getKey().getBlockZ();

        int xCorner2 = corners.getValue().getBlockX()+1;
        int yCorner2 = corners.getValue().getBlockY()+1;
        int zCorner2 = corners.getValue().getBlockZ()+1;

        double stepX = (xCorner2 - xCorner)/(double)steps;
        double stepY = (yCorner2 - yCorner)/(double)steps;
        double stepZ = (zCorner2 - zCorner)/(double)steps;

        System.out.println(stepX+" | "+stepY+" | "+stepZ);

        for(int i =0; i<= steps; i++){
            // 111
            result.add(new Location(world, xCorner+stepX*i, yCorner, zCorner));
            result.add(new Location(world, xCorner, yCorner+i*stepY, zCorner));
            result.add(new Location(world, xCorner, yCorner, zCorner+i*stepZ));
            //122
            result.add(new Location(world, xCorner2, yCorner2, zCorner+i*stepZ));
            result.add(new Location(world, xCorner2-i*stepX, yCorner2, zCorner));
            result.add(new Location(world, xCorner2, yCorner2-i*stepY, zCorner));
            //212
            result.add(new Location(world, xCorner2, yCorner+stepY*i, zCorner2));
            result.add(new Location(world, xCorner2-i*stepX, yCorner, zCorner2));
            result.add(new Location(world, xCorner2, yCorner, zCorner2-i*stepZ));
            //221
            result.add(new Location(world, xCorner, yCorner2-stepY*i, zCorner2));
            result.add(new Location(world, xCorner, yCorner2, zCorner2-i*stepZ));
            result.add(new Location(world, xCorner+i*stepX, yCorner2, zCorner2));
        }

        return result;
    }

    public static void showOutliner(Location location, BlockFace blockFace, RegionType regionType, Player player){
        Pair<Location, Location> corners = getRegionCorners(location, blockFace, regionType);
        List<Location> outliner = getOutliner(corners, location.getWorld(), 20);
        final Integer[] counter = {20};

        if(bukkitTasks.get(player) != null && !bukkitTasks.get(player).isCancelled()){
            bukkitTasks.get(player).cancel();
        }
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if(player == null || counter[0] <= 0){
                    this.cancel();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            bukkitTasks.remove(player);
                        }
                    }.runTaskLater(MyColony.plugin, 0L);
                    return;
                }
                for(Location loc : outliner) {
                    new ParticleBuilder(ParticleEffect.FLAME, loc)
                            .setOffset(new Vector(0.1, 0.1, 0.1))
                            .setSpeed(0F)
                            .display(player);
                }
                counter[0]--;
            }
        }.runTaskTimerAsynchronously(MyColony.plugin, 0L, 10L);
        bukkitTasks.put(player, task);
    }

}
