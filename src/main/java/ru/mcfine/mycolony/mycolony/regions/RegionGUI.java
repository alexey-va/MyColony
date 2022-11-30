package ru.mcfine.mycolony.mycolony.regions;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.tasks.TickerRunnable;

public class RegionGUI {

    public static ChestGui getHomeGui(Region region, Block block) {
        ChestGui gui = new ChestGui(5, "Region: " + region.getRegionName());
        gui.setOnGlobalClick(event -> event.setCancelled(true));

        OutlinePane background = new OutlinePane(0, 0, 9, 5, Pane.Priority.LOWEST);
        background.addItem(new GuiItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), event -> {
            System.out.println("Clicked on background");
        }));
        background.setRepeat(true);

        gui.addPane(background);

        gui.setOnClose(event -> {
            TickerRunnable.guis.remove(event.getPlayer().getName());
        });

        OutlinePane navigationPane = new OutlinePane(3,1,3,1);

        ItemStack chest = new ItemStack(Material.CHEST);
        ItemMeta meta = chest.getItemMeta();
        meta.setDisplayName("Open inventory");
        chest.setItemMeta(meta);
        navigationPane.addItem(new GuiItem(chest, event ->{
            event.getWhoClicked().closeInventory();
            event.getWhoClicked().openInventory(((Chest)block).getBlockInventory());
        }));

        ItemStack timer = new ItemStack(Material.CLOCK);
        ItemMeta meta1 = timer.getItemMeta();
        meta1.setDisplayName("Next cycle in: "+Math.ceil(region.getMaxTime() - region.getTimeElapsed())+"s");

        gui.addPane(navigationPane);
        return gui;

    }

}
