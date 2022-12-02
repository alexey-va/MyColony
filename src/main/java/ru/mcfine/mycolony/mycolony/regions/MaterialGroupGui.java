package ru.mcfine.mycolony.mycolony.regions;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import javafx.util.Pair;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ru.mcfine.mycolony.mycolony.util.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class MaterialGroupGui extends ChestGui {

    int vPadding = 2;
    int wPadding = 2;
    BuildGui parent;

    public MaterialGroupGui(HashSet<Material> mats, BuildGui parent) {
        super(6, " ");

        this.parent = parent;

        int rows = mats.size();
        int pages = (int) Math.ceil(rows / (6.0 - vPadding));
        this.setRows(rows);

        OutlinePane background = new OutlinePane(0, 0, 9, rows, Pane.Priority.LOWEST);
        GuiItem bgItem = new GuiItem(Utils.getBackground(), event -> {
            event.setCancelled(true);
        });
        background.addItem(bgItem);
        background.setRepeat(true);
        this.addPane(background);

        PaginatedPane materialPane = new PaginatedPane(wPadding / 2, vPadding / 2, 9 - wPadding, 9 - vPadding, Pane.Priority.NORMAL);
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

        GuiItem prevPage = new GuiItem(new ItemStack(Material.GREEN_WOOL, 1), event -> {
            System.out.println(materialPane.getPage() +" | page");
            if(materialPane.getPage() > 0) materialPane.setPage(materialPane.getPage() - 1);
            else parent.show(event.getWhoClicked());
            this.update();
        });
        navigation.addItem(prevPage, 0, 0);

        GuiItem nextPage = new GuiItem(new ItemStack(Material.GREEN_WOOL), event -> {
            if (materialPane.getPage() < materialPane.getPages()) materialPane.setPage(materialPane.getPage() + 1);
            else materialPane.setPage(1);
        });
        navigation.addItem(nextPage, 8, 0);

        this.addPane(navigation);

        this.addPane(materialPane);
    }

}
