package ru.mcfine.mycolony.mycolony.regions;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.mcfine.mycolony.mycolony.config.Lang;
import ru.mcfine.mycolony.mycolony.config.MyConfig;
import ru.mcfine.mycolony.mycolony.guis.BuildGui;
import ru.mcfine.mycolony.mycolony.guis.MaterialGroupGui;
import ru.mcfine.mycolony.mycolony.tasks.TickerRunnable;

import java.util.List;

public class GroupItem {

    private int currentId = 0;
    private List<Material> matGroup = null;
    private GuiItem guiItem;
    public boolean isGroup = false;
    public boolean isMoney = false;
    String groupName = null;
    private final Gui parent;
    private List<GroupItem> groupItems;

    public GroupItem(BuildingMaterial buildingMaterial, int amount, Gui parent, List<GroupItem> groupItems) {
        this.parent = parent;
        this.groupItems = groupItems;
        groupName = buildingMaterial.getGroupName();
        ItemStack itemStack;
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
                MaterialGroupGui materialGroupGui = new MaterialGroupGui(groupName, this.parent, groupItems);
                materialGroupGui.show(event.getWhoClicked());
            });
        }

        if(this.isGroup) TickerRunnable.groupItemList.add(this);


    }

    public GroupItem(ProductionItem productionItem, int amount, Gui parent, List<GroupItem> groupItems){
        this.parent = parent;
        groupName = productionItem.getGroupName();
        ItemStack itemStack;
        if (productionItem.getMaterial() != null) {
            itemStack = new ItemStack(productionItem.getMaterial(), amount);
            guiItem = new GuiItem(itemStack, inventoryClickEvent -> {
                inventoryClickEvent.setCancelled(true);
            });
        } else if(productionItem.getGroup() != null) {
            isGroup = true;
            matGroup = MyConfig.getMaterialGroup(groupName).stream().toList();
            Material material = matGroup.get(0);
            itemStack = new ItemStack(material, amount);
            ItemMeta meta = itemStack.getItemMeta();
            meta.displayName(Lang.get("groups." + groupName + "-display-name"));
            itemStack.setItemMeta(meta);
            this.guiItem = new GuiItem(itemStack, event -> {
                MaterialGroupGui materialGroupGui = new MaterialGroupGui(groupName, this.parent, groupItems);
                materialGroupGui.show(event.getWhoClicked());
            });
        } else if(productionItem.getMoneyAmount() > 0.0){
            isMoney = true;
            itemStack = new ItemStack(Material.GOLD_INGOT, 1);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.displayName(Component.text("Money: "+productionItem.getMoneyAmount()));
            itemStack.setItemMeta(itemMeta);
            this.guiItem = new GuiItem(itemStack, inventoryClickEvent -> inventoryClickEvent.setCancelled(true));
        }

        if(this.isGroup) TickerRunnable.groupItemList.add(this);
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

    public Gui getParent() {
        return parent;
    }
}
