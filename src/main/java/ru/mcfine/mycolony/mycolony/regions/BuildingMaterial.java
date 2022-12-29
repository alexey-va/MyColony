package ru.mcfine.mycolony.mycolony.regions;

import org.bukkit.Material;
import org.bukkit.block.Block;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.config.MyConfig;

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
            HashSet<Material> materials = MyConfig.getMaterialGroup(groupName);
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

