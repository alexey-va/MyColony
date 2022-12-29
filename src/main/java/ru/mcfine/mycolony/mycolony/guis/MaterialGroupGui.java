package ru.mcfine.mycolony.mycolony.guis;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.mcfine.mycolony.mycolony.config.Lang;
import ru.mcfine.mycolony.mycolony.config.MyConfig;
import ru.mcfine.mycolony.mycolony.regions.GroupItem;
import ru.mcfine.mycolony.mycolony.tasks.TickerRunnable;
import ru.mcfine.mycolony.mycolony.util.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MaterialGroupGui extends ChestGui {

    int vPadding = 2;
    int wPadding = 2;
    Gui parent;
    List<GroupItem> groupItems;

    public MaterialGroupGui(String groupName, Gui parent, List<GroupItem> groupItems, Player p) {
        super(6, Lang.getString("groups." + groupName + "-display-name", p));

        HashSet<Material> mats = MyConfig.getMaterialGroup(groupName);

        this.groupItems=groupItems;
        this.parent = parent;

        int rows = Math.min(6, (int)(Math.ceil(mats.size()/(9.0-wPadding) ) ) );
        this.setRows(rows);

        OutlinePane background = new OutlinePane(0, 0, 9, rows, Pane.Priority.LOWEST);
        GuiItem bgItem = new GuiItem(Utils.getBackground(), event -> event.setCancelled(true));
        background.addItem(bgItem);
        background.setRepeat(true);
        this.addPane(background);

        if(rows -2 > 0) {
            OutlinePane background2 = new OutlinePane(1, 1, 7, 4, Pane.Priority.LOW);
            background2.addItem(new GuiItem(Utils.getBackground(Material.GRAY_STAINED_GLASS_PANE), event -> event.setCancelled(true)));
            background2.setRepeat(true);
            this.addPane(background2);
        }

        PaginatedPane materialPane = new PaginatedPane(wPadding / 2, vPadding / 2, 9 - wPadding, rows - vPadding, Pane.Priority.NORMAL);
        List<ItemStack> itemStacks = new ArrayList<>();
        for (Material material : mats) {
            itemStacks.add(new ItemStack(material, 1));
        }
        materialPane.populateWithItemStacks(itemStacks);
        materialPane.setOnClick(event -> event.setCancelled(true));

        this.addPane(materialPane);

        StaticPane navigation = new StaticPane(0, rows - 1, 9, 1);

        GuiItem prevPage = new GuiItem(Utils.getPrevPage(Material.ARROW), event -> {
            //System.out.println(materialPane.getPage() +" | page");
            if(materialPane.getPage() > 0){
                materialPane.setPage(materialPane.getPage() - 1);
                this.update();
            }
            else{
                parent.show(event.getWhoClicked());
                if(groupItems != null) TickerRunnable.groupItemList.addAll(groupItems.stream().filter(s -> s.isGroup).toList());
            }
        });
        navigation.addItem(prevPage, 0, 0);

        GuiItem nextPage = new GuiItem(Utils.getNextPage(Material.ARROW), event -> {
            if (materialPane.getPage() < materialPane.getPages() - 1) materialPane.setPage(materialPane.getPage() + 1);
            else materialPane.setPage(0);
            this.update();
        });
        navigation.addItem(nextPage, 8, 0);

        this.addPane(navigation);

        this.addPane(materialPane);
    }

}
