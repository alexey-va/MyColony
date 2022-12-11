package ru.mcfine.mycolony.mycolony.guis;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ru.mcfine.mycolony.mycolony.production.ProductionEntry;
import ru.mcfine.mycolony.mycolony.regions.GroupItem;
import ru.mcfine.mycolony.mycolony.regions.ProductionItem;
import ru.mcfine.mycolony.mycolony.regions.RegionType;
import ru.mcfine.mycolony.mycolony.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class ShopItemGui extends ChestGui {
    private ShopGroupGui parentMenu;
    private List<StaticPane> entriesPanes = new ArrayList<>();
    private PaginatedPane prod;
    private List<GroupItem> groupItems = new ArrayList<>();
    public ShopItemGui(RegionType regionType, ShopGroupGui parentMenu) {
        super(6, regionType.getDisplayName());
        this.parentMenu = parentMenu;

        setOnClose(close -> {
            Utils.getBackground(Material.GRAY_STAINED_GLASS_PANE);
        });

        OutlinePane background = new OutlinePane(0,0,9,6, Pane.Priority.LOWEST);
        background.addItem(new GuiItem(Utils.getBackground(), event -> event.setCancelled(true)));
        background.setRepeat(true);
        this.addPane(background);

        OutlinePane background2 = new OutlinePane(1,2,7,3, Pane.Priority.LOW);
        background2.addItem(new GuiItem(Utils.getBackground(Material.GRAY_STAINED_GLASS_PANE), event -> event.setCancelled(true)));
        background2.setRepeat(true);
        this.addPane(background2);

        prod = new PaginatedPane(1, 2, 7, 3, Pane.Priority.NORMAL);
        GuiItem separatorItem = new GuiItem(Utils.getBackground(Material.BLUE_STAINED_GLASS_PANE), event -> event.setCancelled(true));
        int pageId = 0;
        for(ProductionEntry entry : regionType.getProductionEntries()){
            StaticPane staticPane = new StaticPane(0, 0, 7, 4, Pane.Priority.NORMAL);
            staticPane.addItem(separatorItem, 3, 0);
            staticPane.addItem(separatorItem, 3, 1);
            staticPane.addItem(separatorItem, 3, 2);
            int coordinate = 0;
            for(ProductionItem item : entry.getInput()){
                GroupItem groupItem = new GroupItem(item, item.getAmount(), this, groupItems);
                GuiItem inputItem = groupItem.getGuiItem();
                staticPane.addItem(inputItem, (coordinate%3), (coordinate/3));
                groupItems.add(groupItem);
                coordinate++;
            }
            coordinate = 0;
            for(ProductionItem item : entry.getOutput()){
                GroupItem groupItem = new GroupItem(item, item.getAmount(), this, groupItems);
                GuiItem outputItem = groupItem.getGuiItem();
                staticPane.addItem(outputItem, (coordinate%3 + 4), (coordinate/3));
                groupItems.add(groupItem);
                coordinate++;
            }
            prod.addPane(pageId, staticPane);
            pageId++;
        }
        this.addPane(prod);

        StaticPane navigation = new StaticPane(0, 5, 9, 1, Pane.Priority.HIGH);
        GuiItem back = new GuiItem(new ItemStack(Material.RED_WOOL), inventoryClickEvent -> {
           if(prod.getPage() == 0){
               parentMenu.show(inventoryClickEvent.getWhoClicked());
           } else{
               this.prod.setPage(prod.getPage()-1);
               this.update();
           }
           inventoryClickEvent.setCancelled(true);
        });
        navigation.addItem(back, 0, 0);

        if(prod.getPages() > 1) {
            GuiItem next = new GuiItem(new ItemStack(Material.GREEN_WOOL), inventoryClickEvent -> {
                if (prod.getPage() == prod.getPages() - 1) {
                    prod.setPage(0);
                } else {
                    prod.setPage(prod.getPage() + 1);
                }
                this.update();
                inventoryClickEvent.setCancelled(true);
            });
            navigation.addItem(next, 8, 0);
        }
        this.addPane(navigation);
    }
}
