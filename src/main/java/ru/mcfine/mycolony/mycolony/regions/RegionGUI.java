package ru.mcfine.mycolony.mycolony.regions;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RegionGUI {

    public static ChestGui getHomeGui(Region region, Inventory chestInventory) {
        ChestGui gui = new ChestGui(5, "Region: " + region.getRegionName());
        gui.setOnGlobalClick(event -> event.setCancelled(true));

        OutlinePane background = new OutlinePane(0, 0, 9, 5, Pane.Priority.LOWEST);
        background.addItem(new GuiItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), event -> {
            System.out.println("Clicked on background");
        }));
        background.setRepeat(true);

        gui.addPane(background);

        OutlinePane navigationPane = new OutlinePane(3,1,3,1);
        ItemStack chest = new ItemStack(Material.CHEST);
        ItemMeta meta = chest.getItemMeta();
        meta.setDisplayName("Open inventory");
        chest.setItemMeta(meta);
        navigationPane.addItem(new GuiItem(chest, event ->{
            event.getWhoClicked().openInventory(chestInventory);
        }));
        gui.addPane(navigationPane);
        return gui;

    }

}
