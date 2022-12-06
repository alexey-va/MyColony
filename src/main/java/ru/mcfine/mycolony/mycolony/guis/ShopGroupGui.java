package ru.mcfine.mycolony.mycolony.guis;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.regions.RegionType;
import ru.mcfine.mycolony.mycolony.shop.ShopGroup;
import ru.mcfine.mycolony.mycolony.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class ShopGroupGui extends ChestGui {
    private List<GuiItem> guiItemList = new ArrayList<>();
    private ShopMenu parentMenu;
    public ShopGroupGui(String groupName, ShopMenu parentMenu) {
        super(3, groupName);
        this.parentMenu = parentMenu;
        ShopGroup shopGroup = MyColony.plugin.config.getShopGroups().get(groupName);
        if(shopGroup == null){
            System.out.println("Group "+groupName+" not found!");
            this.getViewers().forEach(HumanEntity::closeInventory);
            return;
        }

        int rows = (int)Math.ceil(shopGroup.getRegionTypes().size()/7.0);
        int invRows = Math.min(6, rows+2);
        this.setRows(invRows);

        OutlinePane background = new OutlinePane(0,0,9,invRows, Pane.Priority.LOWEST);
        background.addItem(new GuiItem(Utils.getBackground(), event -> {
            event.setCancelled(true);
        }));
        background.setRepeat(true);
        this.addPane(background);

        PaginatedPane shopPane = new PaginatedPane(1,1, 7, invRows-2);
        for(RegionType regionType : shopGroup.getRegionTypes()){
            ItemStack itemStack = new ItemStack(regionType.getShopIcon(), regionType.getShopAmount());
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.displayName(Component.text(regionType.getDisplayName()));
            List<Component> descr = new ArrayList<>();
            descr.add(Component.text("Level: "+regionType.getLevel()));
            descr.add(Component.text("Cost: "+regionType.getPrice()));
            itemMeta.lore(descr);
            itemStack.setItemMeta(itemMeta);
            GuiItem guiItem = new GuiItem(itemStack, inventoryClickEvent -> {

            });
        }

    }
}
