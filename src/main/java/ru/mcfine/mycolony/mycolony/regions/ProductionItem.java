package ru.mcfine.mycolony.mycolony.regions;

import org.bukkit.Material;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.config.MyConfig;

import java.util.HashSet;

public class ProductionItem {
    public Material material = null;
    public int amount = 0;
    public String groupName = null;
    public HashSet<Material> group = null;
    public Type type;
    double moneyAmount = 0;

    public ProductionItem(double amount){
        this.moneyAmount = amount;
        type = Type.MONEY;
    }

    public ProductionItem(Material material, int amount, Type type) {
        this.material = material;
        this.amount = amount;
        this.type = type;
    }

    public ProductionItem(String groupName, int amount, Type type){
        this.groupName = groupName;
        this.group = MyConfig.getMaterialGroup(groupName);
        this.amount = amount;
        this.type = type;
    }

    public boolean hasMaterial(Material material){
        if(this.type == Type.MONEY) return false;
        if(this.material != null) return (material == this.material);
        else if (group != null) return this.group.contains(material);
        return false;
    }

    public enum Type{
        MONEY,
        GROUP,
        MATERIAL,
        TOOL,
        TOOL_GROUP
    }

    public Material getMaterial() {
        return material;
    }

    public int getAmount() {
        return amount;
    }

    public String getGroupName() {
        return groupName;
    }

    public HashSet<Material> getGroup() {
        return group;
    }

    public Type getType() {
        return type;
    }

    public double getMoneyAmount() {
        return moneyAmount;
    }
}
