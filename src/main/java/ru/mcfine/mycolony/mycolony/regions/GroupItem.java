package ru.mcfine.mycolony.mycolony.regions;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.mcfine.mycolony.mycolony.config.Lang;
import ru.mcfine.mycolony.mycolony.config.MyConfig;
import ru.mcfine.mycolony.mycolony.guis.BuildGui;
import ru.mcfine.mycolony.mycolony.guis.MaterialGroupGui;

import java.util.List;

public class GroupItem {

    private int currentId = 0;
    private List<Material> matGroup = null;
    private GuiItem guiItem;
    private ItemStack itemStack;
    public boolean isGroup = false;
    String groupName = null;
    private BuildGui parent;

    public GroupItem(BuildingMaterial buildingMaterial, int amount, BuildGui parent) {
        this.parent = parent;
        groupName = buildingMaterial.getGroupName();
        if (buildingMaterial.getMaterial() != null) {
            itemStack = new ItemStack(buildingMaterial.getMaterial(), amount);
            guiItem = new GuiItem(itemStack, inventoryClickEvent -> {
                inventoryClickEvent.setCancelled(true);
            });
        } else {
            isGroup = true;
            matGroup = MyConfig.getMaterialGroup(groupName).stream().toList();
            Material material = matGroup.get(0);
            itemStack = new ItemStack(material, amount);
            ItemMeta meta = itemStack.getItemMeta();
            meta.displayName(Lang.get("groups." + groupName + "-display-name"));
            itemStack.setItemMeta(meta);
            this.guiItem = new GuiItem(itemStack, event -> {
                MaterialGroupGui materialGroupGui = new MaterialGroupGui(groupName, this.parent);
                materialGroupGui.show(event.getWhoClicked());
            });
        }


    }

    public void setNext() {
        if (matGroup == null) return;

        int nextId = currentId;
        if (matGroup.size() > 1) nextId = (currentId + 1) % (matGroup.size() - 1);
        currentId = nextId;

        Material material = matGroup.get(nextId);
        this.guiItem.getItem().setType(material);
    }

    public GuiItem getGuiItem() {
        return guiItem;
    }


}
