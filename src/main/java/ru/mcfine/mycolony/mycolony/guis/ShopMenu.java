package ru.mcfine.mycolony.mycolony.guis;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.regions.RegionType;
import ru.mcfine.mycolony.mycolony.shop.ShopGroup;
import ru.mcfine.mycolony.mycolony.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class ShopMenu extends ChestGui {

    private int rows = 3;
    private List<GuiItem> guiItemList = new ArrayList<>();

    public ShopMenu() {
        super(3, "Shop");
        this.setRows(rows);

        OutlinePane background = new OutlinePane(0,0,9,rows, Pane.Priority.LOWEST);
        background.setRepeat(true);
        background.addItem(new GuiItem(Utils.getBackground(), event -> {
            event.setCancelled(true);
        }));
        this.addPane(background);

        if(rows - 2 > 0) {
            OutlinePane background2 = new OutlinePane(1, 1, 7, rows - 2, Pane.Priority.LOW);
            background2.addItem(new GuiItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), event -> event.setCancelled(true)));
            background2.setRepeat(true);
            this.addPane(background2);
        }

        PaginatedPane shopPane = new PaginatedPane(1,1,7, 1, Pane.Priority.HIGH);
        for(ShopGroup shopGroup : MyColony.plugin.config.getShopGroups().values()){
            ItemStack itemStack = new ItemStack(shopGroup.getGroupIcon(), shopGroup.getGroupIconAmount());
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.displayName(Component.text(shopGroup.getGroupName()));
            // TODO - replace string arraylist with component one in class
            List<Component> loreComponents = new ArrayList<>();
            for(String s : shopGroup.getDescription()) loreComponents.add(Component.text(s));
            itemMeta.lore(loreComponents);
            itemStack.setItemMeta(itemMeta);
            GuiItem guiItem = new GuiItem(itemStack, event -> {
                event.setCancelled(true);
                ShopGroupGui shopGroupGui = new ShopGroupGui(shopGroup, this);
                shopGroupGui.show(event.getWhoClicked());
            });
            guiItemList.add(guiItem);
        }
        shopPane.populateWithGuiItems(guiItemList);
        this.addPane(shopPane);
    }
}