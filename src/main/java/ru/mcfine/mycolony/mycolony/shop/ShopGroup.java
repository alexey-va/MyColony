package ru.mcfine.mycolony.mycolony.shop;

import org.bukkit.Material;
import ru.mcfine.mycolony.mycolony.config.Lang;
import ru.mcfine.mycolony.mycolony.regions.RegionType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShopGroup {

    private String groupName;
    private Material groupIcon;
    private int groupIconAmount;
    private String permission;
    private final List<String> description = new ArrayList<>();
    private int prioroty;
    private final Set<RegionType> regionTypes = new HashSet<>();


    public ShopGroup(String groupName, Material groupIcon, int groupIconAmount,
                     String permission, List<String> description, int prioroty) {
        this.groupName = groupName;
        this.groupIcon = groupIcon;
        this.groupIconAmount = groupIconAmount;
        this.permission = permission;
        for(String s : description){
            this.description.add(Lang.translate(s));
        }
        this.prioroty = prioroty;
    }

    public Set<RegionType> getRegionTypes() {
        return regionTypes;
    }

    public void addRegionType(RegionType regionType){
        this.regionTypes.add(regionType);
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Material getGroupIcon() {
        return groupIcon;
    }

    public void setGroupIcon(Material groupIcon) {
        this.groupIcon = groupIcon;
    }

    public int getGroupIconAmount() {
        return groupIconAmount;
    }

    public void setGroupIconAmount(int groupIconAmount) {
        this.groupIconAmount = groupIconAmount;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public List<String> getDescription() {
        return description;
    }


    public int getPrioroty() {
        return prioroty;
    }

    public void setPrioroty(int prioroty) {
        this.prioroty = prioroty;
    }

}
