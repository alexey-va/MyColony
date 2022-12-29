package ru.mcfine.mycolony.mycolony.guis;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.mcfine.mycolony.mycolony.config.Lang;
import ru.mcfine.mycolony.mycolony.regions.BuildingMaterial;
import ru.mcfine.mycolony.mycolony.regions.GroupItem;
import ru.mcfine.mycolony.mycolony.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class BuildGui extends ChestGui {

    int wPadding = 2;
    int vPadding = 2;
    public PaginatedPane materialPane = null;
    private final ArrayList<GroupItem> groupItems = new ArrayList<>();

    public BuildGui(List<BuildingMaterial> mats, Player p) {
        super(6, Lang.getString("menu.not-enough-materials", p));
        int rows = calculateRows(mats, wPadding, vPadding);
        int pages = (int)Math.ceil(rows/(6.0-vPadding));
        this.setRows(rows);

        OutlinePane background = new OutlinePane(0, 0, 9, rows, Pane.Priority.LOWEST);
        GuiItem bgItem = new GuiItem(Utils.getBackground(), event -> event.setCancelled(true));
        background.addItem(bgItem);
        background.setRepeat(true);
        this.addPane(background);

        if(rows - 2 > 0) {
            OutlinePane background2 = new OutlinePane(1, 1, 7, rows - 2, Pane.Priority.LOW);
            background2.addItem(new GuiItem(Utils.getBackground(Material.GRAY_STAINED_GLASS_PANE), event -> event.setCancelled(true)));
            background2.setRepeat(true);
            this.addPane(background2);
        }

        setOnClose(close -> Utils.getBackground(Material.GRAY_STAINED_GLASS_PANE));

        materialPane = new PaginatedPane(wPadding/2, vPadding/2, 9-wPadding, 9-vPadding, Pane.Priority.NORMAL);

        int id = 0;
        List<GuiItem> guiItems = new ArrayList<>();

        for(BuildingMaterial material : mats){
            int amount = material.getAmount();
            while(amount > 0){
                GroupItem groupItem = new GroupItem(material, Math.min(64, material.getAmount()), this, groupItems, p);
                groupItems.add(groupItem);
                guiItems.add(groupItem.getGuiItem());
                id++;
                amount-=64;
            }
        }

        materialPane.populateWithGuiItems(guiItems);
        this.addPane(materialPane);
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

    public ArrayList<GroupItem> getGroupItems() {
        return groupItems;
    }
}
