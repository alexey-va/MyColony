package ru.mcfine.mycolony.mycolony.guis;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ru.mcfine.mycolony.mycolony.config.Lang;
import ru.mcfine.mycolony.mycolony.config.MyConfig;
import ru.mcfine.mycolony.mycolony.util.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MaterialGroupGui extends ChestGui {

    int vPadding = 2;
    int wPadding = 2;
    BuildGui parent;

    public MaterialGroupGui(String groupName, BuildGui parent) {
        super(6, Lang.getString("groups." + groupName + "-display-name"));

        HashSet<Material> mats = MyConfig.getMaterialGroup(groupName);

        this.parent = parent;

        int rows = Math.min(6, (int)(Math.ceil(mats.size()/(9.0-wPadding) ) ) );
        this.setRows(rows);

        OutlinePane background = new OutlinePane(0, 0, 9, rows, Pane.Priority.LOWEST);
        GuiItem bgItem = new GuiItem(Utils.getBackground(), event -> {
            event.setCancelled(true);
        });
        background.addItem(bgItem);
        background.setRepeat(true);
        this.addPane(background);

        PaginatedPane materialPane = new PaginatedPane(wPadding / 2, vPadding / 2, 9 - wPadding, rows - vPadding, Pane.Priority.NORMAL);
        List<ItemStack> itemStacks = new ArrayList<>();
        for (Material material : mats) {
            itemStacks.add(new ItemStack(material, 1));
        }
        materialPane.populateWithItemStacks(itemStacks);
        materialPane.setOnClick(event -> {
            event.setCancelled(true);
        });

        this.addPane(materialPane);

        StaticPane navigation = new StaticPane(0, rows - 1, 9, 1);

        GuiItem prevPage = new GuiItem(new ItemStack(Material.RED_WOOL), event -> {
            //System.out.println(materialPane.getPage() +" | page");
            if(materialPane.getPage() > 0){
                materialPane.setPage(materialPane.getPage() - 1);
                this.update();
            }
            else{
                parent.show(event.getWhoClicked());
                BuildGui.buildGuis.add(parent);
            }
        });
        navigation.addItem(prevPage, 0, 0);

        GuiItem nextPage = new GuiItem(new ItemStack(Material.GREEN_WOOL), event -> {
            if (materialPane.getPage() < materialPane.getPages() - 1) materialPane.setPage(materialPane.getPage() + 1);
            else materialPane.setPage(0);
            this.update();
        });
        navigation.addItem(nextPage, 8, 0);

        this.addPane(navigation);

        this.addPane(materialPane);
    }

}
