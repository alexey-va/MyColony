package ru.mcfine.mycolony.mycolony.regions;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import javafx.util.Pair;
import ru.mcfine.mycolony.mycolony.config.Lang;
import ru.mcfine.mycolony.mycolony.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class BuildGui extends ChestGui {

    int wPadding = 2;
    int vPadding = 2;
    public PaginatedPane materialPane = null;
    private ArrayList<Pair<GroupItem, Integer>> groupItems = new ArrayList<>();
    public static ArrayList<BuildGui> buildGuis = new ArrayList<>();

    public BuildGui(List<BuildingMaterial> mats) {
        super(6, Lang.getString("menu.not-enough-materials"));
        int rows = calculateRows(mats, wPadding, vPadding);
        int pages = (int)Math.ceil(rows/(6.0-vPadding));
        this.setRows(rows);

        OutlinePane background = new OutlinePane(0, 0, 9, rows, Pane.Priority.LOWEST);
        GuiItem bgItem = new GuiItem(Utils.getBackground(), event -> {
            event.setCancelled(true);
        });
        background.addItem(bgItem);
        background.setRepeat(true);
        this.addPane(background);

        materialPane = new PaginatedPane(wPadding/2, vPadding/2, 9-wPadding, 9-vPadding, Pane.Priority.NORMAL);

        int id = 0;
        List<GuiItem> guiItems = new ArrayList<>();

        for(BuildingMaterial material : mats){
            int amount = material.getAmount();
            while(amount > 0){
                GroupItem groupItem = new GroupItem(material, Math.min(64, material.getAmount()), this);
                groupItems.add(new Pair<>(groupItem, id));
                guiItems.add(groupItem.getGuiItem());
                id++;
                amount-=64;
            }
        }

        materialPane.populateWithGuiItems(guiItems);


        this.addPane(materialPane);

        buildGuis.add(this);

        this.setOnClose(event -> {
            buildGuis.remove(this);
        });


    }


    private int calculateRows(List<BuildingMaterial> mats, int wPadding, int vPadding){
        int slots = 0;
        for(BuildingMaterial material : mats){
            System.out.println(material.getGroupName()+" | "+material.getMaterial());
            int newSlots = (int)Math.ceil(material.getAmount()/64.0);
            slots+=newSlots;
        }
        return (int)(Math.ceil(slots/(9.0-wPadding)))+vPadding;
    }

    public ArrayList<Pair<GroupItem, Integer>> getGroupItems() {
        return groupItems;
    }
}
