package ru.mcfine.mycolony.mycolony.guis;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.config.Lang;
import ru.mcfine.mycolony.mycolony.regions.RegionType;
import ru.mcfine.mycolony.mycolony.shop.ShopGroup;
import ru.mcfine.mycolony.mycolony.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class ShopGroupGui extends ChestGui {
    private List<GuiItem> guiItemList = new ArrayList<>();
    private ShopMenu parentMenu;
    public ShopGroupGui(ShopGroup shopGroup, ShopMenu parentMenu) {
        super(3, shopGroup.getGroupName());
        this.parentMenu = parentMenu;

        int rows = Math.max((int)Math.ceil(shopGroup.getRegionTypes().size()/7.0),1);
        int invRows = Math.min(6, rows+2);
        this.setRows(invRows);

        OutlinePane background = new OutlinePane(0,0,9,invRows, Pane.Priority.LOWEST);
        background.addItem(new GuiItem(Utils.getBackground(), event -> {
            event.setCancelled(true);
        }));
        background.setRepeat(true);
        this.addPane(background);

        if(invRows -2 >0) {
            OutlinePane background2 = new OutlinePane(1, 1, 7, invRows-2, Pane.Priority.LOW);
            background2.addItem(new GuiItem(Utils.getBackground(Material.GRAY_STAINED_GLASS_PANE), event -> event.setCancelled(true)));
            background2.setRepeat(true);
            this.addPane(background2);
        }

        PaginatedPane shopPane = new PaginatedPane(1,1, 7, invRows-2);
        for(RegionType regionType : shopGroup.getRegionTypes()){
            System.out.println("Region type: "+regionType);
            ItemStack itemStack = new ItemStack(regionType.getShopIcon(), regionType.getShopAmount());
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.displayName(Lang.translateToComponent(regionType.getDisplayName()));
            List<Component> descr = new ArrayList<>();
            descr.add(Lang.translateToComponent("Level: "+regionType.getLevel()));
            descr.add(Lang.translateToComponent("Cost: "+regionType.getPrice()));
            itemMeta.lore(descr);
            itemStack.setItemMeta(itemMeta);
            GuiItem guiItem = new GuiItem(itemStack, inventoryClickEvent -> {
                ShopItemGui shopItemGui = new ShopItemGui(regionType, this);
                shopItemGui.show(inventoryClickEvent.getWhoClicked());
                inventoryClickEvent.setCancelled(true);
            });
            guiItemList.add(guiItem);
        }
        shopPane.populateWithGuiItems(guiItemList);
        this.addPane(shopPane);

        StaticPane navigation = new StaticPane(0, invRows-1, 9, 1, Pane.Priority.HIGH);
        ItemStack
        GuiItem prev = new GuiItem(new ItemStack(Material.RED_WOOL), inventoryClickEvent -> {
            if(shopPane.getPage() == 0){
                parentMenu.show(inventoryClickEvent.getWhoClicked());
            } else{
                shopPane.setPage(shopPane.getPage() -1);
                this.update();
            }
        });
        navigation.addItem(prev, 0 ,0 );

        GuiItem next = new GuiItem(new ItemStack(Material.GREEN_WOOL), inventoryClickEvent -> {
            if(shopPane.getPages() ==1) return;
           if(shopPane.getPage() == shopPane.getPages() - 1){
               shopPane.setPage(0);
           } else{
               shopPane.setPage(shopPane.getPage()+1);
           }
           this.update();
        });
        navigation.addItem(next, 8, 0);
        this.addPane(navigation);

    }
}
