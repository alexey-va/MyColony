package ru.mcfine.mycolony.mycolony.regions;

import com.github.stefvanschie.inventoryframework.adventuresupport.TextHolder;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.config.Lang;
import ru.mcfine.mycolony.mycolony.tasks.TickerRunnable;

public class RegionGUI extends ChestGui{

    public OutlinePane navigationPane;
    public GuiItem clockItem;
    public RegionGUI(Block block, Region region) {
        super(5, (TextHolder) Lang.get(region.getRegionName()+"_name"));
        OutlinePane background = new OutlinePane(0, 0, 9, 5, Pane.Priority.LOWEST);
        background.addItem(new GuiItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), event -> {
            event.setCancelled(true);
        }));
        background.setRepeat(true);

        this.addPane(background);

        this.setOnClose(event -> {
            TickerRunnable.mainMenuGuis.remove(this);
        });

        navigationPane = new OutlinePane(3,1,3,1);

        ItemStack chest = new ItemStack(Material.CHEST);
        ItemMeta meta = chest.getItemMeta();
        meta.setDisplayName("Open inventory");
        chest.setItemMeta(meta);
        GuiItem chestItem = new GuiItem(chest, event ->{
            event.getWhoClicked().openInventory(((Chest)block.getState()).getInventory());
            event.setCancelled(true);
        });
        navigationPane.addItem(chestItem);

        ItemStack timer = new ItemStack(Material.CLOCK);
        ItemMeta meta1 = timer.getItemMeta();
        meta1.setDisplayName("Next cycle in: "+Math.ceil(region.getMaxTime() - region.getTimeElapsed())+"s");
        timer.setItemMeta(meta1);
        clockItem = new GuiItem(timer, event ->{
            event.setCancelled(true);
            update();
        });
        navigationPane.addItem(clockItem);

        this.addPane(navigationPane);
    }

    public static ChestGui getHomeGui(Region region, Block block) {
        ChestGui  gui = new ChestGui(5, "Region: " + region.getRegionName());
        //gui.setOnGlobalClick(event -> event.setCancelled(true));


        return gui;

    }

}
