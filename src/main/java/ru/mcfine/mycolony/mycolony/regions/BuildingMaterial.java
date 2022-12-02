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

    public BuildingMaterial(BuildingMaterial mat){
        this.material = mat.getMaterial();
        this.amount = mat.getAmount();
        this.groupName = mat.getGroupName();
    }

    public int materialSatisfy(List<Block> blocks) {
        int counter = 0;
        if(this.groupName != null && this.material == null){
            HashSet<Material> materials = MyColony.plugin.config.getMaterialGroup(groupName);
            for(Block block : blocks){
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

    public static List<BuildingMaterial> locationSatisfyBlocks(Location location, BlockFace blockFace, RegionType regionType){
        List<BuildingMaterial> result = new ArrayList<>();
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
            result.add(bm);
        }

        return result;
    }


    public String getGroupName() {
        return groupName;
    }

    public int getAmount() {
        return amount;
    }

    public Material getMaterial() {
        return material;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}

