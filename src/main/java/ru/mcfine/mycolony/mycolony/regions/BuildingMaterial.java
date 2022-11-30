package ru.mcfine.mycolony.mycolony.regions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import ru.mcfine.mycolony.mycolony.MyColony;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class BuildingMaterial {

    private String groupName = null;
    private int amount = 0;
    private Material material = null;

    public BuildingMaterial(String groupName, int amount){
        this.groupName = groupName;
        this.amount = amount;
    }

    public BuildingMaterial(Material material, int amount){
        this.material = material;
        this.amount = amount;
    }

    public int materialSatisfy(List<Block> blocks) {
        int counter = 0;
        System.out.println(groupName + " | "+material);
        if(this.groupName != null && this.material == null){
            HashSet<Material> materials = MyColony.plugin.config.getMaterialGroup(groupName);
            for(Block block : blocks){
                System.out.println(block.getType()+" | "+counter);
                if(materials.contains(block.getType())) counter++;
                if(counter >= amount) return 0;
            }
        } else if(this.groupName == null && this.material != null){
            for(Block block : blocks){
                if(block.getType() == this.material) counter++;
                if(counter >= amount) return 0;
            }
        } else {
            MyColony.plugin.getLogger().severe("BuildingMaterial has a group AND a material!!!");
        }

        return (amount - counter);
    }

    public static HashMap<BuildingMaterial, Integer> locationSatisfyBlocks(Location location, BlockFace blockFace, RegionType regionType){
        HashMap<BuildingMaterial, Integer> result = new HashMap<>();
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
        if(zCorner > zCorner2){
            int temp = zCorner2;
            zCorner2 = zCorner;
            zCorner = temp;
        }
        if(xCorner > xCorner2){
            int temp = xCorner2;
            xCorner2 = xCorner;
            xCorner = temp;
        }
        List<Block> blocks = new ArrayList<>();
        System.out.println(xCorner+" | "+yCorner+" | "+zCorner+" | "+xCorner2+" | "+yCorner2+" | "+zCorner2);

        for(int x = xCorner; x<= xCorner2; x++){
            for(int y = yCorner; y<=yCorner2; y++){
                for(int z = zCorner; z<=zCorner2; z++){
                    blocks.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }

        for(BuildingMaterial buildingMaterial : regionType.getBuildingMaterials()){
            result.put(buildingMaterial, buildingMaterial.materialSatisfy(blocks));
        }

        return result;
    }


}

